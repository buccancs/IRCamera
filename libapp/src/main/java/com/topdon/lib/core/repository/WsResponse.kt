package com.topdon.lib.core.repository

/**
 * websocket [Chinese text].
 * @param cmd [Chinese text]
 * @param data [Chinese text], [Chinese text]
 * @param id [Chinese text]id
 */
data class WsResponse<T>(
    val cmd: Int,
    val data: T?,
    val id: String,
)

/**
 * websocket - Pseudo color[Chinese text]
 */
data class WsPseudoColor(
    val enable: Boolean?, // [Chinese text]-1, [Chinese text]-2, [Chinese text]-12, [Chinese text]-5, [Chinese text]-16
    val mode: Int?,
)

/**
 * websocket - [Chinese text]
 */
data class WsRange(
    val state: Int?, // 0-[Chinese text], 1-[Chinese text]
)

/**
 * websocket - [Chinese text]
 */
data class WsLight(
    val brightness: Int?, // 81-100 high, 61-80 in progress, 0-60 low
)

/**
 * websocket - [Chinese text]in progress[Chinese text]
 */
data class WsPip(
    val enable: Int?, // 0-[Chinese text], 1-[Chinese text]
)

/**
 * websocket - [Chinese text]
 */
data class WsZoom(
    val enable: Boolean?, // [Chinese text]
    val factor: Int?, // [Chinese text]
)
