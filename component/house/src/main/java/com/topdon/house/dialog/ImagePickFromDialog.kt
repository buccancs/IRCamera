package com.topdon.house.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topdon.house.R
import com.topdon.lib.core.R as LibR
import com.topdon.lib.core.utils.ScreenUtil

/**
 * [CN_TEXT] - [CN_TEXT].
 *
 * Created by LCG on 2024/1/23.
 */
class ImagePickFromDialog(private val context: Context) : Dialog(context, LibR.style.InfoDialog), View.OnClickListener {

    /**
     * [CN_TEXT].
     * 0-[CN_TEXT] 1-Visible light[CN_TEXT] 2-[CN_TEXT]
     */
    private var onSelectListener: ((type: Int) -> Unit)? = null

    private lateinit var contentView: View
    private lateinit var tvGallery: View
    private lateinit var tvLightCamera: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_image_pick_from, null)
        tvGallery = contentView.findViewById(R.id.tv_gallery)
        tvLightCamera = contentView.findViewById(R.id.tv_light_camera)
        
        tvGallery.setOnClickListener(this)
        tvLightCamera.setOnClickListener(this)
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
     * Settings[CN_TEXT].
     * 0-[CN_TEXT] 1-Visible light[CN_TEXT] 2-[CN_TEXT]
     */
    fun setSelectListener(l: ((type: Int) -> Unit)): ImagePickFromDialog {
        this.onSelectListener = l
        return this
    }

    override fun onClick(v: View?) {
        when (v) {
            tvGallery -> {//[CN_TEXT]
                dismiss()
                onSelectListener?.invoke(0)
            }
            tvLightCamera -> {//[CN_TEXT]Photo
                dismiss()
                onSelectListener?.invoke(1)
            }
        }
    }
}