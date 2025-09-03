plugins {
    id("com.android.library")
}

android {
    namespace = "com.topdon.ble"
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            buildConfigField("boolean", "DEBUG", "true")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("boolean", "DEBUG", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    buildFeatures {
        buildConfig = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    // LMS SDK - available transitively from libapp
    // api("com.topdon.lms.sdk:lms_international:3.90.009.0")
    api("androidx.appcompat:appcompat:1.2.0")
    api("org.greenrobot:eventbus:3.2.0")
    api("com.blankj:utilcodex:1.30.6") // 工具包
    api("com.google.code.gson:gson:2.8.8")
    api("com.elvishew:xlog:1.10.1")
    // UMeng Analytics - testing dependency availability
    // api("com.umeng.umsdk:analytics:9.4.0") 
    // FastJSON - testing dependency availability
    // api("com.alibaba:fastjson:1.2.83") 
    implementation(files("libs/ini4j-0.5.5.jar"))
}