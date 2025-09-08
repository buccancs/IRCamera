package com.topdon.lib.core.bean.event

/**
 * target WIFI [Chinese text](TS004 [Chinese text] TC007)[Chinese text]event.
 * Created by LCG on 2024/4/23.
 *
 * @param isConnect true-[Chinese text] false-[Chinese text]
 * @param isTS004 true-TS004 false-TC007
 */
data class SocketStateEvent(val isConnect: Boolean, val isTS004: Boolean)
