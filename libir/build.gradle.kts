plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    // Removed KAPT - no longer needed without ARouter
    // id("kotlin-kapt")
}

// Removed KAPT configuration - no longer needed without ARouter
// kapt {
//     arguments {
//         arg("AROUTER_MODULE_NAME", project.name)
//     }
// }

android {
    namespace = "com.infisense.usbir"
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        // targetSdk = AndroidConfig.targetSdk  // Deprecated in library modules
        ndkVersion = AndroidConfig.ndkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs", "src/main/jnilibs")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    
    // Original libir AAR dependencies restored - all essential components enabled
    api(files("libs/libusbdualsdk_1.3.4_2406271906_standard.aar"))  // Required for infisense thermal camera classes
    implementation(files("libs/opengl_1.3.2_standard.aar"))  // OpenGL functionality
    api(files("libs/suplib-release.aar"))  // Required for thermal-lite iruvc classes
    api(files("libs/ai-upscale-release.aar"))  // AI upscale functionality
    api(files("libs/texturegesture-release.aar"))  // Texture gesture functionality
    api(files("libs/jetified-tas_api-1.0.4.0.aar"))  // TAS API
    api(files("libs/library_1.0.aar"))  // Additional library support
    
    api("com.conghuahuadan:superlayout:1.1.0")
    implementation(project(":libapp"))
    // LocalRepo:libcommon moved to app/libs - will be available transitively through app module
}