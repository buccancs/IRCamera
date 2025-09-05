package com.energy.iruvc.ircmd

/**
 * Stub implementation for IRCMD hardware SDK class
 * TODO: Replace with actual hardware library when available
 */
open class IRCMD {
    // Stub implementation - all methods are no-ops
    open fun setMirror(mirror: Boolean) {}
    open fun setAutoShutter(isAutoShutter: Boolean) {}
    open fun setPropDdeLevel(level: Int) {}
    open fun setContrast(contrast: Int) {}
    open fun setPropImageParams(param1: Any, param2: Any) {}
    open fun setPropTPDParams(param1: Any, param2: Any) {}
    open fun tc1bShutterManual() {}
    open fun updateOOCOrB(type: Any) {}
    
    // Add other methods as needed by the hardware SDK
}