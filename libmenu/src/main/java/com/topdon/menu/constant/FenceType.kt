package com.topdon.menu.constant

/**
 * Point, Line, Area, Full image, Trend chart (optional), Delete menu types.
 *
 * Created by LCG on 2024/11/18.
 */
enum class FenceType {
    /** Point measurement */
    POINT,

    /** Line measurement */
    LINE,

    /** Rectangle/Area measurement */
    RECT,

    /** Full image measurement */
    FULL,

    /** Trend chart */
    TREND,

    /** Delete operation */
    DEL,
}