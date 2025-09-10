
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
        self.hdf5_file: Optional[h5py.File] = None
        self.export_enabled = True

        logger.info(
            f"Data aggregation engine initialized for session: {session_directory}"
        )

    def start(self) -> None:

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

        if self.export_enabled and self.hdf5_file:
            self._create_hdf5_dataset(stream_id, stream)

        logger.info(f"Added data stream: {stream_id} ({stream_type}, {sample_rate}Hz)")
        return stream_id

    def remove_stream(self, stream_id: str) -> bool:

        if stream_id not in self.streams:
            return False

        # Mark stream as inactive
        self.streams[stream_id].is_active = False

        # Export remaining data
        if self.export_enabled:
            self._export_stream_data(stream_id)

        del self.streams[stream_id]

        logger.info(f"Removed data stream: {stream_id}")
        return True

    def add_data(self, stream_id: str, timestamp_ns: int, data: Any) -> bool:

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
        metadata: Optional[Dict[Any, Any]] = None,
    ) -> None:

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

        if stream_id not in self.streams:
            return []

        stream = self.streams[stream_id]
        data_list = list(stream.data_buffer)

        if last_n < len(data_list):
            data_list = data_list[-last_n:]

        return data_list

    def export_session_data(self, export_path: Optional[Path] = None) -> Path:

        if export_path is None:
            export_path = self.session_directory / "exports"

        export_path.mkdir(parents=True, exist_ok=True)

        # Export sync events
        sync_data = []
        for event in self.sync_events:
            sync_data.append(
                {
                    "timestamp_ns": event.timestamp_ns,
                    "event_type": event.event_type,
                    "source_device": event.source_device,
                    "metadata": event.metadata,
                }
            )

        sync_df = pd.DataFrame(sync_data)
        sync_df.to_csv(export_path / "sync_events.csv", index=False)

        # Export stream summaries
        stream_summary = []
        for stream_id, stream in self.streams.items():
            stream_summary.append(
                {
                    "stream_id": stream_id,
                    "device_id": stream.device_id,
                    "stream_type": stream.stream_type,
                    "sample_rate": stream.sample_rate,
                    "start_timestamp_ns": stream.start_timestamp_ns,
                    "total_samples": stream.total_samples,
                    "dropped_samples": stream.dropped_samples,
                    "is_active": stream.is_active,
                }
            )

        summary_df = pd.DataFrame(stream_summary)
        summary_df.to_csv(export_path / "stream_summary.csv", index=False)

        # Export session metadata
        session_metadata = {
            "session_directory": str(self.session_directory),
            "start_time": self.start_time,
            "duration_seconds": time.time() - self.start_time,
            "total_streams": len(self.streams),
            "total_sync_events": len(self.sync_events),
            "statistics": {
                "total_devices": self.stats.total_devices,
                "data_rate_mbps": self.stats.data_rate_mbps,
                "sync_quality_percent": self.stats.sync_quality_percent,
                "dropped_frames_total": self.stats.dropped_frames_total,
            },
        }

        with open(export_path / "session_metadata.json", "w") as f:
            json.dump(session_metadata, f, indent=2)

        logger.info(f"Session data exported to: {export_path}")
        return export_path

    def _aggregation_loop(self) -> None:

    global _global_aggregator
    _global_aggregator = aggregator

def calculate_temporal_alignment(
    sync_events: List[SyncEvent], tolerance_ms: float = 5.0
) -> Dict[str, float]:

    device_offsets: Dict[str, float] = {}

    if not sync_events:
        return device_offsets

    # Group sync events by type and timestamp
    flash_events = [e for e in sync_events if e.event_type == "flash"]

    if len(flash_events) < 2:
        return device_offsets

    # Use first flash event as reference
    reference_event = flash_events[0]
    reference_device = reference_event.source_device
    reference_timestamp = reference_event.timestamp_ns

    device_offsets[reference_device] = 0.0  # Reference device has zero offset

    # Calculate offsets for other devices
    for event in flash_events[1:]:
        if event.source_device != reference_device:
            offset_ns = event.timestamp_ns - reference_timestamp
            offset_ms = offset_ns / 1e6

            if abs(offset_ms) <= tolerance_ms:
                device_offsets[event.source_device] = offset_ns
            else:
                logger.warning(
                    f"Device {event.source_device} offset {offset_ms:.2f}ms "
                    f"exceeds tolerance"
                )

    return device_offsets

def validate_data_synchronization(
    streams: Dict[str, DataStream], tolerance_ms: float = 5.0
) -> Dict[str, Any]:

    report = _initialize_sync_report(streams)

    if len(streams) < 2:
        return report

    stream_timestamps = _extract_stream_timestamps(streams)
    if len(stream_timestamps) < 2:
        return report

    _calculate_sync_metrics(stream_timestamps, tolerance_ms, report)
    return report

def _initialize_sync_report(streams: Dict[str, DataStream]) -> Dict[str, Any]:
    """Initialize synchronization report structure"""
    return {
        "total_streams": len(streams),
        "synchronized_streams": 0,
        "max_offset_ms": 0.0,
        "synchronization_rate": 0.0,
        "quality_issues": [],
    }

def _extract_stream_timestamps(streams: Dict[str, DataStream]) -> Dict[str, List]:
    """Extract recent timestamps from active streams"""
    stream_timestamps = {}
    for stream_id, stream in streams.items():
        if stream.data_buffer and stream.is_active:
            recent_data = list(stream.data_buffer)[-10:]  # Last 10 samples
            if recent_data:
                stream_timestamps[stream_id] = [ts for ts, _ in recent_data]
    return stream_timestamps

def _calculate_sync_metrics(
    stream_timestamps: Dict[str, List], tolerance_ms: float, report: Dict[str, Any]
):
    """Calculate synchronization metrics and update report"""
    all_timestamps = []
    for timestamps in stream_timestamps.values():
        all_timestamps.extend(timestamps)

    if not all_timestamps:
        return

    min_timestamp = min(all_timestamps)
    max_timestamp = max(all_timestamps)
    max_offset_ns = max_timestamp - min_timestamp
    max_offset_ms = max_offset_ns / 1e6
    report["max_offset_ms"] = max_offset_ms

    synchronized_count = _count_synchronized_streams(
        stream_timestamps, max_timestamp, tolerance_ms, report
    )

    report["synchronized_streams"] = synchronized_count
    report["synchronization_rate"] = synchronized_count / len(stream_timestamps)

def _count_synchronized_streams(
    stream_timestamps: Dict[str, List],
    max_timestamp: float,
    tolerance_ms: float,
    report: Dict[str, Any],
) -> int:
    """Count streams within synchronization tolerance"""
    synchronized_count = 0
    for stream_id, timestamps in stream_timestamps.items():
        if timestamps:
            latest_timestamp = max(timestamps)
            offset_ms = abs(latest_timestamp - max_timestamp) / 1e6

            if offset_ms <= tolerance_ms:
                synchronized_count += 1
            else:
                report["quality_issues"].append(
                    f"Stream {stream_id} offset {offset_ms:.2f}ms exceeds tolerance"
                )
    return synchronized_count
