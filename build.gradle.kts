// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
        // HMS repository for Huawei services
        maven { url = uri("https://developer.huawei.com/repo/") }
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://maven.zohodl.com") }
        // Aliyun repositories as fallback
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        // HMS configuration for Huawei services
        classpath(libs.huawei.agconnect)
    }
}

// Development build optimization - configure all subprojects with fast build settings
allprojects {
    // Apply common optimization to all modules
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            // Parallel compilation across all modules
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.addAll(listOf(
                "-Xuse-k2", 
                "-Xskip-prerelease-check"
            ))
        }
    }
    
    // Enable parallel Java compilation across modules
    tasks.withType<JavaCompile> {
        options.isFork = true
        options.forkOptions.jvmArgs = listOf("-Xmx2048m")
        options.isIncremental = true
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory.get().asFile)
}