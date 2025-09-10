
"""Time synchronization service for multi-device coordination."""

import asyncio
import statistics
import time
from dataclasses import dataclass, field
from datetime import datetime, timezone
from enum import Enum
from typing import Any, Dict, List, Optional

try:
    import numpy as np
    NUMPY_AVAILABLE = True
except ImportError:
    NUMPY_AVAILABLE = False

from loguru import logger


class SyncQuality(Enum):
    """Time synchronization quality levels."""
    UNKNOWN = "unknown"
    POOR = "poor"
    FAIR = "fair"
    GOOD = "good"
    EXCELLENT = "excellent"


@dataclass
class TimeSyncInfo:
    """Time synchronization information for a device."""
    device_id: str
    last_sync: Optional[datetime] = None
    offset_ms: float = 0.0
    round_trip_ms: float = 0.0
    sync_count: int = 0
    median_offset_ms: float = 0.0
    p95_offset_ms: float = 0.0
    recent_offsets: List[float] = field(default_factory=list)
    network_jitter_ms: float = 0.0
    drift_rate_ppm: float = 0.0
    quality: SyncQuality = SyncQuality.UNKNOWN
    accuracy_us: float = 0.0

    def __post_init__(self) -> None:
        """Post-initialization processing."""
        if not self.recent_offsets:
            return

    def update_statistics(self, max_samples: int = 100) -> None:
        """Update synchronization statistics."""
        if not self.recent_offsets:
            return

        # Maintain sliding window of recent samples
        if len(self.recent_offsets) > max_samples:
            self.recent_offsets = self.recent_offsets[-max_samples:]

        # Calculate statistical metrics
        if len(self.recent_offsets) >= 3:
            self.median_offset_ms = statistics.median(self.recent_offsets)
            self.p95_offset_ms = (
                np.percentile(self.recent_offsets, 95)
                if NUMPY_AVAILABLE
                else max(self.recent_offsets)
            )

            # Calculate network jitter (standard deviation of offsets)
            self.network_jitter_ms = statistics.stdev(self.recent_offsets)

            # Assess synchronization quality
            self._assess_quality()

    def _assess_quality(self) -> None:
        """Assess synchronization quality based on metrics."""
        if self.network_jitter_ms < 1.0:
            self.quality = SyncQuality.EXCELLENT
        elif self.network_jitter_ms < 5.0:
            self.quality = SyncQuality.GOOD
        elif self.network_jitter_ms < 10.0:
            self.quality = SyncQuality.FAIR
        else:
            self.quality = SyncQuality.POOR


class TimeSyncService:
    """Service for synchronizing time across devices."""

    def __init__(self) -> None:
        """Initialize time sync service."""
        self._device_sync: Dict[str, TimeSyncInfo] = {}
        self._is_running = False

    async def start_server(self, host: str = "0.0.0.0", port: int = 8889) -> None:
        """Start the time sync server."""
        if self._is_running:
            logger.warning("Time sync service is already running")
            return

        try:
            # Simplified sync server implementation
            self._is_running = True
            logger.info(f"Time sync service started on {host}:{port}")

        except Exception as e:
            logger.error(f"Failed to start time sync server: {e}")
            self._is_running = False

    async def stop_server(self) -> None:
        """Stop the time sync server."""
        if not self._is_running:
            logger.warning("Time sync service is not running")
            return

        self._is_running = False
        logger.info("Time sync service stopped")

    async def sync_device(self, device_id: str) -> TimeSyncInfo:
        """Synchronize time with a device."""
        if device_id not in self._device_sync:
            self._device_sync[device_id] = TimeSyncInfo(device_id=device_id)

        sync_info = self._device_sync[device_id]
        
        # Simplified sync logic - in real implementation this would
        # perform NTP-like handshake
        current_time = time.time()
        sync_info.last_sync = datetime.now(timezone.utc)
        sync_info.sync_count += 1
        sync_info.offset_ms = 0.0  # Placeholder
        sync_info.round_trip_ms = 10.0  # Placeholder
        
        return sync_info

    def get_device_sync_info(self, device_id: str) -> Optional[TimeSyncInfo]:
        """Get sync info for a device."""
        return self._device_sync.get(device_id)

    def get_all_sync_info(self) -> Dict[str, TimeSyncInfo]:
        """Get sync info for all devices."""
        return self._device_sync.copy()
