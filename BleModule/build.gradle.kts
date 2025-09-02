plugins {
    id("com.android.library")
}

android {
    namespace = "com.topdon.ble"
    compileSdk = AndroidConfig.compileSdk
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdk = AndroidConfig.minSdk
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    flavorDimensions += "versionCode"
    productFlavors {
        create("nj1000pro") {
            dimension = "versionCode"
        }
        // AD900
        create("ultradiag") {
            dimension = "versionCode"
        }
        // ArtiDiag900 ArtiDiag900Lite
        // ArtiDiag900 ArtiDiag900Lite 打包时需要修改DiagnoseModule和BluetoothModule 中的进程名称 进程名：com.topdon.diagnose.adliteservice  Constants.mAppVersion  此字段也需要修改
        create("artidiag900lite") {
            dimension = "versionCode"
        }
        create("topscan") {
            dimension = "versionCode"
        }
        create("keynow") {
            dimension = "versionCode"
        }
    }
}

dependencies {
    api("com.topdon.lms.sdk2:lms:3.80.005")
    api("androidx.appcompat:appcompat:1.2.0")
    api("org.greenrobot:eventbus:3.2.0")
    api("com.blankj:utilcodex:1.30.6") // 工具包
    api("com.google.code.gson:gson:2.8.8")
    api("com.elvishew:xlog:1.10.1")
    implementation(files("libs/ini4j-0.5.5.jar"))
}