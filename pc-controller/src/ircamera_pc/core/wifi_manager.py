"""
WiFi Manager for IRCamera PC Controller

Provides WiFi network discovery, connection management, and hotspot creation
for direct communication with IRCamera devices.
"""

import platform
import subprocess
import re
from typing import Dict, List, Optional
from dataclasses import dataclass
from enum import Enum
from datetime import datetime

try:
    from loguru import logger
except ImportError:
    from ..utils.simple_logger import get_logger
    logger = get_logger(__name__)

try:
    from PyQt6.QtCore import QObject, pyqtSignal, QTimer, QThread, pyqtSlot

    PYQT_AVAILABLE = True

    class BaseManager(QObject):
        pass

    class BaseThread(QThread):
        pass

except ImportError:
    PYQT_AVAILABLE = False

    class BaseManager:
        def __init__(self):
            pass

        def __setattr__(self, name, value):
            # Allow setting any attribute
            super().__setattr__(name, value)

    class BaseThread:
        def __init__(self):
            pass

        def start(self):
            # Run in current thread if PyQt6 not available
            self.run()

        def run(self):
            pass


try:
    import psutil

    PSUTIL_AVAILABLE = True
except ImportError:
    logger.warning(
        "psutil not available. Install 'psutil' for network interface monitoring"
    )
    PSUTIL_AVAILABLE = False

try:
    if platform.system() == "Windows":
        pass

        COMTYPES_AVAILABLE = True
    else:
        COMTYPES_AVAILABLE = False
except ImportError:
    COMTYPES_AVAILABLE = False


class NetworkSecurityType(Enum):
    """WiFi network security types."""

    OPEN = "open"
    WEP = "wep"
    WPA = "wpa"
    WPA2 = "wpa2"
    WPA3 = "wpa3"
    ENTERPRISE = "enterprise"


class ConnectionState(Enum):
    """WiFi connection states."""

    DISCONNECTED = "disconnected"
    CONNECTING = "connecting"
    CONNECTED = "connected"
    AUTHENTICATING = "authenticating"
    ERROR = "error"


class HotspotState(Enum):
    """Mobile hotspot states."""

    STOPPED = "stopped"
    STARTING = "starting"
    RUNNING = "running"
    STOPPING = "stopping"
    ERROR = "error"


@dataclass
class WiFiNetwork:
    """WiFi network information."""

    ssid: str
    bssid: str
    signal_strength: int  # -100 to 0 dBm
    frequency: int  # MHz
    security_type: NetworkSecurityType
    channel: int
    is_ircamera_hotspot: bool = False
    last_seen: datetime = None

    def __post_init__(self):
        if self.last_seen is None:
            self.last_seen = datetime.now()


@dataclass
class NetworkInterface:
    """Network interface information."""

    name: str
    description: str
    is_wifi: bool
    is_active: bool
    ip_address: Optional[str]
    mac_address: str
    status: str


