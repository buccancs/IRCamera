"""Bluetooth device management with async discovery and connection handling."""

from __future__ import annotations

import asyncio
import logging
from dataclasses import dataclass
from typing import Any, Callable, Dict, Optional

try:
    pass

    BLUETOOTH_AVAILABLE = True
except ImportError:
    BLUETOOTH_AVAILABLE = False

try:
    from PyQt6.QtCore import QTimer

    PYQT_AVAILABLE = True
except ImportError:
    PYQT_AVAILABLE = False

from .base_manager import BaseManager

logger = logging.getLogger(__name__)


@dataclass
class BluetoothDevice:
    """Bluetooth device information."""

    address: str
    name: str
    rssi: Optional[int] = None
    connected: bool = False
    device_class: Optional[int] = None


class BluetoothManager(BaseManager):
    """Manages Bluetooth operations and device discovery."""

    def __init__(self, signal_callback: Optional[Callable] = None) -> None:
        """Initialize Bluetooth manager."""
        super().__init__("BluetoothManager")
        self._signal_callback = signal_callback
        self._devices: Dict[str, BluetoothDevice] = {}
        self._scanning = False
        self._scan_timer: Optional[QTimer] = None

        if PYQT_AVAILABLE:
            self._scan_timer = QTimer()
            self._scan_timer.timeout.connect(self._timer_scan)

    @property
    def is_available(self) -> bool:
        """Check if Bluetooth is available."""
        return BLUETOOTH_AVAILABLE

    @property
    def devices(self) -> Dict[str, BluetoothDevice]:
        """Get discovered devices."""
        return self._devices.copy()

    @property
    def is_scanning(self) -> bool:
        """Check if currently scanning."""
        return self._scanning

    def initialize(self) -> bool:
        """Initialize Bluetooth manager."""
        if not BLUETOOTH_AVAILABLE:
            self._handle_error("initialization", "Bluetooth library not available")
            return False

        self._initialized = True
        logger.info("Bluetooth manager initialized")
        return True

    def cleanup(self) -> None:
        """Cleanup Bluetooth resources."""
        self.stop_scanning()
        if self._scan_timer:
            self._scan_timer.stop()
        self._devices.clear()
        logger.info("Bluetooth manager cleaned up")

    def _emit_signal(self, signal_name: str, *args) -> None:
        """Emit signal through callback if available."""
        if self._signal_callback:
            self._signal_callback(signal_name, *args)

    def start_scanning(
        self, duration: int = 10, continuous: bool = False, interval: int = 30
    ) -> None:
        """Start scanning for Bluetooth devices."""
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
        """Stop device scanning."""
        if self._scan_timer:
            self._scan_timer.stop()

        self._scanning = False
        logger.info("Stopped Bluetooth device scanning")

    async def _scan_devices(self) -> None:
        """Scan for BLE devices."""
        try:
            logger.debug("Scanning for BLE devices...")
            devices = await BleakScanner.discover(timeout=5.0)

            discovered_count = 0
            for device in devices:
                if device.address not in self._devices or self._should_update_device(
                    device
                ):
                    bt_device = self._create_bluetooth_device(device)
                    self._devices[device.address] = bt_device
                    self._emit_signal("device_discovered", bt_device)
                    discovered_count += 1
                    logger.debug(
                        f"Discovered device: {device.name}" "({device.address})"
                    )

            self._emit_signal("scan_completed", discovered_count)
            logger.info(f"Scan completed - found {discovered_count}" "new devices")

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Error during device scan: {e}")
            self._emit_signal("error_occurred", "scan", str(e))

    def _periodic_scan(self) -> None:
        """
        Periodic scan callback.

        Initiates device scanning if scanning is currently active.
        Called automatically by the scan timer to maintain device discovery.
        """
        if self._scanning:
            asyncio.create_task(self._scan_devices())

    def _should_update_device(self, device: BLEDevice) -> bool:
        """
        Check if device information should be updated.

        Determines whether a discovered device's information has changed
        enough to warrant updating the stored device record.

        Args:
            device: BLE device to check for updates

        Returns:
            True if device should be updated, False otherwise
        """
        if device.address not in self._devices:
            self._devices[device.address] = device
            logger.info(f"Added Bluetooth device: {device.name} ({device.address})")
            self._emit_signal("device_discovered", device)

    def remove_device(self, address: str) -> None:
        """Remove device from list."""
        if address in self._devices:
            device = self._devices.pop(address)
            logger.info(f"Removed Bluetooth device: {device.name} ({address})")
            self._emit_signal("device_removed", device)

    def connect_device(self, address: str) -> bool:
        """Connect to Bluetooth device."""
        if address not in self._devices:
            self._handle_error("connection", f"Device not found: {address}")
            return False

        try:
            # Simulate connection (replace with actual Bluetooth connection)
            device = self._devices[address]
            device.connected = True
            logger.info(f"Connected to Bluetooth device: {device.name}")
            self._emit_signal("device_connected", device)
            return True

        except Exception as e:
            self._handle_error("connection", f"Failed to connect to {address}: {e}", e)
            return False

    def disconnect_device(self, address: str) -> bool:
        """Disconnect from Bluetooth device."""
        if address not in self._devices:
            self._handle_error("disconnection", f"Device not found: {address}")
            return False

        try:
            device = self._devices[address]
            device.connected = False
            logger.info(f"Disconnected from Bluetooth device: {device.name}")
            self._emit_signal("device_disconnected", device)
            return True

        except Exception as e:
            self._handle_error(
                "disconnection", f"Failed to disconnect from {address}: {e}", e
            )
            return False
            return True

        existing = self._devices[device.address]

        return abs(existing.rssi - (device.rssi or -100)) > 10 or (
            not existing.name and device.name
        )

    def _create_bluetooth_device(self, device: BLEDevice) -> BluetoothDevice:
        """
        Create BluetoothDevice from BLEDevice.

        Converts a BLEDevice from the bleak library into our internal
        BluetoothDevice representation with additional metadata.

        Args:
            device: BLE device from bleak scan results

        Returns:
            BluetoothDevice with enhanced information and IRCamera detection
        """
        # Check if this could be an IRCamera device
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

    def _is_ircamera_device(self, device_info: Dict[str, Any]) -> bool:
        """Check if device is an IRCamera device based on name or services."""
        name = device_info.get("name", "").lower()
        return "ircamera" in name or "thermal" in name
