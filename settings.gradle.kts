dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
        maven { url = uri("https://developer.huawei.com/repo/") }
        
        // Official ShimmerAndroidAPI Maven repositories
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ShimmerEngineering/ShimmerAndroidAPI")
            credentials {
                username = System.getenv("GPR_USER")
                password = System.getenv("GPR_TOKEN")
            }
        }
        maven {
            name = "GitHubPackagesShimmerJava"
            url = uri("https://maven.pkg.github.com/ShimmerEngineering/Shimmer-Java-Android-API")
            credentials {
                username = System.getenv("GPR_USER")
                password = System.getenv("GPR_TOKEN")
            }
        }
        
        // Aliyun repositories as fallback
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        // Consolidated local AAR files directories
        flatDir {
            dirs("libir/libs", "libapp/libs", "app/libs", "libmatrix/libs", "BleModule/libs")
        }
    }
}

rootProject.name = "MPDC4GSR"

// Core application modules
include(":app")

// Active sensor and recording modules 
include(":component:thermal")
include(":component:thermal-ir")
include(":component:thermal-lite")
include(":component:pseudo")
include(":component:user")
include(":component:CommonComponent")
include(":component:gsr-recording") // Backup GSR recording implementation

// Active library modules
include(":libapp")
include(":libcom")
include(":libir")
include(":libmatrix")
include(":libui")
include(":libmenu")
include(":BleModule")
include(":RangeSeekBar")