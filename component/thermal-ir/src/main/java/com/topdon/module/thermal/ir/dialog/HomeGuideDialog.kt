package com.topdon.module.thermal.ir.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.topdon.module.thermal.R
import com.topdon.module.thermal.databinding.DialogHomeGuideBinding
import com.topdon.module.thermal.databinding.LayoutHomeGuide1Binding
import com.topdon.module.thermal.databinding.LayoutHomeGuide2Binding
import com.topdon.module.thermal.databinding.LayoutHomeGuide3Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 首页操作指引弹框.
 *
 * Created by LCG on 2024/4/8.
 */
class HomeGuideDialog(context: Context, private val currentStep: Int) : Dialog(context, R.style.TransparentDialog) {

    private lateinit var binding: DialogHomeGuideBinding
    private lateinit var guide1Binding: LayoutHomeGuide1Binding
    private lateinit var guide2Binding: LayoutHomeGuide2Binding  
    private lateinit var guide3Binding: LayoutHomeGuide3Binding

    /**
     * 下一步点击事件监听，step：当前处于第`[1,3]`，在该步骤点击的下一步
     */
    var onNextClickListener: ((step: Int) -> Unit)? = null

    /**
     * 跳过点击事件监听.
     */
    var onSkinClickListener: (() -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(false)
        
        binding = DialogHomeGuideBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        
        // Initialize sub-layout bindings
        guide1Binding = LayoutHomeGuide1Binding.bind(binding.clGuide1)
        guide2Binding = LayoutHomeGuide2Binding.bind(binding.clGuide2)
        guide3Binding = LayoutHomeGuide3Binding.bind(binding.clGuide3)

        when (currentStep) {
            1 -> {
                binding.clGuide1.isVisible = true
                binding.clGuide2.isVisible = false
                binding.clGuide3.isVisible = false
            }
            2 -> {
                binding.clGuide1.isVisible = false
                binding.clGuide2.isVisible = true
                binding.clGuide3.isVisible = false
            }
            3 -> {
                binding.clGuide1.isVisible = false
                binding.clGuide2.isVisible = false
                binding.clGuide3.isVisible = true
            }
        }

        guide1Binding.tvNext1.setOnClickListener {
            onNextClickListener?.invoke(1)
            binding.clGuide1.isVisible = false
            binding.clGuide2.isVisible = true
        }
        guide2Binding.tvNext2.setOnClickListener {
            onNextClickListener?.invoke(2)
            binding.clGuide2.isVisible = false
            binding.clGuide3.isVisible = true
        }
        guide3Binding.tvIKnow.setOnClickListener {
            onNextClickListener?.invoke(3)
            dismiss()
        }


        guide1Binding.tvSkin1.setOnClickListener {
            onSkinClickListener?.invoke()
            dismiss()
        }
        guide2Binding.tvSkin2.setOnClickListener {
            onSkinClickListener?.invoke()
            dismiss()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onSkinClickListener?.invoke()
    }

    fun blurBg(rootView: View) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sourceBitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
                val outputBitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(sourceBitmap)
                rootView.draw(canvas)

                val renderScript = RenderScript.create(context)
                val inputAllocation = Allocation.createFromBitmap(renderScript, sourceBitmap)
                val outputAllocation = Allocation.createTyped(renderScript, inputAllocation.type)

                val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
                blurScript.setRadius(20f)
                blurScript.setInput(inputAllocation)
                blurScript.forEach(outputAllocation)
                outputAllocation.copyTo(outputBitmap)
                renderScript.destroy()

                launch(Dispatchers.Main) {
                    binding.ivBlurBg.isVisible = true
                    binding.ivBlurBg.setImageBitmap(outputBitmap)
                }
            } catch (_: Exception) {

            }
        }
    }
}