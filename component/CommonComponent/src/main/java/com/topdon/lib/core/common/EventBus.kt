package com.topdon.lib.core.common

/**
 * EventBus stub for component modules
 * Simplified event handling - component modules should use callbacks instead
 */
object EventBus {
    
    fun getDefault(): EventBus {
        return this
    }
    
    fun register(subscriber: Any) {
        // Simplified implementation - components should use direct callbacks
    }
    
    fun unregister(subscriber: Any) {
        // Simplified implementation
    }
    
    fun post(event: Any) {
        // Simplified implementation - components should use direct callbacks
    }
    
    fun postSticky(event: Any) {
        // Simplified implementation
    }
    
    fun removeStickyEvent(event: Any) {
        // Simplified implementation
    }
}