# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Keep thermal camera classes
-keep class com.topdon.tc001.sensors.** { *; }
-keep class com.infisense.usbir.** { *; }
-keep class com.energy.iruvc.** { *; }

# Keep sensor recorder interfaces
-keep class * implements com.topdon.tc001.sensors.SensorRecorder { *; }

# Keep USB related classes
-keep class android.hardware.usb.** { *; }

# Keep OpenCSV classes
-keep class com.opencsv.** { *; }

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile