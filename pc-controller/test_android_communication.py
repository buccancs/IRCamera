#!/usr/bin/env python3
"""
Test script to validate Android-to-PC communication for IRCamera project.

This script tests the bidirectional communication between PC Controller and Android app.
"""

import socket
import json
import time
import threading
from typing import Dict, Any, Optional

class AndroidCommunicationTester:
    """Test bidirectional communication with Android device"""
    
    def __init__(self, android_ip: str = "192.168.1.100", port: int = 8080):
        self.android_ip = android_ip
        self.port = port
        self.socket: Optional[socket.socket] = None
        self.connected = False
        self.running = False
        
    def connect_to_android(self) -> bool:
        """Connect to Android device"""
        try:
            print(f"🔌 Connecting to Android at {self.android_ip}:{self.port}")
            
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.settimeout(10.0)
            self.socket.connect((self.android_ip, self.port))
            
            self.connected = True
            print("✅ Connected to Android device!")
            return True
            
        except Exception as e:
            print(f"❌ Connection failed: {e}")
            return False
    
    def send_message(self, message: Dict[str, Any]) -> bool:
        """Send JSON message to Android"""
        if not self.connected or not self.socket:
            print("❌ Not connected")
            return False
        
        try:
            message_json = json.dumps(message)
            message_bytes = message_json.encode('utf-8')
            
            # Send 4-byte length header + message
            self.socket.send(len(message_bytes).to_bytes(4, byteorder='big'))
            self.socket.send(message_bytes)
            
            print(f"📤 Sent: {message_json}")
            return True
            
        except Exception as e:
            print(f"❌ Send error: {e}")
            return False
    
    def receive_message(self, timeout: float = 5.0) -> Dict[str, Any]:
        """Receive JSON message from Android"""
        if not self.connected or not self.socket:
            return {}
        
        try:
            self.socket.settimeout(timeout)
            
            # Read 4-byte length header
            length_bytes = self.socket.recv(4)
            if len(length_bytes) != 4:
                return {}
            
            message_length = int.from_bytes(length_bytes, byteorder='big')
            
            # Read full message
            message_bytes = b''
            while len(message_bytes) < message_length:
                chunk = self.socket.recv(message_length - len(message_bytes))
                if not chunk:
                    break
                message_bytes += chunk
            
            if len(message_bytes) == message_length:
                message_json = message_bytes.decode('utf-8')
                message = json.loads(message_json)
                print(f"📥 Received: {message_json}")
                return message
            
        except socket.timeout:
            print("⏱️ Receive timeout")
        except Exception as e:
            print(f"❌ Receive error: {e}")
        
        return {}
    
    def test_basic_communication(self):
        """Test basic request-response"""
        print("\n🧪 Testing basic communication...")
        
        # Test status request
        self.send_message({
            "type": "get_status",
            "timestamp": time.time()
        })
        
        response = self.receive_message()
        if response and response.get("type") == "status":
            print("✅ Status request successful")
            print(f"   Recording: {response.get('recording', 'unknown')}")
            print(f"   Sensors: {response.get('sensors_initialized', 'unknown')}")
            return True
        else:
            print("❌ Status request failed")
            return False
    
    def test_time_sync(self):
        """Test time synchronization"""
        print("\n🕐 Testing time synchronization...")
        
        client_time = int(time.time() * 1000)  # milliseconds
        self.send_message({
            "type": "time_sync",
            "client_timestamp": client_time
        })
        
        response = self.receive_message()
        if response and response.get("type") == "time_sync_response":
            server_time = response.get("server_timestamp", 0)
            returned_client_time = response.get("client_timestamp", 0)
            
            if returned_client_time == client_time:
                offset = server_time - client_time
                print(f"✅ Time sync successful, offset: {offset}ms")
                return True
            else:
                print("❌ Time sync failed - timestamp mismatch")
        else:
            print("❌ Time sync failed - no response")
        
        return False
    
    def test_sync_flash(self):
        """Test screen flash synchronization"""
        print("\n⚡ Testing sync flash...")
        
        self.send_message({
            "type": "sync_flash",
            "duration_ms": 1000
        })
        
        # Don't expect immediate response, just check if command is accepted
        time.sleep(0.5)
        print("✅ Sync flash command sent")
        return True
    
    def test_recording_control(self):
        """Test recording start/stop"""
        print("\n🎥 Testing recording control...")
        
        # Create session info
        session_info = {
            "sessionId": f"test_{int(time.time())}",
            "name": "Communication Test",
            "description": "Test session for PC-Android communication",
            "timestamp": time.time()
        }
        
        # Start recording
        self.send_message({
            "type": "start_recording",
            "session": session_info
        })
        
        response = self.receive_message()
        if response and response.get("status") == "success":
            print("✅ Recording started successfully")
            
            # Wait a bit then stop
            time.sleep(2)
            self.send_message({
                "type": "stop_recording"
            })
            
            time.sleep(1)
            print("✅ Recording control test completed")
            return True
        else:
            print(f"❌ Recording start failed: {response}")
            return False
    
    def run_all_tests(self):
        """Run all communication tests"""
        print("🚀 Starting Android communication tests...")
        
        if not self.connect_to_android():
            return False
        
        tests = [
            ("Basic Communication", self.test_basic_communication),
            ("Time Synchronization", self.test_time_sync),
            ("Sync Flash", self.test_sync_flash),
            ("Recording Control", self.test_recording_control)
        ]
        
        results = []
        for test_name, test_func in tests:
            try:
                result = test_func()
                results.append((test_name, result))
            except Exception as e:
                print(f"❌ {test_name} crashed: {e}")
                results.append((test_name, False))
        
        # Print summary
        print("\n📊 Test Results:")
        print("=" * 50)
        passed = 0
        for test_name, result in results:
            status = "✅ PASS" if result else "❌ FAIL"
            print(f"{test_name:<25} {status}")
            if result:
                passed += 1
        
        print(f"\nPassed: {passed}/{len(results)} tests")
        
        self.disconnect()
        return passed == len(results)
    
    def disconnect(self):
        """Disconnect from Android"""
        if self.socket:
            try:
                self.socket.close()
            except:
                pass
        self.connected = False
        print("🔌 Disconnected")

if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description="Test Android communication")
    parser.add_argument("--android-ip", default="192.168.1.100", 
                       help="Android device IP address")
    parser.add_argument("--port", type=int, default=8080, 
                       help="Android server port")
    
    args = parser.parse_args()
    
    tester = AndroidCommunicationTester(args.android_ip, args.port)
    success = tester.run_all_tests()
    
    exit(0 if success else 1)