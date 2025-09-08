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
 * [Chinese text] - [Chinese text].
 *
 * Created by LCG on 2024/1/23.
 */
class ImagePickFromDialog(private val context: Context) : Dialog(context, LibR.style.InfoDialog), View.OnClickListener {

    /**
     * [Chinese text]eventlistener.
     * 0-[Chinese text] 1-visible[Chinese text] 2-[Chinese text]
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
     * Settings[Chinese text]eventlistener.
     * 0-[Chinese text] 1-visible[Chinese text] 2-[Chinese text]
     */
    fun setSelectListener(l: ((type: Int) -> Unit)): ImagePickFromDialog {
        this.onSelectListener = l
        return this
    }

    override fun onClick(v: View?) {
        when (v) {
            tvGallery -> {// [Chinese text]
                dismiss()
                onSelectListener?.invoke(0)
            }
            tvLightCamera -> {// [Chinese text]Photo capture
                dismiss()
                onSelectListener?.invoke(1)
            }
        }
    }
}