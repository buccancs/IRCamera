# IRCamera Coding Standards

## Overview

This document defines coding standards and best practices for the IRCamera multi-modal physiological sensing platform. These standards ensure code quality, maintainability, and international accessibility across Python (PC Controller) and Android (Sensor Node) components.

## Table of Contents

- [General Guidelines](#general-guidelines)
- [Python Standards (PC Controller)](#python-standards-pc-controller)
- [Kotlin/Java Standards (Android)](#kotlinjava-standards-android)
- [Documentation Standards](#documentation-standards)
- [Internationalization](#internationalization)
- [Quality Assurance](#quality-assurance)
- [Git Workflow](#git-workflow)

## General Guidelines

### Language Requirements
- **All code, comments, and documentation MUST be in English**
- Variable names, function names, and class names MUST use English
- Code comments MUST be translated from Chinese to English for international accessibility

### Code Style Principles
1. **Readability First**: Code should be self-documenting with clear, descriptive names
2. **Consistency**: Follow language-specific conventions consistently throughout the project
3. **Error Handling**: Implement robust error handling with meaningful error messages
4. **Logging**: Use structured logging for debugging and monitoring
5. **Performance**: Optimize critical paths, especially real-time sensor data processing

## Python Standards (PC Controller)

### Style Guide
- **Strictly follow PEP 8** for all Python code
- Use **88-character line length** (Black formatter standard)
- Use **type hints** for all function and method signatures

### Example:
```python
def process_sensor_data(
    data: Dict[str, Any], 
    timestamp: float, 
    device_id: str
) -> ProcessedData:
    """
    Process incoming sensor data with timestamp validation.
    
    Args:
        data: Raw sensor data dictionary
        timestamp: UTC timestamp in seconds
        device_id: Unique device identifier
        
    Returns:
        ProcessedData: Validated and processed sensor data
        
    Raises:
        ValidationError: If data format is invalid
        TimestampError: If timestamp is out of acceptable range
    """
    # Implementation here
    pass
```

### Threading and Async
- **All network operations MUST use background QThread workers**
- GUI operations MUST remain on the main thread
- Use **PyBind11 for performance-critical C++ integration**

### Type Annotations
- All function signatures MUST include type hints
- Use `typing` module for complex types: `Dict[str, Any]`, `Optional[str]`, etc.
- Variables with ambiguous types SHOULD have type annotations

## Kotlin/Java Standards (Android)

### Architecture
- **Follow MVVM (Model-View-ViewModel) pattern strictly**
- Use **Android Architecture Components** (ViewModel, LiveData)
- **All async operations MUST use Kotlin Coroutines**

### Style Guide
- Follow the **official Kotlin Style Guide** recommended by Google
- Use **4-space indentation** for Kotlin/Java
- **Lifecycle-aware components** MUST be used for UI interactions

### Example:
```kotlin
/**
 * Manages Bluetooth Low Energy scanning for thermal camera devices.
 * 
 * This class provides lifecycle-aware BLE scanning with automatic cleanup
 * and proper permission handling.
 * 
 * @param context Application context for system service access
 * @param lifecycle Activity lifecycle for automatic cleanup
 */
class ThermalDeviceScanner(
    private val context: Context,
    private val lifecycle: Lifecycle
) : DefaultLifecycleObserver {
    
    /**
     * Starts BLE scanning for supported thermal camera models.
     * 
     * @param deviceTypes List of supported device type prefixes
     * @param onDeviceFound Callback invoked when a compatible device is discovered
     * @return true if scanning started successfully, false if permissions missing
     */
    @SuppressLint("MissingPermission")
    suspend fun startScan(
        deviceTypes: List<String>,
        onDeviceFound: (String) -> Unit
    ): Boolean {
        // Implementation here
    }
}
```

### Resource Management
- Use `use` blocks for all `Closeable` resources
- **AES256-GCM encryption** via Android Keystore for local storage
- Implement proper lifecycle cleanup

## Documentation Standards

### JavaDoc/KDoc Format
All public classes and methods MUST include comprehensive documentation:

#### Required Elements:
- **Purpose**: What the class/method does
- **Parameters**: `@param` for each parameter with description
- **Return Value**: `@return` with description of return value
- **Exceptions**: `@throws` for all checked exceptions
- **Usage Examples**: For complex APIs
- **Thread Safety**: Document thread-safety guarantees
- **Lifecycle**: For Android components, document lifecycle behavior

#### Example JavaDoc:
```java
/**
 * Validates and processes thermal camera calibration data.
 * 
 * This class implements stereo calibration algorithms for dual-camera
 * thermal imaging systems, ensuring accurate temperature measurements
 * across the field of view.
 * 
 * <p>Thread Safety: This class is not thread-safe. External synchronization
 * is required for concurrent access.
 * 
 * <p>Example usage:
 * <pre>{@code
 * CalibrationProcessor processor = new CalibrationProcessor();
 * CalibrationResult result = processor.calibrate(leftImages, rightImages);
 * }</pre>
 * 
 * @author TopDon Development Team
 * @since 1.0
 * @see StereoCalibration
 */
public class CalibrationProcessor {
    
    /**
     * Performs stereo calibration using provided image pairs.
     * 
     * @param leftImages List of calibration images from left camera
     * @param rightImages List of calibration images from right camera
     * @param boardSize Calibration board size in (width, height)
     * @return CalibrationResult containing calibration matrices and parameters
     * @throws CalibrationException if insufficient or invalid calibration data
     * @throws IllegalArgumentException if image lists have different sizes
     */
    public CalibrationResult calibrate(
        List<Mat> leftImages, 
        List<Mat> rightImages, 
        Size boardSize
    ) throws CalibrationException {
        // Implementation
    }
}
```

### Python Docstring Format (PEP 257)
```python
def synchronize_timestamps(
    pc_timestamp: float, 
    android_timestamps: List[float],
    offset_ms: float
) -> List[float]:
    """
    Synchronize Android device timestamps with PC controller time.
    
    Applies calculated time offset to align all sensor data to a common
    time reference for multi-device synchronization.
    
    Args:
        pc_timestamp: Reference timestamp from PC controller (UTC seconds)
        android_timestamps: List of Android device timestamps to synchronize
        offset_ms: Calculated time offset in milliseconds
        
    Returns:
        List of synchronized timestamps aligned to PC time reference
        
    Raises:
        TimeSyncError: If offset is outside acceptable range (>100ms)
        ValueError: If timestamp lists are empty
        
    Example:
        >>> sync_times = synchronize_timestamps(
        ...     pc_time, android_times, calculated_offset
        ... )
        >>> assert all(abs(t - pc_time) < 0.005 for t in sync_times)
    """
```

## Internationalization

### Comment Translation Guidelines

#### Required Translations:
- Chinese characters (中文) → English
- Technical terms in local language → English technical terms
- Cultural-specific references → Universal technical descriptions

#### Translation Examples:
```kotlin
// ❌ Before
/**
 * 蓝牙扫描工具类
 * 用于扫描热像仪设备
 */

// ✅ After  
/**
 * Bluetooth scanning utility for thermal camera devices.
 * 
 * Provides low-energy Bluetooth scanning with device filtering
 * and automatic lifecycle management.
 */
```

#### Best Practices:
1. **Technical Accuracy**: Ensure translations maintain technical precision
2. **Context Preservation**: Keep technical context and implementation details
3. **Consistency**: Use consistent terminology across the codebase
4. **Documentation**: Document translation choices for complex technical terms

## Quality Assurance

### Automated Quality Gates

#### Pre-commit Hooks (Required)
```bash
# Install pre-commit hooks
pip install pre-commit
pre-commit install
```

#### Python Quality Checks:
- **Black**: Code formatting (88-character lines)
- **isort**: Import sorting
- **flake8**: Style and error checking
- **mypy**: Type checking (target: <100 errors)
- **bandit**: Security vulnerability scanning

#### Android Quality Checks:
- **ktlint**: Kotlin style checking
- **Android Lint**: Resource and code analysis
- **Detekt**: Code smell detection

#### General Checks:
- **Chinese character detection**: Automatic detection of non-English comments
- **TODO/FIXME tracking**: Format validation for development tasks
- **Secret detection**: Prevent accidental credential commits
- **Dependency vulnerability scanning**: Security assessment

### Quality Metrics Targets

#### Python Code Quality:
- **flake8**: 0 errors (mandatory)
- **mypy**: <100 type errors (target: continuous improvement)
- **Black compliance**: 100% (enforced by pre-commit)
- **Test coverage**: >80% for critical modules

#### Android Code Quality:
- **Compilation**: All modules MUST compile without errors
- **ktlint**: 0 style violations
- **Circular dependencies**: None allowed
- **Memory leaks**: Lifecycle-aware components required

## Git Workflow

### Commit Message Format (Conventional Commits)
```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

#### Types:
- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, missing semicolons, etc.)
- **refactor**: Code refactoring without feature changes
- **test**: Adding or modifying tests
- **chore**: Maintenance tasks, dependency updates

#### Examples:
```
feat(thermal): Implement stereo calibration for dual cameras

Add stereo calibration algorithm using OpenCV for improved
temperature measurement accuracy across the thermal imaging field.

- Implement CalibrationProcessor class
- Add calibration data validation
- Include unit tests for calibration algorithms

Closes #123
```

```
fix(bluetooth): Resolve BLE scanning permission handling

Fix permission check logic that was preventing successful device
discovery on Android 12+ devices.

- Update permission request flow for location services
- Add runtime permission status checking
- Improve error messaging for permission denials

Fixes #456
```

### Branch Naming
- **Feature branches**: `feat/description-here`
- **Bug fixes**: `fix/description-here`
- **Documentation**: `docs/description-here`
- **Refactoring**: `refactor/description-here`

### Pull Request Requirements
1. **All quality checks MUST pass** (pre-commit hooks, CI/CD)
2. **Code review required** from at least one team member
3. **Documentation updated** for API changes
4. **Tests added/updated** for new functionality
5. **No merge commits** (use rebase or squash merge)

## Development Tools Setup

### Required Tools Installation

#### Python Environment:
```bash
# Install Python dependencies
pip install black isort flake8 mypy bandit pre-commit

# Configure tools
black --check pc-controller/src/
flake8 pc-controller/src/
mypy pc-controller/src/
```

#### Android Environment:
```bash
# Install ktlint
./gradlew ktlintCheck

# Run Android lint
./gradlew lint

# Build all modules
./gradlew assembleDebug
```

#### Quality Automation:
```bash
# Install and configure pre-commit
pre-commit install
pre-commit run --all-files

# Run security scan
bandit -r pc-controller/src/ -f json -o security-report.json
```

## Enforcement

### Automated Enforcement
- **Pre-commit hooks**: Block commits that violate standards
- **CI/CD pipeline**: Enforce quality gates on pull requests
- **Code review**: Human verification of complex changes

### Quality Review Process
1. **Automated checks**: All tools must pass
2. **Code review**: Focus on architecture, logic, and maintainability
3. **Documentation review**: Verify completeness and accuracy
4. **Integration testing**: Ensure compatibility across components

### Continuous Improvement
- **Monthly quality metrics review**: Track improvement over time
- **Standards updates**: Evolve guidelines based on project needs
- **Tool updates**: Keep quality tools current with security patches
- **Training**: Regular team training on coding standards and tools

---

## Quick Reference

### Essential Commands
```bash
# Format Python code
black pc-controller/src/

# Check Python style
flake8 pc-controller/src/

# Type checking
mypy pc-controller/src/

# Check Android style
./gradlew ktlintCheck

# Run all quality checks
pre-commit run --all-files

# Check for Chinese characters
./scripts/check-chinese-comments.sh

# Validate TODO formatting
./scripts/check-todos.sh
```

### Quality Targets Summary
- **Python**: 0 flake8 errors, <100 mypy errors, 100% Black compliance
- **Android**: 0 compilation errors, 0 ktlint violations, no circular dependencies
- **Documentation**: 100% English comments, comprehensive JavaDoc/KDoc
- **Security**: 0 high-severity security vulnerabilities
- **Git**: Conventional commits, proper branch naming, quality gates pass

This document is living and should be updated as the project evolves and new quality requirements are identified.