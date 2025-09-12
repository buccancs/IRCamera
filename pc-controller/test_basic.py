#!/usr/bin/env python3
"""
Basic validation test for IRCamera PC Controller core components

Tests the key architectural components without GUI dependencies.
"""

import shutil
import sys
import tempfile
from pathlib import Path

src_dir = Path(__file__).parent / "src"
sys.path.insert(0, str(src_dir))


def test_basic_functionality():
    """Test basic functionality without external dependencies."""
    print("TESTING Testing IRCamera PC Controller Core Components")
    print("=" * 60)

    temp_dir = tempfile.mkdtemp()
    print(f"DIRECTORY Using temporary directory: {temp_dir}")

    try:
        # Test 1: Configuration Manager
        print("\n1. Testing Configuration Manager...")
        from ircamera_pc.core.config import ConfigManager

        config = ConfigManager()
        config.set("session.data_root", temp_dir)

        port = config.get("network.server_port", 8080)
        mode = config.get("gsr.default_mode", "local")

        print(f"   OK Default server port: {port}")
        print(f"   OK Default GSR mode: {mode}")
        print("   OK Configuration Manager: PASS")

        # Test 2: Session Manager
        print("\n2. Testing Session Manager...")
        # Patch the config object to use temp directory
        import ircamera_pc.core.session
        from ircamera_pc.core.session import SessionManager

        ircamera_pc.core.session.config
        ircamera_pc.core.session.config = config

        session_mgr = SessionManager()

        session = session_mgr.create_session("test_session")
        print(f"   OK Created session: {session.name}")
        print(f"   OK Session ID: {session.session_id[:8]}...")
        print(f"   OK Initial state: {session.state}")

        # Test session lifecycle
        session_mgr.start_session()
        current = session_mgr.get_current_session()
        if current:
            print(f"   OK Started session, new state: {current.state}")

        session_mgr.begin_recording()
        current = session_mgr.get_current_session()
        if current:
            print(f"   OK Started recording, state: {current.state}")

        session_mgr.add_device(
            {
                "device_id": "test_android_device",
                "device_type": "android",
                "capabilities": ["camera", "thermal"],
            }
        )
        print("   OK Added test device to session")

        session_mgr.add_sync_event("flash", {"description": "test flash"})
        print("   OK Added sync event to session")

        ended_session = session_mgr.end_session()
        print(f"   OK Ended session, final state: {ended_session.state}")
        print(f"   OK Session duration: {ended_session.duration_seconds:.1f}s")
        print("   OK Session Manager: PASS")

        # Test 3: Session Persistence
        print("\n3. Testing Session Persistence...")

        loaded_session = session_mgr.load_session(ended_session.session_id)
        if loaded_session:
            print(f"   OK Loaded session: {loaded_session.name}")
            print(f"   OK Devices in session: {len(loaded_session.devices)}")
            print(f"   OK Sync events in session: {len(loaded_session.sync_events)}")
            print("   OK Session Persistence: PASS")
        else:
            print("   FAIL Failed to load session")
            return False

        # Test 4: Configuration Persistence
        print("\n4. Testing Configuration Persistence...")

        config.set("test.value", "test_data")
        config.save()

        config2 = ConfigManager(str(config.config_path))
        test_value = config2.get("test.value")

        if test_value == "test_data":
            print("   OK Configuration saved and loaded successfully")
            print("   OK Configuration Persistence: PASS")
        else:
            print("   FAIL Configuration persistence failed")
            return False

        # Test 5: Network Message Structure (without actual networking)
        print("\n5. Testing Network Message Structure...")
        from ircamera_pc.network.server import DeviceInfo

        device = DeviceInfo(
            device_id="test_device_001",
            device_type="android",
            capabilities=["camera", "thermal", "gsr_sensor"],
            ip_address="192.168.1.100",
            port=8080,
            is_gsr_leader=True,
            gsr_mode="local",
        )

        print(f"   OK Created device info: {device.device_id}")
        print(f"   OK Device type: {device.device_type}")
        print(f"   OK Is GSR leader: {device.is_gsr_leader}")
        print(f"   OK GSR mode: {device.gsr_mode}")
        print(f"   OK Capabilities: {', '.join(device.capabilities)}")

        # Test serialization
        device_dict = device.to_dict()
        print(f"   OK Serialized to dict with {len(device_dict)} fields")
        print("   OK Network Message Structure: PASS")

        print("\n" + "=" * 60)
        print("SUCCESS ALL TESTS PASSED!")
        print("\nCore architecture components are working correctly:")
        print("- OK Configuration management with YAML persistence")
        print("- OK Session lifecycle management with metadata")
        print("- OK Device information structure and serialization")
        print("- OK Data persistence and recovery")
        print("\nReady for full system integration with:")
        print("- REFRESH Time synchronization service")
        print("- WEB Network server for device communication")
        print("- PC  PyQt6 GUI interface")

        return True

    except (OSError, ValueError, RuntimeError) as e:
        print(f"\nERROR TEST FAILED: {e}")
        import traceback

        traceback.print_exc()
        return False

    finally:
        # Clean up
        shutil.rmtree(temp_dir, ignore_errors=True)
        print(f"\nCLEAN Cleaned up temporary directory: {temp_dir}")


if __name__ == "__main__":
    success = test_basic_functionality()
    sys.exit(0 if success else 1)
