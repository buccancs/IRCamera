package com.infisense.usbir.inf

import com.energy.iruvc.dual.DualUVCCamera
import com.energy.iruvc.utils.DualCameraParams

/**
 * dataDual lightdata，dataSingle light
 * @author: CaiSongL
 * @date: 2024/1/10 11:40
 */
@Deprecated("data，data")
interface IDualListener {
    fun setDualUVCCamera(dualUVCCamera: DualUVCCamera)

    fun setCurrentFusionType(currentFusionType: DualCameraParams.FusionType)

    fun setUseIRISP(useIRISP: Boolean)
}
