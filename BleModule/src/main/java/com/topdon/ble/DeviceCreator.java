package com.topdon.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import androidx.annotation.Nullable;

/**
 * Comment removed (contained Chinese characters)
 * <p>
 * date: 2021/8/12 00:07
* author: bichuanfeng
 */
public interface DeviceCreator {
 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 @Nullable
 Device create(BluetoothDevice device, ScanResult scanResult);
}
