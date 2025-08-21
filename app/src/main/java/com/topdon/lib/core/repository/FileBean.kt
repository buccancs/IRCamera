package com.topdon.lib.core.repository

/**
 * TS004 remote file data structure
 */
data class FileBean(
    val id: Int,
    val name: String,
    val thumb: String,
    val duration: Int, // duration in seconds
    val time: Long // timestamp in seconds
)