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
    namespace = "com.topdon.libcom"
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        // targetSdk = AndroidConfig.targetSdk  // Deprecated in library modules

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
    implementation(project(":libui"))

    kapt(Deps.arouter_compiler)
    api(Deps.colorpickerview)
    implementation(Deps.brvah)

    implementation("org.apache.poi:poi-ooxml:3.17")
    implementation("org.apache.xmlbeans:xmlbeans:3.1.0")
    implementation("javax.xml.stream:stax-api:1.0")
    implementation("com.fasterxml:aalto-xml:1.2.2")
    implementation(project(":RangeSeekBar"))
}