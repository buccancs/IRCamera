package com.topdon.module.thermal.camera.task;

import com.energy.iruvccamera.usb.USBMonitor;

/**
 * Start preview task - stub implementation for compilation
 */
public class StartPreviewTask extends BaseTask {
    
    private USBMonitor.UsbControlBlock controlBlock;
    private DeviceState deviceState;
    
    public StartPreviewTask(USBMonitor.UsbControlBlock controlBlock, DeviceState deviceState) {
        this.controlBlock = controlBlock;
        this.deviceState = deviceState;
    }
    
    /**
     * Execute start preview
     */
    public void execute() {
        // Stub implementation
    }
    
    /**
     * Cancel task
     */
    public void cancel() {
        // Stub implementation
    }
}