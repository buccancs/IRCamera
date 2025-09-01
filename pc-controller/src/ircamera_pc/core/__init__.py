"""
Core modules for IRCamera PC Controller

This package contains the core business logic and services.
"""

from .config import config, ConfigManager
from .session import SessionManager, SessionMetadata, SessionState
from .timesync import TimeSyncService, TimeSyncStats
from .gsr_ingestor import GSRIngestor, GSRMode, GSRSample, GSRDataSet
from .file_transfer import FileTransferManager, FileManifest, TransferStatus, FileType
from .calibration import CameraCalibrator, CameraType, CalibrationResult, CalibrationStatus
from .bluetooth_manager import (
    BluetoothManager,
    BluetoothDevice,
    BluetoothDeviceType,
    ConnectionState,
)
from .wifi_manager import (
    WiFiManager,
    WiFiNetwork,
    NetworkSecurityType,
    ConnectionState as WiFiConnectionState,
    HotspotState,
    NetworkInterface,
)
from .admin_privileges import (
    AdminPrivilegesManager,
    PrivilegeLevel,
    ElevationResult,
    SystemPermissions,
)

__all__ = [
    "config",
    "ConfigManager",
    "SessionManager",
    "SessionMetadata",
    "SessionState",
    "TimeSyncService",
    "TimeSyncStats",
    "GSRIngestor",
    "GSRMode",
    "GSRSample",
    "GSRDataSet",
    "FileTransferManager",
    "FileManifest",
    "TransferStatus",
    "FileType",
    "CameraCalibrator",
    "CameraType",
    "CalibrationResult",
    "CalibrationStatus",
    "BluetoothManager",
    "BluetoothDevice",
    "BluetoothDeviceType",
    "ConnectionState",
    "WiFiManager",
    "WiFiNetwork",
    "NetworkSecurityType",
    "WiFiConnectionState",
    "HotspotState",
    "NetworkInterface",
    "AdminPrivilegesManager",
    "PrivilegeLevel",
    "ElevationResult",
    "SystemPermissions",
]
