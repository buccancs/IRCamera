package com.topdon.lib.core.tools

import android.util.Log

/**
 * XLog utility for logging throughout the application
 */
object XLog {
    private const val TAG = "IRCamera"
    
    fun d(msg: String) {
        Log.d(TAG, msg)
    }
    
    fun d(tag: String, msg: String) {
        Log.d(tag, msg)
    }
    
    fun i(msg: String) {
        Log.i(TAG, msg)
    }
    
    fun i(tag: String, msg: String) {
        Log.i(tag, msg)
    }
    
    fun e(msg: String) {
        Log.e(TAG, msg)
    }
    
    fun e(tag: String, msg: String) {
        Log.e(tag, msg)
    }
    
    fun w(msg: String) {
        Log.w(TAG, msg)
    }
    
    fun w(tag: String, msg: String) {
        Log.w(tag, msg)
    }
    
    fun v(msg: String) {
        Log.v(TAG, msg)
    }
    
    fun v(tag: String, msg: String) {
        Log.v(tag, msg)
    }
}