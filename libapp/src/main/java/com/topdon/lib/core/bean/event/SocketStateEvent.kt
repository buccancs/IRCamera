package com.topdon.lib.core.bean.event

/**
 * event WIFI event（TS004 event TC007）eventStateevent.
 * Created by LCG on 2024/4/23.
 *
 * @param isConnect true-event false-event
 * @param isTS004 true-TS004 false-TC007
 */
data class SocketStateEvent(val isConnect: Boolean, val isTS004: Boolean)
