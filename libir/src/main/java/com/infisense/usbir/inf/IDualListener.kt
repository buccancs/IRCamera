package com.infisense.usbir.inf

import com.energy.iruvc.dual.DualUVCCamera
import com.energy.iruvc.utils.DualCameraParams


 * @author: CaiSongL
 * @date: 2024/1/10 11:40
@Deprecated("，")
interface IDualListener {


    /**
     * Function description.
     */
    fun setDualUVCCamera(dualUVCCamera : DualUVCCamera)

    /**
     * Function description.
     */
    fun setCurrentFusionType(currentFusionType : DualCameraParams.FusionType)

    /**
     * Function description.
     */
    fun setUseIRISP(useIRISP : Boolean)



}