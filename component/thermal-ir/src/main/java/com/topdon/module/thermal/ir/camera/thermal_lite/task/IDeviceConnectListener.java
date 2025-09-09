package com.topdon.module.thermal.ir.camera.thermal_lite.task;

/**
 * Created by fengjibo on 2022/4/19.
 */
public interface IDeviceConnectListener {
    void onPrepareConnect();
    void onConnected();
    void onDisconnected();
    void onPaused();
    void onResumed();
}
