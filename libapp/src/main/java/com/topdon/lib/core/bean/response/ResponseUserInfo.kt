package com.topdon.lib.core.bean.response

data class ResponseUserInfo(
    /** topdonId property */
    val topdonId: String,
    /** userName property */
    val userName: String,
    /** email property */
    val email: String,
    /** url property */
    val url: String,
    /** pwd property */
    val pwd: String,
    /** remark property */
    val remark: String,
    /** createTime property */
    val createTime: Long,
    /** updateTime property */
    val updateTime: Long,
    /** profilePicture property */
    val profilePicture: String,
    /** lastVisitTime property */
    val lastVisitTime: String,
    /** phone property */
    val phone:String?,
    /** avatar property */
    val avatar:String?,
)