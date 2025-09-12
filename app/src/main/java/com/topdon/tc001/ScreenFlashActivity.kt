package com.topdon.tc001

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * Overlay activity that flashes the screen for synchronization purposes.
 * Used by RecordingService to provide visual sync markers across devices.
 */
class ScreenFlashActivity : Activity() {
    
    companion object {
        private const val TAG = "ScreenFlashActivity"
        private const val DEFAULT_FLASH_DURATION_MS = 100L
    }
    
    private val handler = Handler(Looper.getMainLooper())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Configure as overlay
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
            
            // Create white overlay view
            val flashView = View(this).apply {
                setBackgroundColor(Color.WHITE)
            }
            setContentView(flashView)
            
            // Get parameters
            val timestampNs = intent.getLongExtra("timestamp_ns", System.nanoTime())
            val flashDuration = intent.getLongExtra("flash_duration_ms", DEFAULT_FLASH_DURATION_MS)
            
            Log.i(TAG, "Screen flash triggered at timestamp: $timestampNs for ${flashDuration}ms")
            
            // Auto-finish after flash duration
            handler.postDelayed({
                finish()
            }, flashDuration)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in screen flash activity", e)
            finish()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}