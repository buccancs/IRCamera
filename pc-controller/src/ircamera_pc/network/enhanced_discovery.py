"""
Enhanced Network Discovery Service with Threading Support for IRCamera PC Controller

Implements UI threading improvements and responsive device discovery to prevent GUI freezes.
Addresses the specific UI responsiveness issues identified in the documentation.
"""

import asyncio
import threading
import time
from concurrent.futures import ThreadPoolExecutor
from dataclasses import dataclass
from datetime import datetime, timezone
from typing import Callable, Dict, List, Optional, Set
from queue import Queue, Empty

try:
    from PyQt6.QtCore import QObject, QThread, pyqtSignal, QTimer
    from PyQt6.QtNetwork import QNetworkAccessManager, QNetworkRequest, QNetworkReply
    from PyQt6.QtCore import QUrl
    PYQT_AVAILABLE = True
except ImportError:
    # Fallback for non-GUI environments
    PYQT_AVAILABLE = False
    QObject = object
    QThread = object
    pyqtSignal = lambda: None

try:
    from zeroconf import ServiceInfo, Zeroconf
    from zeroconf.asyncio import AsyncServiceBrowser, AsyncZeroconf
    ZEROCONF_AVAILABLE = True
except ImportError:
    ZEROCONF_AVAILABLE = False

try:
    from loguru import logger
except ImportError:
    from ..utils.simple_logger import logger

from .discovery import DeviceType, DiscoveredDevice, NetworkDiscoveryService


@dataclass
class DiscoveryEvent:
    """Event data for device discovery notifications."""
    event_type: str  # 'discovered', 'lost', 'updated'
    device: DiscoveredDevice
    timestamp: datetime


class NetworkDiscoveryThread(QThread if PYQT_AVAILABLE else threading.Thread):
    """
    Background thread for network operations to prevent UI freezing.
    Implements the threading improvements specified in the documentation.
    """
    
    # PyQt signals for thread-safe communication
    if PYQT_AVAILABLE:
        device_discovered = pyqtSignal(DiscoveredDevice)
        device_lost = pyqtSignal(str)  # service_name
        device_updated = pyqtSignal(DiscoveredDevice)
        discovery_status_changed = pyqtSignal(bool)  # is_running
        connection_timeout = pyqtSignal(str, int)  # device_id, timeout_seconds
    
    def __init__(self, parent=None):
        if PYQT_AVAILABLE:
            super().__init__(parent)
        else:
            super().__init__(daemon=True)
        
        self.discovery_service = NetworkDiscoveryService()
        self.is_running = False
        self.should_stop = False
        self.discovery_queue = Queue()
        self.connection_timeout_seconds = 5  # Reduced from 30s as per documentation
        
        # Thread pool for concurrent operations
        self.executor = ThreadPoolExecutor(max_workers=4, thread_name_prefix="discovery")
    
    def run(self):
        """Main thread execution loop."""
        logger.info("Starting enhanced discovery thread...")
        self.is_running = True
        
        if PYQT_AVAILABLE:
            self.discovery_status_changed.emit(True)
        
        try:
            # Set up async event loop for this thread
            loop = asyncio.new_event_loop()
            asyncio.set_event_loop(loop)
            
            # Start discovery service
            loop.run_until_complete(self._setup_discovery())
            
            # Main discovery loop
            while not self.should_stop:
                try:
                    # Process discovery events
                    self._process_discovery_events()
                    
                    # Refresh discovery periodically
                    loop.run_until_complete(self._periodic_refresh())
                    
                    # Short sleep to prevent busy waiting
                    time.sleep(0.1)
                    
                except Exception as e:
                    logger.error(f"Error in discovery thread: {e}")
                    time.sleep(1.0)
            
            # Cleanup
            loop.run_until_complete(self._cleanup_discovery())
            loop.close()
            
        except Exception as e:
            logger.error(f"Fatal error in discovery thread: {e}")
        finally:
            self.is_running = False
            if PYQT_AVAILABLE:
                self.discovery_status_changed.emit(False)
            logger.info("Discovery thread stopped")
    
    async def _setup_discovery(self):
        """Setup discovery service in background thread."""
        try:
            await self.discovery_service.start_discovery()
            
            # Register callbacks
            self.discovery_service.add_discovery_listener(self._on_device_discovered)
            logger.info("Discovery service setup complete")
            
        except Exception as e:
            logger.error(f"Failed to setup discovery: {e}")
    
    async def _cleanup_discovery(self):
        """Cleanup discovery service."""
        try:
            await self.discovery_service.stop_discovery()
            self.executor.shutdown(wait=True)
            logger.debug("Discovery cleanup complete")
        except Exception as e:
            logger.error(f"Error during discovery cleanup: {e}")
    
    def _process_discovery_events(self):
        """Process queued discovery events."""
        try:
            while True:
                try:
                    event = self.discovery_queue.get_nowait()
                    self._handle_discovery_event(event)
                except Empty:
                    break
        except Exception as e:
            logger.error(f"Error processing discovery events: {e}")
    
    def _handle_discovery_event(self, event: DiscoveryEvent):
        """Handle a discovery event by emitting appropriate signals."""
        if not PYQT_AVAILABLE:
            return
        
        try:
            if event.event_type == "discovered":
                self.device_discovered.emit(event.device)
            elif event.event_type == "lost":
                self.device_lost.emit(event.device.service_name)
            elif event.event_type == "updated":
                self.device_updated.emit(event.device)
        except Exception as e:
            logger.error(f"Error handling discovery event: {e}")
    
    async def _periodic_refresh(self):
        """Periodically refresh discovery to find new devices."""
        try:
            await self.discovery_service.refresh_discovery()
        except Exception as e:
            logger.error(f"Error during periodic refresh: {e}")
    
    def _on_device_discovered(self, event_type: str, device: DiscoveredDevice):
        """Callback for device discovery events."""
        try:
            event = DiscoveryEvent(
                event_type=event_type,
                device=device,
                timestamp=datetime.now(timezone.utc)
            )
            self.discovery_queue.put(event)
        except Exception as e:
            logger.error(f"Error queuing discovery event: {e}")
    
    def stop_discovery(self):
        """Stop the discovery thread."""
        logger.info("Stopping discovery thread...")
        self.should_stop = True
        
        if PYQT_AVAILABLE and self.isRunning():
            self.wait(5000)  # Wait up to 5 seconds for clean shutdown


