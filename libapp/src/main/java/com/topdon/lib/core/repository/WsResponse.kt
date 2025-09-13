package com.topdon.lib.core.repository

/**
 */
data class WsResponse<T>(
 val cmd: Int,
 val data: T?,
 val id: String,
)

/**
 */
data class WsPseudoColor(
 val enable: Boolean?, // -1，-2，-12, -5，-16
 val mode: Int?,
)

/**
 */
data class WsRange(
 val state: Int?, // 0-，1-
)

/**
 */
data class WsLight(
 val brightness: Int?, // 81-100 ，61-80 ，0-60 
)

/**
 */
data class WsPip(
 val enable: Int?, // 0-，1-
)

/**
 */
data class WsZoom(
 val enable: Boolean?, // 
 val factor: Int?, // 
)
