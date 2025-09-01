"""
Custom widgets for IRCamera PC Controller GUI

Specialized UI components for device management and session control.
"""

from PyQt6.QtWidgets import (
    QWidget,
    QVBoxLayout,
    QHBoxLayout,
    QPushButton,
    QLabel,
    QListWidget,
    QListWidgetItem,
    QProgressBar,
    QGroupBox,
    QGridLayout,
    QLCDNumber,
    QFrame,
)
from PyQt6.QtCore import Qt, pyqtSignal, QTimer
from PyQt6.QtGui import QFont, QColor, QPalette
from typing import Dict, Any, Optional
from datetime import datetime

from ..network.server import DeviceInfo, DeviceState
from ..core.session import SessionMetadata, SessionState


class DeviceListWidget(QListWidget):
    """
    Custom widget for displaying connected devices with status indicators.
    """

    device_selected = pyqtSignal(str)  # device_id

    def __init__(self):
        """Initialize device list widget."""
        super().__init__()
        self.setMinimumHeight(200)
        self._device_items: Dict[str, QListWidgetItem] = {}

        # Connect selection signal
        self.itemClicked.connect(self._on_item_clicked)

    def update_devices(self, devices: Dict[str, DeviceInfo]) -> None:
        """
        Update the device list with current device information.

        Args:
            devices: Dictionary mapping device_id to DeviceInfo
        """
        # Remove devices that are no longer connected
        for device_id in list(self._device_items.keys()):
            if device_id not in devices:
                item = self._device_items.pop(device_id)
                row = self.row(item)
                self.takeItem(row)

        # Add or update existing devices
        for device_id, device_info in devices.items():
            if device_id in self._device_items:
                self._update_device_item(device_id, device_info)
            else:
                self._add_device_item(device_id, device_info)

    def _add_device_item(self, device_id: str, device_info: DeviceInfo) -> None:
        """Add new device item to list."""
        item = QListWidgetItem()
        item.setData(Qt.ItemDataRole.UserRole, device_id)

        self._device_items[device_id] = item
        self.addItem(item)
        self._update_device_item(device_id, device_info)

    def _update_device_item(self, device_id: str, device_info: DeviceInfo) -> None:
        """Update existing device item."""
        item = self._device_items[device_id]

        # Create status text
        status_text = self._format_device_status(device_info)
        item.setText(status_text)

        # Set color based on state
        if device_info.state == DeviceState.CONNECTED.value:
            item.setForeground(QColor(0, 120, 0))  # Green
        elif device_info.state == DeviceState.RECORDING.value:
            item.setForeground(QColor(200, 0, 0))  # Red
        elif device_info.state == DeviceState.ERROR.value:
            item.setForeground(QColor(200, 100, 0))  # Orange
        else:
            item.setForeground(QColor(100, 100, 100))  # Gray

    def _format_device_status(self, device_info: DeviceInfo) -> str:
        """Format device information for display."""
        lines = []

        # Device name and type
        name_line = f"📱 {device_info.device_id}"
        if device_info.is_gsr_leader:
            name_line += " (GSR Leader)"
        lines.append(name_line)

        # Device type and capabilities
        lines.append(f"   Type: {device_info.device_type}")

        if device_info.capabilities:
            caps = ", ".join(device_info.capabilities)
            lines.append(f"   Capabilities: {caps}")

        # Status and battery
        status_parts = [f"State: {device_info.state}"]

        if device_info.battery_level is not None:
            battery_icon = "🔋" if device_info.battery_level > 20 else "🪫"
            status_parts.append(f"Battery: {device_info.battery_level}% {battery_icon}")

        lines.append(f"   {', '.join(status_parts)}")

        # Connection info
        if device_info.last_heartbeat:
            try:
                last_hb = datetime.fromisoformat(device_info.last_heartbeat.replace("Z", "+00:00"))
                time_since = (
                    datetime.now().replace(tzinfo=last_hb.tzinfo) - last_hb
                ).total_seconds()
                lines.append(f"   Last heartbeat: {time_since:.0f}s ago")
            except (ValueError, TypeError, AttributeError):
                lines.append(f"   Last heartbeat: {device_info.last_heartbeat}")

        return "\n".join(lines)

    def _on_item_clicked(self, item: QListWidgetItem) -> None:
        """Handle item click."""
        device_id = item.data(Qt.ItemDataRole.UserRole)
        if device_id:
            self.device_selected.emit(device_id)


