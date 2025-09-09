"""
Consolidated Base Manager for IRCamera PC Controller

Provides a single, unified base class for all manager components
to eliminate code duplication and ensure consistent behavior.
"""

import logging
from abc import ABC, abstractmethod
from typing import Any, Dict, Optional, Union

try:
    from abc import ABCMeta

    from PyQt6.QtCore import QObject as QtQObject
    from PyQt6.QtCore import pyqtSignal

    PYQT_AVAILABLE = True

    # Create a hybrid metaclass that combines QObject and ABC metaclasses
    class QObjectMeta(type(QtQObject), ABCMeta):
        """Metaclass to resolve conflict between QObject and ABC"""

        pass

except ImportError:
    PYQT_AVAILABLE = False

    # Mock classes and functions for when PyQt6 is not available
    class QtQObject:
        """Mock QObject when PyQt6 is not available"""

        def __init__(self, parent: Optional[Any] = None) -> None:
            self.parent = parent

    class QObjectMeta(ABCMeta):
        """Fallback metaclass when PyQt6 is not available"""

        pass

    def pyqtSignal(*args: Any, **kwargs: Any) -> Any:
        """Mock pyqtSignal decorator"""

        def decorator(func: Any) -> Any:
            return func

        return decorator


class BaseManager(QtQObject, ABC, metaclass=QObjectMeta):
    """
    Unified base manager class for all IRCamera PC Controller components.

    Provides common functionality including:
    - Logging setup
    - State management
    - Error handling patterns
    - Optional PyQt6 signal support
    """

    if PYQT_AVAILABLE:
        # Real PyQt6 signals
        status_changed = pyqtSignal(str, dict)  # status_name, details
        error_occurred = pyqtSignal(str, str)  # error_type, message
        operation_completed = pyqtSignal(str, bool, str)  # operation, success, message
    else:
        # Mock signals when PyQt6 is not available
        status_changed = None
        error_occurred = None
        operation_completed = None

    def __init__(
        self, name: str, parent: Optional[Union[QtQObject, Any]] = None
    ) -> None:
        if PYQT_AVAILABLE:
            super().__init__(parent)
        else:
            # Initialize without PyQt6 when not available
            pass
        self._setup_base_manager(name)

    def _setup_base_manager(self, name: str) -> None:
        """Common setup for both PyQt and non-PyQt versions"""
        self._name = name
        self._logger = logging.getLogger(f"ircamera_pc.{name.lower()}")
        self._is_initialized = False
        self._state: Dict[str, Any] = {}
        self._last_error: Optional[str] = None

    @property
    def name(self) -> str:
        """Get manager name."""
        return self._name

    @property
    def logger(self) -> logging.Logger:
        """Get logger instance."""
        return self._logger

    @property
    def is_initialized(self) -> bool:
        """Check if manager is initialized."""
        return self._is_initialized

    @property
    def state(self) -> Dict[str, Any]:
        """Get current state dictionary."""
        return self._state.copy()

    @property
    def last_error(self) -> Optional[str]:
        """Get last error message."""
        return self._last_error

    @abstractmethod
    async def initialize(self) -> bool:
        """
        Initialize the manager.

        Returns:
            bool: True if initialization successful, False otherwise
        """
        pass

    @abstractmethod
    async def cleanup(self) -> None:
        """Clean up manager resources."""
        pass

    def set_state(self, key: str, value: Any) -> None:
        """Set a state value."""
        self._state[key] = value
        if PYQT_AVAILABLE and self.status_changed is not None:
            self.status_changed.emit("state_updated", {key: value})

    def get_state(self, key: str, default: Any = None) -> Any:
        """Get a state value."""
        return self._state.get(key, default)

    def log_error(self, error_type: str, message: str, exc_info: bool = False) -> None:
        """Log an error and emit error signal."""
        self._last_error = message
        self.logger.error(f"{error_type}: {message}", exc_info=exc_info)
        if PYQT_AVAILABLE and self.error_occurred is not None:
            self.error_occurred.emit(error_type, message)

    def log_info(self, message: str) -> None:
        """Log an info message."""
        self.logger.info(message)

    def log_debug(self, message: str) -> None:
        """Log a debug message."""
        self.logger.debug(message)


class AsyncContextManager(BaseManager):
    """
    Base manager with async context manager support.
    """

    async def __aenter__(self) -> "AsyncContextManager":
        """Async context manager entry."""
        if await self.initialize():
            return self
        else:
            raise RuntimeError(f"Failed to initialize {self._name}")

    async def __aexit__(self, exc_type: Any, exc_val: Any, exc_tb: Any) -> None:
        """Async context manager exit."""
        await self.cleanup()


class SingletonManager(BaseManager):
    """
    Base manager with singleton pattern support.
    """

    _instances: Dict[str, "SingletonManager"] = {}

    def __new__(cls, name: str, parent: Optional[Any] = None) -> "SingletonManager":
        """Ensure singleton instance per name."""
        if name not in cls._instances:
            instance = super().__new__(cls)
            cls._instances[name] = instance
        return cls._instances[name]

    @classmethod
    def get_instance(cls, name: str) -> Optional["SingletonManager"]:
        """Get existing singleton instance by name."""
        return cls._instances.get(name)

    @classmethod
    def clear_instances(cls) -> None:
        """Clear all singleton instances (mainly for testing)."""
        cls._instances.clear()


__all__ = ["BaseManager", "AsyncContextManager", "SingletonManager", "PYQT_AVAILABLE"]
