plugins {
    id("com.android.library")
    kotlin("android")
    // Temporarily disable KAPT to fix compilation issues
    // kotlin("kapt")
    kotlin("plugin.parcelize")
}

// kapt {
//     arguments {
//         arg("AROUTER_MODULE_NAME", project.name)
//     }
//     correctErrorTypes = true
// }

android {
    namespace = "com.example.thermal_lite"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        // targetSdk removed for library modules - only set in main app module per AGP 8.0+

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        
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
        isCoreLibraryDesugaringEnabled = true
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    // Core library desugaring support
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(project(":libapp"))
    implementation(project(":libcom"))
    implementation(project(":libir"))
    implementation(project(":libui"))
    implementation(project(":libmenu"))
    implementation(project(":component:CommonComponent"))
    implementation(project(":component:pseudo"))
    // Re-add thermal-ir dependency - needed for thermal-lite functionality
    implementation(project(":component:thermal-ir"))
    // Add commonlibrary dependency for thermal-lite
    implementation(project(":commonlibrary"))
    
    // Temporarily disable ARouter compiler until KAPT issues are resolved

    // Use shared UI bundle for common dependencies
    implementation(libs.bundles.ui.common)
    implementation(libs.utilcode)
    
    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso.core)
}
