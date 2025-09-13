package com.topdon.ble.callback;

import android.Manifest;

import com.topdon.ble.Device;

/**
 * bluetoothSearchListener器
 * <p>
 * date: 2021/8/12 09:17
 * author: bichuanfeng
 */
public interface ScanListener {
    /**
     * 缺少定位Permission。 {@link Manifest.permission#ACCESS_COARSE_LOCATION} 或者 {@link Manifest.permission#ACCESS_FINE_LOCATION}
     */
    int ERROR_LACK_LOCATION_PERMISSION = 0;
    /**
     * 系统位置service未开启
     */
    int ERROR_LOCATION_SERVICE_CLOSED = 1;
    /**
     * Searcherror
     */
    int ERROR_SCAN_FAILED = 2;
    /**
     * 缺少bluetoothPermission。 {@link Manifest.permission#BLUETOOTH_SCAN} 或者 {@link Manifest.permission#BLUETOOTH_CONNECT}
     */
    int ERROR_LACK_BLUETOOTH_PERMISSION = 3;

    /**
     * bluetoothSearchstart
     */
    void onScanStart();

    /**
     * bluetoothSearchstop
     */
    void onScanStop();

    /**
     * Search到BLEdevice
     *
     * @deprecated 使用 {@link #onScanResult(Device, boolean)}，不要再覆写此method，因为不再会被Callback
     */
    @Deprecated
    default void onScanResult(Device device) {
    }

    /**
     * Search到BLEdevice
     *
     * @param device           Search到的device
     * @param isConnectedBySys 是否已被系统bluetoothconnection上
     */
    void onScanResult(Device device, boolean isConnectedBySys);

    /**
     * Searcherror
     *
     * @param errorCode {@link #ERROR_LACK_LOCATION_PERMISSION}, {@link #ERROR_LOCATION_SERVICE_CLOSED}, {@link #ERROR_LACK_BLUETOOTH_PERMISSION}
     */
    void onScanError(int errorCode, String errorMsg);
}
