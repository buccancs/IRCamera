package com.topdon.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import androidx.annotation.Nullable;

/**
 * <p>
 * date: 2021/8/12 00:07
* author: bichuanfeng
 */
public interface DeviceCreator {
 /**
 *
 */
 @Nullable
 Device create(BluetoothDevice device, ScanResult scanResult);
}
