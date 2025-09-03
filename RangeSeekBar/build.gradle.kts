plugins {
    id("com.android.library")
}

group = "com.github.Jay-Goo"

android {
    namespace = "com.jaygoo.widget"
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.android.support:appcompat-v7:28.0.0")
}

// 指定编码
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// 打包源码
tasks.register<Jar>("sourcesJar") {
    from(android.sourceSets.getByName("main").java.srcDirs)
    archiveClassifier.set("sources")
}
