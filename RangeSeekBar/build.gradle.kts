plugins {
    id("com.android.library")
}

group = "com.github.Jay-Goo"

android {
    namespace = "com.jaygoo.widget"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    // Configure single release variant for easier maintenance
    androidComponents {
        beforeVariants { variant ->
            // Enable both debug and release variants for development efficiency
            variant.enable = variant.buildType == "debug" || variant.buildType == "release"
        }
    }
}

dependencies {
    // Core library desugaring support
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    
    // Use AndroidX instead of support libraries for compatibility
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.core:core:1.12.0")

    // Testing dependencies - using Robolectric for context-based testing
    testImplementation(libs.junit)
    testImplementation("org.robolectric:robolectric:4.10.3")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso.core)
}

// Set encoding
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Package source code
tasks.register<Jar>("sourcesJar") {
    from(android.sourceSets.getByName("main").java.srcDirs)
    archiveClassifier.set("sources")
}
