package com.topdon.lib.core.bean.event

/**
 * 目标 WIFI device（TS004 或 TC007）connectionstate变更Event.
 * Created by LCG on 2024/4/23.
 *
 * @param isConnect true-已connection false-已disconnect
 * @param isTS004 true-TS004 false-TC007
 */
data class SocketStateEvent(val isConnect: Boolean, val isTS004: Boolean)
