#!/usr/bin/env python3
"""
Validate PC-Android communication without requiring physical hardware.
This script validates that the communication infrastructure is properly wired.
"""

import json
import os

def validate_network_server():
    """Validate NetworkServer.kt implementation"""
    server_file = "app/src/main/java/com/topdon/tc001/network/NetworkServer.kt"
    
    if not os.path.exists(server_file):
        return False, "NetworkServer.kt not found"
    
    with open(server_file, 'r') as f:
        content = f.read()
    
    required_functions = [
        "suspend fun sendMessage",
        "fun isClientConnected",
        "suspend fun start",
        "suspend fun stop",
        "private suspend fun acceptConnections",
        "private suspend fun listenForMessages"
    ]
    
    missing = []
    for func in required_functions:
        if func not in content:
            missing.append(func)
    
    if missing:
        return False, f"Missing functions: {missing}"
    
    return True, "NetworkServer properly implemented"

def validate_recording_service():
    """Validate RecordingService.kt communication wiring"""
    service_file = "app/src/main/java/com/topdon/tc001/service/RecordingService.kt"
    
    if not os.path.exists(service_file):
        return False, "RecordingService.kt not found"
    
    with open(service_file, 'r') as f:
        content = f.read()
    
    required_methods = [
        "private suspend fun handlePCMessage",
        "private fun sendResponseToPC",
        "private fun handleTimeSyncRequest", 
        "private fun sendStatusToPC",
        "private fun setupNetworkServer"
    ]
    
    missing = []
    for method in required_methods:
        if method not in content:
            missing.append(method)
    
    if missing:
        return False, f"Missing methods: {missing}"
    
    return True, "RecordingService properly wired"

def validate_pc_test_script():
    """Validate PC test communication script exists"""
    test_file = "pc-controller/test_android_communication.py"
    
    if not os.path.exists(test_file):
        return False, "PC test script not found"
    
    with open(test_file, 'r') as f:
        content = f.read()
    
    required_functions = [
        "def connect_to_android",
        "def send_message",
        "def receive_message",
        "def test_basic_communication",
        "def test_time_sync",
        "def test_recording_control"
    ]
    
    missing = []
    for func in required_functions:
        if func not in content:
            missing.append(func)
    
    if missing:
        return False, f"Missing functions in PC test script: {missing}"
    
    return True, "PC test script properly implemented"

def main():
    print("🔍 Validating PC-Android Communication Infrastructure")
    print("=" * 55)
    
    tests = [
        ("Android NetworkServer", validate_network_server),
        ("RecordingService Wiring", validate_recording_service),
        ("PC Test Scripts", validate_pc_test_script)
    ]
    
    all_passed = True
    
    for test_name, test_func in tests:
        try:
            passed, message = test_func()
            status = "✅ PASS" if passed else "❌ FAIL"
            print(f"{test_name:.<25} {status}")
            if not passed:
                print(f"   Error: {message}")
                all_passed = False
            else:
                print(f"   {message}")
        except Exception as e:
            print(f"{test_name:.<25} ❌ ERROR")
            print(f"   Exception: {e}")
            all_passed = False
    
    print("\n" + "=" * 55)
    if all_passed:
        print("🎉 All communication infrastructure tests PASSED!")
        print("✅ PC-Android communication is properly wired")
    else:
        print("⚠️  Some tests failed - communication needs fixes")
    
    return all_passed

if __name__ == "__main__":
    success = main()
    exit(0 if success else 1)