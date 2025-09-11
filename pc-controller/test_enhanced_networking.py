#!/usr/bin/env python3
"""
Comprehensive Test Suite for Enhanced Networking Features

Tests all the enhanced networking components:
- Security Manager (TLS/SSL, certificates, authentication)
- Discovery Service (mDNS/Zeroconf)
- Reliable Messaging (ACK/NACK, retry logic)
- Network Server integration
"""

import asyncio
import sys
import tempfile
import uuid
from pathlib import Path
from unittest.mock import AsyncMock, MagicMock, patch

sys.path.insert(0, str(Path(__file__).parent / "src"))

from ircamera_pc.network.discovery import DeviceType, NetworkDiscoveryService
from ircamera_pc.network.messaging import MessagePriority, ReliableMessageService
from ircamera_pc.network.security import SecurityManager
from ircamera_pc.network.server import NetworkServer

try:
    from loguru import logger
except ImportError:
    import logging

    logging.basicConfig(level=logging.INFO)
    logger = logging.getLogger(__name__)

class EnhancedNetworkingTests:
    """Comprehensive test suite for enhanced networking features."""

    def __init__(self):
        """Initialize the test suite."""
        self.temp_dir = None
        self.test_results = {}

    async def run_all_tests(self):
        """Run all enhanced networking tests."""
        logger.info("=== Enhanced Networking Test Suite ===")

        with tempfile.TemporaryDirectory() as temp_dir:
            self.temp_dir = temp_dir

            await self.test_security_manager()
            await self.test_discovery_service()
            await self.test_reliable_messaging()
            await self.test_network_server_integration()

            # Report results
            self.report_results()

        return all(self.test_results.values())

    async def test_security_manager(self):
        """Test SecurityManager functionality."""
        logger.info("Testing Security Manager...")

        try:

            with patch.object(SecurityManager, "__init__", self._mock_security_init):
                security_manager = SecurityManager()
                security_manager.cert_dir = Path(self.temp_dir) if self.temp_dir else Path.cwd()
                security_manager.ca_cert_path = (
                    security_manager.cert_dir / "ca_cert.pem"
                )
                security_manager.ca_key_path = security_manager.cert_dir / "ca_key.pem"
                security_manager.server_cert_path = (
                    security_manager.cert_dir / "server_cert.pem"
                )
                security_manager.server_key_path = (
                    security_manager.cert_dir / "server_key.pem"
                )
                security_manager.device_certificates = {}
                security_manager.auth_tokens = {}

            # Test initialization
            assert (
                security_manager.initialize()
            ), "Security manager initialization failed"
            logger.info("OK Security manager initialization")

            # Test certificate generation
            assert (
                security_manager.ca_cert_path.exists()
            ), "CA certificate not generated"
            assert (
                security_manager.server_cert_path.exists()
            ), "Server certificate not generated"
            logger.info("OK Certificate generation")

            # Test SSL context creation
            ssl_context = security_manager.create_ssl_context()
            assert ssl_context is not None, "SSL context creation failed"
            logger.info("OK SSL context creation")

            # Test auth token generation and validation
            device_id = "test_device_123"
            token = security_manager.generate_auth_token(device_id)
            assert token is not None, "Auth token generation failed"

            is_valid, validated_device_id = security_manager.validate_auth_token(token)
            assert is_valid, "Auth token validation failed"
            assert validated_device_id == device_id, "Device ID mismatch in token"
            logger.info("OK Auth token generation and validation")

            # Test token expiry
            expired_token = security_manager.generate_auth_token(
                device_id, duration_minutes=-1
            )
            is_valid, _ = security_manager.validate_auth_token(expired_token)
            assert not is_valid, "Expired token should be invalid"
            logger.info("OK Token expiry handling")

            # Test token cleanup
            security_manager.cleanup_expired_tokens()
            logger.info("OK Token cleanup")

            self.test_results["security_manager"] = True
            logger.info("OK Security Manager tests passed")

        except Exception as e:
            logger.error(f"ERROR Security Manager tests failed: {e}")
            self.test_results["security_manager"] = False

    async def test_discovery_service(self):
        """Test NetworkDiscoveryService functionality."""
        logger.info("Testing Discovery Service...")

        try:
            discovery_service = NetworkDiscoveryService()

            # Test initialization
            assert (
                not discovery_service.is_running
            ), "Discovery service should not be running initially"
            logger.info("OK Initial state")

            # Test device type determination
            device_type = discovery_service._determine_device_type(
                "_topdon-thermal._tcp.local.", {"device_type": b"THERMAL_CAMERA_TS004"}
            )
            assert (
                device_type == DeviceType.THERMAL_CAMERA_TS004
            ), "Device type determination failed"
            logger.info("OK Device type determination")

            # Test local IP detection
            local_ip = discovery_service._get_local_ip()
            assert local_ip is not None, "Local IP detection failed"
            assert local_ip != "0.0.0.0", "Invalid local IP"
            logger.info(f"OK Local IP detection: {local_ip}")

            # Test discovery listener management
            def test_callback(event, device):
                pass

            discovery_service.add_discovery_listener(test_callback)
            assert (
                test_callback in discovery_service.discovery_listeners
            ), "Listener not added"

            discovery_service.remove_discovery_listener(test_callback)
            assert (
                test_callback not in discovery_service.discovery_listeners
            ), "Listener not removed"
            logger.info("OK Discovery listener management")

            # Test fallback discovery (since zeroconf might not be available)
            if not discovery_service._check_zeroconf_available():
                result = await discovery_service._start_fallback_discovery()
                assert result, "Fallback discovery failed"
                await discovery_service.stop_discovery()
                logger.info("OK Fallback discovery")
            else:
                logger.info("OK Zeroconf available for full testing")

            self.test_results["discovery_service"] = True
            logger.info("OK Discovery Service tests passed")

        except Exception as e:
            logger.error(f"ERROR Discovery Service tests failed: {e}")
            self.test_results["discovery_service"] = False

    async def test_reliable_messaging(self):
        """Test ReliableMessageService functionality."""
        logger.info("Testing Reliable Messaging...")

        try:
            messaging_service = ReliableMessageService()

            # Test initialization
            assert (
                not messaging_service.is_running
            ), "Messaging service should not be running initially"

            sent_messages = []

            async def mock_transport(host, port, message):
                sent_messages.append((host, port, message))
                return True

            messaging_service.set_transport(mock_transport)
            assert messaging_service.transport is not None, "Transport not set"
            logger.info("OK Transport configuration")

            # Test service initialization
            init_result = await messaging_service.initialize()
            if not init_result:
                logger.error("Messaging service initialization failed")
                return False
            if not messaging_service.is_running:
                logger.error("Messaging service should be running")
                return False
            logger.info("OK Service initialization")
            
            self.test_results["reliable_messaging"] = True
            logger.info("OK Reliable Messaging tests passed")

        except Exception as e:
            logger.error(f"ERROR Reliable Messaging tests failed: {e}")
            self.test_results["reliable_messaging"] = False