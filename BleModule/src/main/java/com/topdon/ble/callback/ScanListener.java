package com.topdon.ble.callback;

import android.Manifest;

import com.topdon.ble.Device;

/**
 * [Chinese text]listener[Chinese text]
 * <p>
 * date: 2021/8/12 09:17
 * author: bichuanfeng
 */
public interface ScanListener {
    /**
     * [Chinese text].  {@link Manifest.permission#ACCESS_COARSE_LOCATION} [Chinese text] {@link Manifest.permission#ACCESS_FINE_LOCATION}
     */
    int ERROR_LACK_LOCATION_PERMISSION = 0;
    /**
     * [Chinese text]
     */
    int ERROR_LOCATION_SERVICE_CLOSED = 1;
    /**
     * [Chinese text]
     */
    int ERROR_SCAN_FAILED = 2;

    /**
     * [Chinese text]start
     */
    void onScanStart();

    /**
     * [Chinese text]stop
     */
    void onScanStop();

    /**
     * [Chinese text]BLE[Chinese text]
     *
     * @deprecated [Chinese text] {@link #onScanResult(Device, boolean)}, [Chinese text], [Chinese text]
     */
    @Deprecated
    default void onScanResult(Device device) {
    }

    /**
     * [Chinese text]BLE[Chinese text]
     *
     * @param device           [Chinese text]
     * @param isConnectedBySys [Chinese text]
     */
    void onScanResult(Device device, boolean isConnectedBySys);

    /**
     * [Chinese text]
     *
     * @param errorCode {@link #ERROR_LACK_LOCATION_PERMISSION}, {@link #ERROR_LOCATION_SERVICE_CLOSED}
     */
    void onScanError(int errorCode, String errorMsg);
}
