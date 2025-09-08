package com.topdon.lib.core.repository


 * websocket .
 * @param cmd
 * @param data
 * @param id id
data class WsResponse<T>(
    /** cmd property */
    val cmd: Int,
    /** data property */
    val data: T?,
    /** id property */
    val id: String,
)

 * websocket
data class WsPseudoColor(
    /** enable property */
    val enable: Boolean?,//-1，-2，-12, -5，-16
    /** mode property */
    val mode: Int?,
)

 * websocket
data class WsRange(
    /** state property */
    val state: Int?//0-，1-
)

 * websocket
data class WsLight(
    /** brightness property */
    val brightness: Int?//81-100 ，61-80 ，0-60
)
 * websocket
data class WsPip(
    /** enable property */
    val enable: Int?,//0-，1-
)

 * websocket
data class WsZoom(
    /** enable property */
    val enable: Boolean?,//
    /** factor property */
    val factor: Int?,//
)
