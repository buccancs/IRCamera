package com.topdon.module.thermal.ir.report.bean

import android.os.Build
import android.os.Parcelable
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.tools.AppLanguageUtils
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SoftwareInfo(
    val app_language: String,   // APP[Chinese text]
    val sdk_version: String,    // SDK[Chinese text]
) : Parcelable {

    val software_code = BaseApplication.instance.getSoftWareCode() //[Chinese text]
    val system_language = AppLanguageUtils.getSystemLanguage()// [Chinese text]
    val app_version = "1.10.000"//[Chinese text]
    val hardware_version = ""//[Chinese text]
    val app_sn = ""
    val mobile_phone_model = Build.BRAND//[Chinese text]
    val system_version = Build.VERSION.RELEASE//[Chinese text]
}