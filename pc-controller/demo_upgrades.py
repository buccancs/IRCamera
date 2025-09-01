#!/usr/bin/env python
"""
IRCamera PC Controller - Protocol Demo
Demonstrates the new JSON-based communication protocol and PyQt6 integration.
"""

import sys
import json
from pathlib import Path

# Add src directory to path
sys.path.insert(0, str(Path(__file__).parent / "src"))

from ircamera_pc.network.protocol import get_protocol_manager, create_message, validate_message
from ircamera_pc.network.server import NetworkServer


def demo_protocol_features():
    """Demonstrate the new JSON-based protocol features."""
    print("🚀 IRCamera PC Controller - Protocol Demonstration")
    print("=" * 60)
    
    # Load protocol manager
    pm = get_protocol_manager()
    info = pm.get_protocol_info()
    
    print(f"📋 Protocol: {info['name']} v{info['version']}")
    print(f"📝 Description: {info['description']}")
    print(f"🔧 Message Types: {info['message_types']}")
    print()
    
    # Show transport configuration
    transport = pm.get_transport_config()
    print("🌐 Transport Configuration:")
    print(f"   Protocol: {transport.get('protocol', 'TCP')}")
    print(f"   Encoding: {transport.get('encoding', 'UTF-8')}")
    print(f"   Max Message Size: {transport.get('message_framing', {}).get('max_message_size', 0):,} bytes")
    print()
    
    # Demonstrate message creation and validation
    print("📨 Message Creation & Validation:")
    
    # Device registration
    device_msg = create_message('device_register',
                               device_id='demo_phone_001',
                               device_type='android_phone',
                               capabilities=['thermal', 'visual', 'gsr', 'audio'],
                               ip_address='192.168.1.100',
                               port=8080,
                               battery_level=85,
                               device_name='Research Phone #1')
    
    print(f"✅ Device Registration: {validate_message(device_msg, strict=False)}")
    print(f"   Device: {device_msg['device_id']}")
    print(f"   Type: {device_msg['device_type']}")
    print(f"   Capabilities: {', '.join(device_msg['capabilities'])}")
    print()
    
    # Session control
    session_msg = create_message('session_start',
                                session_id='study_2025_001',
                                session_name='Multi-Modal Data Collection',
                                recording_params={
                                    'thermal_fps': 30,
                                    'visual_fps': 60,
                                    'audio_sample_rate': 48000,
                                    'gsr_sample_rate': 128
                                })
    
    print(f"✅ Session Start: {validate_message(session_msg, strict=False)}")
    print(f"   Session: {session_msg['session_name']}")
    print(f"   ID: {session_msg['session_id']}")
    print(f"   Thermal FPS: {session_msg['recording_params']['thermal_fps']}")
    print()
    
    # Synchronization
    sync_msg = create_message('sync_flash',
                             duration_ms=200,
                             intensity=0.8,
                             color='red')
    
    print(f"✅ Sync Flash: {validate_message(sync_msg, strict=False)}")
    print(f"   Duration: {sync_msg['duration_ms']}ms")
    print(f"   Color: {sync_msg['color']}")
    print()
    
    # File transfer
    transfer_msg = create_message('file_transfer_request',
                                 device_id='demo_phone_001',
                                 session_id='study_2025_001',
                                 file_info={
                                     'filename': 'thermal_data_001.h264',
                                     'file_type': 'thermal',
                                     'file_size': 15728640,  # 15MB
                                     'checksum': 'sha256:a1b2c3d4e5f6...',
                                     'compression': 'gzip'
                                 },
                                 priority=8)
    
    print(f"✅ File Transfer Request: {validate_message(transfer_msg, strict=False)}")
    print(f"   File: {transfer_msg['file_info']['filename']}")
    print(f"   Size: {transfer_msg['file_info']['file_size']:,} bytes")
    print(f"   Type: {transfer_msg['file_info']['file_type']}")
    print()
    
    # Error handling
    error_msg = create_message('error',
                              error_code='DEVICE_BUSY',
                              error_message='Device is currently recording',
                              context={
                                  'device_id': 'demo_phone_001',
                                  'session_id': 'study_2025_001'
                              },
                              retry_after_s=30)
    
    print(f"✅ Error Message: {validate_message(error_msg, strict=False)}")
    print(f"   Code: {error_msg['error_code']}")
    print(f"   Message: {error_msg['error_message']}")
    print(f"   Retry After: {error_msg['retry_after_s']}s")
    print()


def demo_network_server():
    """Demonstrate the upgraded network server."""
    print("🌐 Network Server with Protocol Integration:")
    print("-" * 40)
    
    server = NetworkServer()
    protocol_info = server._protocol.get_protocol_info()
    
    print(f"✅ Server initialized with {protocol_info['name']} v{protocol_info['version']}")
    print(f"📍 Host: {server._host}:{server._port}")
    print(f"🔒 Max connections: {server._max_connections}")
    print(f"💾 Max message size: {server._max_message_size:,} bytes")
    print(f"🔧 Message handlers: {len(server._message_handlers)}")
    
    print("\n📥 Supported incoming message types:")
    for handler in sorted(server._message_handlers.keys()):
        print(f"   • {handler}")
    
    print("\n📤 Outgoing message examples:")
    
    # Demonstrate protocol-based message sending
    try:
        import asyncio
        
        # Session start message
        session_start = create_message('session_start',
                                      session_id='demo_session',
                                      session_name='Protocol Demo')
        print(f"   • {session_start['message_type']}: {session_start['session_name']}")
        
        # Sync flash message  
        sync_flash = create_message('sync_flash',
                                   duration_ms=100,
                                   color='white')
        print(f"   • {sync_flash['message_type']}: {sync_flash['duration_ms']}ms {sync_flash['color']}")
        
    except Exception as e:
        print(f"   ⚠️  Demo messages created (runtime: {e})")


def show_upgrade_summary():
    """Show the upgrade summary."""
    print("\n🎯 Upgrade Summary:")
    print("=" * 60)
    
    upgrades = [
        ("JSON Protocol Definition", "18 message types with schema validation"),
        ("PyQt6 Framework", "Latest GUI framework with compatibility fixes"),  
        ("Protocol Manager", "Message validation and creation system"),
        ("Network Server", "Protocol-aware message handling"),
        ("Package Versions", "Latest compatible versions (numpy 2.2.6, etc.)"),
        ("Transport Layer", "Configurable message framing and validation"),
        ("Error Handling", "Structured error messages with context"),
        ("Message Validation", "Real-time schema validation with strict mode")
    ]
    
    for feature, description in upgrades:
        print(f"✅ {feature:<25} | {description}")
    
    print("\n🔧 Technical Highlights:")
    print("• Full JSON Schema validation with jsonschema>=4.19.0")
    print("• PyQt6>=6.4.0 with compatibility layer")
    print("• Protocol versioning and extensibility")
    print("• Message framing with configurable size limits")
    print("• Structured error handling with retry logic")
    print("• Transport-agnostic message definitions")


def main():
    """Run the full demonstration."""
    try:
        demo_protocol_features()
        demo_network_server()
        show_upgrade_summary()
        
        print("\n" + "🎉" * 20)
        print("IRCamera PC Controller successfully upgraded!")
        print("Ready for Android device integration with:")
        print("• JSON-based communication protocol")  
        print("• PyQt6 modern GUI framework")
        print("• Enhanced reliability and validation")
        print("🎉" * 20)
        
    except Exception as e:
        print(f"❌ Demo failed: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)


if __name__ == "__main__":
    main()