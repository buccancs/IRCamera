package com.energy.iruvc.ircmd

import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.utils.DeviceType

/**
 * ConcreteIRCMDBuilder for building IRCMD instances
 */
class ConcreteIRCMDBuilder {
    private var ircmdType: IRCMDType? = null
    private var idCamera: Long = 0
    
    fun setIrcmdType(type: IRCMDType): ConcreteIRCMDBuilder {
        this.ircmdType = type
        return this
    }
    
    fun setIdCamera(id: Long): ConcreteIRCMDBuilder {
        this.idCamera = id
        return this
    }
    
    fun build(): IRCMD {
        return ConcreteIRCMD(ircmdType ?: IRCMDType.USB_IR_256_384, idCamera)
    }
}

/**
 * Concrete implementation of IRCMD
 * This is a minimal implementation for compilation purposes
 */
private class ConcreteIRCMD(
    private val type: IRCMDType,
    private val cameraId: Long
) : IRCMD() {
    
    override fun startPreview(
        channel: CommonParams.PreviewPathChannel,
        source: CommonParams.StartPreviewSource,
        fps: Int,
        mode: CommonParams.StartPreviewMode,
        dataFlowMode: CommonParams.DataFlowMode
    ): Int {
        // Stub implementation - would interface with actual hardware
        return 0
    }
    
    override fun startY16ModePreview(
        channel: CommonParams.PreviewPathChannel,
        srcType: CommonParams.Y16ModePreviewSrcType
    ): Int {
        // Stub implementation - would interface with actual hardware
        return 0
    }
    
    override fun setPropImageParams(param: CommonParams.PropImageParams, value: Any): Int {
        // Stub implementation - would interface with actual hardware
        return 0
    }
    
    override fun setPropAutoShutterParameter(param: CommonParams.PropAutoShutterParameter, value: Any) {
        // Stub implementation - would interface with actual hardware
    }
    
    override fun isTempReplacedWithTNREnabled(deviceType: DeviceType): Boolean {
        // Stub implementation - would interface with actual hardware
        return false
    }
}