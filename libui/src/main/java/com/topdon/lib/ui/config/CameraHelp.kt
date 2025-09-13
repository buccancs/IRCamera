package com.topdon.lib.ui.config

/**
 * 
 * @author: CaiSongL
 * @date: 2023/4/4 9:57
 */
/**
 * CameraHelp class
 */
/**
 * Camera help utility class for thermal imaging operations.
 * Provides helper functions and common functionality.
 */
object CameraHelp {
    /**
     * pseudo color
     */
    val TYPE_SET_PSEUDOCOLOR = 4

    /**
     * 
     */
    val TYPE_SET_ParamLevelContrast = 3

    /**
     * （）
     */
    val TYPE_SET_ParamLevelDde = 2

    /**
     * warning
     */
    val TYPE_SET_ALARM = 12 // 

    /**
     * 
     */
    val TYPE_SET_ROTATE = 1

    /**
     * font
     */
    val TYPE_SET_COLOR = 13 // color

    /**
     * 
     */
    val TYPE_SET_MIRROR = 14 // 

    /**
     *  2D ：watermark
     */
    val TYPE_SET_WATERMARK = 15 // watermark

    /**
     *  TS001-observation：
     */
    val TYPE_SET_COMPASS = 23 // 

    // TS001 -- 
    val TYPE_SET_HIGHTEMP = 20 // 
    val TYPE_SET_LOWTEMP = 21 // 
    val TYPE_SET_DETELE = 22 // 

    // TS001 -- targetmenu
    val TYPE_SET_TARGET_MODE = 30 // target
    val TYPE_SET_TARGET_ZOOM = 31 // 
    val TYPE_SET_MEASURE_MODE = 32 // measurement mode
    val TYPE_SET_TARGET_COLOR = 33 // targetcolor
    val TYPE_SET_TARGET_DELETE = 34 // 
    val TYPE_SET_TARGET_HELP = 35 // 
}
