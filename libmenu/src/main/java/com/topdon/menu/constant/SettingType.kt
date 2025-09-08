package com.topdon.menu.constant

/**
 * Temperature measurement mode - Menu 5 - Settings / Observation mode - Menu 6 - Settings menu types.
 *
 * Created by LCG on 2024/11/28.
 */
enum class SettingType {
    /** Pseudo color bar */
    PSEUDO_BAR,

    /** Contrast adjustment */
    CONTRAST,

    /** Sharpness (detail enhancement) */
    DETAIL,

    /** Image rotation */
    ROTATE,

    /** Mirror/flip image */
    MIRROR,

    /** Alarm settings */
    ALARM,

    /** Font settings */
    FONT,

    /** Compass (Observation mode only) */
    COMPASS,

    /** Watermark (2D editing only) */
    WATERMARK,
}