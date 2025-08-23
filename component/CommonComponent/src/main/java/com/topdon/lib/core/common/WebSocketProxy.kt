package com.topdon.lib.core.common

/**
 * WebSocket proxy for network connectivity
 */
class WebSocketProxy private constructor() {
    
    private var connected = false
    
    companion object {
        @Volatile
        private var INSTANCE: WebSocketProxy? = null
        
        fun getInstance(): WebSocketProxy {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WebSocketProxy().also { INSTANCE = it }
            }
        }
    }
    
    fun isConnected(): Boolean {
        return connected
    }
    
    fun setConnected(connected: Boolean) {
        this.connected = connected
    }
}