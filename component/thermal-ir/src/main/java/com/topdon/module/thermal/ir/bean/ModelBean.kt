package com.topdon.module.thermal.ir.bean

/**
 * Mode
 */
data class ModelBean(
    var defaultModel: DataBean,
    var myselfModel: ArrayList<DataBean> = arrayListOf()
)

data class DataBean(
    var id: Int = 1,
    var name: String = "1",
    var environment: Float = 30.0f,//[CN_TEXT]，[CN_TEXT]Celsius
    var distance: Float = 0.25f,//[CN_TEXT]，[CN_TEXT]
    var radiation: Float = 0.95f,//[CN_TEXT]
    var use: Boolean = false
)
