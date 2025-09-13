package com.topdon.ble;

import android.bluetooth.BluetoothDevice;

/**
 * clear已配对device时的Filter器
 * <p>
 * date: 2021/8/12 21:11
 * author: bichuanfeng
 */
public interface RemoveBondFilter {
    boolean accept(BluetoothDevice device);
}
