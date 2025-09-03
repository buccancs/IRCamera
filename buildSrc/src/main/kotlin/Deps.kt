object Deps {
    // AndroidX
    const val androidx_core = "androidx.core:core-ktx:1.9.0"
    const val androidx_appcompat = "androidx.appcompat:appcompat:1.6.1"
    const val appcompat = "androidx.appcompat:appcompat:1.6.1"
    const val fragment_ktx = "androidx.fragment:fragment-ktx:1.5.5"
    const val material = "com.google.android.material:material:1.8.0"
    const val multidex = "androidx.multidex:multidex:2.0.1"
    
    // Lifecycle
    const val lifecycle_runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:2.6.2"
    const val lifecycle_viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2"
    const val lifecycle_livedata_ktx = "androidx.lifecycle:lifecycle-livedata-ktx:2.6.2"
    
    // Room - Updated to 2.5.0 for AGP 8.0 compatibility
    const val room_compiler = "androidx.room:room-compiler:2.5.0"
    const val room_ktx = "androidx.room:room-ktx:2.5.0"
    
    // Work Manager
    const val work_runtime_ktx = "androidx.work:work-runtime-ktx:2.8.1"
    
    // Kotlin
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:1.8.20"
    const val kotlinx_coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
    const val kotlinx_coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
    
    // Networking
    const val fastjson = "com.alibaba:fastjson:1.2.78"
    const val jsbridge = "com.github.lzyzsd:jsbridge:1.0.4"
    const val retrofit2 = "com.squareup.retrofit2:retrofit:2.9.0"
    const val converter_gson = "com.squareup.retrofit2:converter-gson:2.9.0"
    const val adapter_rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:2.9.0"
    const val logging_interceptor = "com.squareup.okhttp3:logging-interceptor:4.10.0"
    
    // RxJava
    const val rxjava2 = "io.reactivex.rxjava2:rxjava:2.2.21"
    const val rxandroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    // Updated RxLifecycle dependencies with available versions
    const val rxpermissions = "com.github.tbruyelle:rxpermissions:0.10.2"
    const val rxlifecycle = "com.trello.rxlifecycle2:rxlifecycle:2.2.2"
    const val rxlifecycle_android = "com.trello.rxlifecycle2:rxlifecycle-android:2.2.2"
    const val rxlifecycle_components = "com.trello.rxlifecycle2:rxlifecycle-components:2.2.2"
    // Using available versions for KTX extensions
    const val rxlifecycle_ktx = "com.trello.rxlifecycle3:rxlifecycle-kotlin:3.1.0"
    const val rxlifecycle_android_lifecycle_ktx = "com.trello.rxlifecycle3:rxlifecycle-android-lifecycle-kotlin:3.1.0"
    
    // UI Libraries
    const val ucrop = "com.github.yalantis:ucrop:2.2.4"
    const val play_app_update = "com.google.android.play:app-update:2.1.0"
    const val immersionbar = "com.gyf.immersionbar:immersionbar:3.0.0"
    const val xpopup = "com.github.li-xiaojun:XPopup:2.9.0"
    const val smart_refresh_layout = "com.scwang.smartrefresh:SmartRefreshLayout:1.1.3"
    const val smart_refresh_header = "com.scwang.smartrefresh:SmartRefreshHeader:1.1.3"
    // UI Libraries - using available versions
    const val refresh_layout_kernel = "com.scwang.smart:refresh-layout-kernel:2.0.3"
    const val refresh_header_classics = "com.scwang.smart:refresh-header-classics:2.0.5"
    const val refresh_header_material = "com.scwang.smart:refresh-header-material:2.0.5"
    const val brvah = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.7"
    const val colorpickerview = "com.github.skydoves:colorpickerview:2.2.4"
    const val PhotoView = "com.github.chrisbanes:PhotoView:2.3.0"
    // const val MNImageBrowser = "com.github.maning0303:MNImageBrowser:v2.1.9"  // Temporary comment out
    const val nifty = "com.nineoldandroids:library:2.4.0"
    // const val nifty_effect = "com.github.sd6352051:NiftyDialogEffects:1.0.0"  // Temporary comment out
    
    // Image Loading
    const val glide = "com.github.bumptech.glide:glide:4.14.2"
    const val glide_compiler = "com.github.bumptech.glide:compiler:4.14.2"
    
    // Utils
    const val utilcode = "com.blankj:utilcodex:1.31.1"
    const val XXPermissions = "com.github.getActivity:XXPermissions:16.8"
    const val xlog = "com.elvishew:xlog:1.10.1"
    // const val android_pdf_viewr = "com.github.barteksc:AndroidPdfViewer:3.2.0-beta.1"  // Temporary comment out
    const val lottie = "com.airbnb.android:lottie:5.2.0"
    const val eventbus = "org.greenrobot:eventbus:3.3.1"
    
    // Compass and sensor libraries - using stable version from Maven Central
    const val andromeda_core = "com.kylecorry.andromeda:core:15.3.0"
    const val andromeda_sense = "com.kylecorry.andromeda:sense:15.3.0"
    
    // JavaCV
    const val javacv = "org.bytedeco:javacv:1.5.7"
    const val javacpp = "org.bytedeco:javacpp:1.5.7"
    
    // Third-party services
    const val wechat_sdk = "com.tencent.mm.opensdk:wechat-sdk-android:6.8.0"
    const val umeng_apm = "com.umeng.umsdk:apm:1.9.0"
    const val zoho_salesiq = "com.zoho.salesiq:mobilisten:7.0.0"
    
    // Firebase
    const val firebase_bom = "com.google.firebase:firebase-bom:28.4.1"
    const val firebase_crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val firebase_analytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebase_messaging = "com.google.firebase:firebase-messaging:21.0.1"
    const val firebase_iid = "com.google.firebase:firebase-iid"
    
    // UMeng
    const val umeng_common = "com.umeng.umsdk:common:9.6.7"
    const val umeng_asms = "com.umeng.umsdk:asms:1.8.0"
    
    // Testing
    const val junit = "junit:junit:4.13.2"
    const val test_ext_junit = "androidx.test.ext:junit:1.1.5"
    const val test_espresso_core = "androidx.test.espresso:espresso-core:3.5.1"
    
    // License (may be commented dependencies)
    const val lms2 = "com.example:lms2:1.0.0" // Placeholder - needs actual implementation
    const val lms3 = "com.example:lms3:1.0.0" // Placeholder - needs actual implementation
}