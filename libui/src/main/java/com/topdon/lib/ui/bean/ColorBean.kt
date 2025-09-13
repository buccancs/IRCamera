package com.topdon.lib.ui.bean

/**
 * Color data model for thermal imaging information.
 * Encapsulates thermal measurement and configuration data.
 */
data class ColorBean(
    val res: Int,
    val name: String,
    val code: Int,
    var isSelect: Boolean = false,
    var n_res: Int = 0,
    var isMutually: Boolean = false, 
)
