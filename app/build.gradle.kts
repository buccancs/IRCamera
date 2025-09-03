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
    namespace = AndroidConfig.applicationId
    compileSdk = AndroidConfig.compileSdk
    
    defaultConfig {
        applicationId = AndroidConfig.applicationId
        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk
        versionCode = AndroidConfig.versionCode
        versionName = AndroidConfig.versionName
        ndkVersion = AndroidConfig.ndkVersion
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        
        ndk {
            abiFilters += listOf("arm64-v8a")
        }

        buildConfigField("String", "VERSION_DATE", "\"$dayStr\"")
        
        manifestPlaceholders["JPUSH_PKGNAME"] = applicationId!!
        manifestPlaceholders["JPUSH_APPKEY"] = "cbd4eafc9049d751fc5a8c58"
        manifestPlaceholders["JPUSH_CHANNEL"] = "developer-default"

        setProperty("archivesBaseName", "TC001-v${AndroidConfig.versionName}.google")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
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
            buildConfigField("String", "SOFT_CODE", "\"${SoftCode.topInfrared}\"")
            buildConfigField("String", "APP_KEY", "\"${AppKey.topInfrared}\"")
            buildConfigField("String", "APP_SECRET", "\"${AppSecret.topInfrared}\"")
            manifestPlaceholders["app_name"] = "TopInfrared"
        }
        create("beta") {
            dimension = "app"
            buildConfigField("int", "ENV_TYPE", "0")
            buildConfigField("String", "SOFT_CODE", "\"${SoftCode.topInfrared}\"")
            buildConfigField("String", "APP_KEY", "\"${AppKey.topInfrared}\"")
            buildConfigField("String", "APP_SECRET", "\"${AppSecret.topInfrared}\"")
            manifestPlaceholders["app_name"] = "IRCamera"
        }
        create("prod") {
            dimension = "app"
            buildConfigField("int", "ENV_TYPE", "0")
            buildConfigField("String", "SOFT_CODE", "\"${SoftCode.topInfrared}\"")
            buildConfigField("String", "APP_KEY", "\"${AppKey.topInfrared}\"")
            buildConfigField("String", "APP_SECRET", "\"${AppSecret.topInfrared}\"")
            manifestPlaceholders["app_name"] = "IRCamera"
        }
        create("prodTopdon") {
            dimension = "app"
            targetSdk = 27
            buildConfigField("int", "ENV_TYPE", "0")
            buildConfigField("String", "SOFT_CODE", "\"${SoftCode.topInfrared10}\"")
            buildConfigField("String", "APP_KEY", "\"${AppKey.topInfrared10}\"")
            buildConfigField("String", "APP_SECRET", "\"${AppSecret.topInfrared10}\"")
            manifestPlaceholders["app_name"] = "IRCamera"
        }
        create("insideChina") {
            dimension = "app"
            buildConfigField("int", "ENV_TYPE", "1")
            buildConfigField("String", "SOFT_CODE", "\"${SoftCode.topInfraredCN}\"")
            buildConfigField("String", "APP_KEY", "\"${AppKey.topInfraredCN}\"")
            buildConfigField("String", "APP_SECRET", "\"${AppSecret.topInfraredCN}\"")
            manifestPlaceholders["app_name"] = "热视界"
        }
        create("prodTopdonInsideChina") {
            dimension = "app"
            targetSdk = 27
            buildConfigField("int", "ENV_TYPE", "1")
            buildConfigField("String", "SOFT_CODE", "\"${SoftCode.topInfraredCN10}\"")
            buildConfigField("String", "APP_KEY", "\"${AppKey.topInfraredCN10}\"")
            buildConfigField("String", "APP_SECRET", "\"${AppSecret.topInfraredCN10}\"")
            val currentYear = SimpleDateFormat("yy", Locale.getDefault()).format(Date()).toInt()
            versionCode = AndroidConfig.versionCode + currentYear * 10000
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

    implementation(Deps.jsbridge)
    implementation(Deps.fastjson)
    implementation(Deps.ucrop)
    implementation(Deps.play_app_update)
    implementation(Deps.immersionbar)
    implementation(Deps.xpopup)
    implementation(Deps.smart_refresh_layout)
    implementation(Deps.smart_refresh_header)
    implementation(Deps.wechat_sdk)
    implementation(Deps.umeng_apm)
    implementation(Deps.zoho_salesiq)

    // Firebase - International versions
    implementation(platform(Deps.firebase_bom))
    "devImplementation"(Deps.firebase_crashlytics)
    "devImplementation"(Deps.firebase_analytics)
    "devImplementation"(Deps.firebase_messaging)
    "devImplementation"(Deps.firebase_iid)
    "betaImplementation"(Deps.firebase_crashlytics)
    "betaImplementation"(Deps.firebase_analytics)
    "betaImplementation"(Deps.firebase_messaging)
    "betaImplementation"(Deps.firebase_iid)
    "prodImplementation"(Deps.firebase_crashlytics)
    "prodImplementation"(Deps.firebase_analytics)
    "prodImplementation"(Deps.firebase_messaging)
    "prodImplementation"(Deps.firebase_iid)
    "prodTopdonImplementation"(Deps.firebase_crashlytics)
    "prodTopdonImplementation"(Deps.firebase_analytics)
    "prodTopdonImplementation"(Deps.firebase_messaging)
    "prodTopdonImplementation"(Deps.firebase_iid)

    // UMeng - Google customized version
    "betaImplementation"(files("libs/umeng-common-9.4.4+000.jar"))
    "devImplementation"(files("libs/umeng-common-9.4.4+000.jar"))
    "prodImplementation"(files("libs/umeng-common-9.4.4+000.jar"))
    "prodTopdonImplementation"(files("libs/umeng-common-9.4.4+000.jar"))

    // UMeng - China versions
    "insideChinaImplementation"(Deps.umeng_common)
    "insideChinaImplementation"(Deps.umeng_asms)
}

// Fix Google Services task dependency issue for Gradle 8.0+
tasks.whenTaskAdded {
    if (name.contains("merge") && name.contains("Resources")) {
        mustRunAfter(tasks.matching { it.name.contains("processGoogleServices") })
    }
}