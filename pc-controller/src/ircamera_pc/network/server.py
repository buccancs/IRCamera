
        results = {}

        devices_to_target = target_devices or list(self._clients.keys())

        for device_id in devices_to_target:
            if device_id in self._clients:
                try:
                    await self._send_message(self._clients[device_id], command)
                    results[device_id] = True
                    logger.debug(
                        f"Command sent to {device_id}: " "{command.get('message_type')}"
                    )
                except (OSError, ValueError, RuntimeError) as e:
                    logger.error(f"Failed to send command to {device_id}: {e}")
                    results[device_id] = False
            else:
                results[device_id] = False

        return results

    async def start_recording_session(
        self, session_id: str, session_name: Optional[str] = None
    ) -> Dict[str, bool]:

        try:
            # Find device by IP address
            target_device = None
            for device in self._devices.values():
                if device.ip_address == host:
                    target_device = device
                    break

            if not target_device:
                logger.warning(f"No device found for {host}:{port}")
                return False

            # Send message to device
            writer = self._clients.get(target_device.device_id)
            if writer:
                await self._send_message(writer, message)
            else:
                logger.warning(
                    f"No active connection for device {target_device.device_id}"
                )
            return True

        except Exception as e:
            logger.error(f"Failed to send message to {host}:{port}: {e}")
            return False

    async def _on_device_discovered(self, event_type: str, device) -> None:

        device = self._devices.get(device_id)
        if not device:
            raise ValueError(f"Device {device_id} not found")

        return await self._messaging_service.send_message(
            target_host=device.ip_address,
            target_port=device.port or self._port,
            message_type=message_type,
            content=content,
            priority=priority,
            timeout_seconds=timeout_seconds,
        )

    @property
    def is_running(self) -> bool:
        """Check if server is running."""
        return self._is_running

    def _calculate_network_latency(self, device_id: str) -> float:
        """Calculate network latency for a device."""
        # Simple latency estimation based on heartbeat timing
        device = self._devices.get(device_id)
        if device and hasattr(device, "last_heartbeat"):
            current_time = datetime.now()
            if device.last_heartbeat:
                try:
                    # Parse last_heartbeat string to datetime
                    last_heartbeat = datetime.fromisoformat(
                        device.last_heartbeat.replace("Z", "+00:00")
                    )

                    # Estimate round-trip time based on heartbeat response
                    latency_ms = (
                        current_time - last_heartbeat
                    ).total_seconds() * 500  # Rough estimate
                    return float(min(latency_ms, 1000.0))  # Cap at 1 second
                except (ValueError, TypeError):
                    logger.warning(
                        f"Invalid heartbeat timestamp for device {device_id}"
                    )
        return 50.0  # Default estimate

    def _calculate_data_hash(self, data_point: Dict[str, Any]) -> str:
        """Calculate integrity hash for data verification."""
        import hashlib

        hash_data = (
            f"{data_point.get('timestamp_ns', 0)}"
            f"{data_point.get('gsr_raw', 0)}"
            f"{data_point.get('ppg_raw', 0)}"
        )

        return hashlib.md5(hash_data.encode()).hexdigest()[:8]

    def _update_realtime_gsr_visualization(
        self, device_id: str, data_points: List[Dict[str, Any]]
    ) -> None:
        """Update real-time GSR visualization if available."""
        try:
            # This would interface with the PyQtGraph plotting widgets
            # For now, just log the data summary
            if data_points:
                latest_point = data_points[-1]
                gsr_value = latest_point.get("gsr_microsiemens", 0)
                logger.debug(f"Real-time GSR from {device_id}: {gsr_value:.4f} microS")

                # In a full implementation, this would:
                # 1. Send data to GUI plotting thread
                # 2. Update real-time charts
                # 3. Trigger alarms if values exceed thresholds
                # 4. Update device status indicators

        except Exception as e:
            logger.debug(f"Real-time visualization update failed: {e}")

    def _buffer_gsr_data(
        self, device_id: str, data_points: List[Dict[str, Any]]
    ) -> None:
        """Fallback method to buffer GSR data when aggregator is unavailable."""
        if not hasattr(self, "_gsr_data_buffer"):
            self._gsr_data_buffer: Dict[str, List[Dict[str, Any]]] = {}

        if device_id not in self._gsr_data_buffer:
            self._gsr_data_buffer[device_id] = []

        timestamped_points = []
        for point in data_points:
            enhanced_point = point.copy()
            enhanced_point["reception_timestamp_ns"] = time.time_ns()
            timestamped_points.append(enhanced_point)

        self._gsr_data_buffer[device_id].extend(timestamped_points)

        # Limit buffer size to prevent memory issues
        max_buffer_size = 10000  # Keep last 10k points per device
        if len(self._gsr_data_buffer[device_id]) > max_buffer_size:
            self._gsr_data_buffer[device_id] = self._gsr_data_buffer[device_id][
                -max_buffer_size:
            ]

        logger.debug(
            f"Buffered {len(data_points)} GSR points from {device_id}, "
            f"buffer size: {len(self._gsr_data_buffer[device_id])}"
        )
