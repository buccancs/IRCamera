package com.topdon.lib.core.bean.event

/**
 * Created by LCG on 2024/4/23.
 *
 * @param isTS004 true-TS004 false-TC007
 */
data class SocketStateEvent(val isConnect: Boolean, val isTS004: Boolean)
