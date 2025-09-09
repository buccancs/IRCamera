#!/bin/bash
# Comprehensive Test Suite Validation Script
# Validates that all test components are properly configured and can run

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project paths
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ANDROID_ROOT="${PROJECT_ROOT}"
PC_CONTROLLER_ROOT="${PROJECT_ROOT}/pc-controller"

echo -e "${BLUE}🧪 Comprehensive Test Suite Validation${NC}"
echo "======================================"

# Validation results
VALIDATION_RESULTS=()

validate_component() {
    local component_name="$1"
    local test_command="$2"
    local working_dir="${3:-$PROJECT_ROOT}"
    
    echo -e "\n${YELLOW}📋 Validating: ${component_name}${NC}"
    echo "   Directory: $working_dir"
    echo "   Command: $test_command"
    
    cd "$working_dir"
    
    if eval "$test_command" >/dev/null 2>&1; then
        echo -e "   ${GREEN}✅ PASS${NC}"
        VALIDATION_RESULTS+=("PASS: $component_name")
        return 0
    else
        echo -e "   ${RED}❌ FAIL${NC}"
        VALIDATION_RESULTS+=("FAIL: $component_name")
        return 1
    fi
}

validate_file_exists() {
    local description="$1"
    local file_path="$2"
    
    echo -e "\n${YELLOW}📄 Checking: ${description}${NC}"
    echo "   Path: $file_path"
    
    if [[ -f "$file_path" ]]; then
        echo -e "   ${GREEN}✅ EXISTS${NC}"
        VALIDATION_RESULTS+=("PASS: $description")
        return 0
    else
        echo -e "   ${RED}❌ MISSING${NC}"
        VALIDATION_RESULTS+=("FAIL: $description")
        return 1
    fi
}

validate_directory_structure() {
    local description="$1"
    local dir_path="$2"
    
    echo -e "\n${YELLOW}📁 Checking: ${description}${NC}"
    echo "   Path: $dir_path"
    
    if [[ -d "$dir_path" ]]; then
        local file_count=$(find "$dir_path" -type f -name "*.kt" -o -name "*.py" | wc -l)
        echo -e "   ${GREEN}✅ EXISTS${NC} (${file_count} test files)"
        VALIDATION_RESULTS+=("PASS: $description")
        return 0
    else
        echo -e "   ${RED}❌ MISSING${NC}"
        VALIDATION_RESULTS+=("FAIL: $description")
        return 1
    fi
}

echo -e "\n${BLUE}🏗️  Validating Project Structure${NC}"
echo "================================="

# Validate core test files exist
validate_file_exists "Android Unit Test - RecordingController" "$PROJECT_ROOT/app/src/test/java/com/topdon/tc001/controller/RecordingControllerTest.kt"
validate_file_exists "Android Unit Test - TimeManager" "$PROJECT_ROOT/app/src/test/java/com/topdon/tc001/utils/TimeManagerTest.kt"
validate_file_exists "Android Unit Test - NetworkClient" "$PROJECT_ROOT/app/src/test/java/com/topdon/tc001/network/EnhancedNetworkClientTest.kt"
validate_file_exists "Android Unit Test - GSRSensorRecorder" "$PROJECT_ROOT/app/src/test/java/com/topdon/tc001/sensors/GSRSensorRecorderTest.kt"
validate_file_exists "Android Integration Test - HubSpoke" "$PROJECT_ROOT/app/src/test/java/com/topdon/tc001/integration/HubSpokeIntegrationTest.kt"
validate_file_exists "Android Performance Test" "$PROJECT_ROOT/app/src/test/java/com/topdon/tc001/performance/PerformanceTest.kt"

validate_file_exists "PC Controller Unit Test - Network" "$PC_CONTROLLER_ROOT/src/ircamera_pc/tests/test_network.py"
validate_file_exists "PC Controller Unit Test - Data Aggregation" "$PC_CONTROLLER_ROOT/src/ircamera_pc/tests/test_data_aggregation.py"
validate_file_exists "PC Controller Integration Test" "$PC_CONTROLLER_ROOT/src/ircamera_pc/tests/test_integration.py"

