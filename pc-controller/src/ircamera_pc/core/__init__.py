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

__all__ = [
    'config',
    'ConfigManager',
    'SessionManager',
    'SessionMetadata', 
    'SessionState',
    'TimeSyncService',
    'TimeSyncStats',
    'GSRIngestor',
    'GSRMode',
    'GSRSample', 
    'GSRDataSet',
    'FileTransferManager',
    'FileManifest',
    'TransferStatus',
    'FileType',
    'CameraCalibrator',
    'CameraType',
    'CalibrationResult',
    'CalibrationStatus'
]