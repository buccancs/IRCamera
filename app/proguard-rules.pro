# === IR CAMERA PROGUARD RULES - MODERN ANDROID ===
# Updated for Kotlin 2.0, AGP 8.1.4, and enhanced security

# === DEBUGGING CONFIGURATION ===
# Keep line numbers for better crash reports in production
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# === MODERN KOTLIN & ANDROIDX SUPPORT ===
# Kotlin metadata and reflection support
-keepattributes *Annotation*,Signature,Exception,InnerClasses,EnclosingMethod
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Kotlin Coroutines support
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# === ANDROIDX & MATERIAL DESIGN ===
# ViewBinding classes - keep all binding classes
-keep class **.*Binding { *; }
-keep class **.*BindingImpl { *; }

# Material Design Components
-keep class com.google.android.material.** { *; }

# RecyclerView and ViewHolders
-keep class androidx.recyclerview.widget.** { *; }
-keepclassmembers class * extends androidx.recyclerview.widget.RecyclerView$ViewHolder {
    public <init>(...);
}

# === REFLECTION & SERIALIZATION ===
# Gson serialization support
-keepattributes Signature
-keep class com.google.gson.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# === THIRD-PARTY LIBRARIES ===
# RxJava
-keep class io.reactivex.** { *; }
-dontwarn io.reactivex.**

# Retrofit & OkHttp
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**

# Glide image loading
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.** { *; }

# EventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# ARouter navigation
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# === THERMAL IR SPECIFIC ===
# Keep all thermal processing classes
-keep class com.topdon.module.thermal.** { *; }
-keep class com.topdon.thermal.** { *; }
-keep class com.topdon.libir.** { *; }

# JavaCV and OpenCV native libraries
-keep class org.bytedeco.** { *; }
-keepclassmembers class org.bytedeco.** { *; }

# GSY Video Player
-keep class com.shuyu.gsyvideoplayer.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.**

# === SECURITY & OBFUSCATION ===
# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove debug and test code
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# === R8 OPTIMIZATIONS ===
# Enable advanced R8 optimizations
-allowaccessmodification
-overloadaggressively
-repackageclasses ''

# === NATIVE CODE PROTECTION ===
# Keep native method signatures
-keepclasseswithmembernames class * {
    native <methods>;
}

# === WEBVIEW SECURITY ===
# Secure WebView JavaScript interfaces
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# === ERROR SUPPRESSION ===
# Suppress warnings for known safe libraries
-dontwarn java.lang.invoke.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.animal_sniffer.**