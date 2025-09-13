package com.topdon.ble;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

/**
 * Searchconfiguration
 * 
 * date: 2021/8/12 15:31
 * author: bichuanfeng
 */
public class ScanConfiguration {
    int scanPeriodMillis = 10000;
    boolean acceptSysConnectedDevice;
    ScanSettings scanSettings;
    boolean onlyAcceptBleDevice;
    int rssiLowLimit = -120;
    List<ScanFilter> filters;

    public int getScanPeriodMillis() {
        return scanPeriodMillis;
    }

    public boolean isAcceptSysConnectedDevice() {
        return acceptSysConnectedDevice;
    }

    public ScanSettings getScanSettings() {
        return scanSettings;
    }

    public boolean isOnlyAcceptBleDevice() {
        return onlyAcceptBleDevice;
    }

    public int getRssiLowLimit() {
        return rssiLowLimit;
    }

    public List<ScanFilter> getFilters() {
        return filters;
    }

    /**
     * Search周期
     *
     * @param scanPeriodMillis Search一次的毫秒值
     */
    public ScanConfiguration setScanPeriodMillis(int scanPeriodMillis) {
        //至少1秒
        if (scanPeriodMillis >= 1000) {
            this.scanPeriodMillis = scanPeriodMillis;
        }
        return this;
    }

    /**
     * 是否将通过系统bluetooth配对connection的deviceadd到Search结果中（有些手机无法Get/Retrieve到系统已connection的bluetoothdevice）
     */
    public ScanConfiguration setAcceptSysConnectedDevice(boolean acceptSysConnectedDevice) {
        this.acceptSysConnectedDevice = acceptSysConnectedDevice;
        return this;
    }

    /**
     * {@link BluetoothLeScanner}的Searchsettings
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public ScanConfiguration setScanSettings(ScanSettings scanSettings) {
        Inspector.requireNonNull(scanSettings, "scanSettings can't be null");
        this.scanSettings = scanSettings;
        return this;
    }

    /**
     * 是否Filter非bledevice
     */
    public ScanConfiguration setOnlyAcceptBleDevice(boolean onlyAcceptBleDevice) {
        this.onlyAcceptBleDevice = onlyAcceptBleDevice;
        return this;
    }

    /**
     * 根据信号强度Filter
     */
    public ScanConfiguration setRssiLowLimit(int rssiLowLimit) {
        this.rssiLowLimit = rssiLowLimit;
        return this;
    }

    /**
     * SearchFilter器。{@link BluetoothLeScanner#startScan(List, ScanSettings, ScanCallback)}
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public ScanConfiguration setFilters(List<ScanFilter> filters) {
        this.filters = filters;
        return this;
    }
}
