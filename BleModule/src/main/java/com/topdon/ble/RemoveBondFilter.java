package com.topdon.ble;

import android.bluetooth.BluetoothDevice;

/**
 * [Chinese text]
 * <p>
 * date: 2021/8/12 21:11
 * author: bichuanfeng
 */
public interface RemoveBondFilter {
    boolean accept(BluetoothDevice device);
}
