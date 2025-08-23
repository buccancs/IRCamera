package com.topdon.lib.core.tools

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import androidx.core.content.ContextCompat

/**
 * Span builder utility - stub implementation for compilation
 */
class SpanBuilder(private val initialText: String = "") {
    
    private val builder = SpannableStringBuilder(initialText)
    
    fun append(text: String): SpanBuilder {
        builder.append(text)
        return this
    }
    
    fun appendDrawable(context: Context, drawableRes: Int, size: Int): SpanBuilder {
        val drawable = ContextCompat.getDrawable(context, drawableRes)
        if (drawable != null) {
            drawable.setBounds(0, 0, size, size)
            val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BASELINE)
            builder.append(" ")
            builder.setSpan(imageSpan, builder.length - 1, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return this
    }
    
    fun setColor(color: Int): SpanBuilder {
        if (builder.isNotEmpty()) {
            builder.setSpan(ForegroundColorSpan(color), 0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return this
    }
    
    fun setTextSize(size: Float): SpanBuilder {
        // Stub implementation
        return this
    }
    
    fun build(): SpannableString {
        return SpannableString(builder)
    }
    
    companion object {
        fun create(): SpanBuilder {
            return SpanBuilder()
        }
        
        fun buildSpan(text: String, color: Int): SpannableString {
            val spannableString = SpannableString(text)
            spannableString.setSpan(ForegroundColorSpan(color), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannableString
        }
    }
}