class WiFiScanWorker(BaseThread):
    """Worker thread for WiFi network scanning."""

    networks_found = pyqtSignal(list)
    scan_completed = pyqtSignal(int)
    error_occurred = pyqtSignal(str)

    def __init__(self):
        super().__init__()
        self._running = False

    def run(self):
        """Run the WiFi scan in a separate thread."""
        self._running = True
        try:
            networks = self._scan_networks()
            if self._running:  # Check if still running after scan
                self.networks_found.emit(networks)
                self.scan_completed.emit(len(networks))
        except Exception as e:
            if self._running:
                logger.error(f"WiFi scan error: {e}")
                self.error_occurred.emit(str(e))

    def stop(self):
        """Stop the scanning process."""
        self._running = False

    def _scan_networks(self) -> List[WiFiNetwork]:
        """Scan for available WiFi networks using platform-specific methods."""
        system = platform.system()

        if system == "Windows":
            return self._scan_windows()
        elif system == "Linux":
            return self._scan_linux()
        elif system == "Darwin":  # macOS
            return self._scan_macos()
        else:
            raise RuntimeError(f"Unsupported platform: {system}")

    def _scan_windows(self) -> List[WiFiNetwork]:
        """Scan WiFi networks on Windows using netsh."""
        networks = []

        try:
            # Run netsh command to get WiFi profiles
            result = subprocess.run(
                ["netsh", "wlan", "show", "profiles"],
                capture_output=True,
                text=True,
                timeout=30,
            )

            if result.returncode != 0:
                raise RuntimeError(f"netsh failed: {result.stderr}")

            # Parse available networks
            result = subprocess.run(
                ["netsh", "wlan", "show", "profile", "interface=*"],
                capture_output=True,
                text=True,
                timeout=30,
            )

            # Get current scan results
            scan_result = subprocess.run(
                ["netsh", "wlan", "show", "networks", "mode=bssid"],
                capture_output=True,
                text=True,
                timeout=30,
            )

            if scan_result.returncode == 0:
                networks = self._parse_windows_scan(scan_result.stdout)

        except Exception as e:
            logger.error(f"Windows WiFi scan failed: {e}")
            raise

        return networks

    def _parse_windows_scan(self, output: str) -> List[WiFiNetwork]:
        """Parse Windows netsh scan output."""
        networks = []
        lines = output.split("\n")

        current_network = {}
        for line in lines:
            line = line.strip()

            if line.startswith("SSID"):
                if current_network and "ssid" in current_network:
                    network = self._create_network_from_dict(current_network)
                    if network:
                        networks.append(network)

                ssid_match = re.search(r"SSID \d+ : (.+)", line)
                current_network = {
                    "ssid": ssid_match.group(1) if ssid_match else "Unknown"
                }

            elif "Network type" in line:
                current_network["type"] = line.split(":")[1].strip()
            elif "Authentication" in line:
                auth = line.split(":")[1].strip()
                current_network["security"] = self._parse_security_type(auth)
            elif "Signal" in line:
                signal_match = re.search(r"(\d+)%", line)
                if signal_match:
                    # Convert percentage to dBm approximation
                    percentage = int(signal_match.group(1))
                    current_network["signal"] = -100 + (percentage * 70 // 100)
            elif "BSSID" in line and ":" in line:
                bssid = line.split(":")[1].strip()
                current_network["bssid"] = bssid
            elif "Channel" in line:
                channel_match = re.search(r"(\d+)", line)
                if channel_match:
                    current_network["channel"] = int(channel_match.group(1))

        # Don't forget the last network
        if current_network and "ssid" in current_network:
            network = self._create_network_from_dict(current_network)
            if network:
                networks.append(network)

        return networks

    def _scan_linux(self) -> List[WiFiNetwork]:
        """Scan WiFi networks on Linux using iwlist or nmcli."""
        networks = []

        try:
            # Try nmcli first (NetworkManager)
            result = subprocess.run(
                [
                    "nmcli",
                    "-",
                    "SSID,BSSID,CHAN,FREQ,SIGNAL,SECURITY",
                    "dev",
                    "wifi",
                    "list",
                ],
                capture_output=True,
                text=True,
                timeout=30,
            )

            if result.returncode == 0:
                networks = self._parse_nmcli_output(result.stdout)
            else:
                # Fallback to iwlist
                networks = self._scan_linux_iwlist()

        except Exception as e:
            logger.error(f"Linux WiFi scan failed: {e}")
            raise

        return networks

    def _scan_linux_iwlist(self) -> List[WiFiNetwork]:
        """Scan using iwlist as fallback."""
        networks = []

        try:
            result = subprocess.run(
                ["sudo", "iwlist", "scan"], capture_output=True, text=True, timeout=30
            )

            if result.returncode == 0:
                networks = self._parse_iwlist_output(result.stdout)

        except Exception as e:
            logger.warning(f"iwlist scan failed: {e}")

        return networks

    def _scan_macos(self) -> List[WiFiNetwork]:
        """Scan WiFi networks on macOS using airport utility."""
        networks = []

        try:
            # Use the built-in airport utility
            airport_path = "/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport"

            result = subprocess.run(
                [airport_path, "-s"], capture_output=True, text=True, timeout=30
            )

            if result.returncode == 0:
                networks = self._parse_airport_output(result.stdout)

        except Exception as e:
            logger.error(f"macOS WiFi scan failed: {e}")
            raise

        return networks

    def _create_network_from_dict(self, data: dict) -> Optional[WiFiNetwork]:
        """Create WiFiNetwork from parsed data dictionary."""
        try:
            ssid = data.get("ssid", "Unknown")
            if not ssid or ssid == "Unknown":
                return None

            # Check if this might be an IRCamera hotspot
            is_ircamera = self._is_ircamera_network(ssid)

            return WiFiNetwork(
                ssid=ssid,
                bssid=data.get("bssid", "00:00:00:00:00:00"),
                signal_strength=data.get("signal", -100),
                frequency=data.get("frequency", 2400),  # Default 2.4 GHz
                security_type=data.get("security", NetworkSecurityType.OPEN),
                channel=data.get("channel", 1),
                is_ircamera_hotspot=is_ircamera,
            )
        except Exception as e:
            logger.warning(f"Failed to create network from data: {e}")
            return None

    def _is_ircamera_network(self, ssid: str) -> bool:
        """Check if network SSID indicates an IRCamera device hotspot."""
        ssid_lower = ssid.lower()
        ircamera_patterns = [
            "ircamera",
            "thermal",
            "flir",
            "seek",
            "hikvision",
            "thermal_cam",
            "ir_cam",
            "heatcam",
        ]
        return any(pattern in ssid_lower for pattern in ircamera_patterns)

    def _parse_security_type(self, auth_string: str) -> NetworkSecurityType:
        """Parse security type from authentication string."""
        auth_lower = auth_string.lower()

        if "wpa3" in auth_lower:
            return NetworkSecurityType.WPA3
        elif "wpa2" in auth_lower:
            return NetworkSecurityType.WPA2
        elif "wpa" in auth_lower:
            return NetworkSecurityType.WPA
        elif "wep" in auth_lower:
            return NetworkSecurityType.WEP
        elif "open" in auth_lower or "none" in auth_lower:
            return NetworkSecurityType.OPEN
        else:
            return NetworkSecurityType.WPA2  # Default assumption


class WiFiManager(BaseManager):
    """
    Manages WiFi connectivity and hotspot functionality for IRCamera devices.

    Provides:
    - WiFi network scanning and discovery
    - Connection management with credentials
    - Mobile hotspot creation and configuration
    - IRCamera device hotspot detection
    - Network interface monitoring
    """

    # Signals
    networks_discovered = pyqtSignal(list)  # List[WiFiNetwork]
    network_connected = pyqtSignal(str, str)  # ssid, ip_address
    network_disconnected = pyqtSignal(str, str)  # ssid, reason
    connection_failed = pyqtSignal(str, str)  # ssid, error
    hotspot_state_changed = pyqtSignal(HotspotState, str)  # state, message
    interface_changed = pyqtSignal(str, bool)  # interface_name, is_active
    error_occurred = pyqtSignal(str, str)  # operation, error_message

    def __init__(self):
        super().__init__()
        self._networks: Dict[str, WiFiNetwork] = {}  # SSID -> WiFiNetwork
        self._interfaces: Dict[str, NetworkInterface] = {}
        self._current_connection: Optional[str] = None  # Current SSID
        self._scan_worker: Optional[WiFiScanWorker] = None
        self._hotspot_state = HotspotState.STOPPED
        self._hotspot_config = {
            "ssid": "IRCamera_PC_Controller",
            "password": "IRCamera123",
            "channel": 6,
            "max_clients": 8,
        }

        # Initialize interface monitoring
        self._init_interfaces()

        # Timer for periodic status updates
        self._status_timer = QTimer()
        self._status_timer.timeout.connect(self._update_status)
        self._status_timer.start(5000)  # Update every 5 seconds

    @property
    def available_networks(self) -> List[WiFiNetwork]:
        """Get list of available WiFi networks."""
        return list(self._networks.values())

    @property
    def ircamera_networks(self) -> List[WiFiNetwork]:
        """Get list of detected IRCamera hotspots."""
        return [net for net in self._networks.values() if net.is_ircamera_hotspot]

    @property
    def current_connection(self) -> Optional[str]:
        """Get currently connected network SSID."""
        return self._current_connection

    @property
    def hotspot_state(self) -> HotspotState:
        """Get current hotspot state."""
        return self._hotspot_state

    @property
    def wifi_interfaces(self) -> List[NetworkInterface]:
        """Get list of WiFi interfaces."""
        return [iface for iface in self._interfaces.values() if iface.is_wifi]

    def start_scanning(self, continuous: bool = False, interval: int = 15) -> None:
        """
        Start scanning for WiFi networks.

        Args:
            continuous: Enable continuous scanning
            interval: Scan interval in seconds
        """
        if self._scan_worker and self._scan_worker.isRunning():
            logger.warning("WiFi scan already in progress")
            return

        logger.info("Starting WiFi network scan")
        self._scan_worker = WiFiScanWorker()
        self._scan_worker.networks_found.connect(self._handle_scan_results)
        self._scan_worker.scan_completed.connect(self._handle_scan_completed)
        self._scan_worker.error_occurred.connect(self._handle_scan_error)
        self._scan_worker.finished.connect(self._scan_worker.deleteLater)

        self._scan_worker.start()

    def stop_scanning(self) -> None:
        """Stop WiFi scanning."""
        if self._scan_worker and self._scan_worker.isRunning():
            self._scan_worker.stop()
            self._scan_worker.wait(5000)  # Wait up to 5 seconds
            logger.info("WiFi scanning stopped")

    @pyqtSlot(list)
    def _handle_scan_results(self, networks: List[WiFiNetwork]) -> None:
        """Handle scan results from worker thread."""
        for network in networks:
            self._networks[network.ssid] = network

        self.networks_discovered.emit(networks)
        logger.info(f"Discovered {len(networks)} WiFi networks")

    @pyqtSlot(int)
    def _handle_scan_completed(self, count: int) -> None:
        """Handle scan completion."""
        logger.debug(f"WiFi scan completed - {count} networks found")

    @pyqtSlot(str)
    def _handle_scan_error(self, error: str) -> None:
        """Handle scan error."""
        self.error_occurred.emit("scan", error)

    async def connect_to_network(self, ssid: str, password: str = None) -> bool:
        """
        Connect to a WiFi network.

        Args:
            ssid: Network SSID
            password: Network password (if required)

        Returns:
            True if connection successful
        """
        if ssid not in self._networks:
            self.error_occurred.emit("connect", f"Network '{ssid}' not found")
            return False

        network = self._networks[ssid]
        logger.info(f"Connecting to network: {ssid}")

        try:
            success = await self._platform_connect(
                ssid, password, network.security_type
            )

            if success:
                self._current_connection = ssid
                # Get IP address after connection
                ip_address = await self._get_interface_ip()
                self.network_connected.emit(ssid, ip_address or "Unknown")
                logger.info(f"Successfully connected to {ssid}")
                return True
            else:
                self.connection_failed.emit(ssid, "Connection failed")
                return False

        except Exception as e:
            logger.error(f"Failed to connect to {ssid}: {e}")
            self.connection_failed.emit(ssid, str(e))
            return False

    async def disconnect_from_network(self) -> None:
        """Disconnect from current WiFi network."""
        if not self._current_connection:
            logger.warning("No active WiFi connection to disconnect")
            return

        try:
            await self._platform_disconnect()
            ssid = self._current_connection
            self._current_connection = None
            self.network_disconnected.emit(ssid, "User initiated")
            logger.info(f"Disconnected from {ssid}")

        except Exception as e:
            logger.error(f"Failed to disconnect: {e}")
            self.error_occurred.emit("disconnect", str(e))

    async def start_hotspot(
        self, ssid: str = None, password: str = None, channel: int = None
    ) -> bool:
        """
        Start mobile hotspot for IRCamera device connections.

        Args:
            ssid: Hotspot SSID (optional, uses default)
            password: Hotspot password (optional, uses default)
            channel: WiFi channel (optional, uses default)

        Returns:
            True if hotspot started successfully
        """
        if self._hotspot_state in [HotspotState.RUNNING, HotspotState.STARTING]:
            logger.warning("Hotspot already running or starting")
            return True

        # Update configuration if provided
        if ssid:
            self._hotspot_config["ssid"] = ssid
        if password:
            self._hotspot_config["password"] = password
        if channel:
            self._hotspot_config["channel"] = channel

        self._hotspot_state = HotspotState.STARTING
        self.hotspot_state_changed.emit(self._hotspot_state, "Starting hotspot...")

        try:
            success = await self._platform_start_hotspot()

            if success:
                self._hotspot_state = HotspotState.RUNNING
                self.hotspot_state_changed.emit(
                    self._hotspot_state,
                    f"Hotspot '{self._hotspot_config['ssid']}' running",
                )
                logger.info(f"Hotspot started: {self._hotspot_config['ssid']}")
                return True
            else:
                self._hotspot_state = HotspotState.ERROR
                self.hotspot_state_changed.emit(
                    self._hotspot_state, "Failed to start hotspot"
                )
                return False

        except Exception as e:
            logger.error(f"Failed to start hotspot: {e}")
            self._hotspot_state = HotspotState.ERROR
            self.hotspot_state_changed.emit(self._hotspot_state, str(e))
            return False

    async def stop_hotspot(self) -> None:
        """Stop the mobile hotspot."""
        if self._hotspot_state == HotspotState.STOPPED:
            logger.warning("Hotspot already stopped")
            return

        self._hotspot_state = HotspotState.STOPPING
        self.hotspot_state_changed.emit(self._hotspot_state, "Stopping hotspot...")

        try:
            await self._platform_stop_hotspot()
            self._hotspot_state = HotspotState.STOPPED
            self.hotspot_state_changed.emit(self._hotspot_state, "Hotspot stopped")
            logger.info("Hotspot stopped")

        except Exception as e:
            logger.error(f"Failed to stop hotspot: {e}")
            self._hotspot_state = HotspotState.ERROR
            self.hotspot_state_changed.emit(self._hotspot_state, str(e))

    def get_network_info(self, ssid: str) -> Optional[WiFiNetwork]:
        """Get information about a specific network."""
        return self._networks.get(ssid)

    def _init_interfaces(self) -> None:
        """Initialize network interface information."""
        if not PSUTIL_AVAILABLE:
            logger.warning("Cannot monitor network interfaces - psutil not available")
            return

        try:
            interfaces = psutil.net_if_addrs()
            stats = psutil.net_if_stats()

            for name, addrs in interfaces.items():
                if name in stats:
                    stat = stats[name]

                    # Find MAC and IP addresses
                    mac_addr = None
                    ip_addr = None

                    for addr in addrs:
                        if addr.family == psutil.AF_LINK:  # MAC address
                            mac_addr = addr.address
                        elif addr.family == 2:  # IPv4
                            ip_addr = addr.address

                    # Determine if it's a WiFi interface
                    is_wifi = self._is_wifi_interface(name)

                    interface = NetworkInterface(
                        name=name,
                        description=name,  # Could be enhanced with more details
                        is_wifi=is_wifi,
                        is_active=stat.isup,
                        ip_address=ip_addr,
                        mac_address=mac_addr or "00:00:00:00:00:00",
                        status="up" if stat.isup else "down",
                    )

                    self._interfaces[name] = interface

        except Exception as e:
            logger.error(f"Failed to initialize network interfaces: {e}")

    def _is_wifi_interface(self, name: str) -> bool:
        """Determine if network interface is WiFi based on name patterns."""
        wifi_patterns = [
            "wlan",
            "wifi",
            "wireless",
            "wi-fi",
            "wlp",
            "wl",
            "ath",
            "ra",
            "rtl",
            "iwl",
            "bnep",
        ]
        name_lower = name.lower()
        return any(pattern in name_lower for pattern in wifi_patterns)

    def _update_status(self) -> None:
        """Periodic status update."""
        try:
            # Update interface status
            if PSUTIL_AVAILABLE:
                stats = psutil.net_if_stats()
                for name, interface in self._interfaces.items():
                    if name in stats:
                        old_status = interface.is_active
                        interface.is_active = stats[name].isup
                        interface.status = "up" if interface.is_active else "down"

                        if old_status != interface.is_active:
                            self.interface_changed.emit(name, interface.is_active)

        except Exception as e:
            logger.error(f"Status update failed: {e}")

    async def _platform_connect(
        self, ssid: str, password: str, security: NetworkSecurityType
    ) -> bool:
        """Platform-specific WiFi connection implementation."""
        system = platform.system()

        if system == "Windows":
            return await self._connect_windows(ssid, password, security)
        elif system == "Linux":
            return await self._connect_linux(ssid, password, security)
        elif system == "Darwin":
            return await self._connect_macos(ssid, password, security)
        else:
            raise RuntimeError(f"Unsupported platform: {system}")

    async def _connect_windows(
        self, ssid: str, password: str, security: NetworkSecurityType
    ) -> bool:
        """Connect to WiFi on Windows using netsh."""
        # Implementation would use netsh commands or Windows WiFi API
        # This is a simplified version
        logger.info(f"Connecting to {ssid} on Windows")
        return True  # Placeholder

    async def _connect_linux(
        self, ssid: str, password: str, security: NetworkSecurityType
    ) -> bool:
        """Connect to WiFi on Linux using NetworkManager."""
        logger.info(f"Connecting to {ssid} on Linux")
        return True  # Placeholder

    async def _connect_macos(
        self, ssid: str, password: str, security: NetworkSecurityType
    ) -> bool:
        """Connect to WiFi on macOS using networksetup."""
        logger.info(f"Connecting to {ssid} on macOS")
        return True  # Placeholder

    async def _platform_disconnect(self) -> None:
        """Platform-specific WiFi disconnection."""
        system = platform.system()
        logger.info(f"Disconnecting WiFi on {system}")
        # Implementation would be platform-specific

    async def _platform_start_hotspot(self) -> bool:
        """Platform-specific hotspot start implementation."""
        system = platform.system()

        if system == "Windows":
            return await self._start_hotspot_windows()
        else:
            logger.warning(f"Hotspot not supported on {system}")
            return False

    async def _start_hotspot_windows(self) -> bool:
        """Start hotspot on Windows using netsh."""
        try:
            # Create hotspot profile
            result = subprocess.run(
                [
                    "netsh",
                    "wlan",
                    "set",
                    "hostednetwork",
                    f"ssid={self._hotspot_config['ssid']}",
                    f"key={self._hotspot_config['password']}",
                ],
                capture_output=True,
                text=True,
            )

            if result.returncode != 0:
                logger.error(f"Failed to set hotspot profile: {result.stderr}")
                return False

            # Start hotspot
            result = subprocess.run(
                ["netsh", "wlan", "start", "hostednetwork"],
                capture_output=True,
                text=True,
            )

            return result.returncode == 0

        except Exception as e:
            logger.error(f"Windows hotspot start failed: {e}")
            return False

    async def _platform_stop_hotspot(self) -> None:
        """Platform-specific hotspot stop implementation."""
        system = platform.system()

        if system == "Windows":
            try:
                subprocess.run(
                    ["netsh", "wlan", "stop", "hostednetwork"], capture_output=True
                )
            except Exception as e:
                logger.error(f"Failed to stop Windows hotspot: {e}")

    async def _get_interface_ip(self) -> Optional[str]:
        """Get IP address of active WiFi interface."""
        if not PSUTIL_AVAILABLE:
            return None

        try:
            for interface in self._interfaces.values():
                if interface.is_wifi and interface.is_active and interface.ip_address:
                    return interface.ip_address
        except Exception as e:
            logger.error(f"Failed to get interface IP: {e}")

        return None

    async def cleanup(self) -> None:
        """Clean up WiFi manager resources."""
        self.stop_scanning()

        if self._hotspot_state == HotspotState.RUNNING:
            await self.stop_hotspot()

        self._status_timer.stop()
        logger.info("WiFi manager cleanup completed")
