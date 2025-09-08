package com.topdon.lib.core.bean.event

 * Device connection state event (legacy - now TC001 uses USB connection only).
 * Created by LCG on 2024/4/23.
 * @param isConnect true- false
 * @param isTS004 legacy parameter (unused for TC001)
data class SocketStateEvent(val isConnect: Boolean, val isTS004: Boolean)