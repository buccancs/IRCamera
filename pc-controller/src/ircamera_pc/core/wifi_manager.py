
        networks: List[Dict[str, Any]] = []
        lines = output.split("\n")
        current_network: Dict[str, Any] = {}

        for line in lines:
            line = line.strip()
            current_network = self._process_scan_line(line, current_network, networks)

        self._finalize_current_network(current_network, networks)

        # Convert dict representations back to WiFiNetwork objects
        wifi_networks = []
        for net_dict in networks:
            if isinstance(net_dict, dict) and "ssid" in net_dict:
                network = self._create_network_from_dict(net_dict)
                if network:
                    wifi_networks.append(network)

        return wifi_networks

    def _process_scan_line(
        self, line: str, current_network: Dict[str, Any], networks: List[Dict[str, Any]]
    ) -> Dict[str, Any]:

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

                ip_address = await self._get_interface_ip()
                self.network_connected.emit(ssid, ip_address or "Unknown")
                logger.info(f"Successfully connected to {ssid}")
                return True
            else:
                self.connection_failed.emit(ssid, "Connection failed")
                return False

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to connect to {ssid}: {e}")
            self.connection_failed.emit(ssid, str(e))
            return False

    async def disconnect_from_network(self) -> None:

        if self._hotspot_state in [
            HotspotState.RUNNING,
            HotspotState.STARTING,
        ]:
            logger.warning("Hotspot already running or starting")
            return True

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

        except (OSError, ValueError, RuntimeError) as e:
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

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to stop hotspot: {e}")
            self._hotspot_state = HotspotState.ERROR
            self.hotspot_state_changed.emit(self._hotspot_state, str(e))

    def get_network_info(self, ssid: str) -> Optional[WiFiNetwork]:
        """Get information about a specific network."""
        return self._networks.get(ssid)

    def _init_interfaces(self) -> None:
        """Initialize network interface information."""
        if not PSUTIL_AVAILABLE:
            logger.warning("Cannot monitor network interfaces" "- psutil not available")
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

        except (OSError, ValueError, RuntimeError) as e:
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

            if PSUTIL_AVAILABLE:
                stats = psutil.net_if_stats()
                for name, interface in self._interfaces.items():
                    if name in stats:
                        old_status = interface.is_active
                        interface.is_active = stats[name].isup
                        interface.status = "up" if interface.is_active else "down"

                        if old_status != interface.is_active:
                            self.interface_changed.emit(name, interface.is_active)

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Status update failed: {e}")

    async def _platform_connect(
        self, ssid: str, password: Optional[str], security: NetworkSecurityType
    ) -> bool:
        """Platform-specific WiFi connection implementation."""
        system = platform.system()

        if system == "Windows":
            return await self._connect_windows(ssid, password or "", security)
        elif system == "Linux":
            return await self._connect_linux(ssid, password or "", security)
        elif system == "Darwin":
            return await self._connect_macos(ssid, password or "", security)
        else:
            raise RuntimeError(f"Unsupported platform: {system}")

    async def _connect_windows(
        self, ssid: str, password: str, security: NetworkSecurityType
    ) -> bool:
        """Connect to WiFi on Windows using netsh and Windows WiFi API."""
        try:
            logger.info(f"Connecting to {ssid} on Windows")

            # Security: Use full path for netsh command
            netsh_path = "C:\\Windows\\System32\\netsh.exe"
            if not os.path.exists(netsh_path):
                raise FileNotFoundError("netsh.exe not found")

            if password and security != NetworkSecurityType.OPEN:
                profile_xml = self._create_wifi_profile_xml(ssid, password, security)

                # Write profile to temporary file
                import tempfile

                with tempfile.NamedTemporaryFile(
                    mode="w", suffix=".xml", delete=False
                ) as f:
                    f.write(profile_xml)
                    profile_path = f.name

                try:

                    result = await asyncio.create_subprocess_exec(
                        netsh_path,
                        "wlan",
                        "add",
                        "profile",
                        f"filename={profile_path}",
                        stdout=asyncio.subprocess.PIPE,
                        stderr=asyncio.subprocess.PIPE,
                    )
                    stdout, stderr = await result.communicate()

                    if result.returncode != 0:
                        logger.error(f"Failed to add WiFi profile: {stderr.decode()}")
                        return False
                finally:
                    # Clean up temporary profile file
                    try:
                        os.unlink(profile_path)
                    except OSError:
                        pass

            # Connect to the network
            result = await asyncio.create_subprocess_exec(
                netsh_path,
                "wlan",
                "connect",
                f"name={ssid}",
                stdout=asyncio.subprocess.PIPE,
                stderr=asyncio.subprocess.PIPE,
            )
            stdout, stderr = await result.communicate()

            if result.returncode == 0:
                logger.info(f"Successfully initiated connection to {ssid}")

                # Wait for connection to establish (up to 30 seconds)
                for _ in range(30):
                    await asyncio.sleep(1)
                    if await self._check_connection_status(ssid):
                        return True

                logger.error(f"Connection to {ssid} timed out")
                return False
            else:
                logger.error(f"Failed to connect to {ssid}: {stderr.decode()}")
                return False

        except Exception as e:
            logger.error(f"Windows WiFi connection failed: {e}")
            return False

    async def _connect_linux(
        self, ssid: str, password: str, security: NetworkSecurityType
    ) -> bool:
        """Connect to WiFi on Linux using NetworkManager (nmcli)."""
        try:
            logger.info(f"Connecting to {ssid} on Linux")

            # Security: Validate nmcli path
            nmcli_path = shutil.which("nmcli")
            if not nmcli_path:
                raise FileNotFoundError("nmcli not found - NetworkManager required")

            # Build connection command
            cmd = [nmcli_path, "device", "wifi", "connect", ssid]

            if password and security != NetworkSecurityType.OPEN:
                cmd.extend(["password", password])

            result = await asyncio.create_subprocess_exec(
                *cmd, stdout=asyncio.subprocess.PIPE, stderr=asyncio.subprocess.PIPE
            )
            stdout, stderr = await result.communicate()

            if result.returncode == 0:
                logger.info(f"Successfully connected to {ssid} on Linux")
                return True
            else:
                error_msg = stderr.decode().strip()
                logger.error(f"Failed to connect to {ssid}: {error_msg}")

                # Try alternative approach with connection profile
                if "already exists" in error_msg or "activation failed" in error_msg:
                    return await self._connect_linux_with_profile(
                        ssid, password, security
                    )

                return False

        except Exception as e:
            logger.error(f"Linux WiFi connection failed: {e}")
            return False

    async def _connect_macos(
        self, ssid: str, password: str, security: NetworkSecurityType
    ) -> bool:
        """Connect to WiFi on macOS using networksetup and security framework."""
        try:
            logger.info(f"Connecting to {ssid} on macOS")

            # Security: Validate networksetup path
            networksetup_path = "/usr/sbin/networksetup"
            if not os.path.exists(networksetup_path):
                raise FileNotFoundError("networksetup not found")

            result = await asyncio.create_subprocess_exec(
                networksetup_path,
                "-listallhardwareports",
                stdout=asyncio.subprocess.PIPE,
                stderr=asyncio.subprocess.PIPE,
            )
            stdout, stderr = await result.communicate()

            wifi_interface = None
            lines = stdout.decode().split("\n")
            for i, line in enumerate(lines):
                if "Wi-Fi" in line and i + 1 < len(lines):
                    device_line = lines[i + 1]
                    if device_line.startswith("Device:"):
                        wifi_interface = device_line.split(":")[1].strip()
                        break

            if not wifi_interface:
                logger.error("Could not find WiFi interface")
                return False

            # Connect to network
            if password and security != NetworkSecurityType.OPEN:
                # For secured networks, use networksetup with password
                result = await asyncio.create_subprocess_exec(
                    networksetup_path,
                    "-setairportnetwork",
                    wifi_interface,
                    ssid,
                    password,
                    stdout=asyncio.subprocess.PIPE,
                    stderr=asyncio.subprocess.PIPE,
                )
            else:
                # For open networks
                result = await asyncio.create_subprocess_exec(
                    networksetup_path,
                    "-setairportnetwork",
                    wifi_interface,
                    ssid,
                    stdout=asyncio.subprocess.PIPE,
                    stderr=asyncio.subprocess.PIPE,
                )

            stdout, stderr = await result.communicate()

            if result.returncode == 0:
                logger.info(f"Successfully connected to {ssid} on macOS")
                return True
            else:
                error_msg = stderr.decode().strip()
                logger.error(f"Failed to connect to {ssid}: {error_msg}")
                return False

        except Exception as e:
            logger.error(f"macOS WiFi connection failed: {e}")
            return False

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

            result = subprocess.run(
                ["netsh", "wlan", "start", "hostednetwork"],
                capture_output=True,
                text=True,
            )

            return result.returncode == 0

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Windows hotspot start failed: {e}")
            return False

    async def _platform_stop_hotspot(self) -> None:
        """Platform-specific hotspot stop implementation."""
        system = platform.system()

        if system == "Windows":
            try:
                subprocess.run(
                    ["netsh", "wlan", "stop", "hostednetwork"],
                    capture_output=True,
                )
            except (OSError, ValueError, RuntimeError) as e:
                logger.error(f"Failed to stop Windows hotspot: {e}")

    async def _get_interface_ip(self) -> Optional[str]:
        """Get IP address of active WiFi interface."""
        if not PSUTIL_AVAILABLE:
            return None

        try:
            for interface in self._interfaces.values():
                if interface.is_wifi and interface.is_active and interface.ip_address:
                    return interface.ip_address
        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to get interface IP: {e}")

        return None

    def _create_wifi_profile_xml(
        self, ssid: str, password: str, security: NetworkSecurityType
    ) -> str:
        """Create Windows WiFi profile XML."""
        auth_type = "WPA2PSK" if security == NetworkSecurityType.WPA2 else "WPAPSK"
        encryption = "AES" if security == NetworkSecurityType.WPA2 else "TKIP"

        return f"""<?xml version="1.0"?>
<WLANProfile xmlns="http://www.microsoft.com/networking/WLAN/profile/v1">
    <name>{ssid}</name>
    <SSIDConfig>
        <SSID>
            <hex>{ssid.encode().hex()}</hex>
            <name>{ssid}</name>
        </SSID>
    </SSIDConfig>
    <connectionType>ESS</connectionType>
    <connectionMode>auto</connectionMode>
    <MSM>
        <security>
            <authEncryption>
                <authentication>{auth_type}</authentication>
                <encryption>{encryption}</encryption>
                <useOneX>false</useOneX>
            </authEncryption>
            <sharedKey>
                <keyType>passPhrase</keyType>
                <protected>false</protected>
                <keyMaterial>{password}</keyMaterial>
            </sharedKey>
        </security>
    </MSM>
</WLANProfile>"""

    async def _check_connection_status(self, ssid: str) -> bool:
        """Check if connected to specified WiFi network."""
        try:
            netsh_path = "C:\\Windows\\System32\\netsh.exe"
            if not os.path.exists(netsh_path):
                return False

            result = await asyncio.create_subprocess_exec(
                netsh_path,
                "wlan",
                "show",
                "interfaces",
                stdout=asyncio.subprocess.PIPE,
                stderr=asyncio.subprocess.PIPE,
            )
            stdout, stderr = await result.communicate()

            if result.returncode == 0:
                output = stdout.decode()
                return (
                    f"SSID                   : {ssid}" in output
                    and "State                  : connected" in output
                )

        except Exception as e:
            logger.error(f"Failed to check connection status: {e}")

        return False

    async def _connect_linux_with_profile(
        self, ssid: str, password: str, security: NetworkSecurityType
    ) -> bool:
        """Connect to WiFi on Linux using connection profile."""
        try:
            nmcli_path = shutil.which("nmcli")
            if not nmcli_path:
                return False

            # Try to activate existing connection first
            result = await asyncio.create_subprocess_exec(
                nmcli_path,
                "connection",
                "up",
                ssid,
                stdout=asyncio.subprocess.PIPE,
                stderr=asyncio.subprocess.PIPE,
            )
            stdout, stderr = await result.communicate()

            if result.returncode == 0:
                logger.info(f"Activated existing connection to {ssid}")
                return True

            security_type = (
                "wpa-psk"
                if security in [NetworkSecurityType.WPA, NetworkSecurityType.WPA2]
                else "none"
            )

            cmd = [
                nmcli_path,
                "connection",
                "add",
                "type",
                "wifi",
                "con-name",
                ssid,
                "ssid",
                ssid,
            ]

            if password and security != NetworkSecurityType.OPEN:
                cmd.extend(
                    ["wifi-sec.key-mgmt", security_type, "wifi-sec.psk", password]
                )

            result = await asyncio.create_subprocess_exec(
                *cmd, stdout=asyncio.subprocess.PIPE, stderr=asyncio.subprocess.PIPE
            )
            stdout, stderr = await result.communicate()

            if result.returncode == 0:
                # Activate the new connection
                result = await asyncio.create_subprocess_exec(
                    nmcli_path,
                    "connection",
                    "up",
                    ssid,
                    stdout=asyncio.subprocess.PIPE,
                    stderr=asyncio.subprocess.PIPE,
                )
                stdout, stderr = await result.communicate()
                return result.returncode == 0

            return False

        except Exception as e:
            logger.error(f"Failed to connect with profile: {e}")
            return False

    async def cleanup(self) -> None:
        """Clean up WiFi manager resources."""
        self.stop_scanning()

        if self._hotspot_state == HotspotState.RUNNING:
            await self.stop_hotspot()

        self._status_timer.stop()
        logger.info("WiFi manager cleanup completed")
