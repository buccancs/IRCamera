plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs", "src/main/jnilibs")
        }
    }
    flavorDimensions += "app"
    productFlavors {
        create("dev") {
            dimension = "app"
        }
        create("beta") {
            dimension = "app"
        }
        create("prod") {
            dimension = "app"
        }
        create("prodTopdon") {
            dimension = "app"
        }
        create("insideChina") {
            dimension = "app"
        }
        create("prodTopdonInsideChina") {
            dimension = "app"
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    
    // AAR dependencies from libs folder
    api(files("libs/libusbdualsdk_1.3.4_2406271906_standard.aar"))
    implementation(files("libs/opengl_1.3.2_standard.aar"))
    api(files("libs/suplib-release.aar"))
    api(files("libs/ai-upscale-release.aar"))
    
    api("com.conghuahuadan:superlayout:1.1.0")
    implementation(project(":libapp"))
    api(project(":LocalRepo:libcommon"))
}