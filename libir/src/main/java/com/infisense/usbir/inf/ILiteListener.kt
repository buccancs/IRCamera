package com.infisense.usbir.inf

/**
 * data
 * @author: CaiSongL
 * @date: 2024/1/10 11:40
 */
interface ILiteListener {
    fun getDeltaNucAndVTemp(): Float

    fun compensateTemp(temp: Float): Float
}
