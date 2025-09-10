package com.topdon.lib.core.bean.event

/**
 * @param isForcedUpgrade data
 * @param description data
 * @param downPageUrl data Url
 */
data class VersionUpData(
    val versionNo: String,
    val isForcedUpgrade: Boolean,
    val description: String,
    val downPageUrl: String,
    val sizeStr: String,
)
