"""
Main Window for IRCamera PC Controller

Provides the main researcher interface with device monitoring and session control.
"""

from PyQt5.QtWidgets import (
    QMainWindow, QWidget, QVBoxLayout, QHBoxLayout, QGridLayout,
    QPushButton, QLabel, QListWidget, QListWidgetItem, QGroupBox,
    QStatusBar, QMessageBox, QInputDialog, QProgressBar, QTextEdit,
    QSplitter, QFrame
)
from PyQt5.QtCore import QTimer, Qt, pyqtSignal
from PyQt5.QtGui import QFont, QIcon, QPixmap
from typing import Optional, Dict, Any
from datetime import datetime
from loguru import logger

from ..core.session import SessionManager, SessionState
from ..core.timesync import TimeSyncService
from ..network.server import NetworkServer, DeviceInfo, DeviceState
from .widgets import DeviceListWidget, SessionControlWidget, StatusDisplayWidget


class MainWindow(QMainWindow):
    """
    Main application window for IRCamera PC Controller.
    
    Implements the GUI requirements from FR6:
    - Device list with status indicators
    - Session start/stop controls  
    - Real-time monitoring displays
    - Recording status and elapsed time
    - Device disconnect alerts
    """
    
    # Custom signals
    session_started = pyqtSignal(str)
    session_stopped = pyqtSignal(str)
    sync_flash_triggered = pyqtSignal()
    
    def __init__(self, session_manager: SessionManager, 
                 network_server: NetworkServer,
                 time_sync_service: TimeSyncService):
        """
        Initialize main window.
        
        Args:
            session_manager: Session management service
            network_server: Network server for device communication
            time_sync_service: Time synchronization service
        """
        super().__init__()
        
        # Core services
        self.session_manager = session_manager
        self.network_server = network_server
        self.time_sync_service = time_sync_service
        
        # GUI components
        self.device_list_widget: Optional[DeviceListWidget] = None
        self.session_control_widget: Optional[SessionControlWidget] = None
        self.status_display_widget: Optional[StatusDisplayWidget] = None
        self.log_display: Optional[QTextEdit] = None
        
        # State tracking
        self._current_session_id: Optional[str] = None
        self._session_start_time: Optional[datetime] = None
        self._update_timer: Optional[QTimer] = None
        
        self._setup_ui()
        self._setup_connections()
        self._setup_network_callbacks()
        self._start_ui_updates()
        
        logger.info("Main window initialized")
    
    def _setup_ui(self) -> None:
        """Set up the user interface."""
        self.setWindowTitle("IRCamera PC Controller")
        self.setMinimumSize(800, 600)
        
        # Central widget
        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        
        # Main layout
        main_layout = QHBoxLayout(central_widget)
        
        # Create splitter for resizable panes
        splitter = QSplitter(Qt.Horizontal)
        main_layout.addWidget(splitter)
        
        # Left pane - Device management and session control
        left_pane = self._create_left_pane()
        splitter.addWidget(left_pane)
        
        # Right pane - Status and logs
        right_pane = self._create_right_pane()
        splitter.addWidget(right_pane)
        
        # Set splitter proportions
        splitter.setStretchFactor(0, 1)  # Left pane
        splitter.setStretchFactor(1, 1)  # Right pane
        
        # Status bar
        self._setup_status_bar()
        
        # Set initial state
        self._update_ui_state()
    
    def _create_left_pane(self) -> QWidget:
        """Create the left pane with device management and session controls."""
        pane = QWidget()
        layout = QVBoxLayout(pane)
        
        # Device management section
        device_group = QGroupBox("Connected Devices")
        device_layout = QVBoxLayout(device_group)
        
        self.device_list_widget = DeviceListWidget()
        device_layout.addWidget(self.device_list_widget)
        
        layout.addWidget(device_group)
        
        # Session control section
        session_group = QGroupBox("Session Control")
        session_layout = QVBoxLayout(session_group)
        
        self.session_control_widget = SessionControlWidget()
        session_layout.addWidget(self.session_control_widget)
        
        layout.addWidget(session_group)
        
        # Sync controls section
        sync_group = QGroupBox("Synchronization")
        sync_layout = QVBoxLayout(sync_group)
        
        self.sync_flash_btn = QPushButton("Flash Sync")
        self.sync_flash_btn.setToolTip("Send visual sync flash to all devices")
        self.sync_flash_btn.clicked.connect(self._on_sync_flash_clicked)
        sync_layout.addWidget(self.sync_flash_btn)
        
        self.sync_mark_btn = QPushButton("Add Sync Mark")
        self.sync_mark_btn.setToolTip("Add synchronization marker to timeline")
        self.sync_mark_btn.clicked.connect(self._on_sync_mark_clicked)
        sync_layout.addWidget(self.sync_mark_btn)
        
        layout.addWidget(sync_group)
        
        # Stretch at bottom
        layout.addStretch()
        
        return pane
    
    def _create_right_pane(self) -> QWidget:
        """Create the right pane with status displays and logs."""
        pane = QWidget()
        layout = QVBoxLayout(pane)
        
        # Status display section
        status_group = QGroupBox("System Status")
        status_layout = QVBoxLayout(status_group)
        
        self.status_display_widget = StatusDisplayWidget()
        status_layout.addWidget(self.status_display_widget)
        
        layout.addWidget(status_group)
        
        # Log display section
        log_group = QGroupBox("System Log")
        log_layout = QVBoxLayout(log_group)
        
        self.log_display = QTextEdit()
        self.log_display.setReadOnly(True)
        self.log_display.setMaximumBlockCount(1000)  # Limit log lines
        self.log_display.setFont(QFont("Consolas", 9))
        log_layout.addWidget(self.log_display)
        
        layout.addWidget(log_group)
        
        return pane
    
    def _setup_status_bar(self) -> None:
        """Set up the status bar."""
        self.status_bar = QStatusBar()
        self.setStatusBar(self.status_bar)
        
        # Status labels
        self.devices_label = QLabel("Devices: 0")
        self.session_label = QLabel("No active session")
        self.sync_label = QLabel("Time sync: OK")
        
        self.status_bar.addWidget(self.devices_label)
        self.status_bar.addPermanentWidget(self.sync_label)
        self.status_bar.addPermanentWidget(self.session_label)
        
    def _setup_connections(self) -> None:
        """Set up signal connections."""
        # Session control connections
        if self.session_control_widget:
            self.session_control_widget.start_session_requested.connect(self._on_start_session_requested)
            self.session_control_widget.stop_session_requested.connect(self._on_stop_session_requested)
            self.session_control_widget.new_session_requested.connect(self._on_new_session_requested)
        
        # Device list connections
        if self.device_list_widget:
            self.device_list_widget.device_selected.connect(self._on_device_selected)
    
    def _setup_network_callbacks(self) -> None:
        """Set up network server event callbacks."""
        self.network_server.set_device_connected_callback(self._on_device_connected)
        self.network_server.set_device_disconnected_callback(self._on_device_disconnected)
        self.network_server.set_device_status_update_callback(self._on_device_status_updated)
    
    def _start_ui_updates(self) -> None:
        """Start periodic UI updates."""
        self._update_timer = QTimer()
        self._update_timer.timeout.connect(self._update_displays)
        self._update_timer.start(1000)  # Update every second
    
    def _update_displays(self) -> None:
        """Update all display components."""
        try:
            # Update device count
            connected_devices = self.network_server.get_connected_devices()
            self.devices_label.setText(f"Devices: {len(connected_devices)}")
            
            # Update device list
            if self.device_list_widget:
                self.device_list_widget.update_devices(connected_devices)
            
            # Update status display
            if self.status_display_widget:
                self.status_display_widget.update_time_sync_stats(
                    self.time_sync_service.get_synchronization_quality()
                )
                
                current_session = self.session_manager.get_current_session()
                if current_session:
                    self.status_display_widget.update_session_info(current_session)
            
            # Update session status in status bar
            current_session = self.session_manager.get_current_session()
            if current_session:
                if current_session.state == SessionState.RECORDING.value:
                    if self._session_start_time:
                        elapsed = datetime.now() - self._session_start_time
                        elapsed_str = str(elapsed).split('.')[0]  # Remove microseconds
                        self.session_label.setText(f"Recording: {elapsed_str}")
                    else:
                        self.session_label.setText("Recording: --:--:--")
                else:
                    self.session_label.setText(f"Session: {current_session.name} ({current_session.state})")
            else:
                self.session_label.setText("No active session")
            
            # Update sync status
            sync_quality = self.time_sync_service.get_synchronization_quality()
            if sync_quality['total_devices'] > 0:
                sync_rate = sync_quality['synchronization_rate'] * 100
                if sync_rate >= 90:
                    self.sync_label.setText(f"Time sync: OK ({sync_rate:.0f}%)")
                elif sync_rate >= 70:
                    self.sync_label.setText(f"Time sync: WARNING ({sync_rate:.0f}%)")
                else:
                    self.sync_label.setText(f"Time sync: ERROR ({sync_rate:.0f}%)")
            else:
                self.sync_label.setText("Time sync: No devices")
            
            # Update UI state
            self._update_ui_state()
            
        except Exception as e:
            logger.error(f"Error updating displays: {e}")
    
    def _update_ui_state(self) -> None:
        """Update UI component enabled/disabled state based on current state."""
        current_session = self.session_manager.get_current_session()
        has_devices = len(self.network_server.get_connected_devices()) > 0
        
        # Update session control state
        if self.session_control_widget:
            self.session_control_widget.update_state(current_session, has_devices)
        
        # Update sync controls
        can_sync = (current_session and 
                   current_session.state in [SessionState.ACTIVE.value, SessionState.RECORDING.value] and
                   has_devices)
        
        self.sync_flash_btn.setEnabled(can_sync)
        self.sync_mark_btn.setEnabled(can_sync)
    
    # Event handlers
    def _on_start_session_requested(self) -> None:
        """Handle session start request."""
        try:
            current_session = self.session_manager.get_current_session()
            if not current_session:
                self._show_error("No session created", "Please create a new session first.")
                return
            
            if current_session.state != SessionState.IDLE.value:
                self._show_error("Cannot start session", 
                               f"Session is in {current_session.state} state.")
                return
            
            # Start the session
            self.session_manager.start_session()
            self.session_manager.begin_recording()
            self._session_start_time = datetime.now()
            
            # Send start command to all devices
            import asyncio
            asyncio.create_task(
                self.network_server.start_recording_session(current_session.session_id)
            )
            
            self._current_session_id = current_session.session_id
            self.session_started.emit(current_session.session_id)
            
            logger.info(f"Session started: {current_session.name}")
            self._add_log_message(f"Session started: {current_session.name}")
            
        except Exception as e:
            logger.error(f"Error starting session: {e}")
            self._show_error("Error", f"Failed to start session: {e}")
    
    def _on_stop_session_requested(self) -> None:
        """Handle session stop request."""
        try:
            current_session = self.session_manager.get_current_session()
            if not current_session:
                return
            
            # Send stop command to all devices
            import asyncio
            asyncio.create_task(
                self.network_server.stop_recording_session(current_session.session_id)
            )
            
            # End the session
            ended_session = self.session_manager.end_session()
            self._session_start_time = None
            
            self.session_stopped.emit(ended_session.session_id)
            
            logger.info(f"Session stopped: {ended_session.name}")
            self._add_log_message(f"Session stopped: {ended_session.name} "
                                f"(duration: {ended_session.duration_seconds:.1f}s)")
            
        except Exception as e:
            logger.error(f"Error stopping session: {e}")
            self._show_error("Error", f"Failed to stop session: {e}")
    
    def _on_new_session_requested(self) -> None:
        """Handle new session creation request."""
        try:
            # Get session name from user
            name, ok = QInputDialog.getText(
                self, 
                "New Session", 
                "Enter session name (leave empty for auto-generated):"
            )
            
            if not ok:
                return
            
            session_name = name.strip() if name.strip() else None
            
            # Create new session
            session = self.session_manager.create_session(session_name)
            
            logger.info(f"New session created: {session.name}")
            self._add_log_message(f"New session created: {session.name}")
            
        except Exception as e:
            logger.error(f"Error creating session: {e}")
            self._show_error("Error", f"Failed to create session: {e}")
    
    def _on_sync_flash_clicked(self) -> None:
        """Handle sync flash button click."""
        try:
            import asyncio
            asyncio.create_task(self.network_server.send_sync_flash())
            
            # Add sync event to session
            current_session = self.session_manager.get_current_session()
            if current_session:
                self.session_manager.add_sync_event("flash")
            
            self.sync_flash_triggered.emit()
            
            logger.info("Sync flash sent to all devices")
            self._add_log_message("Sync flash sent to all devices")
            
        except Exception as e:
            logger.error(f"Error sending sync flash: {e}")
            self._show_error("Error", f"Failed to send sync flash: {e}")
    
    def _on_sync_mark_clicked(self) -> None:
        """Handle sync mark button click."""
        try:
            # Get mark description from user
            description, ok = QInputDialog.getText(
                self,
                "Sync Mark",
                "Enter sync mark description:"
            )
            
            if not ok or not description.strip():
                return
            
            import asyncio
            asyncio.create_task(
                self.network_server.send_sync_mark("manual_mark", {"description": description})
            )
            
            # Add sync event to session
            current_session = self.session_manager.get_current_session()
            if current_session:
                self.session_manager.add_sync_event("manual_mark", {"description": description})
            
            logger.info(f"Sync mark added: {description}")
            self._add_log_message(f"Sync mark added: {description}")
            
        except Exception as e:
            logger.error(f"Error adding sync mark: {e}")
            self._show_error("Error", f"Failed to add sync mark: {e}")
    
    def _on_device_selected(self, device_id: str) -> None:
        """Handle device selection in list."""
        device_info = self.network_server.get_device_info(device_id)
        if device_info:
            logger.debug(f"Device selected: {device_id}")
    
    # Network event handlers
    def _on_device_connected(self, device_info: DeviceInfo) -> None:
        """Handle device connection."""
        logger.info(f"Device connected: {device_info.device_id}")
        self._add_log_message(f"Device connected: {device_info.device_id} "
                            f"({device_info.device_type})")
        
        # Add device to current session if active
        current_session = self.session_manager.get_current_session()
        if current_session:
            self.session_manager.add_device(device_info.to_dict())
    
    def _on_device_disconnected(self, device_info: DeviceInfo) -> None:
        """Handle device disconnection."""
        logger.warning(f"Device disconnected: {device_info.device_id}")
        self._add_log_message(f"Device disconnected: {device_info.device_id}")
        
        # Show alert for important devices
        if device_info.is_gsr_leader:
            self._show_warning("GSR Leader Disconnected", 
                             f"GSR leader device {device_info.device_id} has disconnected. "
                             f"A new leader will be elected if available.")
    
    def _on_device_status_updated(self, device_info: DeviceInfo) -> None:
        """Handle device status update."""
        logger.debug(f"Device status updated: {device_info.device_id}")
    
    # Utility methods
    def _add_log_message(self, message: str) -> None:
        """Add message to log display."""
        if self.log_display:
            timestamp = datetime.now().strftime("%H:%M:%S")
            formatted_message = f"[{timestamp}] {message}"
            self.log_display.append(formatted_message)
    
    def _show_error(self, title: str, message: str) -> None:
        """Show error message box."""
        QMessageBox.critical(self, title, message)
    
    def _show_warning(self, title: str, message: str) -> None:
        """Show warning message box."""
        QMessageBox.warning(self, title, message)
    
    def _show_info(self, title: str, message: str) -> None:
        """Show information message box."""
        QMessageBox.information(self, title, message)
    
    def closeEvent(self, event) -> None:
        """Handle window close event."""
        # Stop any active session
        current_session = self.session_manager.get_current_session()
        if current_session and current_session.state in [
            SessionState.ACTIVE.value, SessionState.RECORDING.value
        ]:
            reply = QMessageBox.question(
                self,
                "Active Session",
                "A recording session is active. Stop it before closing?",
                QMessageBox.Yes | QMessageBox.No | QMessageBox.Cancel
            )
            
            if reply == QMessageBox.Cancel:
                event.ignore()
                return
            elif reply == QMessageBox.Yes:
                self._on_stop_session_requested()
        
        # Stop UI updates
        if self._update_timer:
            self._update_timer.stop()
        
        event.accept()