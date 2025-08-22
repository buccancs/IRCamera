package com.topdon.lib.core.tools

/**
 * Check double click utility - stub implementation for compilation
 */
class CheckDoubleClick {
    
    private var lastClickTime: Long = 0
    private val DOUBLE_CLICK_TIME_DELTA = 300L
    
    fun check(): Boolean {
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - lastClickTime
        lastClickTime = currentTime
        
        return delta < DOUBLE_CLICK_TIME_DELTA
    }
    
    companion object {
        fun isDoubleClick(): Boolean {
            return CheckDoubleClick().check()
        }
        
        fun isFastDoubleClick(): Boolean {
            return isDoubleClick()
        }
        
        fun preventDoubleClick(): Boolean {
            return !isDoubleClick()
        }
    }
}