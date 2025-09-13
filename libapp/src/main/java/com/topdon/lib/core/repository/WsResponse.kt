package com.topdon.lib.core.repository

/**
 * websocket .
 * @param cmd 
 * @param data ，
 * @param id id
 */
data class WsResponse<T>(
    val cmd: Int,
    val data: T?,
    val id: String,
)

/**
 * websocket - 
 */
data class WsPseudoColor(
    val enable: Boolean?, // -1，-2，-12, -5，-16
    val mode: Int?,
)

/**
 * websocket - 
 */
data class WsRange(
    val state: Int?, // 0-，1-
)

/**
 * websocket - 
 */
data class WsLight(
    val brightness: Int?, // 81-100 ，61-80 ，0-60 
)

/**
 * websocket - 
 */
data class WsPip(
    val enable: Int?, // 0-，1-
)

/**
 * websocket - 
 */
data class WsZoom(
    val enable: Boolean?, // 
    val factor: Int?, // 
)
