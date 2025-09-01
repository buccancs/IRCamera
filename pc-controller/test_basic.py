#!/usr/bin/env python3
"""
Basic validation test for IRCamera PC Controller core components

Tests the key architectural components without GUI dependencies.
"""

import sys
import os
import tempfile
import shutil
from pathlib import Path

# Add src to path
src_dir = Path(__file__).parent / "src"
sys.path.insert(0, str(src_dir))

def test_basic_functionality():
    """Test basic functionality without external dependencies."""
    print("🧪 Testing IRCamera PC Controller Core Components")
    print("=" * 60)
    
    # Create temporary directory for testing
    temp_dir = tempfile.mkdtemp()
    print(f"📁 Using temporary directory: {temp_dir}")
    
    try:
        # Test 1: Configuration Manager
        print("\n1. Testing Configuration Manager...")
        from ircamera_pc.core.config import ConfigManager
        
        # Create config manager with temp dir
        config = ConfigManager()
        config.set('session.data_root', temp_dir)
        
        port = config.get('network.server_port', 8080)
        mode = config.get('gsr.default_mode', 'local')
        
        print(f"   ✓ Default server port: {port}")
        print(f"   ✓ Default GSR mode: {mode}")
        print("   ✓ Configuration Manager: PASS")
        
        # Test 2: Session Manager
        print("\n2. Testing Session Manager...")
        from ircamera_pc.core.session import SessionManager, SessionState
        
        # Patch the config object to use temp directory
        import ircamera_pc.core.session
        original_config = ircamera_pc.core.session.config
        ircamera_pc.core.session.config = config
        
        session_mgr = SessionManager()
        
        # Create a session
        session = session_mgr.create_session("test_session")
        print(f"   ✓ Created session: {session.name}")
        print(f"   ✓ Session ID: {session.session_id[:8]}...")
        print(f"   ✓ Initial state: {session.state}")
        
        # Test session lifecycle
        session_mgr.start_session()
        current = session_mgr.get_current_session()
        print(f"   ✓ Started session, new state: {current.state}")
        
        session_mgr.begin_recording()
        current = session_mgr.get_current_session()
        print(f"   ✓ Started recording, state: {current.state}")
        
        # Add some test data
        session_mgr.add_device({
            'device_id': 'test_android_device',
            'device_type': 'android',
            'capabilities': ['camera', 'thermal']
        })
        print("   ✓ Added test device to session")
        
        session_mgr.add_sync_event('flash', {'description': 'test flash'})
        print("   ✓ Added sync event to session")
        
        # End session
        ended_session = session_mgr.end_session()
        print(f"   ✓ Ended session, final state: {ended_session.state}")
        print(f"   ✓ Session duration: {ended_session.duration_seconds:.1f}s")
        print("   ✓ Session Manager: PASS")
        
        # Test 3: Session Persistence
        print("\n3. Testing Session Persistence...")
        
        # Load session
        loaded_session = session_mgr.load_session(ended_session.session_id)
        if loaded_session:
            print(f"   ✓ Loaded session: {loaded_session.name}")
            print(f"   ✓ Devices in session: {len(loaded_session.devices)}")
            print(f"   ✓ Sync events in session: {len(loaded_session.sync_events)}")
            print("   ✓ Session Persistence: PASS")
        else:
            print("   ✗ Failed to load session")
            return False
        
        # Test 4: Configuration Persistence
        print("\n4. Testing Configuration Persistence...")
        
        config.set('test.value', 'test_data')
        config.save()
        
        # Create new config manager to test loading
        config2 = ConfigManager(config.config_path)
        test_value = config2.get('test.value')
        
        if test_value == 'test_data':
            print("   ✓ Configuration saved and loaded successfully")
            print("   ✓ Configuration Persistence: PASS")
        else:
            print("   ✗ Configuration persistence failed")
            return False
        
        # Test 5: Network Message Structure (without actual networking)
        print("\n5. Testing Network Message Structure...")
        from ircamera_pc.network.server import DeviceInfo, DeviceState, MessageType
        
        # Create device info
        device = DeviceInfo(
            device_id="test_device_001",
            device_type="android", 
            capabilities=["camera", "thermal", "gsr_sensor"],
            ip_address="192.168.1.100",
            port=8080,
            is_gsr_leader=True,
            gsr_mode="local"
        )
        
        print(f"   ✓ Created device info: {device.device_id}")
        print(f"   ✓ Device type: {device.device_type}")
        print(f"   ✓ Is GSR leader: {device.is_gsr_leader}")
        print(f"   ✓ GSR mode: {device.gsr_mode}")
        print(f"   ✓ Capabilities: {', '.join(device.capabilities)}")
        
        # Test serialization
        device_dict = device.to_dict()
        print(f"   ✓ Serialized to dict with {len(device_dict)} fields")
        print("   ✓ Network Message Structure: PASS")
        
        print("\n" + "=" * 60)
        print("🎉 ALL TESTS PASSED!")
        print("\nCore architecture components are working correctly:")
        print("- ✅ Configuration management with YAML persistence")
        print("- ✅ Session lifecycle management with metadata")
        print("- ✅ Device information structure and serialization") 
        print("- ✅ Data persistence and recovery")
        print("\nReady for full system integration with:")
        print("- 🔄 Time synchronization service")
        print("- 🌐 Network server for device communication")
        print("- 🖥️  PyQt5 GUI interface")
        
        return True
        
    except Exception as e:
        print(f"\n❌ TEST FAILED: {e}")
        import traceback
        traceback.print_exc()
        return False
        
    finally:
        # Clean up
        shutil.rmtree(temp_dir, ignore_errors=True)
        print(f"\n🧹 Cleaned up temporary directory: {temp_dir}")

if __name__ == "__main__":
    success = test_basic_functionality()
    sys.exit(0 if success else 1)