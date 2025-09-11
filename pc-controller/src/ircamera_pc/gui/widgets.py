"""GUI widgets for IRCamera PC Controller."""

from PyQt6.QtWidgets import QWidget, QVBoxLayout, QLabel


class DeviceListWidget(QWidget):
    """Widget displaying connected devices."""
    
    def __init__(self):
        super().__init__()
        self.setup_ui()
    
    def setup_ui(self):
        """Setup the widget UI."""
        layout = QVBoxLayout()
        self.status_label = QLabel("No devices connected")
        layout.addWidget(self.status_label)
        self.setLayout(layout)


class SessionControlWidget(QWidget):
    """Widget for session control."""
    
    def __init__(self):
        super().__init__()
        self.setup_ui()
    
    def setup_ui(self):
        """Setup the widget UI."""
        layout = QVBoxLayout()
        self.control_label = QLabel("Session Control")
        layout.addWidget(self.control_label)
        self.setLayout(layout)


class StatusDisplayWidget(QWidget):
    """Widget for status display."""
    
    def __init__(self):
        super().__init__()
        self.setup_ui()
    
    def setup_ui(self):
        """Setup the widget UI."""
        layout = QVBoxLayout()
        self.info_label = QLabel("Status: Ready")
        layout.addWidget(self.info_label)
        self.setLayout(layout)
