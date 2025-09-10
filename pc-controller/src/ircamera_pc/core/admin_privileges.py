
        if self.is_elevated:
            return ElevationResult.ALREADY_ELEVATED

        if self._elevation_requested:
            logger.warning("Elevation already requested")
            return ElevationResult.FAILED

        logger.info(f"Requesting privilege elevation: {reason}")
        self._emit_signal("elevation_requested", reason)
        self._elevation_requested = True

        try:
            result = self._perform_elevation(reason)
            self._emit_signal(
                "elevation_completed", result, self._get_result_message(result)
            )

            if result == ElevationResult.SUCCESS:
                self._check_current_privileges()
                self._check_system_permissions()

            return result

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Elevation request failed: {e}")
            result = ElevationResult.FAILED
            self._emit_signal("elevation_completed", result, str(e))
            return result
        finally:
            self._elevation_requested = False

    def verify_operation_permissions(self, operation: str) -> bool:

        operation_lower = operation.lower()

        permission_map = {
            "bluetooth": self._permissions.bluetooth_control,
            "wifi": self._permissions.network_config,
            "network": self._permissions.network_config,
            "service": self._permissions.service_management,
            "registry": self._permissions.registry_access,
            "hardware": self._permissions.hardware_access,
            "firewall": self._permissions.firewall_control,
        }

        if operation_lower in permission_map:
            has_permission = permission_map[operation_lower]
            if not has_permission:
                self._emit_signal(
                    "permission_denied", operation, "Insufficient privileges"
                )
            return has_permission

        logger.warning(f"Unknown operation permission check: {operation}")
        return False

    def run_as_admin(self, command: str, arguments: Optional[List[Any]] = None) -> bool:

        if not self.is_elevated:
            logger.error("Cannot run admin command without elevation")
            return False

        try:
            system = platform.system()

            if system == "Windows":
                return self._run_windows_admin_command(command, arguments or [])
            elif system in ["Linux", "Darwin"]:
                return self._run_unix_admin_command(command, arguments or [])
            else:
                logger.error(f"Unsupported platform: {system}")
                return False

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to run admin command: {e}")
            return False

    def check_service_status(self, service_name: str) -> Optional[str]:

        if not self.verify_operation_permissions("service"):
            return None

        try:
            system = platform.system()

            if system == "Windows":
                return self._check_windows_service(service_name)
            elif system == "Linux":
                return self._check_linux_service(service_name)
            elif system == "Darwin":
                return self._check_macos_service(service_name)
            else:
                return None

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to check service status: {e}")
            return None

    def manage_firewall_rule(self, rule_name: str, action: str, **kwargs) -> bool:

        if not self.verify_operation_permissions("firewall"):
            return False

        if platform.system() != "Windows":
            logger.warning("Firewall management only supported on Windows")
            return False

        try:
            return self._manage_windows_firewall_rule(rule_name, action, **kwargs)

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Firewall rule management failed: {e}")
            return False

    def _check_current_privileges(self) -> None:
        """Check and update current privilege level."""
        system = platform.system()

        try:
            if system == "Windows":
                self._current_privilege = self._check_windows_privileges()
            elif system in ["Linux", "Darwin"]:
                self._current_privilege = self._check_unix_privileges()
            else:
                self._current_privilege = PrivilegeLevel.UNKNOWN

            logger.info(f"Current privilege level: {self._current_privilege.value}")
            self._emit_signal("privilege_changed", self._current_privilege)

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to check privileges: {e}")
            self._current_privilege = PrivilegeLevel.UNKNOWN

    def _check_windows_privileges(self) -> PrivilegeLevel:
        """Check privilege level on Windows."""
        try:

            if ctypes.windll.shell32.IsUserAnAdmin():