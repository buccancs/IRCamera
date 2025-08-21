package org.greenrobot.eventbus

/**
 * Simplified EventBus stub for component modules
 * Component modules should use simpler communication patterns
 */
class EventBus {
    
    companion object {
        private val instance = EventBus()
        
        fun getDefault(): EventBus {
            return instance
        }
    }
    
    fun post(event: Any) {
        // Simplified implementation - component modules should avoid complex event systems
    }
    
    fun register(subscriber: Any) {
        // Simplified implementation
    }
    
    fun unregister(subscriber: Any) {
        // Simplified implementation
    }
}