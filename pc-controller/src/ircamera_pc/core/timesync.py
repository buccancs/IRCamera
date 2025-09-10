

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

        if self._is_running:
            logger.warning("Time sync service is already running")
            return

        try:
            loop = asyncio.get_event_loop()

            transport, protocol = await loop.create_datagram_endpoint(
                lambda: TimeSyncProtocol(self), local_addr=(host, port)
            )

            self._server_socket = transport
            self._protocol = protocol
            self._is_running = True

            logger.info(f"Time sync service started on {host}:{port}")

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to start time sync service: {e}")
            raise

    async def stop(self) -> None:

        try:
            # Parse request
            if len(request_data) < 16:
                logger.warning(f"Invalid sync request from {device_id}: too short")
                return b""

            # Extract client timestamp (when request was sent)
            client_send_time = struct.unpack("!Q", request_data[:8])[0] / 1000.0

            server_time = time.time()
            server_time_ms = int(server_time * 1000)

            response = struct.pack(
                "!QQ",
                int(client_send_time * 1000),
                server_time_ms,  # Echo client time
            )  # Server time

            self._update_device_stats(device_id, client_send_time, server_time)

            logger.debug(f"Time sync response sent to {device_id} at {addr}")
            return response

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error handling sync request from {device_id}: {e}")
            return b""

    def _update_device_stats(
        self, device_id: str, client_time: float, server_time: float
    ) -> None:

        stats = self._device_stats.get(device_id)
        if not stats or not stats.last_sync:
            return False

        time_since_sync = (datetime.now(timezone.utc) - stats.last_sync).total_seconds()
        if time_since_sync > self._sync_interval * 2:
            return False

        offset_condition = bool(stats.median_offset_ms <= self._target_accuracy_ms)
        max_offset_condition = bool(stats.p95_offset_ms <= self._max_offset_ms)
        return offset_condition and max_offset_condition

    def get_synchronization_quality(self) -> Dict[str, Any]:

        try:
            # Extract device ID from data
            if len(data) < 16:
                logger.warning(f"Invalid time sync request from {addr}: too short")
                return

            # Simple protocol: first 8 bytes timestamp, next 8 bytes device ID hash
            device_id_hash = struct.unpack("!Q", data[8:16])[0]
            device_id = f"device_{device_id_hash:016x}"

            response = self.service.handle_sync_request(device_id, data, addr)

            if response and self.transport:
                self.transport.sendto(response, addr)

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error processing time sync datagram from {addr}: {e}")

    def error_received(self, exc: Exception) -> None:
        """Handle protocol errors."""
        logger.error(f"Time sync protocol error: {exc}")

    def connection_lost(self, exc: Optional[Exception]) -> None:
        """Handle connection lost."""
        if exc:
            logger.error(f"Time sync connection lost: {exc}")
        else:
            logger.debug("Time sync connection closed")
