package com.topdon.lib.core.bean.event

/**
 * [CN_TEXT] WIFI [CN_TEXT]（TS004 [CN_TEXT] TC007）[CN_TEXT]State[CN_TEXT].
 * Created by LCG on 2024/4/23.
 *
 * @param isConnect true-[CN_TEXT] false-[CN_TEXT]
 * @param isTS004 true-TS004 false-TC007
 */
data class SocketStateEvent(val isConnect: Boolean, val isTS004: Boolean)
