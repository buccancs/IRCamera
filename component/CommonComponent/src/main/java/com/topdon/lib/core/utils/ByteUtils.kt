package com.topdon.lib.core.utils

/**
 * Byte utilities for data conversion
 */
class ByteUtils {
    
    companion object {
        
        /**
         * Convert byte array to integer
         */
        fun bytesToInt(bytes: ByteArray, offset: Int = 0, length: Int = 4): Int {
            if (bytes.size < offset + length) {
                return 0
            }
            
            var result = 0
            for (i in 0 until length) {
                result = (result shl 8) or (bytes[offset + i].toInt() and 0xFF)
            }
            return result
        }
        
        /**
         * Convert integer to byte array
         */
        fun intToBytes(value: Int): ByteArray {
            return byteArrayOf(
                (value shr 24).toByte(),
                (value shr 16).toByte(),
                (value shr 8).toByte(),
                value.toByte()
            )
        }
        
        /**
         * Convert byte array to hex string
         */
        fun bytesToHex(bytes: ByteArray): String {
            return bytes.joinToString("") { "%02x".format(it) }
        }
        
        /**
         * Convert hex string to byte array
         */
        fun hexToBytes(hex: String): ByteArray {
            val len = hex.length
            val data = ByteArray(len / 2)
            for (i in 0 until len step 2) {
                data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
            }
            return data
        }
        
        /**
         * Check if byte array is empty
         */
        fun isEmpty(bytes: ByteArray?): Boolean {
            return bytes == null || bytes.isEmpty()
        }
        
        /**
         * Get index from byte array - stub implementation
         */
        fun getIndex(bytes: ByteArray, value: Byte): Int {
            return bytes.indexOf(value)
        }
        
        /**
         * Get index from integer value
         */
        fun getIndex(value: Int): Int {
            return value and 0xFF
        }
        
        /**
         * Get index from integer value with position
         */
        fun getIndex(value: Int, position: Int): Int {
            return (value shr (position * 8)) and 0xFF
        }
    }
}

/**
 * Extension function for Int to get bit flag at index
 */
fun Int.getIndex(index: Int): Int {
    return if (this and (1 shl (4 * (index - 1))) != 0) 1 else 0
}