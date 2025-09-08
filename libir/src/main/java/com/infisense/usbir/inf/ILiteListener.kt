package com.infisense.usbir.inf

import com.energy.iruvc.dual.DualUVCCamera
import com.energy.iruvc.utils.DualCameraParams


 * @author: CaiSongL
 * @date: 2024/1/10 11:40
interface ILiteListener {


    /**
     * Function description.
     */
    fun getDeltaNucAndVTemp() : Float

    /**
     * Function description.
     */
    fun compensateTemp(temp : Float) : Float

}