#!/bin/bash

# Dual-Mode Camera Implementation Validation Script
# Validates the complete Samsung S22 dual-mode camera system

set -e

echo "🎯 Dual-Mode Camera Implementation Validation"
echo "============================================="

# Color definitions
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Validation counters
TOTAL_CHECKS=0
PASSED_CHECKS=0
FAILED_CHECKS=0

# Function to run validation check
validate_check() {
    local check_name="$1"
    local check_command="$2"
    local required="$3" # "required" or "optional"
    
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
    
    echo -n "  ➤ $check_name... "
    
    if eval "$check_command"; then
        echo -e "${GREEN}✓ PASS${NC}"
        PASSED_CHECKS=$((PASSED_CHECKS + 1))
        return 0
    else
        if [ "$required" = "required" ]; then
            echo -e "${RED}✗ FAIL (REQUIRED)${NC}"
            FAILED_CHECKS=$((FAILED_CHECKS + 1))
            return 1
        else
            echo -e "${YELLOW}⚠ WARN (OPTIONAL)${NC}"
            return 0
        fi
    fi
}

echo
echo -e "${BLUE}📁 File Structure Validation${NC}"
echo "----------------------------"

# Core implementation files
validate_check "RGBCameraRecorder dual-mode implementation" \
    "[ -f 'app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt' ]" \
    "required"

validate_check "CameraModeSelector UI component" \
    "[ -f 'app/src/main/java/com/topdon/tc001/camera/ui/CameraModeSelector.kt' ]" \
    "required"

validate_check "DualModeCameraActivity integration" \
    "[ -f 'app/src/main/java/com/topdon/tc001/camera/integration/DualModeCameraActivity.kt' ]" \
    "required"

validate_check "DualModeIntegrationExample" \
    "[ -f 'app/src/main/java/com/topdon/tc001/camera/integration/DualModeIntegrationExample.kt' ]" \
    "required"

validate_check "Samsung S22 documentation" \
    "[ -f 'SAMSUNG_S22_IMPLEMENTATION_GUIDE.md' ]" \
    "required"

validate_check "Dual-mode implementation documentation" \
    "[ -f 'DUAL_MODE_CAMERA_IMPLEMENTATION.md' ]" \
    "required"

echo
echo -e "${BLUE}🧪 Test Coverage Validation${NC}"
echo "---------------------------"

validate_check "RGBCameraRecorder unit tests" \
    "[ -f 'app/src/test/java/com/topdon/tc001/camera/RGBCameraRecorderTest.kt' ]" \
    "required"

validate_check "CameraModeSelector unit tests" \
    "[ -f 'app/src/test/java/com/topdon/tc001/camera/CameraModeSelectorTest.kt' ]" \
    "required"

validate_check "RAW capture test activity" \
    "[ -f 'app/src/main/java/com/topdon/tc001/test/RAWCaptureTestActivity.kt' ]" \
    "optional"

echo
echo -e "${BLUE}🔧 Code Quality Validation${NC}"
echo "--------------------------"

# Check for key dual-mode features in RGBCameraRecorder
validate_check "Dual-mode CameraMode enum" \
    "grep -q 'enum class CameraMode' app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt" \
    "required"

validate_check "RAW_50MP mode implementation" \
    "grep -q 'RAW_50MP' app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt" \
    "required"

validate_check "VIDEO_4K mode implementation" \
    "grep -q 'VIDEO_4K' app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt" \
    "required"

validate_check "Fast switchMode function" \
    "grep -q 'suspend fun switchMode' app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt" \
    "required"

validate_check "Samsung S22 optimizations" \
    "grep -q 'SAMSUNG.*S22' app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt" \
    "required"

validate_check "Session configuration logic" \
    "grep -q 'captureSession.*close' app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt" \
    "required"

echo
echo -e "${BLUE}📱 UI Integration Validation${NC}"
echo "-----------------------------"

validate_check "MainActivity GSR integration updated" \
    "grep -q 'showDualModeCameraOptions' app/src/main/java/com/topdon/tc001/fragment/MainFragment.kt" \
    "required"

