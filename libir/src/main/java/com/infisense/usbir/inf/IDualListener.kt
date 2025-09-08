package com.infisense.usbir.inf

import com.energy.iruvc.dual.DualUVCCamera
import com.energy.iruvc.utils.DualCameraParams

/**
 * [Chinese text]Dual light[Chinese text], [Chinese text]
 * @author: CaiSongL
 * @date: 2024/1/10 11:40
 */
@Deprecated("[Chinese text], [Chinese text]")
interface IDualListener {

    fun setDualUVCCamera(dualUVCCamera : DualUVCCamera)

    fun setCurrentFusionType(currentFusionType : DualCameraParams.FusionType)

    fun setUseIRISP(useIRISP : Boolean)

}