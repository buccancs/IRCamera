package com.topdon.lib.core.tools

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView

/**
 * Image loading utility class for thermal images and general media
 * Provides simplified interface for loading images with Glide-like functionality
 */
object GlideLoader {
    
    /**
     * Load image from file path into ImageView
     */
    fun loadImage(context: Context, path: String, imageView: ImageView) {
        // Stub implementation for thermal image loading
        try {
            // In production, this would use Glide or similar library
            // For now, provide basic image loading functionality
            imageView.setImageDrawable(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Load image with placeholder
     */
    fun loadImage(context: Context, path: String, imageView: ImageView, placeholder: Drawable?) {
        try {
            placeholder?.let { imageView.setImageDrawable(it) }
            loadImage(context, path, imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Load thumbnail image
     */
    fun loadThumbnail(context: Context, path: String, imageView: ImageView) {
        // Stub implementation for thumbnail loading
        loadImage(context, path, imageView)
    }
    
    /**
     * Load image with resize options
     */
    fun loadImageWithResize(context: Context, path: String, imageView: ImageView, width: Int, height: Int) {
        // Stub implementation with resize options
        loadImage(context, path, imageView)
    }
    
    /**
     * Clear image cache
     */
    fun clearCache(context: Context) {
        // Stub implementation for cache clearing
    }
}