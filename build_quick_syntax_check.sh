#!/bin/bash

# Quick syntax check script to identify compilation errors without full build
echo "🔍 Quick Syntax Check for IRCamera Project"
echo "==========================================="

# Check for Kotlin syntax errors in key files
echo "📋 Checking key Kotlin files for syntax errors..."

# Check RecordingService.kt
echo "1. Checking RecordingService.kt..."
kotlin -cp "." -d /tmp/syntax_check app/src/main/java/com/topdon/tc001/service/RecordingService.kt 2>&1 | head -10

# Check NetworkServer.kt  
echo "2. Checking NetworkServer.kt..."
kotlin -cp "." -d /tmp/syntax_check app/src/main/java/com/topdon/tc001/network/NetworkServer.kt 2>&1 | head -10

# Check SensorCoordinator.kt
echo "3. Checking SensorCoordinator.kt..."
kotlin -cp "." -d /tmp/syntax_check app/src/main/java/com/topdon/tc001/controller/SensorCoordinator.kt 2>&1 | head -10

# Check ThermalCameraRecorder.kt
echo "4. Checking ThermalCameraRecorder.kt..."
kotlin -cp "." -d /tmp/syntax_check app/src/main/java/com/topdon/tc001/sensors/thermal/ThermalCameraRecorder.kt 2>&1 | head -10

echo ""
echo "✅ Quick syntax check completed"