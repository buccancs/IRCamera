package com.energy.commoncomponent.bean

/**
 * Temporary RotateDegree enum to fix compilation issues.
 * This should be replaced with the proper external library dependency.
 */
enum class RotateDegree(private val value: Int) {
    DEGREE_0(0),
    DEGREE_90(90),
    DEGREE_180(180),
    DEGREE_270(270);

    fun getValue(): Int = value
}