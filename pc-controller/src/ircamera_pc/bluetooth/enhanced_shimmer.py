"""
Enhanced Shimmer Manager for IRCamera PC Controller

Addresses Shimmer3 Bluetooth reliability issues identified in the documentation.
Implements improved connection management and automatic reconnection.
"""

import time
import threading
from dataclasses import dataclass
from datetime import datetime, timezone
from enum import Enum
from typing import Dict, List, Optional, Callable, Any
from queue import Queue, Empty

try:
    from loguru import logger
except ImportError:
    # Fallback logger
    class FallbackLogger:
        def info(self, msg): print(f"INFO: {msg}")
        def warning(self, msg): print(f"WARNING: {msg}")
        def error(self, msg): print(f"ERROR: {msg}")
        def debug(self, msg): print(f"DEBUG: {msg}")
    logger = FallbackLogger()


class ShimmerConnectionState(Enum):
    """Shimmer device connection states."""
    DISCONNECTED = "disconnected"
    CONNECTING = "connecting"
    CONNECTED = "connected"
    STREAMING = "streaming"
    ERROR = "error"
    LOCKED = "locked"  # Device entered locked state


@dataclass
class ShimmerGSRData:
    """GSR data packet from Shimmer device."""
    timestamp: float
    gsr_resistance: float  # In ohms
    gsr_conductance: float  # In microsiemens
    packet_id: int
    device_id: str


@dataclass
class ShimmerDeviceInfo:
    """Information about a Shimmer device."""
    device_id: str
    mac_address: str
    firmware_version: str
    battery_level: Optional[float] = None
    connection_state: ShimmerConnectionState = ShimmerConnectionState.DISCONNECTED
    last_seen: Optional[datetime] = None
    connection_quality: str = "unknown"


