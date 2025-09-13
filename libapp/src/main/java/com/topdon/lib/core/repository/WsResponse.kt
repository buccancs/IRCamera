package com.topdon.lib.core.repository

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class WsResponse<T>(
 val cmd: Int,
 val data: T?,
 val id: String,
)

/**
 * Comment removed (contained Chinese characters)
 */
data class WsPseudoColor(
 val enable: Boolean?, // -1，-2，-12, -5，-16
 val mode: Int?,
)

/**
 * Comment removed (contained Chinese characters)
 */
data class WsRange(
 val state: Int?, // 0-，1-
)

/**
 * Comment removed (contained Chinese characters)
 */
data class WsLight(
 val brightness: Int?, // 81-100 ，61-80 ，0-60 
)

/**
 * Comment removed (contained Chinese characters)
 */
data class WsPip(
 val enable: Int?, // 0-，1-
)

/**
 * Comment removed (contained Chinese characters)
 */
data class WsZoom(
 val enable: Boolean?, // 
 val factor: Int?, // 
)
