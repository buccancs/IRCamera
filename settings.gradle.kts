dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
        maven { url = uri("https://developer.huawei.com/repo/") }
        maven { url = uri("https://maven.google.com") }
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://maven.zohodl.com") }
        // Local AAR files directories
        flatDir {
            dirs("libir-demo/libs")
        }
        flatDir {
            dirs("libir/libs")
        }
        flatDir {
            dirs("libapp/libs")
        }
        flatDir {
            dirs("LocalRepo/libac020")
        }
        flatDir {
            dirs("LocalRepo/libirutils")
        }
        flatDir {
            dirs("LocalRepo/libcommon")
        }
        flatDir {
            dirs("commonlibrary")
        }

        flatDir {
            dirs("component/edit3d/libs")
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