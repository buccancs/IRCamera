package com.topdon.lib.core.widget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Image browser for viewing thermal images and gallery content
 * Provides full-screen image viewing with gesture support
 */
class MNImageBrowser {
    
    companion object {
        const val EXTRA_IMAGE_PATHS = "extra_image_paths"
        const val EXTRA_CURRENT_INDEX = "extra_current_index"
        const val EXTRA_TITLE = "extra_title"
        
        /**
         * Start image browser with list of image paths
         */
        fun startImageBrowser(context: Context, imagePaths: List<String>, currentIndex: Int = 0, title: String? = null) {
            val intent = Intent(context, MNImageBrowserActivity::class.java).apply {
                putStringArrayListExtra(EXTRA_IMAGE_PATHS, ArrayList(imagePaths))
                putExtra(EXTRA_CURRENT_INDEX, currentIndex)
                putExtra(EXTRA_TITLE, title)
            }
            context.startActivity(intent)
        }
        
        /**
         * Start image browser with single image path
         */
        fun startImageBrowser(context: Context, imagePath: String, title: String? = null) {
            startImageBrowser(context, listOf(imagePath), 0, title)
        }
    }
}

/**
 * Image browser activity for full-screen image viewing
 */
class MNImageBrowserActivity : AppCompatActivity() {
    
    private var imagePaths: List<String> = emptyList()
    private var currentIndex: Int = 0
    private var title: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get intent data
        imagePaths = intent.getStringArrayListExtra(MNImageBrowser.EXTRA_IMAGE_PATHS) ?: emptyList()
        currentIndex = intent.getIntExtra(MNImageBrowser.EXTRA_CURRENT_INDEX, 0)
        title = intent.getStringExtra(MNImageBrowser.EXTRA_TITLE)
        
        // Set title if provided
        title?.let { supportActionBar?.title = it }
        
        // Stub implementation for image browser UI
        // In production, this would show ViewPager with image views
        finish()
    }
}