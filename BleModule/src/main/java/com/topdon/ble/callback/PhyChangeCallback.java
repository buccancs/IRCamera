package com.topdon.ble.callback;

import android.bluetooth.BluetoothDevice;

import com.topdon.ble.Request;

/**
 * date: 2021/8/12 17:43
* author: bichuanfeng
 */
public interface PhyChangeCallback extends RequestFailedCallback {
 /**
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 void onPhyChange(Request request, int txPhy, int rxPhy);
}
