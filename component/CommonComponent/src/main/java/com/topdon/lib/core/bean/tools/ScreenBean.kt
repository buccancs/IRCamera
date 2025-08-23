package com.topdon.lib.core.bean.tools

/**
 * Screen configuration bean for thermal display
 */
data class ScreenBean(
    val width: Int,
    val height: Int,
    val orientation: Int = 0,
    val density: Float = 1.0f
) {
    companion object {
        /**
         * Create default screen bean
         */
        fun createDefault(): ScreenBean {
            return ScreenBean(1920, 1080)
        }
    }
}