class SessionControlWidget(QWidget):
    """
    Widget for session control operations.
    """

    new_session_requested = pyqtSignal()
    start_session_requested = pyqtSignal()
    stop_session_requested = pyqtSignal()

    def __init__(self):
        """Initialize session control widget."""
        super().__init__()
        self._setup_ui()

        # Current state
        self._current_session: Optional[SessionMetadata] = None
        self._has_devices = False

    def _setup_ui(self) -> None:
        """Set up the user interface."""
        layout = QVBoxLayout(self)

        # Session info display
        info_frame = QFrame()
        info_frame.setFrameStyle(QFrame.Box)
        info_layout = QVBoxLayout(info_frame)

        self.session_name_label = QLabel("No active session")
        self.session_name_label.setFont(QFont("Arial", 10, QFont.Bold))
        info_layout.addWidget(self.session_name_label)

        self.session_status_label = QLabel("Create a new session to begin")
        info_layout.addWidget(self.session_status_label)

        layout.addWidget(info_frame)

        # Control buttons
        button_layout = QVBoxLayout()

        self.new_session_btn = QPushButton("New Session")
        self.new_session_btn.clicked.connect(self.new_session_requested.emit)
        button_layout.addWidget(self.new_session_btn)

        self.start_session_btn = QPushButton("Start Recording")
        self.start_session_btn.clicked.connect(self.start_session_requested.emit)
        self.start_session_btn.setProperty("class", "primary")
        button_layout.addWidget(self.start_session_btn)

        self.stop_session_btn = QPushButton("Stop Recording")
        self.stop_session_btn.clicked.connect(self.stop_session_requested.emit)
        self.stop_session_btn.setProperty("class", "danger")
        button_layout.addWidget(self.stop_session_btn)

        layout.addLayout(button_layout)

    def update_state(self, session: Optional[SessionMetadata], has_devices: bool) -> None:
        """
        Update widget state based on current session and device status.

        Args:
            session: Current session metadata
            has_devices: Whether any devices are connected
        """
        self._current_session = session
        self._has_devices = has_devices

        # Update display
        if session:
            self.session_name_label.setText(f"Session: {session.name}")

            if session.state == SessionState.IDLE.value:
                self.session_status_label.setText("Ready to start recording")
            elif session.state == SessionState.ACTIVE.value:
                self.session_status_label.setText("Session active - preparing to record")
            elif session.state == SessionState.RECORDING.value:
                self.session_status_label.setText("🔴 Recording in progress")
            else:
                self.session_status_label.setText(f"Status: {session.state}")
        else:
            self.session_name_label.setText("No active session")
            self.session_status_label.setText("Create a new session to begin")

        # Update button states
        self._update_button_states()

    def _update_button_states(self) -> None:
        """Update button enabled/disabled states."""
        has_idle_session = (
            self._current_session and self._current_session.state == SessionState.IDLE.value
        )

        is_recording = self._current_session and self._current_session.state in [
            SessionState.ACTIVE.value,
            SessionState.RECORDING.value,
        ]

        # New session: enabled when no active session or session is completed
        self.new_session_btn.setEnabled(
            not self._current_session
            or self._current_session.state
            in [SessionState.COMPLETED.value, SessionState.ERROR.value]
        )

        # Start recording: enabled when idle session exists and devices are connected
        self.start_session_btn.setEnabled(has_idle_session and self._has_devices)

        # Stop recording: enabled when recording is active
        self.stop_session_btn.setEnabled(is_recording)


