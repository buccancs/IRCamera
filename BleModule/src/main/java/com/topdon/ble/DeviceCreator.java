package com.topdon.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import androidx.annotation.Nullable;

/**
 * {@link Device}[Chinese text], [Chinese text]BLE[Chinese text], [Chinese text]{@link Device}
 * <p>
 * date: 2021/8/12 00:07
 * author: bichuanfeng
 */
public interface DeviceCreator {
    /**
     * [Chinese text], [Chinese text]{@link Device}, [Chinese text]
     *
     * @param scanResult [Chinese text]
     * @return [Chinese text], [Chinese text]null, [Chinese text], [Chinese text], [Chinese text]{@link Device}, [Chinese text]
     */
    @Nullable
    Device create(BluetoothDevice device, ScanResult scanResult);
}
