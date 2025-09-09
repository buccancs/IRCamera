package com.topdon.module.thermal.ir.camera.thermal_lite.task;


public abstract class BaseTask implements Runnable {
    protected static final String TAG = BaseTask.class.getSimpleName();
    protected DeviceState mDeviceState;

    public BaseTask() {

    }

    public DeviceState getDeviceState() {
        return mDeviceState;
    }

    public void setDeviceState(DeviceState mDeviceState) {
        this.mDeviceState = mDeviceState;
    }
}
