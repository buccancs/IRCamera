package com.topdon.lib.core.bean.event

/**
 * @param isForcedUpgrade [Chinese text]
 * @param description [Chinese text]
 * @param downPageUrl [Chinese text] Url
 */
data class VersionUpData(
    val versionNo: String,
    val isForcedUpgrade: Boolean,
    val description: String,
    val downPageUrl: String,
    val sizeStr: String,
)
