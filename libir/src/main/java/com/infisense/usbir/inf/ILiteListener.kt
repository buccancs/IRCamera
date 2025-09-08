package com.infisense.usbir.inf

import com.energy.iruvc.dual.DualUVCCamera
import com.energy.iruvc.utils.DualCameraParams

/**
 * [Chinese text]
 * @author: CaiSongL
 * @date: 2024/1/10 11:40
 */
interface ILiteListener {

    fun getDeltaNucAndVTemp() : Float

    fun compensateTemp(temp : Float) : Float

}