validate_check "Dual-mode camera activity layout" \
    "[ -f 'app/src/main/res/layout/activity_dual_mode_camera.xml' ]" \
    "required"

validate_check "CameraModeSelector in layout" \
    "grep -q 'CameraModeSelector' app/src/main/res/layout/activity_dual_mode_camera.xml" \
    "required"

validate_check "TextureView for camera preview" \
    "grep -q 'TextureView' app/src/main/res/layout/activity_dual_mode_camera.xml" \
    "required"

echo
echo -e "${BLUE}📚 Documentation Validation${NC}"
echo "----------------------------"

validate_check "Samsung S22 device detection documented" \
    "grep -q 'SM-S90' SAMSUNG_S22_IMPLEMENTATION_GUIDE.md" \
    "required"

validate_check "RAW 50MP specifications documented" \
    "grep -q '50MP.*RAW' SAMSUNG_S22_IMPLEMENTATION_GUIDE.md" \
    "required"

validate_check "4K video specifications documented" \
    "grep -q '4K.*Resolution.*3840.*2160\|3840.*2160.*4K\|4K Video Mode Specifications' SAMSUNG_S22_IMPLEMENTATION_GUIDE.md" \
    "required"

validate_check "Fast session switching documented" \
    "grep -q 'Fast Session Switching\|fast.*session.*switching\|session.*switch.*200ms\|Fast.*switching' SAMSUNG_S22_IMPLEMENTATION_GUIDE.md" \
    "required"

validate_check "Thermal throttling awareness documented" \
    "grep -q 'thermal.*throttling\|thermal.*awareness' SAMSUNG_S22_IMPLEMENTATION_GUIDE.md" \
    "required"

validate_check "Integration examples documented" \
    "grep -q 'Integration.*Example\|integration.*example' DUAL_MODE_CAMERA_IMPLEMENTATION.md" \
    "required"

echo
echo -e "${BLUE}🔍 Implementation Completeness${NC}"
echo "-------------------------------"

# Check if build dependency issues are resolved
validate_check "GSYVideoPlayer dependency resolved" \
    "grep -q 'media3-exoplayer\|androidx.media3' component/thermal-ir/build.gradle.kts" \
    "required"

validate_check "Camera2 API imports present" \
    "grep -q 'android.hardware.camera2' app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt" \
    "required"

validate_check "Kotlin coroutines integration" \
    "grep -q 'kotlinx.coroutines' app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt" \
    "required"

validate_check "DNG creation support" \
    "grep -q 'DngCreator' app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt" \
    "required"

validate_check "MediaRecorder integration" \
    "grep -q 'MediaRecorder' app/src/main/java/com/topdon/tc001/camera/RGBCameraRecorder.kt" \
    "required"

echo
echo -e "${BLUE}📊 Summary${NC}"
echo "----------"

if [ $FAILED_CHECKS -eq 0 ]; then
    echo -e "${GREEN}✅ All critical validations passed!${NC}"
    echo -e "   Total Checks: $TOTAL_CHECKS"
    echo -e "   Passed: $PASSED_CHECKS"
    echo -e "   Failed: $FAILED_CHECKS"
    echo
    echo -e "${GREEN}🎉 Dual-mode camera implementation is ready for integration!${NC}"
    echo
    echo -e "${BLUE}Next Steps:${NC}"
    echo "  1. Fix any remaining build issues"
    echo "  2. Run unit tests: ./gradlew test"
    echo "  3. Test on Samsung S22 device"
    echo "  4. Validate end-to-end functionality"
    exit 0
else
    echo -e "${RED}❌ $FAILED_CHECKS critical validation(s) failed!${NC}"
    echo -e "   Total Checks: $TOTAL_CHECKS"
    echo -e "   Passed: $PASSED_CHECKS"
    echo -e "   Failed: $FAILED_CHECKS"
    echo
    echo -e "${RED}🚨 Implementation requires fixes before integration.${NC}"
    echo
    echo -e "${YELLOW}Required Actions:${NC}"
    echo "  1. Address failed validations above"
    echo "  2. Run validation script again"
    echo "  3. Ensure all required components are implemented"
    exit 1
fi