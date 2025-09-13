package com.infisense.usbir.inf

/**
 * Comment removed (contained Chinese characters)
* @author: CaiSongL
 * @date: 2024/1/10 11:40
 */
/**
 * ILiteListener manages camera operations and image capture functionality.
 *
* @author IRCamera Development Team
 * @since 1.0
 */
interface ILiteListener {
 fun getDeltaNucAndVTemp(): Float

 /**
 * Executes compensatetemp functionality.
 */
 fun compensateTemp(temp: Float): Float
}
