"""
GUI modules for IRCamera PC Controller

This package contains the PyQt6-based user interface components.
"""

import os

# Check if GUI is available
GUI_AVAILABLE = True
try:
    # Set Qt platform to offscreen if no display is available
    if "DISPLAY" not in os.environ and "QT_QPA_PLATFORM" not in os.environ:
        os.environ["QT_QPA_PLATFORM"] = "offscreen"

    # Test PyQt6 imports
except ImportError:
    GUI_AVAILABLE = False

from .app import IRCameraApp, main
from .utils import (
    format_duration,
    format_file_size,
    setup_logging,
)

# Only import GUI components if available
if GUI_AVAILABLE:
    try:
        __all__ = [
            "IRCameraApp",
            "main",
            "MainWindow",
            "DeviceListWidget",
            "SessionControlWidget",
            "StatusDisplayWidget",
            "setup_logging",
            "format_file_size",
            "format_duration",
        ]
    except ImportError:
        __all__ = [
            "IRCameraApp",
            "main",
            "setup_logging",
            "format_file_size",
            "format_duration",
        ]
else:
    __all__ = [
        "IRCameraApp",
        "main",
        "setup_logging",
        "format_file_size",
        "format_duration",
    ]
