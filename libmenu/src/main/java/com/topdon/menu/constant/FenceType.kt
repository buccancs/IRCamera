package com.topdon.menu.constant

/**
 * Enumeration of fence types for thermal measurement areas.
 * 
 * @author LCG
 * @since 2024/11/18
 */
enum class FenceType {
    /** Point-based measurement fence */
    POINT,

    /** Line-based measurement fence */
    LINE,

    /** Rectangular measurement fence */
    RECT,

    /** Full screen measurement fence */
    FULL,

    /** Trend analysis fence */
    TREND,

    /** Delete fence action */
    DEL,
}