class StatusDisplayWidget(QWidget):
    """
    Widget for displaying system status information.
    """

    def __init__(self):
        """Initialize status display widget."""
        super().__init__()
        self._setup_ui()

    def _setup_ui(self) -> None:
        """Set up the user interface."""
        layout = QGridLayout(self)

        # Time synchronization status
        layout.addWidget(QLabel("Time Synchronization:"), 0, 0)
        self.sync_quality_label = QLabel("No data")
        layout.addWidget(self.sync_quality_label, 0, 1)

        self.sync_median_label = QLabel("Median offset: --")
        layout.addWidget(self.sync_median_label, 1, 0, 1, 2)

        self.sync_p95_label = QLabel("P95 offset: --")
        layout.addWidget(self.sync_p95_label, 2, 0, 1, 2)

        # Session information
        layout.addWidget(QLabel("Current Session:"), 3, 0)
        self.session_info_label = QLabel("No active session")
        layout.addWidget(self.session_info_label, 3, 1)

        self.session_duration_label = QLabel("Duration: --:--:--")
        layout.addWidget(self.session_duration_label, 4, 0, 1, 2)

        # GSR leader status
        layout.addWidget(QLabel("GSR Leader:"), 5, 0)
        self.gsr_leader_label = QLabel("No leader")
        layout.addWidget(self.gsr_leader_label, 5, 1)

        # Stretch at bottom
        layout.setRowStretch(6, 1)

    def update_time_sync_stats(self, stats: Dict[str, Any]) -> None:
        """Update time synchronization statistics display."""
        total_devices = stats.get("total_devices", 0)
        sync_devices = stats.get("synchronized_devices", 0)
        sync_rate = stats.get("synchronization_rate", 0) * 100

        if total_devices > 0:
            self.sync_quality_label.setText(
                f"{sync_devices}/{total_devices} devices ({sync_rate:.0f}%)"
            )

            # Color coding based on sync quality
            if sync_rate >= 90:
                color = "green"
            elif sync_rate >= 70:
                color = "orange"
            else:
                color = "red"

            self.sync_quality_label.setStyleSheet(f"color: {color}; font-weight: bold;")
        else:
            self.sync_quality_label.setText("No devices")
            self.sync_quality_label.setStyleSheet("color: gray;")

        # Update offset displays
        median_offset = stats.get("overall_median_offset_ms", 0)
        p95_offset = stats.get("overall_p95_offset_ms", 0)

        self.sync_median_label.setText(f"Median offset: {median_offset:.1f} ms")
        self.sync_p95_label.setText(f"P95 offset: {p95_offset:.1f} ms")

    def update_session_info(self, session: SessionMetadata) -> None:
        """Update session information display."""
        self.session_info_label.setText(f"{session.name} ({session.state})")

        # Calculate and display duration
        if session.started_at:
            try:
                start_time = datetime.fromisoformat(session.started_at.replace("Z", "+00:00"))

                if session.ended_at:
                    end_time = datetime.fromisoformat(session.ended_at.replace("Z", "+00:00"))
                    duration = end_time - start_time
                else:
                    duration = datetime.now().replace(tzinfo=start_time.tzinfo) - start_time

                # Format duration as HH:MM:SS
                total_seconds = int(duration.total_seconds())
                hours = total_seconds // 3600
                minutes = (total_seconds % 3600) // 60
                seconds = total_seconds % 60

                self.session_duration_label.setText(
                    f"Duration: {hours:02d}:{minutes:02d}:{seconds:02d}"
                )

            except Exception:
                self.session_duration_label.setText("Duration: --:--:--")
        else:
            self.session_duration_label.setText("Duration: --:--:--")

    def update_gsr_leader_info(self, leader_info: Optional[DeviceInfo]) -> None:
        """Update GSR leader information display."""
        if leader_info:
            self.gsr_leader_label.setText(f"{leader_info.device_id} ({leader_info.gsr_mode})")
        else:
            self.gsr_leader_label.setText("No leader")
