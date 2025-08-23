package com.topdon.lib.core.bean

/**
 * Watermark configuration bean for image/video watermarks
 */
data class WatermarkBean(
    var isOpen: Boolean = false,
    var title: String = "",
    var address: String = "",
    var isAddTime: Boolean = true,
    var timeFormat: String = "yyyy-MM-dd HH:mm:ss",
    var position: Int = POSITION_BOTTOM_RIGHT,
    var textSize: Float = 24f,
    var textColor: Int = 0xFFFFFFFF.toInt(),
    var backgroundColor: Int = 0x80000000.toInt()
) {
    companion object {
        const val POSITION_TOP_LEFT = 0
        const val POSITION_TOP_RIGHT = 1
        const val POSITION_BOTTOM_LEFT = 2
        const val POSITION_BOTTOM_RIGHT = 3
        
        fun createDefault(): WatermarkBean {
            return WatermarkBean(
                isOpen = false,
                title = "IR Camera",
                address = "",
                isAddTime = true
            )
        }
    }
}