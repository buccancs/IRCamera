"""Data processing and aggregation module."""

import threading
import time
from collections import deque
from dataclasses import dataclass, field
from pathlib import Path
from queue import Queue
from typing import Any, Dict, List, Optional, Tuple

try:
    pass

    HDF5_AVAILABLE = True
except ImportError:
    HDF5_AVAILABLE = False

try:
    pass

    PANDAS_AVAILABLE = True
except ImportError:
    PANDAS_AVAILABLE = False

from loguru import logger


@dataclass
class SyncEvent:
    """Synchronization event for temporal alignment."""

    timestamp_ns: int
    event_type: str
    source_device: str
    metadata: Dict[str, Any] = field(default_factory=dict)


@dataclass
class DataStream:
    """Data stream container for device data."""

    device_id: str
    stream_type: str
    sample_rate: float
    start_timestamp_ns: int
    data_buffer: deque[Tuple[int, Any]] = field(
        default_factory=lambda: deque(maxlen=10000)
    )
    total_samples: int = 0
    dropped_samples: int = 0
    is_active: bool = True


@dataclass
class AggregationStats:
    """Statistics for data aggregation."""

    total_devices: int = 0
    data_rate_mbps: float = 0.0
    sync_quality_percent: float = 0.0
    dropped_frames_total: int = 0


class DataAggregationEngine:
    """Main data aggregation and synchronization engine."""

    def __init__(self, session_directory: str, buffer_size_mb: int = 100) -> None:
        """Initialize data aggregation engine."""
        self.session_directory = Path(session_directory)
        self.session_directory.mkdir(parents=True, exist_ok=True)

        self.buffer_size_bytes = buffer_size_mb * 1024 * 1024

        # Data streams management
        self.streams: Dict[str, DataStream] = {}  # stream_id -> DataStream
        self.sync_events: List[SyncEvent] = []

        # Threading and async management
        self.data_queue: Queue[Dict[str, Any]] = Queue()
        self.sync_queue: Queue[SyncEvent] = Queue()
        self.aggregation_thread: Optional[threading.Thread] = None
        self.is_running = threading.Event()

        # Performance tracking
        self.stats = AggregationStats()
        self.start_time = time.time()
        self.last_stats_update = time.time()

        # HDF5 export configuration
        self.hdf5_file: Optional[Any] = None
        self.export_enabled = HDF5_AVAILABLE

        logger.info(
            f"Data aggregation engine initialized for session: {session_directory}"
        )

    def start(self) -> None:
        """Start the data aggregation engine."""
        if self.is_running.is_set():
            logger.warning("Data aggregation engine is already running")
            return

        self.is_running.set()
        self.aggregation_thread = threading.Thread(target=self._aggregation_loop)
        self.aggregation_thread.start()

        logger.info("Data aggregation engine started")

    def stop(self) -> None:
        """Stop the data aggregation engine."""
        if not self.is_running.is_set():
            logger.warning("Data aggregation engine is not running")
            return

        self.is_running.clear()

        if self.aggregation_thread:
            self.aggregation_thread.join(timeout=5.0)

        if self.hdf5_file:
            self.hdf5_file.close()
            self.hdf5_file = None

        logger.info("Data aggregation engine stopped")

    def add_stream(self, device_id: str, stream_type: str, sample_rate: float) -> str:
        """Add a new data stream."""
        stream_id = f"{device_id}_{stream_type}"

        if stream_id in self.streams:
            logger.warning(f"Stream {stream_id} already exists, updating configuration")

        stream = DataStream(
            device_id=device_id,
            stream_type=stream_type,
            sample_rate=sample_rate,
            start_timestamp_ns=time.time_ns(),
        )

        self.streams[stream_id] = stream

        logger.info(f"Added data stream: {stream_id} ({stream_type}, {sample_rate}Hz)")
        return stream_id

    def remove_stream(self, stream_id: str) -> bool:
        """Remove a data stream."""
        if stream_id not in self.streams:
            return False

        # Mark stream as inactive
        self.streams[stream_id].is_active = False
        del self.streams[stream_id]

        logger.info(f"Removed data stream: {stream_id}")
        return True

    def add_data(self, stream_id: str, timestamp_ns: int, data: Any) -> bool:
        """Add data to a stream."""
        if not self.is_running.is_set():
            return False

        if stream_id not in self.streams:
            logger.warning(f"Attempted to add data to unknown stream: {stream_id}")
            return False

        # Queue data for processing
        self.data_queue.put(
            {
                "stream_id": stream_id,
                "timestamp_ns": timestamp_ns,
                "data": data,
                "received_at": time.time_ns(),
            }
        )

        return True

    def add_sync_event(
        self,
        event_type: str,
        source_device: str,
        timestamp_ns: Optional[int] = None,
        metadata: Optional[Dict[str, Any]] = None,
    ) -> None:
        """Add a synchronization event."""
        if timestamp_ns is None:
            timestamp_ns = time.time_ns()

        if metadata is None:
            metadata = {}

        sync_event = SyncEvent(
            timestamp_ns=timestamp_ns,
            event_type=event_type,
            source_device=source_device,
            metadata=metadata,
        )

        self.sync_queue.put(sync_event)

        logger.info(f"Added sync event: {event_type} from {source_device}")

    def get_statistics(self) -> AggregationStats:
        """Get current aggregation statistics."""
        return self.stats

    def _aggregation_loop(self) -> None:
        """Main aggregation processing loop."""
        while self.is_running.is_set():
            try:
                # Process data queue
                if not self.data_queue.empty():
                    data_item = self.data_queue.get(timeout=0.1)
                    self._process_data_item(data_item)

                # Process sync events
                if not self.sync_queue.empty():
                    sync_event = self.sync_queue.get(timeout=0.1)
                    self.sync_events.append(sync_event)

                time.sleep(0.001)  # Small sleep to prevent busy waiting

            except Exception as e:
                logger.error(f"Error in aggregation loop: {e}")

    def _process_data_item(self, data_item: Dict[str, Any]) -> None:
        """Process a single data item."""
        stream_id = data_item["stream_id"]
        timestamp_ns = data_item["timestamp_ns"]
        data = data_item["data"]

        if stream_id in self.streams:
            stream = self.streams[stream_id]
            stream.data_buffer.append((timestamp_ns, data))
            stream.total_samples += 1
