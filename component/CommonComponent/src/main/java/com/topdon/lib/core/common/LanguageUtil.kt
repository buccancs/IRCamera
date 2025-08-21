package com.topdon.lib.core.common

import android.content.Context

/**
 * Language utility for locale management
 */
object LanguageUtil {
    
    /**
     * Get current language from context
     */
    fun getCurrentLanguage(context: Context): String {
        return context.resources.configuration.locales[0].language
    }
    
    /**
     * Check if current locale is Chinese
     */
    fun isZhCn(context: Context): Boolean {
        val language = getCurrentLanguage(context)
        return language.startsWith("zh")
    }
    
    /**
     * Get language ID for API calls
     */
    fun getLanguageId(context: Context): Int {
        return when (getCurrentLanguage(context)) {
            "zh" -> 1
            "es" -> 2
            "fr" -> 3
            "de" -> 4
            "ja" -> 5
            "ko" -> 6
            else -> 0 // Default to English
        }
    }
}