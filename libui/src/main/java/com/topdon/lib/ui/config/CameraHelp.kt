package com.topdon.lib.ui.config

/**
 * [CN_TEXT]
 * @author: CaiSongL
 * @date: 2023/4/4 9:57
 */
object CameraHelp {
    /**
     * Pseudo-color[CN_TEXT]
     */
    val TYPE_SET_PSEUDOCOLOR = 4

    /**
     * [CN_TEXT]
     */
    val TYPE_SET_ParamLevelContrast = 3

    /**
     * [CN_TEXT]（[CN_TEXT]）
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
    val TYPE_SET_MIRROR = 14 // [CN_TEXT]

    /**
     * [CN_TEXT] 2D [CN_TEXT]：[CN_TEXT]
     */
    val TYPE_SET_WATERMARK = 15 // [CN_TEXT]

    /**
     * [CN_TEXT] TS001-Observation：[CN_TEXT]
     */
    val TYPE_SET_COMPASS = 23 // [CN_TEXT]

    // TS001 -- [CN_TEXT]Mode
    val TYPE_SET_HIGHTEMP = 20 // [CN_TEXT]High temperature
    val TYPE_SET_LOWTEMP = 21 // [CN_TEXT]Low temperature
    val TYPE_SET_DETELE = 22 // Delete

    // TS001 -- TargetMenu
    val TYPE_SET_TARGET_MODE = 30 // Target
    val TYPE_SET_TARGET_ZOOM = 31 // [CN_TEXT]
    val TYPE_SET_MEASURE_MODE = 32 // [CN_TEXT]Mode
    val TYPE_SET_TARGET_COLOR = 33 // Target[CN_TEXT]
    val TYPE_SET_TARGET_DELETE = 34 // Delete
    val TYPE_SET_TARGET_HELP = 35 // [CN_TEXT]
}
