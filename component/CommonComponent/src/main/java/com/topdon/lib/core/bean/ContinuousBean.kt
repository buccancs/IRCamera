package com.topdon.lib.core.bean

/**
 * Continuous monitoring configuration bean
 */
data class ContinuousBean(
    var isOpen: Boolean = false,
    var count: Int = 5,
    var continuaTime: Int = 1000, // milliseconds
    var interval: Int = 1000 // interval between measurements
) {
    companion object {
        fun createDefault(): ContinuousBean {
            return ContinuousBean(
                isOpen = false,
                count = 5,
                continuaTime = 1000,
                interval = 1000
            )
        }
    }
}