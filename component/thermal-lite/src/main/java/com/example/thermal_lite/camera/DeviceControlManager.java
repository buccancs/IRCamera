package com.example.thermal_lite.camera;

import android.util.Log;

import com.energy.iruvccamera.usb.USBMonitor;
import com.example.thermal_lite.camera.task.DeviceControlWorker;
import com.example.thermal_lite.camera.task.IDeviceConnectListener;
import com.example.thermal_lite.camera.task.PausePreviewTask;
import com.example.thermal_lite.camera.task.ResumePreviewTask;
import com.example.thermal_lite.camera.task.StartPreviewTask;
import com.example.thermal_lite.camera.task.StopPreviewTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjibo on 2024/5/17.
 */
public class DeviceControlManager implements IDeviceConnectListener {

    private static final String TAG = "DualDeviceControlManager";

    private DeviceControlWorker mDeviceControlWorker;

    private HashMap<String, IDeviceConnectListener> mIDeviceConnectListeners;

    private DeviceControlManager() {

    }

    private static DeviceControlManager mInstance;

    public static synchronized DeviceControlManager getInstance() {
        if (mInstance == null) {
            mInstance = new DeviceControlManager();
        }
        return mInstance;
    }

        /**
     * [Chinese text]
     */
    public void init() {
        mDeviceControlWorker = new DeviceControlWorker();
        mDeviceControlWorker.setDeviceControlCallback(this);
        mDeviceControlWorker.startWork();
        mIDeviceConnectListeners = new HashMap<>();
    }

        /**
     * [Chinese text], [Chinese text]activity[Chinese text]fragmentin progress[Chinese text], forUI[Chinese text]
     * @param key [Chinese text]
     * @param iDeviceConnectListener
     */
    public void addDeviceConnectListener(String key, IDeviceConnectListener iDeviceConnectListener) {
        if (mIDeviceConnectListeners != null) {
            mIDeviceConnectListeners.put(key, iDeviceConnectListener);
        }
    }

        /**
     * [Chinese text]
     * @param key
     */
    public void removeDeviceConnectListener(String key) {
        if (mIDeviceConnectListeners != null) {
            mIDeviceConnectListeners.remove(key);
        }
    }

        /**
     * [Chinese text]
     */
    public void release() {
        if (mDeviceControlWorker != null) {
            mDeviceControlWorker.release();
            mDeviceControlWorker = null;
        }
        if (mIDeviceConnectListeners != null) {
            mIDeviceConnectListeners.clear();
            mIDeviceConnectListeners = null;
        }
    }

        /**
     * Dual light[Chinese text]
     * @param ctrlBlock
     */
    public void handleStartPreview(USBMonitor.UsbControlBlock ctrlBlock) {
        if (mDeviceControlWorker != null) {
            Log.d(TAG, "handleStartPreview");
            mDeviceControlWorker.addTask(new StartPreviewTask(ctrlBlock, mDeviceControlWorker.getDeviceState()));
        }
    }

        /**
     * Dual light[Chinese text]
     */
    public void handleStopPreview() {
        if (mDeviceControlWorker != null) {
            Log.d(TAG, "handleStopPreview");
            mDeviceControlWorker.addTask(new StopPreviewTask(mDeviceControlWorker.getDeviceState()));
        }
    }

        /**
     * Dual light[Chinese text]
     */
    public void handlePauseDualPreview() {
        if (mDeviceControlWorker != null) {
            Log.d(TAG, "handlePausePreview");
            mDeviceControlWorker.addTask(new PausePreviewTask(mDeviceControlWorker.getDeviceState()));
        }
    }

        /**
     * Dual light[Chinese text]
     */
    public void handleResumeDualPreview() {
        if (mDeviceControlWorker != null) {
            Log.d(TAG, "handleResumePreview");
            mDeviceControlWorker.addTask(new ResumePreviewTask(mDeviceControlWorker.getDeviceState()));
        }
    }

    @Override
    public void onPrepareConnect() {
        // StartPreview[Chinese text]
        for (Map.Entry<String, IDeviceConnectListener> entry: mIDeviceConnectListeners.entrySet()) {
            entry.getValue().onPrepareConnect();
        }
    }

    @Override
    public void onConnected() {
        // StartPreview[Chinese text], [Chinese text]line[Chinese text]
        for (Map.Entry<String, IDeviceConnectListener> entry: mIDeviceConnectListeners.entrySet()) {
            entry.getValue().onConnected();
        }
    }

    @Override
    public void onDisconnected() {
        // StopPreview[Chinese text], [Chinese text]line[Chinese text]
        for (Map.Entry<String, IDeviceConnectListener> entry: mIDeviceConnectListeners.entrySet()) {
            entry.getValue().onDisconnected();
        }
    }

    @Override
    public void onPaused() {
        // todo [Chinese text]Paused Task[Chinese text]implement
        for (Map.Entry<String, IDeviceConnectListener> entry: mIDeviceConnectListeners.entrySet()) {
            entry.getValue().onPaused();
        }
    }

    @Override
    public void onResumed() {
        // todo [Chinese text]Resumed Task[Chinese text]implement
        for (Map.Entry<String, IDeviceConnectListener> entry: mIDeviceConnectListeners.entrySet()) {
            entry.getValue().onResumed();
        }
    }
}