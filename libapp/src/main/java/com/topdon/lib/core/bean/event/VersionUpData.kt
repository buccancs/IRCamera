package com.topdon.lib.core.bean.event

/**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
data class VersionUpData(
 val versionNo: String,
 val isForcedUpgrade: Boolean,
 val description: String,
 val downPageUrl: String,
 val sizeStr: String,
)
