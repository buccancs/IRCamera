package com.topdon.lib.core.tools

import android.content.Context
import com.blankj.utilcode.util.Utils
import com.topdon.lib.core.R
import com.topdon.lib.core.common.SharedManager
import com.topdon.lib.core.tools.ConstantLanguages

object LanguageTool {

    fun showLanguage(context: Context): String {
        return context.getString(R.string.english)
    }

    fun useLanguage(context: Context): String {
        return "en-WW"
    }

    fun useStatementLanguage(): String {
        return "EN"
    }
}