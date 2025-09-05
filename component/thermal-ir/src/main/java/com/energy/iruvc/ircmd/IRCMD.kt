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
    open fun onDestroy() {}
    open fun setTPDKtBtRecalPoint(point: Any) {}
    open fun restoreDefaultConfig() {}
    open fun rmCoverStsSwitch(enable: Boolean) {}
    open fun rmCoverAutoCalc() {}
    open fun getPropTPDParams(): Any = Any()
    open fun oemRead(cmd: Any): Any = Any()
    open fun getPropImageParams(): Any = Any()
    open fun setAlignTranslateParameter(param: Any) {}
    open fun setPropAutoShutterParameter(param: Any) {}
    open fun setIsothermal(value: Any) {}
    open fun setTempL(temp: Any) {}
    open fun setTempH(temp: Any) {}
    open fun zoomCenterUp() {}
    open fun zoomCenterDown() {}
    
    // Add other methods as needed by the hardware SDK
}