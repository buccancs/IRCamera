"""
Responsive UI Components for IRCamera PC Controller

Implements UI threading improvements to prevent freezes during device operations.
Addresses the specific UI responsiveness issues identified in the documentation.
"""

import asyncio
from datetime import datetime
from typing import Dict, List, Optional, Callable

try:
    from PyQt6.QtCore import Qt, QTimer, pyqtSignal, pyqtSlot, QObject
    from PyQt6.QtWidgets import (
        QWidget, QPushButton, QLabel, QVBoxLayout, QHBoxLayout,
        QTableWidget, QTableWidgetItem, QHeaderView, QMessageBox,
        QProgressBar, QStatusBar, QGroupBox
    )
    from PyQt6.QtGui import QFont, QPalette, QColor
    PYQT_AVAILABLE = True
except ImportError:
    PYQT_AVAILABLE = False

try:
    from loguru import logger
except ImportError:
    from ..utils.simple_logger import logger

from ..network.enhanced_discovery import ResponsiveDeviceManager, DiscoveredDevice
from ..core.device_manager import DeviceConnectionState, DeviceType


class ResponsiveDeviceTable(QWidget if PYQT_AVAILABLE else object):
    """
    Responsive device table that updates without blocking the UI.
    Implements the signal-slot improvements from the documentation.
    """
    
    if PYQT_AVAILABLE:
        device_selected = pyqtSignal(str)  # device_id
        connection_requested = pyqtSignal(str)  # device_id
    
    def __init__(self, parent=None):
        if not PYQT_AVAILABLE:
            logger.warning("PyQt6 not available, ResponsiveDeviceTable will not function")
            return
        
        super().__init__(parent)
        self.discovered_devices: Dict[str, DiscoveredDevice] = {}
        self.device_status: Dict[str, str] = {}
        
        self._setup_ui()
        self._setup_responsive_manager()
    
    def _setup_ui(self):
        """Setup the UI components."""
        layout = QVBoxLayout(self)
        
        # Header
        header_layout = QHBoxLayout()
        self.title_label = QLabel("Discovered Devices")
        self.title_label.setFont(QFont("Arial", 12, QFont.Weight.Bold))
        
        self.refresh_button = QPushButton("Refresh")
        self.refresh_button.clicked.connect(self._on_refresh_clicked)
        
        self.status_label = QLabel("Discovery: Stopped")
        
        header_layout.addWidget(self.title_label)
        header_layout.addStretch()
        header_layout.addWidget(self.status_label)
        header_layout.addWidget(self.refresh_button)
        
        layout.addLayout(header_layout)
        
        # Device table
        self.device_table = QTableWidget()
        self.device_table.setColumnCount(6)
        self.device_table.setHorizontalHeaderLabels([
            "Device Name", "Type", "IP Address", "Port", "Status", "Action"
        ])
        
        # Configure table
        header = self.device_table.horizontalHeader()
        header.setSectionResizeMode(0, QHeaderView.ResizeMode.Stretch)
        header.setSectionResizeMode(1, QHeaderView.ResizeMode.ResizeToContents)
        header.setSectionResizeMode(2, QHeaderView.ResizeMode.ResizeToContents)
        header.setSectionResizeMode(3, QHeaderView.ResizeMode.ResizeToContents)
        header.setSectionResizeMode(4, QHeaderView.ResizeMode.ResizeToContents)
        header.setSectionResizeMode(5, QHeaderView.ResizeMode.ResizeToContents)
        
        layout.addWidget(self.device_table)
        
        # Progress bar for discovery operations
        self.progress_bar = QProgressBar()
        self.progress_bar.setVisible(False)
        layout.addWidget(self.progress_bar)
    
    def _setup_responsive_manager(self):
        """Setup the responsive device manager."""
        try:
            self.responsive_manager = ResponsiveDeviceManager(self)
            
            # Connect callbacks
            self.responsive_manager.add_device_discovered_callback(
                self._on_device_discovered_responsive
            )
            self.responsive_manager.add_connection_status_callback(
                self._on_connection_status_updated
            )
            
            logger.info("Responsive device manager setup complete")
            
        except Exception as e:
            logger.error(f"Failed to setup responsive manager: {e}")
    
    def start_discovery(self):
        """Start responsive device discovery."""
        try:
            self.progress_bar.setVisible(True)
            self.progress_bar.setRange(0, 0)  # Indeterminate progress
            
            success = self.responsive_manager.start_responsive_discovery()
            
            if success:
                self.status_label.setText("Discovery: Running")
                self.status_label.setStyleSheet("color: green;")
                self.refresh_button.setText("Stop")
            else:
                self.progress_bar.setVisible(False)
                self.status_label.setText("Discovery: Failed")
                self.status_label.setStyleSheet("color: red;")
                
        except Exception as e:
            logger.error(f"Error starting discovery: {e}")
            self._show_error_message("Discovery Error", f"Failed to start discovery: {e}")
    
    def stop_discovery(self):
        """Stop device discovery."""
        try:
            self.responsive_manager.stop_responsive_discovery()
            self.progress_bar.setVisible(False)
            self.status_label.setText("Discovery: Stopped")
            self.status_label.setStyleSheet("color: gray;")
            self.refresh_button.setText("Refresh")
            
        except Exception as e:
            logger.error(f"Error stopping discovery: {e}")
    
    def _on_refresh_clicked(self):
        """Handle refresh button click without blocking UI."""
        if self.refresh_button.text() == "Refresh":
            self.start_discovery()
        else:
            self.stop_discovery()
    
    @pyqtSlot(object)
    def _on_device_discovered_responsive(self, device: DiscoveredDevice):
        """Handle device discovered in a thread-safe manner."""
        try:
            device_id = f"{device.service_name}_{device.ip_address}"
            self.discovered_devices[device_id] = device
            self.device_status[device_id] = "discovered"
            
            # Update table in main thread
            self._update_device_table()
            
            logger.info(f"Device discovered: {device.service_name} at {device.ip_address}")
            
        except Exception as e:
            logger.error(f"Error handling discovered device: {e}")
    
    @pyqtSlot(str, str)
    def _on_connection_status_updated(self, device_id: str, status: str):
        """Handle connection status updates."""
        try:
            if device_id in self.device_status:
                self.device_status[device_id] = status
                self._update_device_table()
                
        except Exception as e:
            logger.error(f"Error updating connection status: {e}")
    
    def _update_device_table(self):
        """Update the device table with current devices."""
        try:
            self.device_table.setRowCount(len(self.discovered_devices))
            
            for row, (device_id, device) in enumerate(self.discovered_devices.items()):
                # Device name
                self.device_table.setItem(row, 0, QTableWidgetItem(device.service_name))
                
                # Device type
                device_type = self._format_device_type(device.device_type)
                self.device_table.setItem(row, 1, QTableWidgetItem(device_type))
                
                # IP Address
                self.device_table.setItem(row, 2, QTableWidgetItem(device.ip_address))
                
                # Port
                self.device_table.setItem(row, 3, QTableWidgetItem(str(device.port)))
                
                # Status
                status = self.device_status.get(device_id, "unknown")
                status_item = QTableWidgetItem(status.title())
                status_item = self._style_status_item(status_item, status)
                self.device_table.setItem(row, 4, status_item)
                
                # Action button
                connect_button = QPushButton("Connect")
                connect_button.clicked.connect(
                    lambda checked, did=device_id: self._on_connect_clicked(did)
                )
                
                if status in ["connecting", "connected"]:
                    connect_button.setEnabled(False)
                
                self.device_table.setCellWidget(row, 5, connect_button)
                
        except Exception as e:
            logger.error(f"Error updating device table: {e}")
    
    def _format_device_type(self, device_type: DeviceType) -> str:
        """Format device type for display."""
        type_map = {
            DeviceType.ANDROID_SENSOR_NODE: "Android Sensor",
            DeviceType.THERMAL_CAMERA_TS004: "Thermal Camera",
            DeviceType.GSR_SENSOR: "GSR Sensor",
        }
        return type_map.get(device_type, str(device_type))
    
    def _style_status_item(self, item: QTableWidgetItem, status: str) -> QTableWidgetItem:
        """Apply styling to status item based on status."""
        status_colors = {
            "discovered": "#3498db",    # Blue
            "connecting": "#f39c12",    # Orange
            "connected": "#27ae60",     # Green
            "failed": "#e74c3c",        # Red
            "timeout": "#e67e22",       # Dark orange
            "error": "#c0392b",         # Dark red
        }
        
        color = status_colors.get(status, "#7f8c8d")  # Gray default
        item.setForeground(QColor(color))
        
        if status == "connected":
            item.setFont(QFont("Arial", 9, QFont.Weight.Bold))
        
        return item
    
    def _on_connect_clicked(self, device_id: str):
        """Handle connect button click without blocking UI."""
        try:
            if device_id not in self.discovered_devices:
                self._show_error_message("Connection Error", "Device not found")
                return
            
            device = self.discovered_devices[device_id]
            
            # Update status immediately
            self.device_status[device_id] = "connecting"
            self._update_device_table()
            
            # Start async connection
            self.responsive_manager.connect_to_device_responsive(
                device_id, device.ip_address, device.port
            )
            
            logger.info(f"Initiated connection to {device.service_name}")
            
        except Exception as e:
            logger.error(f"Error connecting to device: {e}")
            self._show_error_message("Connection Error", f"Failed to connect: {e}")
    
    def _show_error_message(self, title: str, message: str):
        """Show error message to user."""
        try:
            msg_box = QMessageBox(self)
            msg_box.setIcon(QMessageBox.Icon.Warning)
            msg_box.setWindowTitle(title)
            msg_box.setText(message)
            msg_box.exec()
        except Exception as e:
            logger.error(f"Error showing message box: {e}")
    
    def cleanup(self):
        """Clean up resources."""
        try:
            if hasattr(self, 'responsive_manager'):
                self.responsive_manager.cleanup()
        except Exception as e:
            logger.error(f"Error during cleanup: {e}")


