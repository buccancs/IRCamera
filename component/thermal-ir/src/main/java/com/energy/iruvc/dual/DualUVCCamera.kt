package com.energy.iruvc.dual

import com.energy.iruvc.utils.DualCameraParams

/**
 * Stub implementation for the hardware SDK DualUVCCamera class
 * This provides compatibility for the thermal camera hardware SDK
 * TODO: Replace with actual hardware library when available
 */
interface DualUVCCamera {
    fun loadParameters(parameters: ByteArray?, typeLoadParameters: DualCameraParams.TypeLoadParameters): Any?
    fun setDisp(disp: Int)
    fun loadPseudocolor(type: Any, data: ByteArray)
    fun onPausePreview()
    fun updateFrame(format: Int, data: ByteArray, width: Int, height: Int)
    fun setAutoShutter(isAutoShutter: Boolean)
    fun setContrast(contrast: Int)
    fun setMirror(mirror: Boolean)
    fun setPropDdeLevel(level: Int)
    fun setAlignTranslateParameter(param: Any)
    fun setIsothermal(enable: Boolean)
    fun setTempL(temp: Float)
    fun setTempH(temp: Float)
}

/**
 * Default stub implementation
 */
class StubDualUVCCamera : DualUVCCamera {
    override fun loadParameters(parameters: ByteArray?, typeLoadParameters: DualCameraParams.TypeLoadParameters): Any? = null
    override fun setDisp(disp: Int) {}
    override fun loadPseudocolor(type: Any, data: ByteArray) {}
    override fun onPausePreview() {}
    override fun updateFrame(format: Int, data: ByteArray, width: Int, height: Int) {}
    override fun setAutoShutter(isAutoShutter: Boolean) {}
    override fun setContrast(contrast: Int) {}
    override fun setMirror(mirror: Boolean) {}
    override fun setPropDdeLevel(level: Int) {}
    override fun setAlignTranslateParameter(param: Any) {}
    override fun setIsothermal(enable: Boolean) {}
    override fun setTempL(temp: Float) {}
    override fun setTempH(temp: Float) {}
}