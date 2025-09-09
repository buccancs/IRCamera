package com.infisense.usbir.inf

import com.energy.iruvc.dual.DualUVCCamera
import com.energy.iruvc.utils.DualCameraParams


/**
 * [CN_TEXT]Dual light[CN_TEXT]，[CN_TEXT]Single light
 * @author: CaiSongL
 * @date: 2024/1/10 11:40
 */
@Deprecated("[CN_TEXT]，[CN_TEXT]")
interface IDualListener {


    fun setDualUVCCamera(dualUVCCamera : DualUVCCamera)

    fun setCurrentFusionType(currentFusionType : DualCameraParams.FusionType)

    fun setUseIRISP(useIRISP : Boolean)



}