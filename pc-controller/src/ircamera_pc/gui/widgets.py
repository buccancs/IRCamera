""""""

import logging
from typing import Dict, List, Optional

from PyQt6.QtCore import QTimer, pyqtSignal, pyqtSlot
from PyQt6.QtGui import QFont
from PyQt6.QtWidgets import (
    QComboBox,
    QGroupBox,
    QHBoxLayout,
    QLabel,
    QListWidget,
    QListWidgetItem,
    QMessageBox,
    QPushButton,
    QTextEdit,
    QVBoxLayout,
    QWidget,
)

try:
    from .plotting_widgets import DataAggregationWidget, MultiModalDashboard
except ImportError:
    # Fallback in case plotting widgets are not available
    logging.warning("Plotting widgets not available - using placeholder classes")

    class MultiModalDashboard(QWidget):