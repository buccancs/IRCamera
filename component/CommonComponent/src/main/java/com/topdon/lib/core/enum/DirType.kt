package com.topdon.lib.core.enum

/**
 * Directory type enumeration for file operations
 */
enum class DirType(val value: Int) {
    IMAGE(1),
    VIDEO(2), 
    REPORT(3),
    THERMAL(4),
    LINE(5),
    TC007(6),
    TS004_LOCALE(7),
    TS004_REMOTE(8),
    OTHER(99);
    
    companion object {
        fun fromValue(value: Int): DirType {
            return values().find { it.value == value } ?: OTHER
        }
    }
}