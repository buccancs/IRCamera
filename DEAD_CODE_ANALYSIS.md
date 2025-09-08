# Dead Code Analysis for IRCamera Repository

## Executive Summary

Based on comprehensive analysis of the repository structure, build files, and code dependencies, this report identifies significant opportunities to make the repository more lean by removing dead code, unused features, and obsolete folders.

## Major Findings

### 1. **Unused Component Modules (MAJOR IMPACT)**

The following component modules are **NOT** used by the main app but are still included in the build:

- **`component:CommonComponent`** (436K) - Not referenced in app/build.gradle.kts
- **`component:edit3d`** (432K) - Contains 3D editing functionality unrelated to MPDC4GSR 
- **`component:house`** (968K) - House inspection features unrelated to physiological sensing
- **`component:transfer`** (156K) - File transfer functionality, potentially redundant

**Total savings**: ~2MB of unused component code

### 2. **Migration Backup Folder (MAJOR IMPACT)**

- **`migration_backup_20250902_153037/`** (2.1MB) - Contains 100+ old activity/fragment files
- This appears to be a historical backup from a migration that should be removed
- Files like `ReportPreviewActivity.kt`, `HouseDetectView.kt` are duplicated

### 3. **Template Test Files (MODERATE IMPACT)**

Found **18 template test files** across all modules:
```
ExampleUnitTest.kt/java - Basic 2+2=4 tests
ExampleInstrumentedTest.kt/java - Android instrumentation templates
```

**Locations:**
- libmenu, libhik, libcom, RangeSeekBar
- component/transfer, component/thermal-lite, component/house, component/edit3d, component/pseudo

### 4. **Backup Files (MINOR IMPACT)**

Found **3 backup files** that should be removed:
- `pc-controller/src/ircamera_pc/gui/widgets.py.backup`
- `libir/build.gradle.backup`
- `component/gsr-recording/src/main/java/com/topdon/gsr/service/GSRRecorder.kt.backup`

### 5. **Commented-Out Code (MODERATE IMPACT)**

**FolderUtil.java** contains extensive commented-out directory creation code:
```java
// log6File.mkdirs(); - Lines for log directories 666666, 777777, 888888, 999999
```

**JNITest.java** has commented library loading:
```java
// System.loadLibrary("SRImage");
// System.loadLibrary("minMaxTemperatureDetect");
```

### 6. **Excessive Path Management (MODERATE IMPACT)**

**FolderUtil.java** creates directories for many unrelated features:
- Vehicle diagnostics (America, Europe, Asia paths)
- IMMO (immobilizer) functionality
- RFID paths
- AutoVin logging paths

Many of these seem unrelated to the core MPDC4GSR physiological sensing platform.

### 7. **Production Artifacts (MINOR IMPACT)**

- `production_artifacts/` folder contains build reports that could be cleaned up

## Detailed Component Analysis

### Components NOT Used by Main App:
1. **`component:CommonComponent`** - Not referenced in app dependencies
2. **`component:edit3d`** - 3D editing unrelated to physiological sensing  
3. **`component:house`** - House inspection features (HouseHomeActivity, house reports)
4. **`component:transfer`** - File transfer functionality

### Components Used by Main App:
- `component:thermal` ✓ (consolidated thermal functionality)
- `component:thermal-ir` ✓ (thermal IR resources)
- `component:thermal-lite` ✓ (thermal lite functionality)
- `component:pseudo` ✓ (pseudo color functionality)
- `component:gsr-recording` ✓ (GSR sensor recording)
- `component:user` ✓ (user module for settings)

**Note:** `component:house` is used by `component:thermal-ir` but thermal-ir could be refactored to remove this dependency.

## Impact Assessment

### High Impact Removals (Recommended):
1. **Migration backup folder** → -2.1MB, 100+ files
2. **Unused components** → -2MB, simplified build
3. **Template test files** → -18 files, cleaner structure

### Medium Impact Removals:
1. **Commented-out code** → Improved readability
2. **Backup files** → -3 files
3. **Unused path management** → Simplified FolderUtil

### Low Risk Removals:
1. **Production artifacts** → Historical build reports

## Recommendations

### Phase 1: Safe Removals (Immediate)
- [ ] Remove migration backup folder
- [ ] Remove all ExampleUnitTest/ExampleInstrumentedTest files
- [ ] Remove .backup files
- [ ] Clean up production artifacts

### Phase 2: Module Cleanup (Requires Testing)
- [ ] Remove component:CommonComponent 
- [ ] Remove component:edit3d
- [ ] Remove component:transfer
- [ ] Evaluate component:house removal (used by thermal-ir)

### Phase 3: Code Cleanup
- [ ] Remove commented-out code in FolderUtil.java
- [ ] Remove commented-out library loading in JNITest.java
- [ ] Simplify path management in FolderUtil for MPDC4GSR focus

## Estimated Total Savings

- **File count**: ~125+ files removed
- **Storage**: ~4MB+ reduction
- **Build complexity**: Significantly simplified
- **Maintenance**: Reduced surface area for bugs

## Risk Assessment

- **Low Risk**: Template tests, backup files, migration folder
- **Medium Risk**: Unused components (need dependency verification)
- **High Risk**: Core path management changes

## Verification Plan

1. Remove safe items first
2. Build and test after each removal
3. Verify no runtime dependencies on removed components
4. Test core MPDC4GSR functionality remains intact