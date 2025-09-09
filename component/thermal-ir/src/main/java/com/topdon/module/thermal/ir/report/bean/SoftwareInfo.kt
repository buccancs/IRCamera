package com.topdon.module.thermal.ir.report.bean

import android.os.Build
import android.os.Parcelable
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.tools.AppLanguageUtils
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SoftwareInfo(
    val app_language: String,   // APP[CN_TEXT]
    val sdk_version: String,    // SDK[CN_TEXT]
) : Parcelable {

    val software_code = BaseApplication.instance.getSoftWareCode() //[CN_TEXT]
    val system_language = AppLanguageUtils.getSystemLanguage()// [CN_TEXT]
    val app_version = "1.10.000"//[CN_TEXT]
    val hardware_version = ""//[CN_TEXT]
    val app_sn = ""
    val mobile_phone_model = Build.BRAND//[CN_TEXT]
    val system_version = Build.VERSION.RELEASE//[CN_TEXT]
}