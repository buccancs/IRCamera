package com.topdon.lib.core.bean.tools

import android.graphics.Bitmap

/**
 * Screen capture bean for thermal screenshots
 */
data class ScreenBean(
    var bitmap: Bitmap? = null,
    var filePath: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var width: Int = 0,
    var height: Int = 0
)