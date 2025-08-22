package com.maning.imagebrowserlibrary

import android.content.Context
import android.view.View
import android.widget.ImageView

/**
 * Stub ImageEngine interface for compatibility
 */
interface ImageEngine {
    fun loadImage(context: Context, url: String, imageView: ImageView, progressView: View, customImageView: View)
}