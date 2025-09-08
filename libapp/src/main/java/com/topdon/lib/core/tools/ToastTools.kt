package com.topdon.lib.core.tools

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.Utils
import com.csl.irCamera.libapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * create by fylder on 2018/7/4
 **/
object ToastTools {

    /** mPublicToast property */
    var mPublicToast: Toast? = null

    /**
     * Function description.
     */
    fun showShort(@StringRes textStr: Int) {
        showShort(Utils.getApp().getString(textStr))
    }

    /**
     * Function description.
     */
    fun showShort(textStr: String) {
        showShort(textStr, Toast.LENGTH_SHORT)
    }

    /**
     * Function description.
     */
    fun showShort(textStr: String, duration: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val inflater =
                Utils.getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.toast_tip, null)
            val text = view.findViewById(R.id.toast_tip_text) as TextView
            text.text = textStr
            if (mPublicToast == null) {
                mPublicToast = Toast(Utils.getApp())
            }
            mPublicToast?.duration = duration
            mPublicToast?.setGravity(Gravity.BOTTOM, 0, ScreenUtils.getScreenHeight() / 8)
            mPublicToast?.view = view
            mPublicToast?.show()
        }
    }
}