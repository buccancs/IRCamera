package com.topdon.lib.core.bean.json

data class CheckVersionJson(
    /** downloadPackageUrl property */
    val downloadPackageUrl: String,
    /** downloadPageUrl property */
    val downloadPageUrl: String,
    /** forcedUpgradeFlag property */
    val forcedUpgradeFlag: String?, //1: 0:
    /** googleVerCode property */
    val googleVerCode: Int,
    /** softConfigOtherTypeVOList property */
    val softConfigOtherTypeVOList: List<SoftConfigOtherTypeVO>,
    /** versionCode property */
    val versionCode: Int,
    /** versionNo property */
    val versionNo: String?,
    /** notUnZipSize property */
    val notUnZipSize: Double,
)

data class SoftConfigOtherTypeVO(
    /** descType property */
    val descType: Int,
    /** descTypeName property */
    val descTypeName: String,
    /** fileUrl property */
    val fileUrl: Any,
    /** textDescription property */
    val textDescription: String
)