class EnhancedNetworkManager(QObject if PYQT_AVAILABLE else object):
    """
    Enhanced network manager with non-blocking operations.
    Implements the QNetworkAccessManager improvements from the documentation.
    """
    
    if PYQT_AVAILABLE:
        connection_status_updated = pyqtSignal(str, str)  # device_id, status
        connection_timeout_occurred = pyqtSignal(str)  # device_id
    
    def __init__(self, parent=None):
        if PYQT_AVAILABLE:
            super().__init__(parent)
            self.network_manager = QNetworkAccessManager(self)
        
        self.active_connections: Dict[str, QNetworkReply] = {}
        self.connection_timeouts: Dict[str, QTimer] = {}
        self.connection_timeout_seconds = 5  # Reduced timeout as per documentation
    
    def connect_to_device_async(self, device_id: str, ip_address: str, port: int):
        """
        Connect to device asynchronously to prevent UI blocking.
        Implements async callbacks as specified in the documentation.
        """
        if not PYQT_AVAILABLE:
            logger.warning("PyQt6 not available, cannot use async network operations")
            return
        
        try:
            # Cancel any existing connection for this device
            self._cancel_device_connection(device_id)
            
            self.connection_status_updated.emit(device_id, "connecting")
            
            # Create network request
            url = QUrl(f"http://{ip_address}:{port}/api/status")
            request = QNetworkRequest(url)
            request.setRawHeader(b"User-Agent", b"IRCamera-PC-Controller/1.0")
            
            # Set connection timeout
            timer = QTimer()
            timer.timeout.connect(lambda: self._handle_connection_timeout(device_id))
            timer.setSingleShot(True)
            timer.start(self.connection_timeout_seconds * 1000)
            self.connection_timeouts[device_id] = timer
            
            # Make async request
            reply = self.network_manager.get(request)
            reply.finished.connect(lambda: self._handle_connection_response(device_id, reply))
            self.active_connections[device_id] = reply
            
            logger.debug(f"Started async connection to {device_id} at {ip_address}:{port}")
            
        except Exception as e:
            logger.error(f"Error starting async connection to {device_id}: {e}")
            self.connection_status_updated.emit(device_id, "error")
    
    def _handle_connection_response(self, device_id: str, reply: QNetworkReply):
        """Handle connection response."""
        try:
            # Clean up
            self._cleanup_device_connection(device_id)
            
            if reply.error() == QNetworkReply.NetworkError.NoError:
                self.connection_status_updated.emit(device_id, "connected")
                logger.info(f"Successfully connected to device {device_id}")
            else:
                error_msg = reply.errorString()
                logger.warning(f"Connection failed to device {device_id}: {error_msg}")
                self.connection_status_updated.emit(device_id, "failed")
        
        except Exception as e:
            logger.error(f"Error handling connection response for {device_id}: {e}")
            self.connection_status_updated.emit(device_id, "error")
        finally:
            reply.deleteLater()
    
    def _handle_connection_timeout(self, device_id: str):
        """Handle connection timeout."""
        try:
            logger.warning(f"Connection timeout for device {device_id} after {self.connection_timeout_seconds}s")
            self._cancel_device_connection(device_id)
            self.connection_timeout_occurred.emit(device_id)
            self.connection_status_updated.emit(device_id, "timeout")
        except Exception as e:
            logger.error(f"Error handling connection timeout for {device_id}: {e}")
    
    def _cancel_device_connection(self, device_id: str):
        """Cancel active connection for device."""
        if device_id in self.active_connections:
            reply = self.active_connections[device_id]
            reply.abort()
            reply.deleteLater()
            del self.active_connections[device_id]
    
    def _cleanup_device_connection(self, device_id: str):
        """Clean up connection resources for device."""
        # Clean up active connection
        if device_id in self.active_connections:
            del self.active_connections[device_id]
        
        # Clean up timeout timer
        if device_id in self.connection_timeouts:
            timer = self.connection_timeouts[device_id]
            timer.stop()
            timer.deleteLater()
            del self.connection_timeouts[device_id]
    
    def cleanup_all_connections(self):
        """Clean up all active connections."""
        for device_id in list(self.active_connections.keys()):
            self._cancel_device_connection(device_id)
        
        for device_id in list(self.connection_timeouts.keys()):
            self._cleanup_device_connection(device_id)


