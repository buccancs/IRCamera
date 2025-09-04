import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val dayStr = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())
val timeStr = SimpleDateFormat("HHmm", Locale.getDefault()).format(Date())

android {
    namespace = "com.csl.irCamera"
    compileSdk = libs.versions.compileSdk.get().toInt()
    
    defaultConfig {
        applicationId = "com.csl.irCamera"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        ndkVersion = libs.versions.ndkVersion.get()
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        
        ndk {
            abiFilters += listOf("arm64-v8a")
        }

        buildConfigField("String", "VERSION_DATE", "\"$dayStr\"")
        
        manifestPlaceholders["JPUSH_PKGNAME"] = applicationId!!
        manifestPlaceholders["JPUSH_APPKEY"] = "cbd4eafc9049d751fc5a8c58"
        manifestPlaceholders["JPUSH_CHANNEL"] = "developer-default"

        setProperty("archivesBaseName", "TC001-v${libs.versions.versionName.get()}.google")
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    signingConfigs {
        getByName("debug") {
            // Use default debug keystore - no need to specify custom keystore for debug builds
            // This allows development without requiring production keystores
        }
        create("release") {
            storeFile = file("artibox_key/ArtiBox.jks")
            keyAlias = "Artibox"
            storePassword = "artibox2017"
            keyPassword = "artibox2017"
            // Removed deprecated isV1SigningEnabled and isV2SigningEnabled
            // Modern signing uses enableV1Signing and enableV2Signing
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    packaging {
        resources {
            merges += listOf(
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE.md",
                "META-INF/proguard/androidx-annotations.pro",
                "META-INF/proguard/coroutines.pro"
            )
            pickFirsts += listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
            )
        }
        jniLibs {
            useLegacyPackaging = true
            pickFirsts += listOf(
                "lib/x86/libc++_shared.so",
                "lib/x86_64/libc++_shared.so",
                "lib/arm64-v8a/libc++_shared.so",
                "lib/armeabi-v7a/libc++_shared.so",
                "lib/arm64-v8a/libnative-window.so",
                "lib/armeabi-v7a/libnative-window.so",
                "lib/armeabi-v7a/libyuv.so",
                "lib/arm64-v8a/libyuv.so",
                "lib/armeabi-v7a/libopencv_java4.so",
                "lib/arm64-v8a/libopencv_java4.so",
                "lib/armeabi-v7a/libomp.so",
                "lib/arm64-v8a/libomp.so",
                "lib/arm64-v8a/liblog.so",
                "lib/armeabi-v7a/liblog.so"
            )
        }
    }
    
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
    
    flavorDimensions += "app"

    productFlavors {
        create("dev") {
            dimension = "app"
            buildConfigField("int", "ENV_TYPE", "0")
            buildConfigField("String", "SOFT_CODE", "\"${libs.versions.softcodeTopinfrared.get()}\"")
            buildConfigField("String", "APP_KEY", "\"${libs.versions.appkeyTopinfrared.get()}\"")
            buildConfigField("String", "APP_SECRET", "\"${libs.versions.appsecretTopinfrared.get()}\"")
            manifestPlaceholders["app_name"] = "TopInfrared"
        }
        create("beta") {
            dimension = "app"
            buildConfigField("int", "ENV_TYPE", "0")
            buildConfigField("String", "SOFT_CODE", "\"${libs.versions.softcodeTopinfrared.get()}\"")
            buildConfigField("String", "APP_KEY", "\"${libs.versions.appkeyTopinfrared.get()}\"")
            buildConfigField("String", "APP_SECRET", "\"${libs.versions.appsecretTopinfrared.get()}\"")
            manifestPlaceholders["app_name"] = "IRCamera"
        }
        create("prod") {
            dimension = "app"
            buildConfigField("int", "ENV_TYPE", "0")
            buildConfigField("String", "SOFT_CODE", "\"${libs.versions.softcodeTopinfrared.get()}\"")
            buildConfigField("String", "APP_KEY", "\"${libs.versions.appkeyTopinfrared.get()}\"")
            buildConfigField("String", "APP_SECRET", "\"${libs.versions.appsecretTopinfrared.get()}\"")
            manifestPlaceholders["app_name"] = "IRCamera"
        }
        create("prodTopdon") {
            dimension = "app"
            targetSdk = 27
            buildConfigField("int", "ENV_TYPE", "0")
            buildConfigField("String", "SOFT_CODE", "\"${libs.versions.softcodeTopinfrared10.get()}\"")
            buildConfigField("String", "APP_KEY", "\"${libs.versions.appkeyTopinfrared10.get()}\"")
            buildConfigField("String", "APP_SECRET", "\"${libs.versions.appsecretTopinfrared10.get()}\"")
            manifestPlaceholders["app_name"] = "IRCamera"
        }
        create("insideChina") {
            dimension = "app"
            buildConfigField("int", "ENV_TYPE", "1")
            buildConfigField("String", "SOFT_CODE", "\"${libs.versions.softcodeTopinfraredCn.get()}\"")
            buildConfigField("String", "APP_KEY", "\"${libs.versions.appkeyTopinfraredCn.get()}\"")
            buildConfigField("String", "APP_SECRET", "\"${libs.versions.appsecretTopinfraredCn.get()}\"")
            manifestPlaceholders["app_name"] = "热视界"
        }
        create("prodTopdonInsideChina") {
            dimension = "app"
            targetSdk = 27
            buildConfigField("int", "ENV_TYPE", "1")
            buildConfigField("String", "SOFT_CODE", "\"${libs.versions.softcodeTopinfraredCn10.get()}\"")
            buildConfigField("String", "APP_KEY", "\"${libs.versions.appkeyTopinfraredCn10.get()}\"")
            buildConfigField("String", "APP_SECRET", "\"${libs.versions.appsecretTopinfraredCn10.get()}\"")
            val currentYear = SimpleDateFormat("yy", Locale.getDefault()).format(Date()).toInt()
            versionCode = libs.versions.versionCode.get().toInt() + currentYear * 10000
            manifestPlaceholders["app_name"] = "热视界"
        }
    }
}

// Dependency resolution strategy to fix Guava conflicts
configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:31.1-android")
        exclude(group = "com.google.guava", module = "listenablefuture")
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
}

// APK naming function - Updated to use Variant instead of deprecated ApplicationVariant
fun getApkName(variantName: String, versionName: String): String {
    val nameStr = "TopInfrared_${versionName}.$dayStr"
    return when (variantName) {
        "devDebug" -> "TopInfrared-v$versionName-debug.apk"
        "devRelease" -> "$nameStr-release.apk"
        "betaDebug" -> "${nameStr}_beta_debug.apk"
        "betaRelease" -> "${nameStr}_beta.apk"
        "prodDebug" -> "${nameStr}_debug.apk"
        "prodRelease" -> "$nameStr.apk"
        "prodTopdonDebug" -> "TopInfrared_Android10_${versionName}.${dayStr}_debug.apk"
        "prodTopdonRelease" -> "TopInfrared_Android10_${versionName}.$dayStr.apk"
        "insideChinaDebug" -> "${nameStr}_debug.apk"
        "insideChinaRelease" -> "$nameStr.apk"
        "prodTopdonInsideChinaDebug" -> "${nameStr}_debug.apk"
        "prodTopdonInsideChinaRelease" -> "$nameStr.apk"
        else -> "TopInfrared.apk"
    }
}

// APK naming will be configured later
// android.applicationVariants.all { variant ->
//     variant.outputs.forEach { output ->
//         if (output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
//             output.outputFileName = getApkName(variant, AndroidConfig.versionName)
//         }
//     }
// }

dependencies {
    implementation(project(":component:edit3d"))
    implementation(project(":component:pseudo"))
    implementation(project(":component:thermal-ir"))
    implementation(project(":component:thermal-lite"))
    implementation(project(":component:transfer"))
    implementation(project(":component:user"))
    implementation(project(":component:gsr-recording"))
    implementation(project(":libapp"))
    implementation(project(":libcom"))
    implementation(project(":libir"))
    implementation(project(":libmenu"))
    implementation(project(":libui"))

    // LocalRepo AAR files moved to app/libs (excluding lms_international which stays in libapp)
    implementation(files("libs/libAC020sdk_USB_IR_1.1.1_2408291439.aar"))
    implementation(files("libs/libirutils_1.2.0_2409241055.aar"))
    implementation(files("libs/libcommon_1.2.0_24052117.aar"))

    implementation(libs.jsbridge)
    implementation(libs.fastjson)
    implementation(libs.ucrop)
    implementation(libs.play.app.update)
    implementation(libs.immersionbar)
    implementation(libs.xpopup)
    implementation(libs.smart.refresh.layout)
    implementation(libs.smart.refresh.header)
    implementation(libs.wechat.sdk)
    implementation(libs.umeng.apm)
    implementation(libs.zoho.salesiq)

    // Firebase - International versions
    implementation(platform(libs.firebase.bom))
    "devImplementation"(libs.firebase.crashlytics)
    "devImplementation"(libs.firebase.analytics)
    "devImplementation"(libs.firebase.messaging)
    "devImplementation"(libs.firebase.iid)
    "betaImplementation"(libs.firebase.crashlytics)
    "betaImplementation"(libs.firebase.analytics)
    "betaImplementation"(libs.firebase.messaging)
    "betaImplementation"(libs.firebase.iid)
    "prodImplementation"(libs.firebase.crashlytics)
    "prodImplementation"(libs.firebase.analytics)
    "prodImplementation"(libs.firebase.messaging)
    "prodImplementation"(libs.firebase.iid)
    "prodTopdonImplementation"(libs.firebase.crashlytics)
    "prodTopdonImplementation"(libs.firebase.analytics)
    "prodTopdonImplementation"(libs.firebase.messaging)
    "prodTopdonImplementation"(libs.firebase.iid)

    // UMeng - Google customized version
    "betaImplementation"(files("libs/umeng-common-9.4.4+000.jar"))
    "devImplementation"(files("libs/umeng-common-9.4.4+000.jar"))
    "prodImplementation"(files("libs/umeng-common-9.4.4+000.jar"))
    "prodTopdonImplementation"(files("libs/umeng-common-9.4.4+000.jar"))

    // UMeng - China versions
    "insideChinaImplementation"(libs.umeng.common)
    "insideChinaImplementation"(libs.umeng.asms)
}

// Fix Google Services task dependency issue for Gradle 8.0+
tasks.whenTaskAdded {
    if (name.contains("merge") && name.contains("Resources")) {
        mustRunAfter(tasks.matching { it.name.contains("processGoogleServices") })
    }
}