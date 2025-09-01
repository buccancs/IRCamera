"""
Network Server for IRCamera PC Controller

Manages JSON-based communication with Android devices using formal protocol definition.
Implements FR2: Synchronised Multi-Modal Recording and FR7: Device Synchronisation.
"""

import asyncio
import json
import uuid
from datetime import datetime, timezone
from typing import Dict, Any, Optional, List, Set, Callable
from dataclasses import dataclass, asdict
from enum import Enum

try:
    from loguru import logger
except ImportError:
    from ..utils.simple_logger import logger

from ..core.config import config
from .protocol import get_protocol_manager, create_message, validate_message, ValidationError


class DeviceState(Enum):
    """Device connection states."""

    DISCONNECTED = "disconnected"
    CONNECTING = "connecting"
    CONNECTED = "connected"
    RECORDING = "recording"
    ERROR = "error"


class MessageType(Enum):
    """Message types for device communication."""

    # Device lifecycle
    DEVICE_REGISTER = "device_register"
    DEVICE_HEARTBEAT = "device_heartbeat"
    DEVICE_STATUS = "device_status"

    # Session control
    SESSION_START = "session_start"
    SESSION_STOP = "session_stop"
    RECORDING_START = "recording_start"
    RECORDING_STOP = "recording_stop"

    # Synchronization
    SYNC_MARK = "sync_mark"
    SYNC_FLASH = "sync_flash"

    # File transfer
    FILE_TRANSFER_REQUEST = "file_transfer_request"
    FILE_TRANSFER_COMPLETE = "file_transfer_complete"

    # Responses
    ACK = "ack"
    ERROR = "error"


