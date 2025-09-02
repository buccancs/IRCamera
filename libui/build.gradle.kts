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
    namespace = "com.topdon.lib.ui"
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(project(":libapp"))
    implementation(project(":libmenu")) // 伪彩条用着 PseudoColorConfig
    kapt(Deps.arouter_compiler)
}