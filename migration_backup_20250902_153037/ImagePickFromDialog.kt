package com.topdon.house.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topdon.house.R
import com.topdon.lib.core.utils.ScreenUtil
import kotlinx.android.synthetic.main.dialog_image_pick_from.view.*

/**
 * [Chinese text] - [Chinese text].
 *
 * Created by LCG on 2024/1/23.
 */
class ImagePickFromDialog(private val context: Context) : Dialog(context, R.style.InfoDialog), View.OnClickListener {

    /**
     * [Chinese text]eventlistener.
     * 0-[Chinese text] 1-visible[Chinese text] 2-[Chinese text]
     */
    private var onSelectListener: ((type: Int) -> Unit)? = null

    private lateinit var contentView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_image_pick_from, null)
        contentView.tv_gallery.setOnClickListener(this)
        contentView.tv_light_camera.setOnClickListener(this)
        setContentView(contentView)

        window?.let {
            val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            val layoutParams = it.attributes
            layoutParams.width = (ScreenUtil.getScreenWidth(context) * if (isPortrait) 0.76f else 0.48f).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }
    }

    /**
     * settings[Chinese text]eventlistener.
     * 0-[Chinese text] 1-visible[Chinese text] 2-[Chinese text]
     */
    fun setSelectListener(l: ((type: Int) -> Unit)): ImagePickFromDialog {
        this.onSelectListener = l
        return this
    }

    override fun onClick(v: View?) {
        when (v) {
            contentView.tv_gallery -> {//[Chinese text]
                dismiss()
                onSelectListener?.invoke(0)
            }
            contentView.tv_light_camera -> {//[Chinese text]photo capture
                dismiss()
                onSelectListener?.invoke(1)
            }
        }
    }
}