"""Base manager class for common functionality."""

from __future__ import annotations

import logging
from abc import ABC, abstractmethod
from typing import Any, Dict, Optional

try:
    from PyQt6.QtCore import QObject, pyqtSignal

    PYQT_AVAILABLE = True
    BaseClass = QObject
except ImportError:
    PYQT_AVAILABLE = False
    BaseClass = object  # type: ignore

logger = logging.getLogger(__name__)


class BaseManager(BaseClass, ABC):
    """Base manager class with common functionality."""

    if PYQT_AVAILABLE:
        status_changed = pyqtSignal(str, dict)
        error_occurred = pyqtSignal(str, str)

    def __init__(self, name: str) -> None:
        """Initialize base manager."""
        super().__init__()
        self._name = name
        self._state: Dict[str, Any] = {}
        self._last_error: Optional[str] = None
        self._initialized = False

    @property
    def name(self) -> str:
        """Get manager name."""
        return self._name

    @property
    def is_initialized(self) -> bool:
        """Check if manager is initialized."""
        return self._initialized

    @property
    def last_error(self) -> Optional[str]:
        """Get last error message."""
        return self._last_error

    @property
    def state(self) -> Dict[str, Any]:
        """Get current state dictionary."""
        return self._state.copy()

    def get_state_value(self, key: str, default: Any = None) -> Any:
        """Get specific state value."""
        return self._state.get(key, default)

    def set_state_value(self, key: str, value: Any) -> None:
        """Set state value and emit signal if changed."""
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
        """Handle and log errors."""
        self._last_error = message
        error_details = f"{error_type}: {message}"

        if exception:
            error_details = f"{error_details} - {exception}"
            logger.error(error_details, exc_info=True)
        else:
            logger.error(error_details)

        if PYQT_AVAILABLE and hasattr(self, "error_occurred"):
            self.error_occurred.emit(error_type, message)

    @abstractmethod
    def initialize(self) -> bool:
        """Initialize the manager."""

    @abstractmethod
    def cleanup(self) -> None:
        """Cleanup manager resources."""

    def __repr__(self) -> str:
        """String representation."""
        return f"{self.__class__.__name__}(name='{self._name}')"

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
