package com.topdon.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;

/**
 * date: 2019/12/2 11:51
 * author: bichuanfeng
 */
public enum ScannerType {
    /**
     * [Chinese text]{@link BluetoothLeScanner}
     */
    LE,
    /**
     * [Chinese text]{@link BluetoothAdapter#startLeScan(BluetoothAdapter.LeScanCallback)}
     */
    LEGACY,
    /**
     * [Chinese text]{@link BluetoothAdapter#startDiscovery()}, [Chinese text], [Chinese text]Settings
     */
    CLASSIC
}
