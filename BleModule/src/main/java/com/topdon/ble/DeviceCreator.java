package com.topdon.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import androidx.annotation.Nullable;

/**
 * {@link Device}实例Build器，Search到BLEdevice时，使用此Build器实例化{@link Device}
 * <p>
 * date: 2021/8/12 00:07
 * author: bichuanfeng
 */
public interface DeviceCreator {
    /**
     * Search到bluetoothdevice后，根据广播data实例化{@link Device}，并且根据广播Filterdevice
     *
     * @param scanResult Search的结果data
     * @return 如果不是需要的device，Returnnull，Filter掉，那么它不会触发Search结果Callback，否则Return实例化的{@link Device}，触发Search结果Callback
     */
    @Nullable
    Device create(BluetoothDevice device, ScanResult scanResult);
}
