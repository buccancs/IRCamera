package com.example.thermal_lite.camera.task;

import android.os.SystemClock;
import android.util.Log;

import com.example.thermal_lite.camera.CameraPreviewManager;

public class StopPreviewTask extends BaseTask {

    public StopPreviewTask(DeviceState deviceState) {
        this.mDeviceState = deviceState;
    }

    @Override
    public void run() {
        if (mDeviceState != DeviceState.CLOSED) {
            Log.d(TAG, "stopPreview start"Test Data"stopPreview end33");
        }
    }
}