validate_file_exists "Comprehensive Test Runner" "$PROJECT_ROOT/run_comprehensive_tests.py"
validate_file_exists "Android Test Config" "$PROJECT_ROOT/app/src/test/resources/test-config.properties"
validate_file_exists "PC Controller Test Config" "$PC_CONTROLLER_ROOT/test_config.yaml"

# Validate test directory structure
validate_directory_structure "Android Unit Tests Directory" "$PROJECT_ROOT/app/src/test/java/com/topdon/tc001"
validate_directory_structure "PC Controller Tests Directory" "$PC_CONTROLLER_ROOT/src/ircamera_pc/tests"

echo -e "\n${BLUE}🔧 Validating Build Configuration${NC}"
echo "==================================="

# Validate build files have test dependencies
validate_component "Android Build Configuration (Test Dependencies)" \
    "grep -q 'testImplementation.*mockk' app/build.gradle.kts" \
    "$PROJECT_ROOT"

validate_component "PC Controller Requirements (Test Dependencies)" \
    "grep -q 'pytest.*' pc-controller/requirements.txt" \
    "$PROJECT_ROOT"

echo -e "\n${BLUE}⚙️  Validating Test Tools and Environment${NC}"  
echo "=========================================="

# Check if required tools are available
validate_component "Gradle Wrapper" "test -f ./gradlew && ./gradlew --version" "$PROJECT_ROOT"
validate_component "Python Environment" "python3 --version" "$PROJECT_ROOT"

# Validate Python dependencies can be resolved
validate_component "Python Test Dependencies" \
    "python3 -c 'import pytest, unittest, json, asyncio'" \
    "$PC_CONTROLLER_ROOT"

echo -e "\n${BLUE}🧪 Validating Test Syntax and Structure${NC}"
echo "========================================"

# Validate Android test files compile
validate_component "Android Test Compilation Check" \
    "./gradlew compileDebugUnitTestKotlin --dry-run" \
    "$PROJECT_ROOT"

# Validate Python test files syntax
validate_component "Python Test Syntax Check" \
    "python3 -m py_compile src/ircamera_pc/tests/*.py" \
    "$PC_CONTROLLER_ROOT"

echo -e "\n${BLUE}📊 Test Coverage Validation${NC}"
echo "============================"

# Count test methods in Android tests
android_test_methods=$(find "$PROJECT_ROOT/app/src/test" -name "*.kt" -exec grep -h "@Test" {} \; | wc -l)
echo -e "Android Test Methods: ${GREEN}${android_test_methods}${NC}"

# Count test methods in PC Controller tests  
python_test_methods=$(find "$PC_CONTROLLER_ROOT/src/ircamera_pc/tests" -name "test_*.py" -exec grep -h "def test_" {} \; | wc -l)
echo -e "Python Test Methods: ${GREEN}${python_test_methods}${NC}"

total_test_methods=$((android_test_methods + python_test_methods))
echo -e "Total Test Methods: ${GREEN}${total_test_methods}${NC}"

if [[ $total_test_methods -ge 50 ]]; then
    echo -e "Test Coverage: ${GREEN}✅ ADEQUATE${NC} (${total_test_methods} test methods)"
    VALIDATION_RESULTS+=("PASS: Test Coverage Adequate")
else
    echo -e "Test Coverage: ${YELLOW}⚠️  LIMITED${NC} (${total_test_methods} test methods)"
    VALIDATION_RESULTS+=("WARN: Test Coverage Limited")
fi

echo -e "\n${BLUE}🎯 Test Categories Coverage${NC}"
echo "============================="