class ResponsiveDeviceManager:
    """
    Enhanced device manager with responsive UI operations.
    Prevents UI freezes during device discovery and connection operations.
    """
    
    def __init__(self, parent=None):
        self.discovery_thread: Optional[NetworkDiscoveryThread] = None
        self.network_manager: Optional[EnhancedNetworkManager] = None
        self.is_discovery_active = False
        
        if PYQT_AVAILABLE:
            self.network_manager = EnhancedNetworkManager(parent)
        
        # Callbacks for UI updates
        self.device_discovered_callbacks: List[Callable] = []
        self.device_lost_callbacks: List[Callable] = []
        self.connection_status_callbacks: List[Callable] = []
    
    def start_responsive_discovery(self) -> bool:
        """
        Start responsive device discovery in background thread.
        Implements the threading improvements from the documentation.
        """
        if self.is_discovery_active:
            logger.warning("Discovery already active")
            return True
        
        try:
            logger.info("Starting responsive device discovery...")
            
            # Create and configure discovery thread
            self.discovery_thread = NetworkDiscoveryThread()
            
            if PYQT_AVAILABLE:
                # Connect signals
                self.discovery_thread.device_discovered.connect(self._on_device_discovered)
                self.discovery_thread.device_lost.connect(self._on_device_lost)
                self.discovery_thread.discovery_status_changed.connect(self._on_discovery_status_changed)
            
            # Start thread
            self.discovery_thread.start()
            self.is_discovery_active = True
            
            logger.info("Responsive discovery started successfully")
            return True
            
        except Exception as e:
            logger.error(f"Failed to start responsive discovery: {e}")
            return False
    
    def stop_responsive_discovery(self):
        """Stop responsive discovery."""
        if not self.is_discovery_active:
            return
        
        logger.info("Stopping responsive discovery...")
        
        try:
            if self.discovery_thread:
                self.discovery_thread.stop_discovery()
            
            if self.network_manager:
                self.network_manager.cleanup_all_connections()
            
            self.is_discovery_active = False
            logger.info("Responsive discovery stopped")
            
        except Exception as e:
            logger.error(f"Error stopping responsive discovery: {e}")
    
    def connect_to_device_responsive(self, device_id: str, ip_address: str, port: int):
        """Connect to device without blocking UI."""
        if self.network_manager:
            self.network_manager.connect_to_device_async(device_id, ip_address, port)
        else:
            logger.warning("Network manager not available for async connections")
    
    def add_device_discovered_callback(self, callback: Callable[[DiscoveredDevice], None]):
        """Add callback for device discovery events."""
        self.device_discovered_callbacks.append(callback)
    
    def add_connection_status_callback(self, callback: Callable[[str, str], None]):
        """Add callback for connection status updates."""
        self.connection_status_callbacks.append(callback)
    
    def _on_device_discovered(self, device: DiscoveredDevice):
        """Handle device discovered signal."""
        for callback in self.device_discovered_callbacks:
            try:
                callback(device)
            except Exception as e:
                logger.error(f"Error in device discovered callback: {e}")
    
    def _on_device_lost(self, service_name: str):
        """Handle device lost signal."""
        for callback in self.device_lost_callbacks:
            try:
                callback(service_name)
            except Exception as e:
                logger.error(f"Error in device lost callback: {e}")
    
    def _on_discovery_status_changed(self, is_running: bool):
        """Handle discovery status change."""
        logger.info(f"Discovery status changed: {'running' if is_running else 'stopped'}")
    
    def cleanup(self):
        """Clean up resources."""
        self.stop_responsive_discovery()