plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

kapt {
    arguments {
        // Disable ARouter KAPT processing - migrating to modern navigation
        // arg("AROUTER_MODULE_NAME", project.name)
        // arg("AROUTER_GENERATE_DOC", "enable")//生成doc文档
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
    }
}

android {
    namespace = "com.topdon.lib.core"
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }
}

//kotlin {
//    experimental {
//        coroutines 'enable'
//    }
//}

dependencies {
    // Using only JAR files to avoid AGP 8.0+ AAR dependency issues
    api(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    api(Deps.appcompat)
    api(Deps.fragment_ktx)
    api(Deps.material)

    api(Deps.lifecycle_runtime_ktx)
    api(Deps.lifecycle_viewmodel_ktx)
    api(Deps.lifecycle_livedata_ktx)

    kapt(Deps.room_compiler)
    api(Deps.room_ktx)

    api(Deps.work_runtime_ktx)

    api(Deps.retrofit2)
    api(Deps.converter_gson)
    api(Deps.adapter_rxjava2)

    api(Deps.eventbus)

    api(Deps.glide)
    kapt(Deps.glide_compiler)

    api(Deps.rxjava2)
    api(Deps.rxandroid)
    // Temporarily disabled problematic dependencies - using modern alternatives
    // api(Deps.rxpermissions)
    // api(Deps.rxlifecycle)
    // api(Deps.rxlifecycle_android)
    // api(Deps.rxlifecycle_components)
    // api(Deps.rxlifecycle_ktx)
    // api(Deps.rxlifecycle_android_lifecycle_ktx)

    api(Deps.utilcode)
    api(Deps.XXPermissions)
    api(Deps.xlog)
    api(Deps.PhotoView)
    // api(Deps.android_pdf_viewr) // Temporary comment out due to dependency resolution issues
    api(Deps.lottie)

    api(Deps.brvah)
    // api(Deps.refresh_layout_kernel) // Temporary comment out
    // api(Deps.refresh_header_classics) // Temporary comment out
    // api(Deps.refresh_header_material) // Temporary comment out

    api(Deps.logging_interceptor)
    api(Deps.colorpickerview)
    // api(Deps.MNImageBrowser) // Temporary comment out
    api(Deps.nifty)
    // api(Deps.nifty_effect) // Temporary comment out

//    "devApi"(Deps.lms2)
//    "betaApi"(Deps.lms2)
//    "prodApi"(Deps.lms2)
//    "prodTopdonApi"(Deps.lms2)
//    "insideChinaApi"(Deps.lms3)
//    "prodTopdonInsideChinaApi"(Deps.lms3)

    // JavaCV
    api(Deps.javacv)
    api(Deps.javacpp)
    // Local AAR dependencies - using proper flatDir repository approach
    // These provide critical functionality (CommonBean, ResponseBean, LMS classes)
    api("abtest:abtest:1.0.1@aar")
    api("auth-number:auth-number:2.13.2.1@aar") 
    api("lms_international:lms_international:3.90.009.0@aar")
    api("logger:logger:2.2.1-release@aar")
    api("main:main:2.2.1-release@aar")
}