plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}

android {
    namespace = "com.topdon.tc004"
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
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
    implementation(project(":libcom"))
    implementation(project(":libir"))
    implementation(project(":libui"))
    implementation(project(":libmenu"))
    
    kapt(Deps.arouter_compiler)
    
    implementation(Deps.androidx_core)
    implementation(Deps.appcompat)
    implementation(Deps.material)
    implementation(Deps.utilcode)
    implementation(Deps.glide)
}
