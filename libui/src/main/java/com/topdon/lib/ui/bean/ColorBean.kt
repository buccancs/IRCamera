package com.topdon.lib.ui.bean

data class ColorBean(
    /** res property */
    val res: Int,
    /** name property */
    val name: String,
    /** code property */
    val code: Int,
    /** isSelect property */
    var isSelect : Boolean = false,
    /** n_res property */
    var n_res : Int = 0,
    /** isMutually property */
    var isMutually : Boolean = false//

)
