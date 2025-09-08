package com.topdon.lib.app.utils

import android.graphics.Bitmap

data class ScreenBean(
    /** bitmap property */
    val bitmap: Bitmap? = null,
    /** filePath property */
    val filePath: String = "",
    /** timestamp property */
    val timestamp: Long = System.currentTimeMillis()
)