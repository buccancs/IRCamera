#!/usr/bin/env python3
"""
Enhanced Integration Example: Complete Hub-and-Spoke Demonstration

This enhanced example demonstrates the full Hub-and-Spoke architecture:
- Native backend for high-performance sensor interfacing
- Real-time plotting with PyQtGraph and enhanced visualization
- Advanced data aggregation engine with scientific export
- Enhanced GUI components with device management
- Complete network server with Android device coordination
- Comprehensive error handling and recovery mechanisms

Usage:
    python integration_example.py [--demo-mode] [--session-dir PATH]
    [--enable-native] [--port PORT]

Features Demonstrated:
    * Multi-modal sensor coordination (RGB, Thermal, GSR)
    * Sub-5ms time synchronization across devices
    * Real-time data visualization and analysis
    * Scientific data export (HDF5, CSV, JSON)
    * Device fault detection and recovery
    * Cross-platform compatibility
"""

import argparse
import sys
import time
from pathlib import Path
from typing import Any, Dict, Optional

import numpy as np
from PyQt6.QtCore import QTimer, pyqtSignal
from PyQt6.QtWidgets import (
    QApplication,
    QHBoxLayout,
    QMainWindow,
    QPushButton,
    QVBoxLayout,
    QWidget,
)

sys.path.insert(0, str(Path(__file__).parent / "src"))

# Local imports - moved after sys.path setup
from ircamera_pc.core.session import SessionManager
from ircamera_pc.core.timesync import TimeSyncService
from ircamera_pc.data import DataAggregationEngine
from ircamera_pc.gui.plotting_widgets import MultiModalDashboard
from ircamera_pc.gui.widgets import (
    DeviceListWidget,
    SessionControlWidget,
    StatusDisplayWidget,
)
from ircamera_pc.network.server import NetworkServer

try:
    import native_backend