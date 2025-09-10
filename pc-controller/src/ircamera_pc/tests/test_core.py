"""
Tests for IRCamera PC Controller

Basic test suite for core functionality validation.
"""

import shutil
import tempfile
from datetime import datetime
from unittest.mock import Mock, patch

import pytest

from ..core.config import ConfigManager
from ..core.session import SessionManager, SessionState
from ..core.timesync import TimeSyncService
from ..network.server import DeviceInfo, NetworkServer

class TestConfigManager:
    """Tests for ConfigManager."""

    def test_config_loading_with_defaults(self):
        """Test config loading with default values."""

        config_manager = ConfigManager("/nonexistent/config.yaml")

        # Should fall back to defaults
        assert config_manager.get("network.server_port") == 8080
        assert config_manager.get("gsr.default_mode") == "local"

    def test_config_get_set(self):
        """Test config get/set operations."""
        config_manager = ConfigManager("/nonexistent/config.yaml")

        # Test dot notation access
        assert config_manager.get("network.server_port") == 8080

        # Test setting values
        config_manager.set("test.value", "test")
        assert config_manager.get("test.value") == "test"

        # Test default values
        assert config_manager.get("nonexistent.key", "default") == "default"

class TestSessionManager:
    """Tests for SessionManager."""

    def setup_method(self):
        """Set up test environment."""
        self.temp_dir = tempfile.mkdtemp()

        # Mock config to use temp directory
        with patch("ircamera_pc.core.session.config") as mock_config:
            mock_config.get.return_value = self.temp_dir
            self.session_manager = SessionManager()

    def teardown_method(self):
        """Clean up test environment."""
        shutil.rmtree(self.temp_dir, ignore_errors=True)

    def test_create_session(self):
        """Test session creation."""
        session = self.session_manager.create_session("test_session")

        assert session.name == "test_session"
        assert session.state == SessionState.IDLE.value
        assert session.session_id is not None
        assert session.created_at is not None

    def test_session_lifecycle(self):
        """Test complete session lifecycle."""

        session = self.session_manager.create_session("test_session")
        assert session.state == SessionState.IDLE.value

        self.session_manager.start_session()
        current = self.session_manager.get_current_session()
        assert current is not None
        assert current.state == SessionState.ACTIVE.value
        assert current.started_at is not None

        self.session_manager.begin_recording()
        current = self.session_manager.get_current_session()
        assert current is not None
        assert current.state == SessionState.RECORDING.value

        ended_session = self.session_manager.end_session()
        assert ended_session.state == SessionState.COMPLETED.value
        assert ended_session.ended_at is not None
        assert ended_session.duration_seconds is not None

    def test_session_metadata_persistence(self):
        """Test session metadata persistence."""

        session = self.session_manager.create_session("test_session")
        session_id = session.session_id

        self.session_manager.add_device(
            {"device_id": "test_device", "device_type": "android"}
        )

        self.session_manager.add_sync_event("test_event", {"data": "test"})

        with patch("ircamera_pc.core.session.config") as mock_config:
            mock_config.get.return_value = self.temp_dir
            new_manager = SessionManager()
            loaded_session = new_manager.load_session(session_id)

        assert loaded_session is not None
        assert loaded_session.name == "test_session"
        assert len(loaded_session.devices) == 1
        assert len(loaded_session.sync_events) == 1

    def test_single_session_constraint(self):
        """Test that only one session can be active at a time."""

        self.session_manager.create_session("session1")
        self.session_manager.start_session()

        # Try to create second session while first is active
        with pytest.raises(ValueError, match="another session is active"):
            self.session_manager.create_session("session2")

class TestTimeSyncService:
    """Tests for TimeSyncService."""

    def setup_method(self):
        """Set up test environment."""
        self.time_sync_service = TimeSyncService()

    @pytest.mark.asyncio
    async def test_service_lifecycle(self):
        """Test time sync service start/stop."""
        assert not self.time_sync_service.is_running

        await self.time_sync_service.start(
            host="localhost", port=0
        )  # Use any available port
        assert self.time_sync_service.is_running

        await self.time_sync_service.stop()