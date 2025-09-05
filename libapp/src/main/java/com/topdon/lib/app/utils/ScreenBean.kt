package com.topdon.lib.app.utils

import android.graphics.Bitmap

data class ScreenBean(
    val bitmap: Bitmap? = null,
    val filePath: String = "",
    val timestamp: Long = System.currentTimeMillis()
)