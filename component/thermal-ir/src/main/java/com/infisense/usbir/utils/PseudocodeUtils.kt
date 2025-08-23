package com.infisense.usbir.utils

import com.energy.iruvc.utils.CommonParams.PseudoColorType

/**
 * Pseudocode utilities for thermal imaging
 */
object PseudocodeUtils {
    
    /**
     * Change pseudocode mode by old configuration value
     */
    fun changePseudocodeModeByOld(oldCode: Int): PseudoColorType {
        return when (oldCode) {
            1 -> PseudoColorType.PSEUDO_1  // 白热
            3 -> PseudoColorType.PSEUDO_3  // 铁红
            4 -> PseudoColorType.PSEUDO_4  // 彩虹1
            5 -> PseudoColorType.PSEUDO_5  // 彩虹2
            6 -> PseudoColorType.PSEUDO_6  // 彩虹3
            7 -> PseudoColorType.PSEUDO_7  // 红热
            8 -> PseudoColorType.PSEUDO_8  // 热铁
            9 -> PseudoColorType.PSEUDO_9  // 彩虹4
            10 -> PseudoColorType.PSEUDO_10 // 彩虹5
            11 -> PseudoColorType.PSEUDO_11 // 黑热
            else -> PseudoColorType.PSEUDO_3 // 默认铁红
        }
    }
}