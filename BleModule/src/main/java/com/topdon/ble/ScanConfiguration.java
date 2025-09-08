package com.topdon.ble;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;



import androidx.annotation.RequiresApi;

import java.util.List;

 * date: 2021/8/12 15:31
 * author: bichuanfeng
public class ScanConfiguration {
    int scanPeriodMillis = 10000;
    boolean acceptSysConnectedDevice;
    ScanSettings scanSettings;
    boolean onlyAcceptBleDevice;
    int rssiLowLimit = -120;
    List<ScanFilter> filters;

    /**
     * Method description.
     */
    public int getScanPeriodMillis() {
        return scanPeriodMillis;
    }

    /**
     * Method description.
     */
    public boolean isAcceptSysConnectedDevice() {
        return acceptSysConnectedDevice;
    }

    /**
     * Method description.
     */
    public ScanSettings getScanSettings() {
        return scanSettings;
    }

    /**
     * Method description.
     */
    public boolean isOnlyAcceptBleDevice() {
        return onlyAcceptBleDevice;
    }

    /**
     * Method description.
     */
    public int getRssiLowLimit() {
        return rssiLowLimit;
    }

    /**
     * Method description.
     */
    public List<ScanFilter> getFilters() {
        return filters;
    }

     * @param scanPeriodMillis
    /**
     * Method description.
     */
    public ScanConfiguration setScanPeriodMillis(int scanPeriodMillis) {
        //1
        if (scanPeriodMillis >= 1000) {
            this.scanPeriodMillis = scanPeriodMillis;
        }
        return this;
    }

    /**
     * Method description.
     */
    public ScanConfiguration setAcceptSysConnectedDevice(boolean acceptSysConnectedDevice) {
        this.acceptSysConnectedDevice = acceptSysConnectedDevice;
        return this;
    }

     * {@link BluetoothLeScanner}
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    /**
     * Method description.
     */
    public ScanConfiguration setScanSettings(ScanSettings scanSettings) {
        Inspector.requireNonNull(scanSettings, "scanSettings can't be null");
        this.scanSettings = scanSettings;
        return this;
    }

     * ble
    /**
     * Method description.
     */
    public ScanConfiguration setOnlyAcceptBleDevice(boolean onlyAcceptBleDevice) {
        this.onlyAcceptBleDevice = onlyAcceptBleDevice;
        return this;
    }

    /**
     * Method description.
     */
    public ScanConfiguration setRssiLowLimit(int rssiLowLimit) {
        this.rssiLowLimit = rssiLowLimit;
        return this;
    }

     * {@link BluetoothLeScanner#startScan(List, ScanSettings, ScanCallback)}
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    /**
     * Method description.
     */
    public ScanConfiguration setFilters(List<ScanFilter> filters) {
        this.filters = filters;
        return this;
    }
}
