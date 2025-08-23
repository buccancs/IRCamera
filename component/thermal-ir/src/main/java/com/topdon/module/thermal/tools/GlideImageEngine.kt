package com.topdon.module.thermal.tools

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.maning.imagebrowserlibrary.ImageEngine

/**
 * Simple Glide image engine implementation
 */
class GlideImageEngine : ImageEngine {
    override fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        progressView: View,
        customImageView: View
    ) {
        progressView.visibility = View.GONE
        
        Glide.with(context)
            .load(url)
            .into(imageView)
    }
}