package com.topdon.lib.core.repository

/**
 * websocket [CN_TEXT].
 * @param cmd [CN_TEXT]
 * @param data [CN_TEXT]，[CN_TEXT]
 * @param id [CN_TEXT]id
 */
data class WsResponse<T>(
    val cmd: Int,
    val data: T?,
    val id: String,
)

/**
 * websocket - Pseudo-color[CN_TEXT]
 */
data class WsPseudoColor(
    val enable: Boolean?, // White hot-1，Black hot-2，[CN_TEXT]-12, Iron red-5，[CN_TEXT]Bird-16
    val mode: Int?,
)

/**
 * websocket - [CN_TEXT]
 */
data class WsRange(
    val state: Int?, // 0-[CN_TEXT]，1-[CN_TEXT]
)

/**
 * websocket - [CN_TEXT]
 */
data class WsLight(
    val brightness: Int?, // 81-100 [CN_TEXT]，61-80 [CN_TEXT]，0-60 [CN_TEXT]
)

/**
 * websocket - Picture in picture
 */
data class WsPip(
    val enable: Int?, // 0-[CN_TEXT]，1-[CN_TEXT]
)

/**
 * websocket - [CN_TEXT]
 */
data class WsZoom(
    val enable: Boolean?, // [CN_TEXT]
    val factor: Int?, // [CN_TEXT]
)
