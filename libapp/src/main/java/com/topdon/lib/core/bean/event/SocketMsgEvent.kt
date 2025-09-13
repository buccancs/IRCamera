package com.topdon.lib.core.bean.event

/**
 * Receive到远端 Socket Send过来的一条messageEvent.
 * Created by LCG on 2024/4/23.
 *
 * @param text message内容
 */
data class SocketMsgEvent(val text: String)
