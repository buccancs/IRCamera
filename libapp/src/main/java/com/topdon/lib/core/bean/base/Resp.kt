package com.topdon.lib.core.bean.base

import android.text.TextUtils

class Resp<T> {

    /** code property */
    var code: String = ""
    /** msg property */
    var msg: String = ""
    /** data property */
    var data: T? = null

    /**
     * Function description.
     */
    fun isSuccess(): Boolean {
        return TextUtils.equals(code, "0")
    }

    override fun toString(): String {
        return "Resp(code='$code', msg='$msg', data=$data)"
    }

}
