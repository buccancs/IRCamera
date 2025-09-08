package com.topdon.lib.core.utils

import android.content.Context
import android.content.res.Configuration

object ScreenUtil {
    @JvmStatic
    /**
     * Function description.
     */
    fun getScreenWidth(context: Context): Int = context.resources.displayMetrics.widthPixels

    @JvmStatic
    /**
     * Function description.
     */
    fun getScreenHeight(context: Context): Int = context.resources.displayMetrics.heightPixels

    @JvmStatic
    /**
     * Function description.
     */
    fun isPortrait(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    @JvmStatic
    /**
     * Function description.
     */
    fun isLandscape(context: Context): Boolean {
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}