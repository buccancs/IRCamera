package com.topdon.ble.callback;

import android.bluetooth.BluetoothDevice;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:43
 * author: bichuanfeng
 */
public interface PhyChangeCallback extends RequestFailedCallback {
    /**
     * @param request [Chinese text]
     * @param txPhy   [Chinese text]. {@link BluetoothDevice#PHY_LE_1M_MASK}[Chinese text]
     * @param rxPhy   [Chinese text]. {@link BluetoothDevice#PHY_LE_1M_MASK}[Chinese text]
     */
    void onPhyChange(Request request, int txPhy, int rxPhy);
}
