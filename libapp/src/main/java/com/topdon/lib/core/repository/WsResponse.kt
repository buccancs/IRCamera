package com.topdon.lib.core.repository

data class WsResponse<T>(
    val cmd: Int,
    val data: T?,
    val id: String,
)

/**
 * websocket - Pseudo-colordata
 */
data class WsPseudoColor(
    val enable: Boolean?, // White hot-1，Black hot-2，data-12, Iron red-5，dataBird-16
    val mode: Int?,
)

/**
 * websocket - data
 */
data class WsRange(
    val state: Int?, // 0-data，1-data
)

/**
 * websocket - data
 */
data class WsLight(
    val brightness: Int?, // 81-100 data，61-80 data，0-60 data
)

/**
 * websocket - Picture in picture
 */
data class WsPip(
    val enable: Int?, // 0-data，1-data
)

/**
 * websocket - data
 */
data class WsZoom(
    val enable: Boolean?, // data
    val factor: Int?, // data
)
