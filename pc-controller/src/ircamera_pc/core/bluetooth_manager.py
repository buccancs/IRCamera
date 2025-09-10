
        if not self.is_available:
            self._emit_signal("error_occurred", "scan", "Bluetooth not available")
            return

        if self._scanning:
            logger.warning("Already scanning for devices")
            return

        self._scanning = True
        logger.info("Starting Bluetooth device scan")

        asyncio.create_task(self._scan_devices())

        if continuous and self._scan_timer:
            self._scan_timer.start(interval * 1000)  # Convert to milliseconds

    def stop_scanning(self) -> None:

        if device.address not in self._devices:
            return True

        existing = self._devices[device.address]

        return abs(existing.rssi - (device.rssi or -100)) > 10 or (
            not existing.name and device.name
        )

    def _create_bluetooth_device(self, device: BLEDevice) -> BluetoothDevice:

        is_ircamera = self._is_ircamera_device(device)

        return BluetoothDevice(
            address=device.address,
            name=device.name or "Unknown Device",
            device_type=BluetoothDeviceType.BLE,
            rssi=device.rssi or -100,
            services=[],  # Services discovered during connection
            last_seen=datetime.now(),
            is_ircamera=is_ircamera,
        )

    def _is_ircamera_device(self, device: BLEDevice) -> bool:

        if not self.is_available:
            self._emit_signal("error_occurred", "connect", "Bluetooth not available")
            return False

        if address not in self._devices:
            self._emit_signal(
                "error_occurred", "connect", f"Device {address} not found"
            )
            return False

        device = self._devices[address]
        device.connection_state = ConnectionState.CONNECTING

        try:
            logger.info(f"Connecting to device {device.name} ({address})")

            client = BleakClient(address)
            await client.connect()

            # Discover services
            services = await client.get_services()
            device.services = [str(service.uuid) for service in services]

            if self.IRCAMERA_SERVICE_UUID in device.services:
                device.is_ircamera = True
                logger.info(f"IRCamera service detected on {device.name}")

            self._connections[address] = client
            device.connection_state = ConnectionState.CONNECTED

            self._emit_signal("device_connected", address, device.name)
            logger.info(f"Successfully connected to {device.name}")

            if device.is_ircamera:
                await self._setup_ircamera_notifications(client)

            return True

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to connect to {address}: {e}")
            device.connection_state = ConnectionState.ERROR
            self._emit_signal("error_occurred", "connect", str(e))
            return False

    async def disconnect_device(self, address: str) -> None:

        if address not in self._connections:
            self._emit_signal(
                "error_occurred", "send", f"Device {address} not connected"
            )
            return False

        if address not in self._devices or not self._devices[address].is_ircamera:
            self._emit_signal(
                "error_occurred",
                "send",
                f"Device {address}" "is not an IRCamera",
            )
            return False

        try:
            client = self._connections[address]
            await client.write_gatt_char(self.IRCAMERA_DATA_CHARACTERISTIC, data)
            logger.debug(f"Sent {len(data)} bytes to {address}")
            return True

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error sending data to {address}: {e}")
            self._emit_signal("error_occurred", "send", str(e))
            return False

    async def _setup_ircamera_notifications(self, client: BleakClient) -> None:
        """Set up notifications for IRCamera data characteristic."""
        try:
            await client.start_notify(
                self.IRCAMERA_DATA_CHARACTERISTIC,
                self._handle_ircamera_notification,
            )
            logger.debug("IRCamera notifications enabled")

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to enable IRCamera notifications: {e}")

    def _handle_ircamera_notification(
        self, sender: BleakGATTCharacteristic, data: bytearray
    ) -> None:
        """Handle incoming data from IRCamera device."""
        try:
            address = sender.service.client.address
            self._emit_signal("data_received", address, bytes(data))
            logger.debug(f"Received {len(data)} bytes from {address}")

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error handling notification: {e}")

    def get_device_info(self, address: str) -> Optional[BluetoothDevice]:
        """Get information about a specific device."""
        return self._devices.get(address)

    def clear_devices(self) -> None:
        """Clear the list of discovered devices."""
        # Don't clear connected devices
        connected_addresses = set(self._connections.keys())
        self._devices = {
            addr: device
            for addr, device in self._devices.items()
            if addr in connected_addresses
        }
        logger.info("Cleared discovered devices list")

    async def cleanup(self) -> None:
        """Clean up all connections and stop scanning."""
        self.stop_scanning()

        # Disconnect all devices
        for address in list(self._connections.keys()):
            await self.disconnect_device(address)

        logger.info("Bluetooth manager cleanup completed")
