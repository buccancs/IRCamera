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
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        // targetSdk = libs.versions.targetSdk.get().toInt()  // Deprecated in library modules
        ndkVersion = libs.versions.ndkVersion.get()

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
    // Large dependencies - downloaded at build time
    if (file("libs/suplib-release.aar").exists()) {
        api(files("libs/suplib-release.aar"))  // Required for thermal-lite iruvc classes
    }
    if (file("libs/ai-upscale-release.aar").exists()) {
        api(files("libs/ai-upscale-release.aar"))  // AI upscale functionality
    }
    if (file("libs/library_1.0.aar").exists()) {
        api(files("libs/library_1.0.aar"))  // Additional library support
    }
    api(files("libs/texturegesture-release.aar"))  // Texture gesture functionality
    api(files("libs/jetified-tas_api-1.0.4.0.aar"))  // TAS API
    
    // Enhanced IR-specific dependencies from user's Deps object
    api("com.conghuahuadan:superlayout:1.1.0")
    api(libs.ir.layout)  // IR layout utilities from CoderCaiSL jitpackMvn
    api(libs.compass.core.user)  // User's preferred compass core version
    api(libs.compass.sense.user)  // User's preferred compass sense version
    api(libs.javacv)  // JavaCV for IR image processing
    api(libs.javacpp)  // JavaCV native dependencies
    
    implementation(project(":libapp"))
    // LocalRepo:libcommon moved to app/libs - will be available transitively through app module
}