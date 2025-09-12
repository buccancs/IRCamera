import asyncio
import socket
from datetime import datetime
from typing import TYPE_CHECKING, Dict, List, Optional

if TYPE_CHECKING:
    pass

try:
    from zeroconf import ServiceInfo, Zeroconf
    from zeroconf.asyncio import AsyncServiceBrowser, AsyncZeroconf

    ZEROCONF_AVAILABLE = True
except ImportError:
    # Graceful fallback implementation without zeroconf
    ServiceInfo = None
    Zeroconf = None
    AsyncServiceBrowser = None
    AsyncZeroconf = None
    ZEROCONF_AVAILABLE = False

try:
    from loguru import logger
except ImportError:
    try:
        from ..utils.simple_logger import logger
    except ImportError:
        # Enterprise fallback logger for testing environments
        class EnterpriseLogger:
            def info(self, msg):
                print(f"INFO: {msg}")

            def warning(self, msg):
                print(f"WARNING: {msg}")

            def error(self, msg):
                print(f"ERROR: {msg}")

        logger = EnterpriseLogger()

    async def start_discovery(self):
        """Start device discovery service."""
        if not self._check_zeroconf_available():
            logger.warning("Zeroconf not available, using fallback discovery")
            return await self._start_fallback_discovery()

        try:
            logger.info("Starting mDNS discovery service...")

            self.zeroconf = AsyncZeroconf()

            # Register our PC controller service
            await self._register_pc_controller_service()

            await self._start_service_browser()

            self.is_running = True
            logger.info("mDNS discovery service started successfully")
            return True

        except Exception as e:
            logger.error(f"Failed to start discovery service: {e}")
            await self.stop_discovery()
            return False

    async def stop_discovery(self):
        """Stop the discovery service."""
        if not self.is_running:
            return

        logger.info("Stopping discovery service...")

        try:
            # Unregister services
            if self.zeroconf and self.registered_services:
                for service in self.registered_services:
                    await self.zeroconf.async_unregister_service(service)
                self.registered_services.clear()

            if self.service_browser:
                await self.service_browser.async_cancel()
                self.service_browser = None

            if self.zeroconf:
                await self.zeroconf.async_close()
                self.zeroconf = None

            self.is_running = False
            logger.info("Discovery service stopped")

        except Exception as e:
            logger.error(f"Error stopping discovery service: {e}")

    async def get_discovered_devices(self) -> List[DiscoveredDevice]:
        """Get list of all discovered devices."""
        return list(self.discovered_devices.values())

    async def get_devices_by_type(
        self, device_type: DeviceType
    ) -> List[DiscoveredDevice]:
        """Get discovered devices of a specific type."""
        return [
            device
            for device in self.discovered_devices.values()
            if device.device_type == device_type
        ]

    async def refresh_discovery(self):
        """Refresh device discovery by restarting the browser."""
        if self.is_running and self.service_browser:
            await self.service_browser.async_cancel()
            await self._start_service_browser()

    def _check_zeroconf_available(self) -> bool:
        """Check if zeroconf dependencies are available."""
        return all([ServiceInfo, Zeroconf, AsyncServiceBrowser, AsyncZeroconf])

    async def _register_pc_controller_service(self):
        """Register this PC as a discoverable controller service."""
        try:
            service_name = f"IRCamera-PC-{self.hostname}"
            port = config.get("network.discovery_port", 8081)

            properties = {
                "device_type": DeviceType.PC_CONTROLLER.value,
                "hostname": self.hostname,
                "version": config.get("version", "1.0.0"),
                "capabilities": "session_control,time_sync,file_transfer",
                "secure": "true",
            }

            # Convert properties to bytes
            properties_bytes = {
                k: str(v).encode("utf-8") for k, v in properties.items()
            }

            service_info = ServiceInfo(
                self.SERVICE_TYPE_PC_CONTROLLER,
                f"{service_name}.{self.SERVICE_TYPE_PC_CONTROLLER}",
                addresses=[socket.inet_aton(self.local_ip)],
                port=port,
                properties=properties_bytes,
                server=f"{self.hostname}.local.",
            )

            if self.zeroconf is not None:
                await self.zeroconf.async_register_service(service_info)
                self.registered_services.append(service_info)
            else:
                logger.error("Zeroconf not available for service registration")

            logger.info(
                f"Registered PC controller service: {service_name} at "
                f"{self.local_ip}:{port}"
            )

        except Exception as e:
            logger.error(f"Failed to register PC controller service: {e}")

    async def _start_service_browser(self):
        """Start browsing for Android and thermal camera services."""
        try:
            service_types = [
                self.SERVICE_TYPE_THERMAL_CAMERA,
                self.SERVICE_TYPE_ANDROID_NODE,
            ]

            handlers = []
            for service_type in service_types:
                handler = ServiceBrowserHandler(self, service_type)
                handlers.append(handler)

            if self.zeroconf is not None:
                self.service_browser = AsyncServiceBrowser(
                    self.zeroconf.zeroconf, service_types, handlers=handlers
                )
            else:
                logger.error("Zeroconf not available for service browsing")

            logger.debug(f"Started browsing for service types: {service_types}")

        except Exception as e:
            logger.error(f"Failed to start service browser: {e}")

    async def _start_fallback_discovery(self) -> bool:
        """Start fallback discovery using subnet scanning."""
        logger.info("Starting fallback subnet discovery...")

        # This would implement subnet scanning as a fallback
        # For now, just log that it would be implemented
        logger.warning(
            "Fallback discovery not fully implemented - install zeroconf "
            "for full functionality"
        )

        self.is_running = True
        return True

    def _get_local_ip(self) -> str:
        """Get the local IP address of this machine."""
        try:
            # Connect to a dummy address to determine local IP
            with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
                s.connect(("8.8.8.8", 80))
                return str(s.getsockname()[0])
        except Exception:
            return "127.0.0.1"

    def _get_explicit_device_type(
        self, properties: Dict[str, bytes]
    ) -> Optional[DeviceType]:
        """Get device type from explicit device_type property."""
        if "device_type" in properties:
            try:
                device_type_str = properties["device_type"].decode("utf-8")
                return DeviceType(device_type_str)
            except ValueError:
                pass
        return None

    def _infer_thermal_camera_type(self, properties: Dict[str, bytes]) -> DeviceType:
        """Infer specific thermal camera type from model property."""
        if "model" in properties:
            model = properties["model"].decode("utf-8").upper()
            if "TS004" in model:
                return DeviceType.THERMAL_CAMERA_TS004
            elif "TC007" in model:
                return DeviceType.THERMAL_CAMERA_TC007
        return DeviceType.THERMAL_CAMERA_TS004  # Default

    def _infer_from_service_type(
        self, service_type: str, properties: Dict[str, bytes]
    ) -> DeviceType:
        """Infer device type from service type and properties."""
        if self.SERVICE_TYPE_PC_CONTROLLER in service_type:
            return DeviceType.PC_CONTROLLER
        elif self.SERVICE_TYPE_THERMAL_CAMERA in service_type:
            return self._infer_thermal_camera_type(properties)
        elif self.SERVICE_TYPE_ANDROID_NODE in service_type:
            return DeviceType.ANDROID_SENSOR_NODE
        return DeviceType.UNKNOWN

    def _determine_device_type(
        self, service_type: str, properties: Dict[str, bytes]
    ) -> DeviceType:
        """Determine device type from service information."""
        try:

            explicit_type = self._get_explicit_device_type(properties)
            if explicit_type:
                return explicit_type

            # Infer from service type and name
            return self._infer_from_service_type(service_type, properties)

        except Exception as e:
            logger.warning(f"Failed to determine device type: {e}")
            return DeviceType.UNKNOWN

    async def _on_device_discovered(self, service_info: ServiceInfo):
        """Handle a newly discovered device."""
        try:
            # Extract device information
            ip_address = socket.inet_ntoa(service_info.addresses[0])
            port = service_info.port
            service_name = service_info.name
            service_type = service_info.type

            # Convert properties from bytes to strings
            properties = {}
            if service_info.properties:
                properties = {
                    k.decode("utf-8"): v.decode("utf-8")
                    for k, v in service_info.properties.items()
                    if isinstance(k, bytes) and isinstance(v, bytes)
                }

            device_type = self._determine_device_type(
                service_type, service_info.properties or {}
            )

            device = DiscoveredDevice(
                service_name=service_name,
                service_type=service_type,
                ip_address=ip_address,
                port=port,
                device_type=device_type,
                attributes=properties,
                discovered_at=datetime.now(),
                last_seen=datetime.now(),
            )

            # Store device
            device_key = f"{ip_address}:{port}"
            self.discovered_devices[device_key] = device

            logger.info(
                f"Discovered device: {service_name} ({device_type.value}) "
                f"at {ip_address}:{port}"
            )

            # Notify listeners
            for callback in self.discovery_listeners:
                try:
                    if asyncio.iscoroutinefunction(callback):
                        await callback("discovered", device)
                    else:
                        callback("discovered", device)
                except Exception as e:
                    logger.error(f"Discovery listener error: {e}")

        except Exception as e:
            logger.error(f"Error processing discovered device: {e}")

    async def _on_device_lost(self, service_name: str):
        """Handle a device that is no longer available."""
        try:
            # Find and remove the device
            device_to_remove = None
            key_to_remove = None

            for key, device in self.discovered_devices.items():
                if device.service_name == service_name:
                    device_to_remove = device
                    key_to_remove = key
                    break

            if device_to_remove and key_to_remove:
                del self.discovered_devices[key_to_remove]
                logger.info(f"Lost device: {service_name}")

                # Notify listeners
                for callback in self.discovery_listeners:
                    try:
                        if asyncio.iscoroutinefunction(callback):
                            await callback("lost", device_to_remove)
                        else:
                            callback("lost", device_to_remove)
                    except Exception as e:
                        logger.error(f"Discovery listener error: {e}")

        except Exception as e:
            logger.error(f"Error processing lost device: {e}")


class ServiceBrowserHandler:
    """Handler for service browser events."""

    def __init__(self, discovery_service: NetworkDiscoveryService, service_type: str):
        self.discovery_service = discovery_service
        self.service_type = service_type

    def add_service(self, zc: Zeroconf, type_: str, name: str):
        """Called when a service is discovered."""
        asyncio.create_task(self._add_service_async(zc, type_, name))

    def remove_service(self, zc: Zeroconf, type_: str, name: str):
        """Called when a service is removed."""
        asyncio.create_task(self.discovery_service._on_device_lost(name))

    def update_service(self, zc: Zeroconf, type_: str, name: str):
        """Called when a service is updated."""
        # Treat updates as new discoveries
        asyncio.create_task(self._add_service_async(zc, type_, name))

    async def _add_service_async(self, zc: Zeroconf, type_: str, name: str):
        """Async handler for service addition."""
        try:
            service_info = zc.get_service_info(type_, name)
            if service_info:
                await self.discovery_service._on_device_discovered(service_info)
        except Exception as e:
            logger.error(f"Error in service addition handler: {e}")
