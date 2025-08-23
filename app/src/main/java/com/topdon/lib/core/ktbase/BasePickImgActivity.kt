package com.topdon.lib.core.ktbase

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.MeasureSpec
import android.widget.*
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.SizeUtils
import com.topdon.tc001.R
import com.topdon.lib.core.dialog.ColorSelectDialog
import com.topdon.lib.core.dialog.TipDialog
import com.topdon.lib.core.view.ImageEditView
import kotlinx.coroutines.launch
import java.io.File
import android.widget.FrameLayout
import com.topdon.lib.core.view.TitleView

abstract class BasePickImgActivity : BaseActivity(), View.OnClickListener {
    val RESULT_IMAGE_PATH = "RESULT_IMAGE_PATH"
    private var hasTakePhoto = false

    override fun initContentView(): Int {
        return R.layout.activity_image_pick_ir_plush
    }

    override fun initView() {
    }

    override fun initData() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<ImageView>(R.id.iv_edit_circle).isSelected = true
        findViewById<ImageEditView>(R.id.image_edit_view).type = ImageEditView.Type.CIRCLE
        findViewById<View>(R.id.view_color).setBackgroundColor(findViewById<ImageEditView>(R.id.image_edit_view).color)

        findViewById<ImageView>(R.id.iv_edit_color).setOnClickListener(this)
        findViewById<ImageView>(R.id.iv_edit_circle).setOnClickListener(this)
        findViewById<ImageView>(R.id.iv_edit_rect).setOnClickListener(this)
        findViewById<ImageView>(R.id.iv_edit_arrow).setOnClickListener(this)
        findViewById<ImageView>(R.id.iv_edit_clear).setOnClickListener(this)
        findViewById<ImageView>(R.id.img_pick).setOnClickListener(this)

        findViewById<TitleView>(R.id.title_view).setLeftClickListener {
            if (hasTakePhoto) {
                switchPhotoState(false)
            } else {
                finish()
            }
        }
        findViewById<TitleView>(R.id.title_view).setRightClickListener {
            if (hasTakePhoto) {
                val absolutePath: String = intent.getStringExtra(RESULT_IMAGE_PATH)!!
                ImageUtils.save(findViewById<ImageEditView>(R.id.image_edit_view).buildResultBitmap(), File(absolutePath), Bitmap.CompressFormat.PNG)
                val intent = Intent()
                intent.putExtra(RESULT_IMAGE_PATH, absolutePath)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        resize()
    }

    private fun resize() {
        val widthPixels = resources.displayMetrics.widthPixels
        val heightPixels = resources.displayMetrics.heightPixels
        findViewById<TitleView>(R.id.title_view).measure(MeasureSpec.makeMeasureSpec(widthPixels, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightPixels, MeasureSpec.AT_MOST))

        val ivPickHeight = SizeUtils.dp2px(60f + 20 + 20) //拍照按钮高度，60dp+上下各20dp margin
        val menuHeight = (widthPixels * 75f / 384).toInt()
        val bottomHeight = ivPickHeight.coerceAtLeast(menuHeight)
        val canUseHeight = heightPixels - findViewById<TitleView>(R.id.title_view).measuredHeight - bottomHeight
        val wantHeight = (widthPixels * 256f / 192).toInt()
        if (wantHeight <= canUseHeight) {//够用
            findViewById<FrameLayout>(R.id.fragment_container_view).layoutParams = findViewById<FrameLayout>(R.id.fragment_container_view).layoutParams.apply {
                width = widthPixels
                height = wantHeight
            }
            findViewById<ImageEditView>(R.id.image_edit_view).layoutParams = findViewById<ImageEditView>(R.id.image_edit_view).layoutParams.apply {
                width = widthPixels
                height = wantHeight
            }
        } else {
            findViewById<FrameLayout>(R.id.fragment_container_view).layoutParams = findViewById<FrameLayout>(R.id.fragment_container_view).layoutParams.apply {
                width = (canUseHeight * 192f / 256).toInt()
                height = canUseHeight
            }
            findViewById<ImageEditView>(R.id.image_edit_view).layoutParams = findViewById<ImageEditView>(R.id.image_edit_view).layoutParams.apply {
                width = (canUseHeight * 192f / 256).toInt()
                height = canUseHeight
            }
        }
    }


    open suspend fun getPickBitmap() : Bitmap?{
       return null
    }


    override fun onClick(v: View?) {
        when (v) {
            findViewById<View>(R.id.img_pick) -> {
                lifecycleScope.launch {
                    getPickBitmap()?.let {
                        switchPhotoState(true)
                        findViewById<ImageEditView>(R.id.image_edit_view).sourceBitmap = it
                        findViewById<ImageEditView>(R.id.image_edit_view).clear()
                    }
                }
            }
            findViewById<View>(R.id.iv_edit_color) -> {
                val colorPickDialog = ColorSelectDialog(this, findViewById<ImageEditView>(R.id.image_edit_view).color)
                colorPickDialog.onPickListener = {
                    findViewById<ImageEditView>(R.id.image_edit_view).color = it
                    findViewById<View>(R.id.view_color).setBackgroundColor(it)
                }
                colorPickDialog.show()
            }
            findViewById<View>(R.id.iv_edit_circle) -> {
                findViewById<ImageView>(R.id.iv_edit_circle).isSelected = true
                findViewById<ImageView>(R.id.iv_edit_rect).isSelected = false
                findViewById<ImageView>(R.id.iv_edit_arrow).isSelected = false
                findViewById<ImageEditView>(R.id.image_edit_view).type = ImageEditView.Type.CIRCLE
            }
            findViewById<View>(R.id.iv_edit_rect) -> {
                findViewById<ImageView>(R.id.iv_edit_circle).isSelected = false
                findViewById<ImageView>(R.id.iv_edit_rect).isSelected = true
                findViewById<ImageView>(R.id.iv_edit_arrow).isSelected = false
                findViewById<ImageEditView>(R.id.image_edit_view).type = ImageEditView.Type.RECT
            }
            findViewById<View>(R.id.iv_edit_arrow) -> {
                findViewById<ImageView>(R.id.iv_edit_circle).isSelected = false
                findViewById<ImageView>(R.id.iv_edit_rect).isSelected = false
                findViewById<ImageView>(R.id.iv_edit_arrow).isSelected = true
                findViewById<ImageEditView>(R.id.image_edit_view).type = ImageEditView.Type.ARROW
            }
            findViewById<View>(R.id.iv_edit_clear) -> findViewById<ImageEditView>(R.id.image_edit_view).clear()
        }
    }

    override fun onBackPressed() {
        if (hasTakePhoto) {
            switchPhotoState(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun switchPhotoState(hasTakePhoto: Boolean) {
        this.hasTakePhoto = hasTakePhoto
        findViewById<ImageEditView>(R.id.image_edit_view).isVisible = hasTakePhoto
        findViewById<View>(R.id.cl_edit_menu).isVisible = hasTakePhoto
        findViewById<View>(R.id.img_pick).isVisible = !hasTakePhoto
        findViewById<FrameLayout>(R.id.fragment_container_view).isVisible = !hasTakePhoto
        findViewById<TitleView>(R.id.title_view).setRightDrawable(if (hasTakePhoto) R.drawable.app_save else 0)
    }

    private fun showExitTipsDialog(listener: (() -> Unit)) {
        TipDialog.Builder(this)
            .setMessage(R.string.diy_tip_save)
            .setPositiveListener(R.string.app_exit) {
                listener.invoke()
            }
            .setCancelListener(R.string.app_cancel)
            .create().show()
    }

    override fun disConnected() {
        super.disConnected()
        finish()
    }

}