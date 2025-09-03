plugins {
    id("com.android.library")
}

android {
    namespace = "com.energy.commoncomponent"
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        // targetSdk = AndroidConfig.targetSdk  // Deprecated in library modules

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Generate APK with specified platform so libraries
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(Deps.appcompat)
    implementation(Deps.material)
    implementation("androidx.multidex:multidex:2.0.1")
    implementation(Deps.utilcode)

    implementation(Deps.lifecycle_runtime_ktx)
    implementation(Deps.lifecycle_viewmodel_ktx)
    implementation(Deps.lifecycle_livedata_ktx)
}
