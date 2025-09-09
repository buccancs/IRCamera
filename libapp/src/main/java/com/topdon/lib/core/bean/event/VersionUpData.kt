package com.topdon.lib.core.bean.event

/**
 * @param isForcedUpgrade [CN_TEXT]
 * @param description [CN_TEXT]
 * @param downPageUrl [CN_TEXT] Url
 */
data class VersionUpData(
    val versionNo: String,
    val isForcedUpgrade: Boolean,
    val description: String,
    val downPageUrl: String,
    val sizeStr: String,
)
