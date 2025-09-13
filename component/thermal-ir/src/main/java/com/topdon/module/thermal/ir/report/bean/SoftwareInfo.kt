package com.topdon.module.thermal.ir.report.bean

import android.os.Build
import android.os.Parcelable
import com.topdon.lib.core.BaseApplication
import com.topdon.lib.core.tools.AppLanguageUtils
import kotlinx.android.parcel.Parcelize

/**
 * Software info utility class for thermal imaging operations.
 * Provides helper functions and common functionality.
 */
@Parcelize
data class SoftwareInfo(
 val app_language: String, // APP
 val sdk_version: String, // SDK
) : Parcelable {
 val software_code = BaseApplication.instance.getSoftWareCode() // 
 val system_language = AppLanguageUtils.getSystemLanguage() // 
 val app_version = "1.10.000" // 
 val hardware_version = "" // 
 val app_sn = ""
 val mobile_phone_model = Build.BRAND // 
 val system_version = Build.VERSION.RELEASE // 
}
