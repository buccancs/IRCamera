// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
        // HMS repository for Huawei services
        maven { url = uri("https://developer.huawei.com/repo/") }
        // FCM repository for Firebase Cloud Messaging
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://maven.zohodl.com") }
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
        // HMS configuration for Huawei services
        classpath(libs.huawei.agconnect)
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}