@dataclass
class DeviceInfo:
    """Information about a connected device."""

    device_id: str
    device_type: str
    capabilities: List[str]
    ip_address: str
    port: int
    state: str = DeviceState.CONNECTED.value
    last_heartbeat: Optional[str] = None
    battery_level: Optional[int] = None
    is_gsr_leader: bool = False
    gsr_mode: str = "local"

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary."""
        return asdict(self)


class NetworkServer:
    """
    Network server for device communication and coordination.

    Implements device communication requirements:
    - JSON/TCP/IP command protocol for Android devices
    - Device registration and heartbeat monitoring
    - Synchronised start/stop commands across all devices
    - Sync signal broadcasting (flash cues)
    - Device fault detection and recovery
    """

    def __init__(self):
        """Initialize network server."""
        self._server: Optional[asyncio.Server] = None
        self._clients: Dict[str, asyncio.StreamWriter] = {}
        self._devices: Dict[str, DeviceInfo] = {}
        self._message_handlers: Dict[str, Callable] = {}
        self._heartbeat_task: Optional[asyncio.Task] = None
        self._is_running = False

        # Protocol manager
        self._protocol = get_protocol_manager()

        # Configuration
        transport_config = self._protocol.get_transport_config()
        self._host = config.get("network.server_host", transport_config.get("host", "0.0.0.0"))
        self._port = config.get("network.server_port", transport_config.get("port", 8080))
        self._max_connections = config.get("network.max_connections", 8)

        connection_config = transport_config.get("connection", {})
        self._heartbeat_interval = config.get(
            "network.heartbeat_interval", connection_config.get("heartbeat_interval_s", 5)
        )
        self._connection_timeout = config.get(
            "network.connection_timeout", connection_config.get("timeout_s", 30)
        )

        # Get max message size from protocol
        framing = transport_config.get("message_framing", {})
        self._max_message_size = framing.get("max_message_size", 1024 * 1024)  # 1MB default

        # Event callbacks
        self._on_device_connected: Optional[Callable] = None
        self._on_device_disconnected: Optional[Callable] = None
        self._on_device_status_update: Optional[Callable] = None

        self._setup_message_handlers()
        protocol_version = self._protocol.get_protocol_info()["version"]
        logger.info(f"Network Server initialized with protocol {protocol_version}")

    def _setup_message_handlers(self) -> None:
        """Set up message handlers for different message types."""
        self._message_handlers = {
            "device_register": self._handle_device_register,
            "device_heartbeat": self._handle_device_heartbeat,
            "device_status": self._handle_device_status,
            "file_transfer_complete": self._handle_file_transfer_complete,
            "time_sync_request": self._handle_time_sync_request,
            "gsr_data_batch": self._handle_gsr_data_batch,
            "gsr_leader_election": self._handle_gsr_leader_election,
        }

    async def start(self) -> None:
        """Start the network server."""
        if self._is_running:
            logger.warning("Network server is already running")
            return

        try:
            self._server = await asyncio.start_server(
                self._handle_client, self._host, self._port, limit=2**16  # 64KB buffer
            )

            # Start heartbeat monitoring
            self._heartbeat_task = asyncio.create_task(self._monitor_heartbeats())

            self._is_running = True

            addr = self._server.sockets[0].getsockname()
            logger.info(f"Network server started on {addr[0]}:{addr[1]}")

        except Exception as e:
            logger.error(f"Failed to start network server: {e}")
            raise

    async def stop(self) -> None:
        """Stop the network server."""
        if not self._is_running:
            return

        self._is_running = False

        # Cancel heartbeat monitoring
        if self._heartbeat_task:
            self._heartbeat_task.cancel()
            try:
                await self._heartbeat_task
            except asyncio.CancelledError:
                pass

        # Close all client connections
        for client in self._clients.values():
            client.close()
            await client.wait_closed()

        self._clients.clear()
        self._devices.clear()

        # Close server
        if self._server:
            self._server.close()
            await self._server.wait_closed()

        logger.info("Network server stopped")

    async def _handle_client(
        self, reader: asyncio.StreamReader, writer: asyncio.StreamWriter
    ) -> None:
        """Handle new client connection."""
        addr = writer.get_extra_info("peername")
        logger.info(f"Client connected from {addr}")

        try:
            while True:
                # Read message length (4 bytes)
                length_data = await reader.readexactly(4)
                message_length = int.from_bytes(length_data, "big")

                if message_length > self._max_message_size:
                    logger.warning(f"Message too large from {addr}: {message_length} bytes")
                    break

                # Read message data
                message_data = await reader.readexactly(message_length)

                try:
                    message = json.loads(message_data.decode("utf-8"))
                    await self._process_message(message, writer)

                except json.JSONDecodeError as e:
                    logger.warning(f"Invalid JSON from {addr}: {e}")
                    await self._send_error(writer, "Invalid JSON format")

        except asyncio.IncompleteReadError:
            logger.debug(f"Client {addr} disconnected")
        except Exception as e:
            logger.error(f"Error handling client {addr}: {e}")
        finally:
            # Clean up client
            device_id = None
            for did, client in self._clients.items():
                if client == writer:
                    device_id = did
                    break

            if device_id:
                await self._handle_device_disconnect(device_id)

            writer.close()
            await writer.wait_closed()

    async def _process_message(self, message: Dict[str, Any], writer: asyncio.StreamWriter) -> None:
        """Process incoming message from device using protocol validation."""
        try:
            # Validate message against protocol
            if not validate_message(message, strict=False):
                await self._send_error(writer, "Message validation failed")
                return

            message_type = message.get("message_type")
            message_id = message.get("message_id", str(uuid.uuid4()))

            if not message_type:
                await self._send_error(writer, "Missing message_type field", message_id)
                return

            # Handle message using protocol-aware handlers
            if message_type in self._message_handlers:
                response = await self._message_handlers[message_type](message, writer)
                if response:
                    response["message_id"] = message_id
                    await self._send_message(writer, response)
            else:
                logger.warning(f"Unknown message type: {message_type}")
                await self._send_error(writer, f"Unknown message type: {message_type}", message_id)

        except ValidationError as e:
            logger.warning(f"Protocol validation error: {e}")
            await self._send_error(writer, f"Protocol validation error: {e}")
        except Exception as e:
            logger.error(f"Error processing message: {e}")
            await self._send_error(writer, str(e))

    async def _handle_device_register(
        self, message: Dict[str, Any], writer: asyncio.StreamWriter
    ) -> Dict[str, Any]:
        """Handle device registration."""
        try:
            device_id = message.get("device_id")
            device_type = message.get("device_type", "unknown")
            capabilities = message.get("capabilities", [])

            if not device_id:
                return {"type": MessageType.ERROR.value, "error": "Missing device_id"}

            # Check connection limit
            if len(self._devices) >= self._max_connections:
                return {"type": MessageType.ERROR.value, "error": "Maximum connections exceeded"}

            # Get client address
            addr = writer.get_extra_info("peername")

            # Create device info
            device_info = DeviceInfo(
                device_id=device_id,
                device_type=device_type,
                capabilities=capabilities,
                ip_address=addr[0],
                port=addr[1],
                last_heartbeat=datetime.now(timezone.utc).isoformat(),
            )

            # Determine GSR leader
            if "gsr_sensor" in capabilities and not any(
                d.is_gsr_leader for d in self._devices.values()
            ):
                device_info.is_gsr_leader = True
                device_info.gsr_mode = config.get("gsr.default_mode", "local")
                logger.info(f"Device {device_id} elected as GSR leader")

            # Store device and client
            self._devices[device_id] = device_info
            self._clients[device_id] = writer

            logger.info(
                f"Device registered: {device_id} ({device_type}) with capabilities: {capabilities}"
            )

            # Notify callback
            if self._on_device_connected:
                self._on_device_connected(device_info)

            return create_message("ack", ack_for="device_register", status="success")

        except Exception as e:
            logger.error(f"Error handling device registration: {e}")
            return create_message("error", error_code="RESOURCE_UNAVAILABLE", error_message=str(e))

    async def _handle_device_heartbeat(
        self, message: Dict[str, Any], writer: asyncio.StreamWriter
    ) -> Dict[str, Any]:
        """Handle device heartbeat using protocol format."""
        device_id = message.get("device_id")

        if device_id in self._devices:
            self._devices[device_id].last_heartbeat = datetime.now(timezone.utc).isoformat()

            # Update device status if provided
            if "battery_level" in message:
                self._devices[device_id].battery_level = message["battery_level"]

            logger.debug(f"Heartbeat from {device_id}")
            return create_message("ack", ack_for="device_heartbeat", status="success")

        return create_message(
            "error", error_code="DEVICE_BUSY", error_message="Device not registered"
        )

    async def _handle_device_status(
        self, message: Dict[str, Any], writer: asyncio.StreamWriter
    ) -> Dict[str, Any]:
        """Handle device status update using protocol format."""
        device_id = message.get("device_id")

        if device_id in self._devices:
            device = self._devices[device_id]

            # Update status fields
            if "status" in message:
                device.state = message["status"]
            if "battery_level" in message:
                device.battery_level = message["battery_level"]

            logger.debug(f"Status update from {device_id}: {message}")

            # Notify callback
            if self._on_device_status_update:
                self._on_device_status_update(device)

            return create_message("ack", ack_for="device_status", status="success")

        return create_message(
            "error", error_code="DEVICE_BUSY", error_message="Device not registered"
        )

    async def _handle_file_transfer_complete(
        self, message: Dict[str, Any], writer: asyncio.StreamWriter
    ) -> Dict[str, Any]:
        """Handle file transfer completion notification using protocol format."""
        device_id = message.get("device_id")
        transfer_id = message.get("transfer_id")
        status = message.get("status")

        logger.info(f"File transfer {status} from {device_id}: {transfer_id}")

        return create_message("ack", ack_for="file_transfer_complete", status="success")

    async def _handle_time_sync_request(
        self, message: Dict[str, Any], writer: asyncio.StreamWriter
    ) -> Dict[str, Any]:
        """Handle time synchronization request using protocol format."""
        device_id = message.get("device_id")
        client_timestamp = message.get("client_timestamp")

        server_timestamp = datetime.now(timezone.utc).isoformat()

        return create_message(
            "time_sync_response",
            server_timestamp=server_timestamp,
            client_timestamp=client_timestamp,
            processing_delay_ms=1.0,
        )  # Minimal processing delay

    async def _handle_gsr_data_batch(
        self, message: Dict[str, Any], writer: asyncio.StreamWriter
    ) -> Dict[str, Any]:
        """Handle GSR data batch using protocol format."""
        device_id = message.get("device_id")
        session_id = message.get("session_id")
        data_points = message.get("data_points", [])

        logger.debug(f"Received GSR data batch from {device_id}: {len(data_points)} points")

        # TODO: Forward to GSR ingestor

        return create_message("ack", ack_for="gsr_data_batch", status="success")

    async def _handle_gsr_leader_election(
        self, message: Dict[str, Any], writer: asyncio.StreamWriter
    ) -> Dict[str, Any]:
        """Handle GSR leader election using protocol format."""
        device_id = message.get("device_id")
        election_type = message.get("election_type")
        priority_score = message.get("priority_score", 0)

        logger.info(
            f"GSR leader election from {device_id}: {election_type} (score: {priority_score})"
        )

        if election_type == "candidate" and device_id in self._devices:
            # Simple leader election - highest priority score wins
            current_leader = None
            for did, device in self._devices.items():
                if device.is_gsr_leader:
                    current_leader = device
                    break

            if not current_leader or priority_score > 0.8:  # High priority threshold
                # Elect new leader
                if current_leader:
                    current_leader.is_gsr_leader = False

                self._devices[device_id].is_gsr_leader = True
                logger.info(f"New GSR leader elected: {device_id}")

                return create_message(
                    "gsr_leader_election",
                    device_id="pc_controller",
                    election_type="leader",
                    priority_score=1.0,
                )

        return create_message("ack", ack_for="gsr_leader_election", status="success")

    async def _handle_device_disconnect(self, device_id: str) -> None:
        """Handle device disconnection."""
        if device_id in self._devices:
            device_info = self._devices[device_id]
            device_info.state = DeviceState.DISCONNECTED.value

            # Remove from active connections
            self._clients.pop(device_id, None)

            logger.info(f"Device disconnected: {device_id}")

            # Notify callback
            if self._on_device_disconnected:
                self._on_device_disconnected(device_info)

            # Handle GSR leader disconnection
            if device_info.is_gsr_leader:
                await self._handle_gsr_leader_disconnect(device_id)

    async def _handle_gsr_leader_disconnect(self, device_id: str) -> None:
        """Handle GSR leader disconnection by electing new leader."""
        logger.warning(f"GSR leader {device_id} disconnected")

        # Find new GSR leader
        for did, device in self._devices.items():
            if (
                did != device_id
                and device.state == DeviceState.CONNECTED.value
                and "gsr_sensor" in device.capabilities
            ):

                device.is_gsr_leader = True
                device.gsr_mode = config.get("gsr.default_mode", "local")

                # Notify new leader
                if did in self._clients:
                    await self._send_message(
                        self._clients[did],
                        {
                            "type": "gsr_leader_assignment",
                            "is_leader": True,
                            "mode": device.gsr_mode,
                        },
                    )

                logger.info(f"New GSR leader elected: {did}")
                break

    async def _monitor_heartbeats(self) -> None:
        """Monitor device heartbeats and handle timeouts."""
        while self._is_running:
            try:
                current_time = datetime.now(timezone.utc)

                for device_id, device in list(self._devices.items()):
                    if not device.last_heartbeat:
                        continue

                    last_heartbeat = datetime.fromisoformat(
                        device.last_heartbeat.replace("Z", "+00:00")
                    )
                    time_since_heartbeat = (current_time - last_heartbeat).total_seconds()

                    if time_since_heartbeat > self._connection_timeout:
                        logger.warning(f"Device {device_id} heartbeat timeout")
                        await self._handle_device_disconnect(device_id)

                await asyncio.sleep(self._heartbeat_interval)

            except asyncio.CancelledError:
                break
            except Exception as e:
                logger.error(f"Error in heartbeat monitoring: {e}")
                await asyncio.sleep(self._heartbeat_interval)

    async def broadcast_command(
        self, command: Dict[str, Any], target_devices: Optional[List[str]] = None
    ) -> Dict[str, bool]:
        """
        Broadcast command to devices.

        Args:
            command: Command to broadcast
            target_devices: List of device IDs to target. If None, broadcasts to all.

        Returns:
            Dictionary mapping device_id to success status
        """
        results = {}

        devices_to_target = target_devices or list(self._clients.keys())

        for device_id in devices_to_target:
            if device_id in self._clients:
                try:
                    await self._send_message(self._clients[device_id], command)
                    results[device_id] = True
                    logger.debug(f"Command sent to {device_id}: {command.get('message_type')}")
                except Exception as e:
                    logger.error(f"Failed to send command to {device_id}: {e}")
                    results[device_id] = False
            else:
                results[device_id] = False

        return results

    async def start_recording_session(
        self, session_id: str, session_name: str = None
    ) -> Dict[str, bool]:
        """Start recording session on all devices using protocol format."""
        command = create_message(
            "session_start",
            session_id=session_id,
            session_name=session_name or f"Session_{session_id[:8]}",
        )

        logger.info(f"Starting recording session {session_id} on all devices")
        return await self.broadcast_command(command)

    async def stop_recording_session(self, session_id: str) -> Dict[str, bool]:
        """Stop recording session on all devices using protocol format."""
        command = create_message("session_stop", session_id=session_id)

        logger.info(f"Stopping recording session {session_id} on all devices")
        return await self.broadcast_command(command)

    async def send_sync_flash(self, duration_ms: int = 100) -> Dict[str, bool]:
        """Send sync flash command to all devices using protocol format."""
        command = create_message(
            "sync_flash", duration_ms=duration_ms, intensity=1.0, color="white"
        )

        logger.info("Sending sync flash to all devices")
        return await self.broadcast_command(command)

    async def send_sync_mark(
        self, mark_type: str, metadata: Dict[str, Any] = None
    ) -> Dict[str, bool]:
        """Send sync mark to all devices using protocol format."""
        command = create_message(
            "sync_mark", mark_type=mark_type, mark_id=str(uuid.uuid4()), metadata=metadata or {}
        )

        logger.info(f"Sending sync mark '{mark_type}' to all devices")
        return await self.broadcast_command(command)

    async def _send_message(self, writer: asyncio.StreamWriter, message: Dict[str, Any]) -> None:
        """Send JSON message to client."""
        try:
            message_data = json.dumps(message).encode("utf-8")
            length_data = len(message_data).to_bytes(4, "big")

            writer.write(length_data + message_data)
            await writer.drain()

        except Exception as e:
            logger.error(f"Failed to send message: {e}")
            raise

    async def _send_error(
        self, writer: asyncio.StreamWriter, error_message: str, message_id: str = None
    ) -> None:
        """Send error response to client using protocol format."""
        error_response = create_message(
            "error", error_code="INVALID_MESSAGE", error_message=error_message
        )

        if message_id:
            error_response["message_id"] = message_id

        await self._send_message(writer, error_response)

    # Event callback setters
    def set_device_connected_callback(self, callback: Callable[[DeviceInfo], None]) -> None:
        """Set callback for device connection events."""
        self._on_device_connected = callback

    def set_device_disconnected_callback(self, callback: Callable[[DeviceInfo], None]) -> None:
        """Set callback for device disconnection events."""
        self._on_device_disconnected = callback

    def set_device_status_update_callback(self, callback: Callable[[DeviceInfo], None]) -> None:
        """Set callback for device status updates."""
        self._on_device_status_update = callback

    # Property accessors
    def get_connected_devices(self) -> Dict[str, DeviceInfo]:
        """Get all connected devices."""
        return {
            did: device
            for did, device in self._devices.items()
            if device.state != DeviceState.DISCONNECTED.value
        }

    def get_device_info(self, device_id: str) -> Optional[DeviceInfo]:
        """Get device information."""
        return self._devices.get(device_id)

    def get_gsr_leader(self) -> Optional[DeviceInfo]:
        """Get current GSR leader device."""
        for device in self._devices.values():
            if device.is_gsr_leader and device.state != DeviceState.DISCONNECTED.value:
                return device
        return None

    @property
    def is_running(self) -> bool:
        """Check if server is running."""
        return self._is_running
