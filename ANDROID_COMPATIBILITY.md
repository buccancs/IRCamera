# Android Compatibility Guide

This document outlines the Android compatibility improvements made to support a broader range of devices and Android versions.

## Supported Android Versions

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Tested on**: Android 7.0 - Android 15

## Device Compatibility

### Supported Architectures
- ARM 64-bit (arm64-v8a) - Primary
- ARM 32-bit (armeabi-v7a) - Secondary support

### Hardware Requirements
- **Camera**: Optional (for RGB recording features)
- **USB Host**: Optional (for thermal camera support)
- **Bluetooth**: Optional (for Shimmer3 GSR sensor)
- **Storage**: 500MB free space minimum

## Permission Handling

### Runtime Permissions by Android Version

#### Android 6.0+ (API 23+)
- Camera permissions requested at runtime
- Storage permissions for legacy devices

#### Android 10+ (API 29+)
- Scoped storage support
- Legacy external storage flag for compatibility

#### Android 11+ (API 30+)
- MANAGE_EXTERNAL_STORAGE for full file access
- Enhanced permission model

#### Android 12+ (API 31+)
- New Bluetooth permissions (BLUETOOTH_SCAN, BLUETOOTH_CONNECT)
- Location flag for Bluetooth scanning

#### Android 13+ (API 33+)
- Granular media permissions (READ_MEDIA_VIDEO, READ_MEDIA_IMAGES)
- Runtime notification permissions

#### Android 14+ (API 34+)
- Visual media picker permissions
- Enhanced security model

## Key Compatibility Features

### 1. Flexible Hardware Requirements
```xml
<!-- USB host support is optional -->
<uses-feature android:name="android.hardware.usb.host" android:required="false" />

<!-- Camera support is optional -->
<uses-feature android:name="android.hardware.camera" android:required="false" />
```

### 2. Version-Specific Permissions
```xml
<!-- Legacy storage for older devices -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
                 android:maxSdkVersion="32" />

<!-- Modern media permissions for new devices -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" 
                 android:minSdkVersion="33" />
```

### 3. Multi-ABI Support
Supports both 64-bit and 32-bit ARM architectures for broader device compatibility.

### 4. Enhanced App Configuration
```xml
android:extractNativeLibs="true"
android:largeHeap="true"
tools:targetApi="34"
```

## Common Issues and Solutions

### Issue: App crashes on older devices
**Solution**: Ensure minimum SDK version compatibility and test on target devices.

### Issue: Permissions denied on Android 13+
**Solution**: Request new granular media permissions instead of legacy storage permissions.

### Issue: Bluetooth functionality fails on Android 12+
**Solution**: Use new Bluetooth permissions with proper flags.

### Issue: USB device not detected
**Solution**: Ensure USB host feature is available and permissions are granted.

## Testing Recommendations

### Device Testing Matrix
- **Primary**: Samsung Galaxy S22 (Android 15)
- **Secondary**: Various Android 10-14 devices
- **Edge Cases**: Android 7-9 legacy devices

### Permission Testing
1. Fresh install permissions flow
2. Runtime permission requests
3. Permission denial handling
4. Settings app permission management

### Feature Testing
1. Thermal camera detection (with/without USB host)
2. Bluetooth GSR sensor connection
3. Camera recording functionality
4. File storage and access

## Migration Notes

### From Previous Versions
- Hardware requirements made optional for broader compatibility
- Permission model updated for modern Android versions
- Multi-ABI support added
- Storage handling modernized

### Breaking Changes
- Removed dependency on specific hardware features
- Updated permission requirements
- Changed storage access patterns

## Developer Guidelines

### Adding New Features
1. Make hardware dependencies optional where possible
2. Use version-specific permission patterns
3. Test on multiple Android versions
4. Handle permission denial gracefully

### Performance Considerations
- Large heap enabled for memory-intensive operations
- Native library extraction enabled for compatibility
- Multi-ABI support may increase APK size

## Troubleshooting

### Build Issues
- Ensure all required dependencies are available
- Check target SDK version compatibility
- Verify permission declarations

### Runtime Issues
- Check device logs for permission denials
- Verify hardware feature availability
- Test on different Android versions