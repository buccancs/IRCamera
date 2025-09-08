package com.topdon.module.thermal.ir.bean

/**
 * mode
 */
data class ModelBean(
    var defaultModel: DataBean,
    var myselfModel: ArrayList<DataBean> = arrayListOf()
)

data class DataBean(
    var id: Int = 1,
    var name: String = "1",
    var environment: Float = 30.0f,// [Chinese text]temperature, [Chinese text]
    var distance: Float = 0.25f,// [Chinese text], [Chinese text]
    var radiation: Float = 0.95f,// [Chinese text]
    var use: Boolean = false
)