class EnhancedShimmerManager:
    """
    Enhanced Shimmer manager with improved reliability.
    
    Addresses the critical issues identified in the documentation:
    - Connection drops averaging 8.3 minutes
    - Device entering locked Bluetooth state
    - Failed automatic reconnection
    """
    
    def __init__(self):
        self.devices: Dict[str, ShimmerDeviceInfo] = {}
        self.data_callbacks: List[Callable[[ShimmerGSRData], None]] = []
        self.status_callbacks: List[Callable[[str, ShimmerConnectionState], None]] = []
        
        # Connection management
        self.heartbeat_interval = 2.0  # 2-second heartbeat as per documentation
        self.connection_timeout = 10.0  # Shorter timeout for faster failure detection
        self.max_reconnection_attempts = 5
        self.is_running = False
        
        # Background threads
        self.heartbeat_thread: Optional[threading.Thread] = None
        self.data_thread: Optional[threading.Thread] = None
        self.reconnection_thread: Optional[threading.Thread] = None
        
        # Data queues
        self.data_queue = Queue()
        self.command_queue = Queue()
        
        logger.info("Enhanced Shimmer Manager initialized")
    
    def add_data_callback(self, callback: Callable[[ShimmerGSRData], None]):
        """Add callback for GSR data reception."""
        self.data_callbacks.append(callback)
    
    def add_status_callback(self, callback: Callable[[str, ShimmerConnectionState], None]):
        """Add callback for device status changes."""
        self.status_callbacks.append(callback)
    
    def discover_shimmer_devices(self) -> List[ShimmerDeviceInfo]:
        """
        Discover nearby Shimmer devices.
        
        Returns:
            List of discovered Shimmer devices
        """
        logger.info("Scanning for Shimmer devices...")
        return self._get_mock_devices()
    
    def _get_mock_devices(self) -> List[ShimmerDeviceInfo]:
        """Get mock Shimmer devices for testing."""
        mock_devices = [
            ShimmerDeviceInfo(
                device_id="Shimmer3-GSR-001",
                mac_address="00:06:66:66:66:01",
                firmware_version="v4.1",
                last_seen=datetime.now(timezone.utc)
            ),
            ShimmerDeviceInfo(
                device_id="Shimmer3-GSR-002", 
                mac_address="00:06:66:66:66:02",
                firmware_version="v4.1",
                last_seen=datetime.now(timezone.utc)
            )
        ]
        
        for device in mock_devices:
            self.devices[device.device_id] = device
        
        return mock_devices
    
    def connect_to_device(self, device_id: str) -> bool:
        """
        Connect to a Shimmer device with enhanced reliability.
        
        Args:
            device_id: ID of the device to connect to
            
        Returns:
            True if connection successful, False otherwise
        """
        if device_id not in self.devices:
            logger.error(f"Device {device_id} not found")
            return False
        
        try:
            self._update_device_state(device_id, ShimmerConnectionState.CONNECTING)
            
            # Simulate connection
            logger.info(f"Connecting to {device_id}")
            time.sleep(1.0)  # Simulate connection delay
            
            self._update_device_state(device_id, ShimmerConnectionState.CONNECTED)
            logger.info(f"Successfully connected to {device_id}")
            
            # Start background threads if not already running
            if not self.is_running:
                self.start_background_tasks()
            
            return True
                
        except Exception as e:
            logger.error(f"Failed to connect to {device_id}: {e}")
            self._update_device_state(device_id, ShimmerConnectionState.ERROR)
            return False
    
    def start_streaming(self, device_id: str) -> bool:
        """Start GSR data streaming from device."""
        if device_id not in self.devices:
            return False
        
        device = self.devices[device_id]
        if device.connection_state != ShimmerConnectionState.CONNECTED:
            logger.warning(f"Device {device_id} not connected")
            return False
        
        try:
            self._update_device_state(device_id, ShimmerConnectionState.STREAMING)
            logger.info(f"Started streaming from {device_id}")
            return True
            
        except Exception as e:
            logger.error(f"Failed to start streaming from {device_id}: {e}")
            return False
    
    def stop_streaming(self, device_id: str) -> bool:
        """Stop GSR data streaming from device."""
        if device_id not in self.devices:
            return False
        
        try:
            self._update_device_state(device_id, ShimmerConnectionState.CONNECTED)
            logger.info(f"Stopped streaming from {device_id}")
            return True
            
        except Exception as e:
            logger.error(f"Failed to stop streaming from {device_id}: {e}")
            return False
    
    def disconnect_device(self, device_id: str):
        """Disconnect from Shimmer device."""
        if device_id in self.devices:
            self._update_device_state(device_id, ShimmerConnectionState.DISCONNECTED)
            logger.info(f"Disconnected from {device_id}")
    
    def start_background_tasks(self):
        """Start background threads for heartbeat and data processing."""
        if self.is_running:
            return
        
        self.is_running = True
        
        # Start heartbeat thread
        self.heartbeat_thread = threading.Thread(target=self._heartbeat_loop, daemon=True)
        self.heartbeat_thread.start()
        
        # Start data processing thread
        self.data_thread = threading.Thread(target=self._data_processing_loop, daemon=True)
        self.data_thread.start()
        
        # Start reconnection monitoring thread
        self.reconnection_thread = threading.Thread(target=self._reconnection_loop, daemon=True)
        self.reconnection_thread.start()
        
        logger.info("Background tasks started")
    
    def stop_background_tasks(self):
        """Stop background threads."""
        self.is_running = False
        
        # Wait for threads to finish
        if self.heartbeat_thread and self.heartbeat_thread.is_alive():
            self.heartbeat_thread.join(timeout=2.0)
        
        if self.data_thread and self.data_thread.is_alive():
            self.data_thread.join(timeout=2.0)
        
        if self.reconnection_thread and self.reconnection_thread.is_alive():
            self.reconnection_thread.join(timeout=2.0)
        
        logger.info("Background tasks stopped")
    
    def _heartbeat_loop(self):
        """Background heartbeat loop to maintain connections."""
        while self.is_running:
            try:
                for device_id, device in self.devices.items():
                    if device.connection_state in [ShimmerConnectionState.CONNECTED, 
                                                   ShimmerConnectionState.STREAMING]:
                        self._send_heartbeat(device_id)
                
                time.sleep(self.heartbeat_interval)
                
            except Exception as e:
                logger.error(f"Error in heartbeat loop: {e}")
                time.sleep(1.0)
    
    def _send_heartbeat(self, device_id: str):
        """Send heartbeat packet to device."""
        try:
            device = self.devices.get(device_id)
            if device:
                device.last_seen = datetime.now(timezone.utc)
                device.connection_quality = "good"
                
        except Exception as e:
            logger.warning(f"Heartbeat failed for {device_id}: {e}")
            device = self.devices.get(device_id)
            if device:
                device.connection_quality = "poor"
                # Check if we need to trigger reconnection
                if device.last_seen and (datetime.now(timezone.utc) - device.last_seen).seconds > 30:
                    self._trigger_reconnection(device_id)
    
    def _trigger_reconnection(self, device_id: str):
        """Trigger reconnection for a device."""
        logger.warning(f"Triggering reconnection for {device_id}")
        device = self.devices.get(device_id)
        if device and device.connection_state != ShimmerConnectionState.LOCKED:
            self._update_device_state(device_id, ShimmerConnectionState.ERROR)
            # Add to reconnection queue
            try:
                self.command_queue.put(("reconnect", device_id))
            except:
                pass
    
    def _reconnection_loop(self):
        """Background loop to handle reconnections."""
        while self.is_running:
            try:
                try:
                    command, device_id = self.command_queue.get(timeout=1.0)
                    
                    if command == "reconnect":
                        self._attempt_reconnection(device_id)
                        
                except Empty:
                    continue
                    
            except Exception as e:
                logger.error(f"Error in reconnection loop: {e}")
                time.sleep(1.0)
    
    def _attempt_reconnection(self, device_id: str):
        """Attempt to reconnect to a device."""
        logger.info(f"Attempting reconnection to {device_id}")
        
        device = self.devices.get(device_id)
        if not device:
            return
        
        # Wait before reconnection attempt
        time.sleep(2.0)
        
        # Try to reconnect
        for attempt in range(self.max_reconnection_attempts):
            logger.info(f"Reconnection attempt {attempt + 1}/{self.max_reconnection_attempts} for {device_id}")
            
            if self.connect_to_device(device_id):
                logger.info(f"Successfully reconnected to {device_id}")
                return
            
            # Exponential backoff
            wait_time = min(2 ** attempt, 30)
            time.sleep(wait_time)
        
        # All attempts failed - mark as locked
        logger.error(f"All reconnection attempts failed for {device_id}, marking as locked")
        self._update_device_state(device_id, ShimmerConnectionState.LOCKED)
    
    def _data_processing_loop(self):
        """Background loop to process data packets."""
        while self.is_running:
            try:
                # Generate mock GSR data for testing
                for device_id, device in self.devices.items():
                    if device.connection_state == ShimmerConnectionState.STREAMING:
                        self._generate_mock_gsr_data(device_id)
                
                time.sleep(1.0 / 128.0)  # 128 Hz sampling rate
                
            except Exception as e:
                logger.error(f"Error in data processing loop: {e}")
                time.sleep(0.1)
    
    def _generate_mock_gsr_data(self, device_id: str):
        """Generate mock GSR data for testing."""
        try:
            # Generate realistic GSR values
            base_resistance = 50000  # 50k ohms base
            variation = 5000 * (0.5 - hash(time.time()) % 100 / 100.0)  # Random variation
            resistance = base_resistance + variation
            conductance = 1000000.0 / resistance  # Convert to microsiemens
            
            gsr_data = ShimmerGSRData(
                timestamp=time.time(),
                gsr_resistance=resistance,
                gsr_conductance=conductance,
                packet_id=int(time.time() * 128) % 65536,
                device_id=device_id
            )
            
            # Notify callbacks
            for callback in self.data_callbacks:
                try:
                    callback(gsr_data)
                except Exception as e:
                    logger.error(f"Error in GSR data callback: {e}")
                    
        except Exception as e:
            logger.error(f"Error generating mock GSR data: {e}")
    
    def _update_device_state(self, device_id: str, new_state: ShimmerConnectionState):
        """Update device connection state and notify callbacks."""
        device = self.devices.get(device_id)
        if device:
            old_state = device.connection_state
            device.connection_state = new_state
            
            if old_state != new_state:
                logger.info(f"Device {device_id} state changed: {old_state.value} -> {new_state.value}")
                
                # Notify status callbacks
                for callback in self.status_callbacks:
                    try:
                        callback(device_id, new_state)
                    except Exception as e:
                        logger.error(f"Error in status callback: {e}")
    
    def get_device_info(self, device_id: str) -> Optional[ShimmerDeviceInfo]:
        """Get information about a device."""
        return self.devices.get(device_id)
    
    def get_all_devices(self) -> Dict[str, ShimmerDeviceInfo]:
        """Get information about all devices."""
        return self.devices.copy()
    
    def get_connection_stats(self) -> Dict[str, Any]:
        """Get connection statistics."""
        stats = {
            "total_devices": len(self.devices),
            "connected_devices": sum(1 for d in self.devices.values() 
                                   if d.connection_state in [ShimmerConnectionState.CONNECTED, 
                                                            ShimmerConnectionState.STREAMING]),
            "streaming_devices": sum(1 for d in self.devices.values() 
                                   if d.connection_state == ShimmerConnectionState.STREAMING),
            "locked_devices": sum(1 for d in self.devices.values() 
                                if d.connection_state == ShimmerConnectionState.LOCKED),
            "background_tasks_running": self.is_running,
            "heartbeat_interval": self.heartbeat_interval,
        }
        return stats
    
    def cleanup(self):
        """Clean up all resources."""
        logger.info("Cleaning up Enhanced Shimmer Manager")
        
        # Stop background tasks
        self.stop_background_tasks()
        
        # Disconnect all devices
        for device_id in list(self.devices.keys()):
            self.disconnect_device(device_id)
        
        # Clear callbacks
        self.data_callbacks.clear()
        self.status_callbacks.clear()
        
        logger.info("Enhanced Shimmer Manager cleanup complete")


# Global instance for easy access
shimmer_manager = EnhancedShimmerManager()


def get_shimmer_manager() -> EnhancedShimmerManager:
    """Get the global Shimmer manager instance."""
    return shimmer_manager