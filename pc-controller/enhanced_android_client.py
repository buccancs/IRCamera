#!/usr/bin/env python3
"""
Enhanced Android Client for PC Controller
Provides robust connection management and device discovery for Android devices.
"""

import socket
import json
import time
import threading
import logging
from typing import Dict, Any, Optional, List, Callable
from dataclasses import dataclass
from enum import Enum

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class ConnectionStatus(Enum):
    DISCONNECTED = "disconnected"
    CONNECTING = "connecting"
    CONNECTED = "connected"
    AUTHENTICATED = "authenticated"
    ERROR = "error"

@dataclass
class AndroidDevice:
    """Represents a discovered Android device"""
    name: str
    ip_address: str
    port: int
    device_id: str
    capabilities: List[str]
    last_seen: float

class AndroidClient:
    """Enhanced Android client with discovery and robust communication"""
    
    def __init__(self, connection_callback: Optional[Callable] = None):
        self.connection_callback = connection_callback
        self.socket: Optional[socket.socket] = None
        self.status = ConnectionStatus.DISCONNECTED
        self.current_device: Optional[AndroidDevice] = None
        
        # Message handling
        self.message_handlers = {}
        self.response_handlers = {}
        
        # Connection management
        self._running = False
        self._listener_thread: Optional[threading.Thread] = None
        
    def discover_devices(self, timeout: float = 5.0) -> List[AndroidDevice]:
        """Discover Android devices on the network"""
        logger.info("Discovering Android devices...")
        devices = []
        
        # Try common IP ranges
        base_ips = ["192.168.1.", "192.168.0.", "10.0.0.", "172.16.0."]
        
        def try_connect(ip: str, port: int = 8080) -> Optional[AndroidDevice]:
            try:
                sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                sock.settimeout(1.0)
                sock.connect((ip, port))
                
                # Send discovery message
                discovery_msg = {
                    "type": "discovery",
                    "pc_id": "pc_controller_001"
                }
                
                self._send_message(sock, discovery_msg)
                response = self._receive_message(sock)
                
                sock.close()
                
                if response and response.get("type") == "discovery_response":
                    return AndroidDevice(
                        name=response.get("device_name", "IRCamera Android"),
                        ip_address=ip,
                        port=port,
                        device_id=response.get("device_id", "unknown"),
                        capabilities=response.get("capabilities", []),
                        last_seen=time.time()
                    )
                    
            except:
                pass
            return None
        
        # Parallel discovery
        threads = []
        results = []
        
        for base_ip in base_ips:
            for i in range(1, 255):
                ip = base_ip + str(i)
                thread = threading.Thread(target=lambda: results.append(try_connect(ip)))
                threads.append(thread)
                thread.start()
                
                # Limit concurrent threads
                if len(threads) >= 50:
                    for t in threads:
                        t.join(timeout=0.1)
                    threads = [t for t in threads if t.is_alive()]
        
        # Wait for all threads
        for thread in threads:
            thread.join(timeout=0.1)
        
        devices = [r for r in results if r is not None]
        logger.info(f"Discovered {len(devices)} Android devices")
        
        return devices
    
    def connect_to_device(self, device: AndroidDevice) -> bool:
        """Connect to a specific Android device"""
        try:
            logger.info(f"Connecting to {device.name} at {device.ip_address}:{device.port}")
            self.status = ConnectionStatus.CONNECTING
            
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.settimeout(10.0)
            self.socket.connect((device.ip_address, device.port))
            
            self.current_device = device
            self.status = ConnectionStatus.CONNECTED
            
            # Start message listener
            self._running = True
            self._listener_thread = threading.Thread(target=self._message_listener, daemon=True)
            self._listener_thread.start()
            
            # Authenticate with device
            if self._authenticate():
                self.status = ConnectionStatus.AUTHENTICATED
                logger.info(f"Successfully connected and authenticated with {device.name}")
                
                if self.connection_callback:
                    self.connection_callback(self.status, device)
                
                return True
            else:
                self.disconnect()
                return False
                
        except Exception as e:
            logger.error(f"Connection failed: {e}")
            self.status = ConnectionStatus.ERROR
            self.disconnect()
            return False
    
    def _authenticate(self) -> bool:
        """Authenticate with the Android device"""
        try:
            auth_msg = {
                "type": "auth_request",
                "pc_id": "pc_controller_001",
                "version": "1.0",
                "timestamp": int(time.time() * 1000)
            }
            
            response = self.send_and_receive(auth_msg, timeout=5.0)
            return response and response.get("status") == "authenticated"
            
        except Exception as e:
            logger.error(f"Authentication failed: {e}")
            return False
    
    def send_and_receive(self, message: Dict[str, Any], timeout: float = 10.0) -> Optional[Dict[str, Any]]:
        """Send message and wait for response"""
        if not self.socket or self.status not in [ConnectionStatus.CONNECTED, ConnectionStatus.AUTHENTICATED]:
            logger.warning("Not connected to device")
            return None
        
        try:
            # Add response handler
            message_id = str(time.time())
            message["message_id"] = message_id
            
            response_event = threading.Event()
            response_data = {}
            
            def response_handler(resp):
                response_data.update(resp)
                response_event.set()
            
            self.response_handlers[message_id] = response_handler
            
            # Send message
            self._send_message(self.socket, message)
            
            # Wait for response
            if response_event.wait(timeout):
                return response_data
            else:
                logger.warning(f"Timeout waiting for response to {message.get('type')}")
                return None
                
        except Exception as e:
            logger.error(f"Send/receive error: {e}")
            return None
        finally:
            # Cleanup handler
            self.response_handlers.pop(message_id, None)
    
    def send_command(self, command_type: str, **kwargs) -> Optional[Dict[str, Any]]:
        """Send a command to the Android device"""
        message = {
            "type": command_type,
            "timestamp": int(time.time() * 1000),
            **kwargs
        }
        
        return self.send_and_receive(message)
    
    def start_recording(self, session_info: Dict[str, Any]) -> bool:
        """Start recording on Android device"""
        response = self.send_command("start_recording", session=session_info)
        return response and response.get("status") == "success"
    
    def stop_recording(self) -> bool:
        """Stop recording on Android device"""
        response = self.send_command("stop_recording")
        return response and response.get("status") == "recording_stopped"
    
    def sync_flash(self, duration_ms: int = 500) -> bool:
        """Trigger synchronization flash"""
        response = self.send_command("sync_flash", duration_ms=duration_ms)
        return response and response.get("status") == "flash_completed"
    
    def get_device_status(self) -> Optional[Dict[str, Any]]:
        """Get current device status"""
        return self.send_command("get_status")
    
    def _send_message(self, sock: socket.socket, message: Dict[str, Any]):
        """Send a JSON message over socket"""
        message_json = json.dumps(message)
        message_bytes = message_json.encode('utf-8')
        
        # Send length first (4 bytes, big-endian)
        length = len(message_bytes)
        sock.send(length.to_bytes(4, byteorder='big'))
        sock.send(message_bytes)
    
    def _receive_message(self, sock: socket.socket) -> Optional[Dict[str, Any]]:
        """Receive a JSON message from socket"""
        try:
            # Read message length
            length_bytes = sock.recv(4)
            if len(length_bytes) < 4:
                return None
                
            message_length = int.from_bytes(length_bytes, byteorder='big')
            
            # Read message data
            message_data = b''
            while len(message_data) < message_length:
                chunk = sock.recv(message_length - len(message_data))
                if not chunk:
                    break
                message_data += chunk
            
            # Parse JSON
            message_json = message_data.decode('utf-8')
            return json.loads(message_json)
            
        except Exception as e:
            logger.error(f"Receive error: {e}")
            return None
    
    def _message_listener(self):
        """Background thread to listen for messages from Android"""
        while self._running and self.socket:
            try:
                message = self._receive_message(self.socket)
                if message:
                    self._handle_message(message)
                else:
                    # Connection lost
                    logger.warning("Connection lost")
                    self.disconnect()
                    break
                    
            except Exception as e:
                logger.error(f"Message listener error: {e}")
                self.disconnect()
                break
    
    def _handle_message(self, message: Dict[str, Any]):
        """Handle incoming message from Android"""
        message_type = message.get("type", "unknown")
        message_id = message.get("message_id")
        
        # Check if this is a response to a sent message
        if message_id and message_id in self.response_handlers:
            self.response_handlers[message_id](message)
        else:
            # Handle as incoming message
            handler = self.message_handlers.get(message_type)
            if handler:
                handler(message)
            else:
                logger.info(f"Received message: {message_type}")
    
    def add_message_handler(self, message_type: str, handler: Callable[[Dict[str, Any]], None]):
        """Add handler for incoming messages"""
        self.message_handlers[message_type] = handler
    
    def disconnect(self):
        """Disconnect from Android device"""
        self._running = False
        
        if self.socket:
            try:
                self.socket.close()
            except:
                pass
            self.socket = None
        
        if self._listener_thread and self._listener_thread.is_alive():
            self._listener_thread.join(timeout=1.0)
        
        self.status = ConnectionStatus.DISCONNECTED
        self.current_device = None
        
        if self.connection_callback:
            self.connection_callback(self.status, None)
        
        logger.info("Disconnected from Android device")

# Example usage
def main():
    def connection_callback(status, device):
        print(f"Connection status: {status}")
        if device:
            print(f"Connected to: {device.name}")
    
    client = AndroidClient(connection_callback)
    
    # Discover devices
    devices = client.discover_devices()
    if devices:
        # Connect to first device
        device = devices[0]
        if client.connect_to_device(device):
            # Test communication
            status = client.get_device_status()
            print(f"Device status: {status}")
            
            # Test recording
            session_info = {
                "id": "test_session",
                "participant_id": "P001",
                "start_time": int(time.time() * 1000)
            }
            
            print("Starting recording...")
            if client.start_recording(session_info):
                print("Recording started successfully")
                time.sleep(2)
                
                print("Stopping recording...")
                if client.stop_recording():
                    print("Recording stopped successfully")
            
        client.disconnect()

if __name__ == "__main__":
    main()