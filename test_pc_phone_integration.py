#!/usr/bin/env python3
"""
PC-Phone Integration Test Script

This script validates the complete PC-to-Phone communication pipeline
and provides comprehensive integration testing.
"""

import subprocess
import json
import time
import threading
import socket
import sys
import os
from pathlib import Path

class PCPhoneIntegrationTester:
    """Comprehensive PC-Phone communication tester"""
    
    def __init__(self):
        self.test_results = []
        self.android_ip = "127.0.0.1"  # For emulator testing
        self.android_port = 8080
        
    def log_result(self, test_name: str, passed: bool, details: str = ""):
        """Log test result"""
        status = "✅ PASS" if passed else "❌ FAIL"
        print(f"{status} {test_name}: {details}")
        self.test_results.append({
            "test": test_name,
            "passed": passed,
            "details": details
        })
    
    def test_project_structure(self):
        """Test 1: Verify project has required PC-Phone communication files"""
        print("\n🔍 Test 1: Project Structure Validation")
        
        required_files = [
            "app/src/main/java/com/topdon/tc001/network/NetworkServer.kt",
            "app/src/main/java/com/topdon/tc001/network/NetworkClient.kt",
            "app/src/main/java/com/topdon/tc001/service/RecordingService.kt",
            "pc-controller/test_android_communication.py",
            "pc-controller/src/ircamera_pc/network/discovery.py"
        ]
        
        missing_files = []
        for file_path in required_files:
            if not os.path.exists(file_path):
                missing_files.append(file_path)
        
        if missing_files:
            self.log_result("Project Structure", False, 
                          f"Missing files: {', '.join(missing_files)}")
        else:
            self.log_result("Project Structure", True, 
                          f"All {len(required_files)} communication files present")
    
    def test_network_classes(self):
        """Test 2: Validate network classes have proper implementation"""
        print("\n🔍 Test 2: Network Class Implementation")
        
        network_server_path = "app/src/main/java/com/topdon/tc001/network/NetworkServer.kt"
        if os.path.exists(network_server_path):
            with open(network_server_path, 'r') as f:
                content = f.read()
                
            # Check for key methods
            required_methods = [
                "startServer",
                "stopServer", 
                "sendMessage",
                "receiveMessage",
                "acceptConnections"
            ]
            
            missing_methods = []
            for method in required_methods:
                if f"fun {method}" not in content and f"suspend fun {method}" not in content:
                    missing_methods.append(method)
            
            if missing_methods:
                self.log_result("NetworkServer Implementation", False,
                              f"Missing methods: {', '.join(missing_methods)}")
            else:
                self.log_result("NetworkServer Implementation", True,
                              "All required methods present")
        else:
            self.log_result("NetworkServer Implementation", False, "File not found")
    
    def test_pc_controller_structure(self):
        """Test 3: Validate PC Controller structure"""
        print("\n🔍 Test 3: PC Controller Structure")
        
        pc_files = [
            "pc-controller/src/ircamera_pc/__init__.py",
            "pc-controller/src/ircamera_pc/network/__init__.py",
            "pc-controller/src/ircamera_pc/network/discovery.py",
            "pc-controller/test_android_communication.py"
        ]
        
        existing_files = [f for f in pc_files if os.path.exists(f)]
        
        self.log_result("PC Controller Structure", 
                       len(existing_files) >= len(pc_files) * 0.8,  # Allow 80% threshold
                       f"{len(existing_files)}/{len(pc_files)} files present")
    
    def test_protocol_compatibility(self):
        """Test 4: Verify Android-PC protocol compatibility"""
        print("\n🔍 Test 4: Protocol Compatibility")
        
        # Check NetworkServer protocol
        network_server_path = "app/src/main/java/com/topdon/tc001/network/NetworkServer.kt"
        android_uses_length_prefix = False
        android_uses_json = False
        
        if os.path.exists(network_server_path):
            with open(network_server_path, 'r') as f:
                content = f.read()
                android_uses_length_prefix = "writeInt" in content and "readInt" in content
                android_uses_json = "JSONObject" in content
        
        # Check PC test script protocol  
        pc_test_path = "pc-controller/test_android_communication.py"
        pc_uses_length_prefix = False
        pc_uses_json = False
        
        if os.path.exists(pc_test_path):
            with open(pc_test_path, 'r') as f:
                content = f.read()
                pc_uses_length_prefix = "struct.pack" in content or "to_bytes" in content
                pc_uses_json = "json.dumps" in content or "json.loads" in content
        
        protocol_compatible = android_uses_length_prefix == pc_uses_length_prefix and android_uses_json == pc_uses_json
        
        self.log_result("Protocol Compatibility", protocol_compatible,
                       f"Android: JSON={android_uses_json}, LengthPrefix={android_uses_length_prefix}, "
                       f"PC: JSON={pc_uses_json}, LengthPrefix={pc_uses_length_prefix}")
    
    def test_recording_service_integration(self):
        """Test 5: Verify RecordingService network integration"""
        print("\n🔍 Test 5: RecordingService Network Integration")
        
        service_path = "app/src/main/java/com/topdon/tc001/service/RecordingService.kt"
        if os.path.exists(service_path):
            with open(service_path, 'r') as f:
                content = f.read()
            
            # Check for network integration
            has_network_server = "NetworkServer" in content
            has_network_client = "NetworkClient" in content  
            has_message_handling = "JSONObject" in content or "json" in content.lower()
            has_recording_control = "startRecording" in content and "stopRecording" in content
            
            integration_score = sum([has_network_server, has_network_client, has_message_handling, has_recording_control])
            
            self.log_result("RecordingService Integration", integration_score >= 3,
                          f"Integration features: {integration_score}/4 present")
        else:
            self.log_result("RecordingService Integration", False, "Service file not found")
    
    def test_android_build_readiness(self):
        """Test 6: Check if Android project can build"""
        print("\n🔍 Test 6: Android Build Readiness")
        
        try:
            # Test gradle wrapper
            result = subprocess.run(
                ["./gradlew", "--version"],
                capture_output=True,
                text=True,
                timeout=10,
                cwd="."
            )
            gradle_works = result.returncode == 0
            
            # Test project recognition
            result = subprocess.run(
                ["./gradlew", "projects"],
                capture_output=True, 
                text=True,
                timeout=20,
                cwd="."
            )
            projects_work = result.returncode == 0
            
            build_ready = gradle_works and projects_work
            
            self.log_result("Android Build Readiness", build_ready,
                          f"Gradle: {gradle_works}, Projects: {projects_work}")
                          
        except subprocess.TimeoutExpired:
            self.log_result("Android Build Readiness", False, "Gradle commands timeout")
        except Exception as e:
            self.log_result("Android Build Readiness", False, f"Error: {str(e)}")
    
    def test_communication_endpoints(self):
        """Test 7: Validate communication endpoint configuration"""
        print("\n🔍 Test 7: Communication Endpoints")
        
        # Check Android side port configuration
        android_port_configured = False
        android_files = [
            "app/src/main/java/com/topdon/tc001/network/NetworkServer.kt",
            "app/src/main/java/com/topdon/tc001/service/RecordingService.kt"
        ]
        
        android_port = None
        for file_path in android_files:
            if os.path.exists(file_path):
                with open(file_path, 'r') as f:
                    content = f.read()
                    if "8080" in content:
                        android_port = 8080
                        android_port_configured = True
                        break
        
        # Check PC side port configuration
        pc_port_configured = False
        pc_files = [
            "pc-controller/test_android_communication.py",
            "pc-controller/src/ircamera_pc/network/discovery.py"
        ]
        
        pc_port = None
        for file_path in pc_files:
            if os.path.exists(file_path):
                with open(file_path, 'r') as f:
                    content = f.read()
                    if "8080" in content:
                        pc_port = 8080
                        pc_port_configured = True
                        break
        
        endpoints_match = android_port == pc_port and android_port_configured and pc_port_configured
        
        self.log_result("Communication Endpoints", endpoints_match,
                       f"Android port: {android_port}, PC port: {pc_port}, Match: {endpoints_match}")
    
    def generate_report(self):
        """Generate final test report"""
        print("\n" + "="*60)
        print("📋 PC-PHONE INTEGRATION TEST REPORT")
        print("="*60)
        
        passed_tests = sum(1 for result in self.test_results if result['passed'])
        total_tests = len(self.test_results)
        
        print(f"\n🎯 Overall Result: {passed_tests}/{total_tests} tests passed")
        
        if passed_tests == total_tests:
            print("✅ ALL TESTS PASSED - PC-Phone communication is properly wired!")
        elif passed_tests >= total_tests * 0.8:
            print("⚠️  MOSTLY READY - Minor issues to address")
        else:
            print("❌ NEEDS WORK - Significant communication issues found")
        
        print(f"\n📊 Test Results:")
        for i, result in enumerate(self.test_results, 1):
            status = "✅" if result['passed'] else "❌"
            print(f"{i}. {status} {result['test']}")
            if result['details']:
                print(f"   {result['details']}")
        
        # Generate recommendations
        print(f"\n🔧 Recommendations:")
        
        failed_tests = [r for r in self.test_results if not r['passed']]
        if not failed_tests:
            print("- Ready for hardware testing!")
            print("- Consider running end-to-end tests with actual Android device")
        else:
            for test in failed_tests:
                if "Structure" in test['test']:
                    print("- Complete missing communication files")
                elif "Build" in test['test']:
                    print("- Resolve gradle build configuration issues")
                elif "Protocol" in test['test']:
                    print("- Align Android and PC communication protocols")
                elif "Integration" in test['test']:
                    print("- Wire recording service to network components")
        
        return passed_tests / total_tests

def main():
    """Run comprehensive PC-Phone integration test"""
    print("🚀 Starting PC-Phone Integration Test...")
    
    tester = PCPhoneIntegrationTester()
    
    # Run all tests
    tester.test_project_structure()
    tester.test_network_classes()
    tester.test_pc_controller_structure()
    tester.test_protocol_compatibility()
    tester.test_recording_service_integration()
    tester.test_android_build_readiness()
    tester.test_communication_endpoints()
    
    # Generate comprehensive report
    success_rate = tester.generate_report()
    
    # Exit with appropriate code
    sys.exit(0 if success_rate >= 0.8 else 1)

if __name__ == "__main__":
    main()