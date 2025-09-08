package com.topdon.module.thermal.ir.report.bean

import android.os.Build
import android.os.Parcelable
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.tools.AppLanguageUtils
import com.topdon.module.thermal.ir.BuildConfig
import kotlinx.parcelize.Parcelize

@Parcelize
data class SoftwareInfo(
    /** app_language property */
    val app_language: String, // APP
    /** sdk_version property */
    val sdk_version: String, // SDK
) : Parcelable {

    /** software_code property */
    val software_code = BaseApplication.instance.getSoftWareCode() //
    /** system_language property */
    val system_language = AppLanguageUtils.getSystemLanguage()//
    /** app_version property */
    val app_version = BuildConfig.VERSION_NAME//
    /** hardware_version property */
    val hardware_version = ""//
    /** app_sn property */
    val app_sn = ""
    /** mobile_phone_model property */
    val mobile_phone_model = Build.BRAND//
    /** system_version property */
    val system_version = Build.VERSION.RELEASE//
}