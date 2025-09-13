#!/usr/bin/env python3
"""
Comprehensive PC-Android Integration Test
Tests bidirectional communication between PC Controller and Android device.
"""

import socket
import json
import time
import threading
import sys
from typing import Dict, Any, Optional

class PCAndroidIntegrationTester:
    """Test complete PC-Android communication integration"""
    
    def __init__(self, android_ip: str = "192.168.1.100", port: int = 8080):
        self.android_ip = android_ip
        self.port = port
        self.socket: Optional[socket.socket] = None
        self.connected = False
        
    def test_complete_integration(self) -> bool:
        """Run complete integration test suite"""
        print("🚀 Starting PC-Android Integration Test Suite")
        print(f"📱 Target: {self.android_ip}:{self.port}")
        
        tests = [
            ("Connection Test", self.test_connection),
            ("Authentication Test", self.test_authentication),
            ("Recording Control Test", self.test_recording_control),
            ("Time Sync Test", self.test_time_sync),
            ("Data Streaming Test", self.test_data_streaming),
            ("Sync Flash Test", self.test_sync_flash),
        ]
        
        results = {}
        for test_name, test_func in tests:
            print(f"\n🧪 Running {test_name}...")
            try:
                result = test_func()
                results[test_name] = "✅ PASS" if result else "❌ FAIL"
                print(f"{results[test_name]} {test_name}")
            except Exception as e:
                results[test_name] = f"❌ ERROR: {e}"
                print(f"{results[test_name]}")
        
        # Summary
        print(f"\n📊 Integration Test Results:")
        for test_name, result in results.items():
            print(f"   {result} {test_name}")
        
        passed = sum(1 for r in results.values() if r.startswith("✅"))
        total = len(results)
        print(f"\n🎯 Score: {passed}/{total} tests passed")
        
        return passed == total
    
    def test_connection(self) -> bool:
        """Test basic TCP connection"""
        try:
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.settimeout(10.0)
            self.socket.connect((self.android_ip, self.port))
            self.connected = True
            return True
        except Exception as e:
            print(f"   Connection failed: {e}")
            return False
    
    def test_authentication(self) -> bool:
        """Test PC-Android authentication handshake"""
        if not self.connected:
            return False
            
        try:
            auth_message = {
                "type": "auth_request",
                "pc_id": "test_pc_001", 
                "version": "1.0",
                "timestamp": int(time.time() * 1000)
            }
            
            response = self._send_and_receive(auth_message)
            return response and response.get("type") == "auth_response"
        except Exception as e:
            print(f"   Authentication failed: {e}")
            return False
    
    def test_recording_control(self) -> bool:
        """Test recording start/stop commands"""
        if not self.connected:
            return False
            
        try:
            # Test start recording
            start_message = {
                "type": "start_recording",
                "session": {
                    "id": "test_session_001",
                    "participant_id": "P001",
                    "start_time": int(time.time() * 1000)
                }
            }
            
            response = self._send_and_receive(start_message)
            if not (response and response.get("status") == "recording_started"):
                return False
            
            # Test stop recording
            stop_message = {"type": "stop_recording"}
            response = self._send_and_receive(stop_message)
            return response and response.get("status") == "recording_stopped"
            
        except Exception as e:
            print(f"   Recording control failed: {e}")
            return False
    
    def test_time_sync(self) -> bool:
        """Test time synchronization"""
        if not self.connected:
            return False
            
        try:
            pc_time = int(time.time() * 1000000000)  # nanoseconds
            sync_message = {
                "type": "time_sync",
                "pc_time_ns": pc_time,
                "sync_id": "sync_001"
            }
            
            response = self._send_and_receive(sync_message)
            return response and response.get("type") == "time_sync_response"
            
        except Exception as e:
            print(f"   Time sync failed: {e}")
            return False
    
    def test_data_streaming(self) -> bool:
        """Test real-time data streaming"""
        if not self.connected:
            return False
            
        try:
            stream_message = {"type": "start_data_stream"}
            response = self._send_and_receive(stream_message)
            
            if response and response.get("status") == "streaming_started":
                # Wait for some data
                time.sleep(2)
                stop_message = {"type": "stop_data_stream"}
                response = self._send_and_receive(stop_message)
                return response and response.get("status") == "streaming_stopped"
            
            return False
            
        except Exception as e:
            print(f"   Data streaming failed: {e}")
            return False
    
    def test_sync_flash(self) -> bool:
        """Test synchronization flash command"""
        if not self.connected:
            return False
            
        try:
            flash_message = {
                "type": "sync_flash",
                "duration_ms": 500
            }
            
            response = self._send_and_receive(flash_message)
            return response and response.get("status") == "flash_completed"
            
        except Exception as e:
            print(f"   Sync flash failed: {e}")
            return False
    
    def _send_and_receive(self, message: Dict[str, Any], timeout: float = 5.0) -> Optional[Dict[str, Any]]:
        """Send message and receive response"""
        try:
            # Send message (4-byte length + JSON payload)
            message_json = json.dumps(message)
            message_bytes = message_json.encode('utf-8')
            
            # Send length first (big-endian)
            length = len(message_bytes)
            self.socket.send(length.to_bytes(4, byteorder='big'))
            self.socket.send(message_bytes)
            
            # Receive response
            self.socket.settimeout(timeout)
            
            # Read response length
            length_bytes = self.socket.recv(4)
            if len(length_bytes) < 4:
                return None
            
            response_length = int.from_bytes(length_bytes, byteorder='big')
            
            # Read response data
            response_data = b''
            while len(response_data) < response_length:
                chunk = self.socket.recv(response_length - len(response_data))
                if not chunk:
                    break
                response_data += chunk
            
            # Parse JSON response
            response_json = response_data.decode('utf-8')
            return json.loads(response_json)
            
        except Exception as e:
            print(f"   Send/receive error: {e}")
            return None
    
    def cleanup(self):
        """Cleanup resources"""
        if self.socket:
            try:
                self.socket.close()
            except:
                pass

def main():
    """Main test function"""
    import argparse
    
    parser = argparse.ArgumentParser(description="PC-Android Integration Test")
    parser.add_argument("--android-ip", default="192.168.1.100", 
                       help="Android device IP address")
    parser.add_argument("--port", type=int, default=8080,
                       help="Android server port")
    
    args = parser.parse_args()
    
    tester = PCAndroidIntegrationTester(args.android_ip, args.port)
    
    try:
        success = tester.test_complete_integration()
        sys.exit(0 if success else 1)
    finally:
        tester.cleanup()

if __name__ == "__main__":
    main()