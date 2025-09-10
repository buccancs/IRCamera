package com.infisense.usbir.inf

import com.energy.iruvc.dual.DualUVCCamera
import com.energy.iruvc.utils.DualCameraParams

@Deprecated("data，data")
interface IDualListener {
    fun setDualUVCCamera(dualUVCCamera: DualUVCCamera)

    fun setCurrentFusionType(currentFusionType: DualCameraParams.FusionType)

    fun setUseIRISP(useIRISP: Boolean)
}
