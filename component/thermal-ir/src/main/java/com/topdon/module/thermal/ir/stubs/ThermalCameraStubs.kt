package com.topdon.module.thermal.ir.stubs

import android.view.SurfaceView
import com.energy.iruvc.utils.IIRFrameCallback
import com.energy.iruvc.utils.DualCameraParams

// TODO: Temporary stubs for thermal camera hardware SDK - replace when hardware libraries are available

class DualViewWithExternalCameraCommonApi(
    surfaceView: SurfaceView,
    uvcCamera: Any?,
    dataFlowMode: Int,
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
    val dualUVCCamera: StubDualUVCCamera = stubDualUVCCamera
    
    fun getDualUVCCamera(): StubDualUVCCamera = stubDualUVCCamera
    fun addFrameCallback(callback: Any) {}
    fun removeFrameCallback(callback: Any) {}
    fun startPreview() {}
    fun stopPreview() {}
    fun setHandler(handler: android.os.Handler) {}
    fun setCurrentFusionType(fusion: DualCameraParams.FusionType) {}
}

class StubDualUVCCamera {
    fun loadParameters(parameters: ByteArray?, typeLoadParameters: DualCameraParams.TypeLoadParameters): Any? = null
    fun setDisp(disp: Int) {}
    fun loadPseudocolor(type: Any, data: ByteArray) {}
    fun onPausePreview() {}
    fun updateFrame(format: Int, data: ByteArray, width: Int, height: Int) {}
}

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
    var ircmd: Any? = null
    
    fun getInstance() = this
    fun init(irPid: Int, isUseIRISP: Boolean, dataFlowMode: Int) {}
    fun addOnUSBConnectListener(listener: Any) {}
    fun removeOnUSBConnectListener(listener: Any) {}
    fun registerUSB() {}
    fun unregisterUSB() {}
    fun stopPreview() {}
}