class ResponsiveConnectionManager(QObject if PYQT_AVAILABLE else object):
    """
    Manages device connections with timeout handling and status updates.
    Implements the 5-second timeout requirement from the documentation.
    """
    
    if PYQT_AVAILABLE:
        connection_status_changed = pyqtSignal(str, str)  # device_id, status
        connection_timeout_occurred = pyqtSignal(str)     # device_id
    
    def __init__(self, parent=None):
        if PYQT_AVAILABLE:
            super().__init__(parent)
        
        self.active_connections: Dict[str, QTimer] = {}
        self.connection_timeout_seconds = 5  # Reduced from 30s as per documentation
    
    def start_connection_with_timeout(self, device_id: str, callback: Callable):
        """Start connection with proper timeout handling."""
        if not PYQT_AVAILABLE:
            return
        
        try:
            # Cancel any existing connection for this device
            self._cancel_connection(device_id)
            
            # Create timeout timer
            timer = QTimer()
            timer.timeout.connect(lambda: self._handle_connection_timeout(device_id))
            timer.setSingleShot(True)
            timer.start(self.connection_timeout_seconds * 1000)
            
            self.active_connections[device_id] = timer
            
            # Emit status update
            self.connection_status_changed.emit(device_id, "connecting")
            
            # Execute connection callback
            callback()
            
        except Exception as e:
            logger.error(f"Error starting connection with timeout: {e}")
            self.connection_status_changed.emit(device_id, "error")
    
    def connection_successful(self, device_id: str):
        """Mark connection as successful."""
        self._cleanup_connection(device_id)
        self.connection_status_changed.emit(device_id, "connected")
    
    def connection_failed(self, device_id: str, reason: str = "failed"):
        """Mark connection as failed."""
        self._cleanup_connection(device_id)
        self.connection_status_changed.emit(device_id, reason)
    
    def _handle_connection_timeout(self, device_id: str):
        """Handle connection timeout."""
        logger.warning(f"Connection timeout for device {device_id}")
        self._cleanup_connection(device_id)
        self.connection_timeout_occurred.emit(device_id)
        self.connection_status_changed.emit(device_id, "timeout")
    
    def _cancel_connection(self, device_id: str):
        """Cancel active connection."""
        if device_id in self.active_connections:
            timer = self.active_connections[device_id]
            timer.stop()
            timer.deleteLater()
            del self.active_connections[device_id]
    
    def _cleanup_connection(self, device_id: str):
        """Clean up connection resources."""
        self._cancel_connection(device_id)
    
    def cleanup_all(self):
        """Clean up all active connections."""
        for device_id in list(self.active_connections.keys()):
            self._cancel_connection(device_id)


def create_responsive_device_dashboard(parent=None) -> ResponsiveDeviceTable:
    """
    Factory function to create responsive device dashboard.
    Implements the UI improvements specified in the documentation.
    """
    if not PYQT_AVAILABLE:
        logger.error("PyQt6 not available, cannot create responsive device dashboard")
        return None
    
    dashboard = ResponsiveDeviceTable(parent)
    logger.info("Created responsive device dashboard")
    return dashboard