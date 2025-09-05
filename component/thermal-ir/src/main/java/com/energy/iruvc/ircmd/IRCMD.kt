package com.energy.iruvc.ircmd

import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.DeviceType

/**
 * IRCMD interface for thermal camera hardware control
 * This is the base interface for infrared camera command operations
 */
abstract class IRCMD {
    
    // Preview and control methods used by hardware library
    abstract fun startPreview(
        channel: CommonParams.PreviewPathChannel,
        source: CommonParams.StartPreviewSource,
        fps: Int,
        mode: CommonParams.StartPreviewMode,
        dataFlowMode: CommonParams.DataFlowMode
    ): Int
    
    abstract fun startY16ModePreview(
        channel: CommonParams.PreviewPathChannel,
        srcType: CommonParams.Y16ModePreviewSrcType
    ): Int
    
    abstract fun setPropImageParams(param: CommonParams.PropImageParams, value: Any): Int
    
    abstract fun setPropAutoShutterParameter(param: CommonParams.PropAutoShutterParameter, value: Any)
    
    abstract fun isTempReplacedWithTNREnabled(deviceType: DeviceType): Boolean
    
    // Additional methods that may be needed by thermal-ir component
    open fun setMirror(mirror: Boolean) {}
    open fun setAutoShutter(isAutoShutter: Boolean) {}
    open fun setPropDdeLevel(level: Int) {}
    open fun setContrast(contrast: Int) {}
    open fun setPropTPDParams(param1: Any, param2: Any): Int = 0
    open fun tc1bShutterManual() {}
    open fun updateOOCOrB(type: Any) {}
    open fun onDestroy() {}
    open fun setTPDKtBtRecalPoint(point: Any) {}
    open fun restoreDefaultConfig(param: Any): Int = 0
    open fun setTPDKtBtRecalPoint(point: Any, extra: Any): Int = 0
    open fun rmCoverStsSwitch(enable: Any): Int = 0
    open fun rmCoverAutoCalc(param: Any): Int = 0
    open fun getPropTPDParams(param: Any, value: Any): Int = 0
    open fun getDeviceInfo(infoType: Any, buffer: ByteArray): Int = 0
    open fun oemRead(cmd: Any, extra: Any): Int = 0
    open fun getPropImageParams(param: Any, value: Any): Int = 0
    open fun setIsothermal(enable: Any) {}
    open fun setTempL(temp: Any) {}
    open fun setTempH(temp: Any) {}
    open fun zoomCenterUp(channel: Any = Any(), step: Any = Any()) {}
    open fun zoomCenterDown(channel: Any = Any(), step: Any = Any()) {}
    open fun zoomCenterLeft(channel: Any = Any(), step: Any = Any()) {}
    open fun zoomCenterRight(channel: Any = Any(), step: Any = Any()) {}
    open fun autoGainSwitch(data: ByteArray, imageRes: Any, info: Any, param: Any, callback: Any) {}
    open fun avoidOverexposure(
        flag: Boolean,
        gainStatus: Any,
        data: ByteArray,
        imageRes: Any,
        lowTemp: Int,
        highTemp: Int,
        pixelProp: Float,
        switchFrameCount: Int,
        closeFrameCount: Int,
        callback: Any
    ) {}
}