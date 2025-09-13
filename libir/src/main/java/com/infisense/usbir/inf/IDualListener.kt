package com.infisense.usbir.inf

import com.energy.iruvc.dual.DualUVCCamera
import com.energy.iruvc.utils.DualCameraParams

/**
 * Comment removed (contained Chinese characters)
* @author: CaiSongL
 * @date: 2024/1/10 11:40
 */
@Deprecated("，")
/**
 * IDualListener manages camera operations and image capture functionality.
 *
* @author IRCamera Development Team
 * @since 1.0
 */
interface IDualListener {
 fun setDualUVCCamera(dualUVCCamera: DualUVCCamera)

 fun setCurrentFusionType(currentFusionType: DualCameraParams.FusionType)

 fun setUseIRISP(useIRISP: Boolean)
}
