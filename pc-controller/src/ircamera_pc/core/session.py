
"""Session management module for the PC Controller."""

import json
import uuid
from dataclasses import asdict, dataclass, field
from datetime import datetime, timezone
from enum import Enum
from pathlib import Path
from typing import Any, Callable, Dict, List, Optional, Union

try:
    from loguru import logger
except ImportError:
    from ..utils.simple_logger import logger

from .config import config


class SessionState(Enum):
    """Session state enumeration."""
    IDLE = "idle"
    ACTIVE = "active"
    RECORDING = "recording"
    PAUSED = "paused"
    COMPLETED = "completed"
    ERROR = "error"


@dataclass
class SessionMetadata:
    """Session metadata container."""
    session_id: str
    name: str
    state: str
    created_at: float
    started_at: Optional[float] = None
    ended_at: Optional[float] = None
    description: Optional[str] = None
    participants: List[str] = field(default_factory=list)
    device_configs: Dict[str, Any] = field(default_factory=dict)
    recording_parameters: Dict[str, Any] = field(default_factory=dict)
    data_files: List[str] = field(default_factory=list)
    session_notes: List[str] = field(default_factory=list)
    metadata: Dict[str, Any] = field(default_factory=dict)

    def to_dict(self) -> Dict[str, Any]:
        """Convert session metadata to dictionary."""
        return asdict(self)


class SessionManager:
    """Manages recording sessions and their lifecycle."""

    def __init__(self, data_root: Optional[Path] = None) -> None:
        """Initialize session manager."""
        self._current_session: Optional[SessionMetadata] = None
        self._session_history: List[str] = []

        if data_root is not None:
            self._data_root = Path(data_root)
        else:
            self._data_root = Path(config.get("session.data_root", "./sessions"))

        self._ensure_data_root()
        self._state_callbacks: Dict[str, List[Callable]] = {}

        logger.info(f"Session Manager initialized with data root: {self._data_root}")

    def _ensure_data_root(self) -> None:
        """Ensure data root directory exists."""
        self._data_root.mkdir(parents=True, exist_ok=True)
        logger.debug(f"Data root directory ensured: {self._data_root}")

    def create_session(self, name: Optional[str] = None, description: Optional[str] = None) -> SessionMetadata:
        """Create a new recording session."""
        if self._current_session and self._current_session.state in [
            SessionState.ACTIVE.value,
            SessionState.RECORDING.value,
        ]:
            raise ValueError("Cannot create new session: another session is active")

        # Generate session ID and name
        session_id = str(uuid.uuid4())
        if name is None:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            name = f"session_{timestamp}"

        self._current_session = SessionMetadata(
            session_id=session_id,
            name=name,
            state=SessionState.IDLE.value,
            created_at=datetime.now(timezone.utc).isoformat(),
            gsr_mode=config.get("gsr.default_mode", "local"),
        )

        session_dir = self._get_session_directory(session_id)
        session_dir.mkdir(parents=True, exist_ok=True)

        self._save_metadata()

        self._session_history.append(session_id)

        logger.info(f"Session created: {name} [{session_id}]")
        return self._current_session

    def start_session(self) -> None:

        if not self._current_session:
            raise ValueError("No active session")

        self._current_session.devices.append(
            {"added_at": datetime.now(timezone.utc).isoformat(), **device_info}
        )

        self._save_metadata()
        logger.debug(
            f"Device added to session: {device_info.get('device_id', 'unknown')}"
        )

    def add_file(self, file_info: Dict[str, Any]) -> None:

        if not self._current_session:
            raise ValueError("No active session")

        self._current_session.files.append(
            {"added_at": datetime.now(timezone.utc).isoformat(), **file_info}
        )

        self._save_metadata()
        logger.debug(f"File added to session: {file_info.get('filename', 'unknown')}")

    def add_sync_event(
        self, event_type: str, event_data: Optional[Dict[str, Any]] = None
    ) -> None:

        if not self._current_session:
            raise ValueError("No active session")

        sync_event = {
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "event_type": event_type,
            "data": event_data or {},
        }

        self._current_session.sync_events.append(sync_event)
        self._save_metadata()

        logger.info(f"Sync event added: {event_type}")

    def get_current_session(self) -> Optional[SessionMetadata]:

        if session_id is None:
            if not self._current_session:
                raise ValueError("No current session")
            session_id = self._current_session.session_id

        return self._get_session_directory(session_id)

    def _get_session_directory(self, session_id: str) -> Path:

        metadata_file = self._get_session_directory(session_id) / "metadata.json"

        try:
            if not metadata_file.exists():
                logger.warning(f"Session metadata not found: {session_id}")
                return None

            with open(metadata_file, "r", encoding="utf-8") as f:
                data = json.load(f)

            return SessionMetadata(**data)

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to load session metadata: {e}")
            return None

    def list_sessions(self) -> List[str]:

        sessions = []

        try:
            for item in self._data_root.iterdir():
                if item.is_dir() and (item / "metadata.json").exists():
                    sessions.append(item.name)

        except (OSError, ValueError, RuntimeError) as e:
            logger.error(f"Failed to list sessions: {e}")

        return sorted(sessions)
