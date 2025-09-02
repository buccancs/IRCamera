dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.zohodl.com") }
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        flatDir {
            dirs("libir/libs")
        }
    }
}

rootProject.name = "TopInfrared"

include(":app")
include(":BleModule")
include(":commonlibrary")
include(":component:CommonComponent")
include(":component:edit3d")
include(":component:house")
include(":component:pseudo")
include(":component:thermal")
include(":component:thermal-hik")
include(":component:thermal-ir")
include(":component:thermal-lite")
include(":component:thermal04")
include(":component:thermal07")
include(":component:transfer")
include(":component:user")
include(":component:gsr-recording")
include(":libapp")
include(":libcom")
include(":libhik")
include(":libir")
include(":libir-demo")
include(":libmatrix")
include(":libmenu")
include(":libui")
include(":LocalRepo:libac020")
include(":LocalRepo:libcommon")
include(":LocalRepo:libirutils")
include(":RangeSeekBar")