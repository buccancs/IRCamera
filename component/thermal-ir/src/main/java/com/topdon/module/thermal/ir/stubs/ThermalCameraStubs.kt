package com.topdon.module.thermal.ir.stubs

import android.view.SurfaceView
import com.energy.iruvc.utils.IIRFrameCallback
import com.energy.iruvc.utils.DualCameraParams
import com.energy.iruvc.utils.CommonParams
import com.energy.iruvc.ircmd.IRCMD
import com.energy.iruvc.dual.DualUVCCamera
import com.energy.iruvc.dual.StubDualUVCCamera

// TODO: Temporary stubs for thermal camera hardware SDK - replace when hardware libraries are available

class DualViewWithExternalCameraCommonApi(
    surfaceView: SurfaceView,
    uvcCamera: Any?,
    dataFlowMode: CommonParams.DataFlowMode,
    imageHeight: Int,
    imageWidth: Int,
    vlCameraWidth: Int,
    vlCameraHeight: Int,
    dualCameraWidth: Int,
    dualCameraHeight: Int,
    isUseIRISP: Boolean,
    dualRotate: Int,
    callback: IIRFrameCallback
) {
    var isOpenAmplify: Boolean = false
    private val stubDualUVCCamera = StubDualUVCCamera()
    val dualUVCCamera: DualUVCCamera = stubDualUVCCamera
    
    fun getDualUVCCamera(): DualUVCCamera = stubDualUVCCamera
    fun addFrameCallback(callback: Any) {}
    fun removeFrameCallback(callback: Any) {}
    fun startPreview() {}
    fun stopPreview() {}
    fun setHandler(handler: android.os.Handler) {}
    fun setCurrentFusionType(fusion: DualCameraParams.FusionType) {}
}

class StubDualUVCCamera : DualUVCCamera

class IRUVCDual(
    cameraWidth: Int,
    cameraHeight: Int,
    context: android.content.Context,
    pid: Int,
    fps: Int,
    connectCallback: Any?,
    frameCallback: Any?
) {
    var TAG: String = ""
    
    fun setHandler(handler: android.os.Handler) {}
    fun registerUSB() {}
    fun unregisterUSB() {}
    fun stopPreview() {}
    fun updateFrame(format: Int, data: ByteArray, width: Int, height: Int) {}
    fun loadParameters(parameters: ByteArray?, typeLoadParameters: DualCameraParams.TypeLoadParameters): Any? = null
    fun setDisp(disp: Int) {}
    fun loadPseudocolor(type: Any, data: ByteArray) {}
    fun onPausePreview() {}
}

object USBMonitorManager {
    var isReStart: Boolean = false
    var uvcCamera: Any? = null
    var ircmd: IRCMD? = null
    
    fun getInstance() = this
    fun init(irPid: Int, isUseIRISP: Boolean, dataFlowMode: CommonParams.DataFlowMode) {}
    fun addOnUSBConnectListener(listener: Any) {}
    fun removeOnUSBConnectListener(listener: Any) {}
    fun registerUSB() {}
    fun unregisterUSB() {}
    fun stopPreview() {}
}