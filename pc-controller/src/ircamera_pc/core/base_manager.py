

    @property
    def name(self) -> str:

        old_value = self._state.get(key)
        self._state[key] = value

        if old_value != value and PYQT_AVAILABLE and hasattr(self, "status_changed"):
            self.status_changed.emit(key, {key: value})

    def _handle_error(
        self,
        error_type: str,
        message: str,
        exception: Optional[Exception] = None,
    ) -> None:

        self._last_error = message
        error_details = f"{error_type}: {message}"

        if exception:
            self._logger.error(f"{error_details} - {str(exception)}", exc_info=True)
        else:
            self._logger.error(error_details)

        if PYQT_AVAILABLE and hasattr(self, "error_occurred"):
            self.error_occurred.emit(error_type, message)

    def _emit_operation_completed(
        self, operation: str, success: bool, message: str = ""
    ) -> None:

        if PYQT_AVAILABLE and hasattr(self, "operation_completed"):
            self.operation_completed.emit(operation, success, message)

    async def health_check(self) -> Dict[str, Any]:

        return {
            "name": self._name,
            "initialized": self._is_initialized,
            "last_error": self._last_error,
            "state_keys": list(self._state.keys()),
            "timestamp": time.time(),
        }
