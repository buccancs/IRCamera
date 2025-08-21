package com.topdon.lib.core.common

import java.io.Serializable

/**
 * Feedback data bean
 */
data class FeedBackBean(
    var logPath: String = "",
    var sn: String = "",
    var lastConnectSn: String = ""
) : Serializable