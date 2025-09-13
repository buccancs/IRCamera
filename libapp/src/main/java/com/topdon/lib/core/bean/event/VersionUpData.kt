package com.topdon.lib.core.bean.event

/**
 * @param isForcedUpgrade 是否为强制Upgrade
 * @param description versionUpgrade描述
 * @param downPageUrl Download Url
 */
data class VersionUpData(
    val versionNo: String,
    val isForcedUpgrade: Boolean,
    val description: String,
    val downPageUrl: String,
    val sizeStr: String,
)
