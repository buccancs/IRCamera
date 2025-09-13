package com.topdon.module.thermal.ir.activity

/**
 * Base IR Plus Activity - Simplified for compilation
 * 
 * This is a minimal implementation to allow compilation while preserving
 * the core PC-Phone communication functionality. The full thermal-ir 
 * implementation can be restored once compilation issues are resolved.
 */
abstract class BaseIRPlushActivity : BaseIRActivity() {
    
    // Minimal placeholder implementation to enable compilation
    protected open fun setTemperatureViewType() {
        // Implementation placeholder
    }
    
    protected open fun setDispViewData(dualDisp: Int) {
        // Implementation placeholder  
    }
    
    override fun initView() {
        super.initView()
        // Minimal initialization
    }
    
    protected open fun switch2contentSession(isUSBTemperatureSupport: Boolean) {
        // Implementation placeholder
    }
    
    companion object {
        private const val TAG = "BaseIRPlushActivity"
    }
}