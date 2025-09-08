package com.example.thermal_lite.camera.task;

import android.os.SystemClock;
import android.util.Log;

import com.example.thermal_lite.camera.CameraPreviewManager;

    /**
     * StopPreviewTask class.
     *
     * Provides stoppreviewtask functionality.
     */
    public class StopPreviewTask extends BaseTask {

    public StopPreviewTask(DeviceState deviceState) {
        this.mDeviceState = deviceState;
    }

    @Override
    public void run() {
        if (mDeviceState != DeviceState.CLOSED) {
            Log.d(TAG, "stopPreview start");

            CameraPreviewManager.getInstance().stopPreview();
            SystemClock.sleep(100);
            CameraPreviewManager.getInstance().closePreview();
            // todo [Chinese text]200ms[Chinese text]usb[Chinese text], [Chinese text]operation[Chinese text], [Chinese text], [Chinese text]stop, [Chinese text]
            SystemClock.sleep(200);
            mDeviceState = DeviceState.CLOSED;
            Log.d(TAG, "stopPreview end33");
        }
    }
}
