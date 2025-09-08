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
import com.topdon.module.thermal.ir.R
import com.csl.irCamera.libapp.R as LibAppR
import com.topdon.module.thermal.ir.databinding.DialogHomeGuideBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

 * .
 * Created by LCG on 2024/4/8.
class HomeGuideDialog(context: Context, private val currentStep: Int) : Dialog(context, LibAppR.style.TransparentDialog) {

    private lateinit var binding: DialogHomeGuideBinding

     * step`[1,3]`
    var onNextClickListener: ((step: Int) -> Unit)? = null

     * .
    var onSkinClickListener: (() -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        setCanceledOnTouchOutside(false)
        binding = DialogHomeGuideBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        // Access views from included layouts
        val clGuide1 = binding.includeGuide1.clGuide1
        val clGuide2 = binding.includeGuide2.clGuide2
        val clGuide3 = binding.includeGuide3.clGuide3
        val tvNext1 = binding.includeGuide1.tvNext1
        val tvNext2 = binding.includeGuide2.tvNext2
        val tvIKnow = binding.includeGuide3.tvIKnow
        val tvSkin1 = binding.includeGuide1.tvSkin1
        val tvSkin2 = binding.includeGuide2.tvSkin2

        when (currentStep) {
            1 -> {
                clGuide1.isVisible = true
                clGuide2.isVisible = false
                clGuide3.isVisible = false
            }
            2 -> {
                clGuide1.isVisible = false
                clGuide2.isVisible = true
                clGuide3.isVisible = false
            }
            3 -> {
                clGuide1.isVisible = false
                clGuide2.isVisible = false
                clGuide3.isVisible = true
            }
        }

        tvNext1.setOnClickListener {
            onNextClickListener?.invoke(1)
            clGuide1.isVisible = false
            clGuide2.isVisible = true
        }
        tvNext2.setOnClickListener {
            onNextClickListener?.invoke(2)
            clGuide2.isVisible = false
            clGuide3.isVisible = true
        }
        tvIKnow.setOnClickListener {
            onNextClickListener?.invoke(3)
            dismiss()
        }


        tvSkin1.setOnClickListener {
            onSkinClickListener?.invoke()
            dismiss()
        }
        tvSkin2.setOnClickListener {
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