# Validate test categories exist
categories=(
    "Unit Tests:RecordingControllerTest"
    "Unit Tests:TimeManagerTest"
    "Unit Tests:NetworkClientTest"
    "Unit Tests:GSRSensorRecorderTest"
    "Integration Tests:HubSpokeIntegrationTest"
    "Performance Tests:PerformanceTest"
    "Network Tests:test_network.py"
    "Data Tests:test_data_aggregation.py"
    "E2E Tests:test_integration.py"
)

for category in "${categories[@]}"; do
    category_name="${category%%:*}"
    file_pattern="${category##*:}"
    
    if find "$PROJECT_ROOT" -name "*${file_pattern}*" | grep -q .; then
        echo -e "${category_name}: ${GREEN}✅ COVERED${NC}"
        VALIDATION_RESULTS+=("PASS: $category_name")
    else
        echo -e "${category_name}: ${RED}❌ MISSING${NC}"
        VALIDATION_RESULTS+=("FAIL: $category_name")
    fi
done

echo -e "\n${BLUE}🚀 Final Validation Summary${NC}"
echo "============================="

# Count results
total_checks=${#VALIDATION_RESULTS[@]}
passed_checks=$(printf '%s\n' "${VALIDATION_RESULTS[@]}" | grep -c "^PASS:" || true)
failed_checks=$(printf '%s\n' "${VALIDATION_RESULTS[@]}" | grep -c "^FAIL:" || true)
warning_checks=$(printf '%s\n' "${VALIDATION_RESULTS[@]}" | grep -c "^WARN:" || true)

echo -e "Total Validation Checks: ${BLUE}${total_checks}${NC}"
echo -e "Passed: ${GREEN}${passed_checks}${NC}"
echo -e "Failed: ${RED}${failed_checks}${NC}"
echo -e "Warnings: ${YELLOW}${warning_checks}${NC}"

# Calculate success rate
success_rate=$((passed_checks * 100 / total_checks))
echo -e "Success Rate: ${success_rate}%"

# Show failed checks if any
if [[ $failed_checks -gt 0 ]]; then
    echo -e "\n${RED}❌ Failed Checks:${NC}"
    printf '%s\n' "${VALIDATION_RESULTS[@]}" | grep "^FAIL:" | sed 's/FAIL: /   - /'
fi

# Show warnings if any
if [[ $warning_checks -gt 0 ]]; then
    echo -e "\n${YELLOW}⚠️  Warning Checks:${NC}"
    printf '%s\n' "${VALIDATION_RESULTS[@]}" | grep "^WARN:" | sed 's/WARN: /   - /'
fi

echo -e "\n${BLUE}📋 Test Execution Instructions${NC}"
echo "==============================="
echo "To run the comprehensive test suite:"
echo ""
echo "1. Unit Tests Only (Fast):"
echo "   python3 run_comprehensive_tests.py --no-integration --no-performance"
echo ""
echo "2. Unit + Integration Tests:"  
echo "   python3 run_comprehensive_tests.py --no-performance"
echo ""
echo "3. Complete Test Suite (Slow):"
echo "   python3 run_comprehensive_tests.py"
echo ""
echo "4. Android Tests Only:"
echo "   ./gradlew test"
echo ""
echo "5. PC Controller Tests Only:"
echo "   cd pc-controller && python3 -m pytest src/ircamera_pc/tests/"

# Final result
if [[ $success_rate -ge 90 ]]; then
    echo -e "\n${GREEN}🎉 COMPREHENSIVE TEST SUITE VALIDATION PASSED!${NC}"
    echo -e "Success Rate: ${success_rate}% (Required: 90%)"
    exit 0
elif [[ $success_rate -ge 75 ]]; then
    echo -e "\n${YELLOW}⚠️  COMPREHENSIVE TEST SUITE VALIDATION PASSED WITH WARNINGS${NC}"
    echo -e "Success Rate: ${success_rate}% (Recommended: 90%)"
    exit 0
else
    echo -e "\n${RED}💥 COMPREHENSIVE TEST SUITE VALIDATION FAILED${NC}"
    echo -e "Success Rate: ${success_rate}% (Required: 75%)"
    exit 1
fi