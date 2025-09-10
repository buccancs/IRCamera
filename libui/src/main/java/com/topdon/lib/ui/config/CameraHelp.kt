package com.topdon.lib.ui.config

/**
 * utility
 * @author: CaiSongL
 * @date: 2023/4/4 9:57
 */
object CameraHelp {
    /**
     * Pseudo-colorutility
     */
    val TYPE_SET_PSEUDOCOLOR = 4

    /**
     * utility
     */
    val TYPE_SET_ParamLevelContrast = 3

    /**
     * utility（utility）
     */
    val TYPE_SET_ParamLevelDde = 2

    /**
     * Alert/Warning
     */
    val TYPE_SET_ALARM = 12 // Early warning

    /**
     * Rotation
     */
    val TYPE_SET_ROTATE = 1

    /**
     * Font/Text
     */
    val TYPE_SET_COLOR = 13 // Color value

    /**
     * Mirror
     */
    val TYPE_SET_MIRROR = 14 // Utility function

    /**
     * utility 2D utility：utility
     */
    val TYPE_SET_WATERMARK = 15 // Utility function

    /**
     * utility TS001-Observation：utility
     */
    val TYPE_SET_COMPASS = 23 // Utility function

    // TS001 -- utilityMode
    val TYPE_SET_HIGHTEMP = 20 // Calculate valueHigh temperature
    val TYPE_SET_LOWTEMP = 21 // Calculate valueLow temperature
    val TYPE_SET_DETELE = 22 // Delete

    // TS001 -- TargetMenu
    val TYPE_SET_TARGET_MODE = 30 // Target
    val TYPE_SET_TARGET_ZOOM = 31 // Calculate value
    val TYPE_SET_MEASURE_MODE = 32 // Calculate valueMode
    val TYPE_SET_TARGET_COLOR = 33 // Targetutility
    val TYPE_SET_TARGET_DELETE = 34 // Delete
    val TYPE_SET_TARGET_HELP = 35 // Utility function
}
