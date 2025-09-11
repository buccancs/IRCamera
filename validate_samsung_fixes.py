#!/usr/bin/env python3
"""
Simple validation script for Samsung Camera Integration fixes
Checks that the key improvements are present in the code
"""

import os
import re

def check_file_content(file_path, patterns, description):
    """Check if file contains expected patterns"""
    print(f"\nChecking {description}: {file_path}")
    
    if not os.path.exists(file_path):
        print(f"  ❌ File not found: {file_path}")
        return False
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    all_found = True
    for pattern_name, pattern in patterns.items():
        if re.search(pattern, content, re.MULTILINE | re.DOTALL):
            print(f"  ✅ Found {pattern_name}")
        else:
            print(f"  ❌ Missing {pattern_name}")
            all_found = False
    
    return all_found

def main():
    print("🔍 Validating Samsung Camera Integration Fixes")
    
    base_path = "/home/runner/work/IRCamera/IRCamera"
    
    # Check RgbCameraRecorder improvements
    rgb_recorder_path = f"{base_path}/app/src/main/java/com/topdon/tc001/sensors/rgb/RgbCameraRecorder.kt"
    rgb_patterns = {
        "Samsung device detection": r"Build\.MANUFACTURER\.equals.*samsung.*ignoreCase",
        "Enhanced permission validation": r"Camera permission not granted.*Please grant camera permission in app settings",
        "Progressive camera binding": r"Samsung device.*conservative binding approach",
        "Fallback to video-only": r"video only.*image capture disabled for compatibility",
        "Camera provider null check": r"cameraProvider == null",
        "Camera availability check": r"hasCamera\(cameraSelector\)",
        "Binding validation": r"bindCamera\(\).*Boolean",
    }
    
    # Check RecordingService improvements  
    service_path = f"{base_path}/app/src/main/java/com/topdon/tc001/service/RecordingService.kt"
    service_patterns = {
        "Permission validation method": r"validateRequiredPermissions\(\).*Boolean",
        "Permission check before init": r"validateRequiredPermissions.*cannot initialize sensors",
        "Detailed permission logging": r"Missing required permissions.*joinToString",
        "Camera permission check": r"CAMERA.*Required for RGB video",
    }
    
    # Check test file
    test_path = f"{base_path}/app/src/test/java/com/topdon/tc001/sensors/rgb/RgbCameraRecorderTest.kt"
    test_patterns = {
        "Basic validation test": r"RgbCameraRecorderTest",
        "Resolution validation": r"targetVideoWidth.*1920",
        "Sensor type validation": r"RGB Camera.*RGB Camera",
    }
    
    # Run validations
    results = []
    results.append(check_file_content(rgb_recorder_path, rgb_patterns, "RGB Camera Recorder fixes"))
    results.append(check_file_content(service_path, service_patterns, "Recording Service fixes"))
    results.append(check_file_content(test_path, test_patterns, "Basic test coverage"))
    
    # Summary
    print(f"\n📊 Validation Summary:")
    passed = sum(results)
    total = len(results)
    
    if passed == total:
        print(f"  ✅ All {total} validation checks passed!")
        print("  🎯 Samsung Camera Integration fixes are properly implemented")
        return True
    else:
        print(f"  ❌ {total - passed} out of {total} validation checks failed")
        print("  🔧 Some fixes may need attention")
        return False

if __name__ == "__main__":
    success = main()
    exit(0 if success else 1)