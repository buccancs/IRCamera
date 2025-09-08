package com.topdon.module.thermal.ir.bean

data class ModelBean(
    /** defaultModel property */
    var defaultModel: DataBean,
    /** myselfModel property */
    var myselfModel: ArrayList<DataBean> = arrayListOf()
)

data class DataBean(
    /** id property */
    var id: Int = 1,
    /** name property */
    var name: String = "1",
    /** environment property */
    var environment: Float = 30.0f,//，
    /** distance property */
    var distance: Float = 0.25f,//，
    /** radiation property */
    var radiation: Float = 0.95f,//
    /** use property */
    var use: Boolean = false
)
