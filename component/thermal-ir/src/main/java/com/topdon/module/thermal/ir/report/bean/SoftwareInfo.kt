package com.topdon.module.thermal.ir.report.bean

import android.os.Build
import android.os.Parcelable
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.tools.AppLanguageUtils
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SoftwareInfo(
    val app_language: String,   // App language setting
    val sdk_version: String,    // SDK version
) : Parcelable {

    val software_code = BaseApplication.instance.getSoftWareCode() // Data field
    val system_language = AppLanguageUtils.getSystemLanguage()// System language
    val app_version = "1.10.000"// Data field
    val hardware_version = ""// Data field
    val app_sn = ""
    val mobile_phone_model = Build.BRAND// Data field
    val system_version = Build.VERSION.RELEASE// Data field
}