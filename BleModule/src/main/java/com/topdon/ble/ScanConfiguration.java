package com.topdon.ble;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

/**
 * Comment removed (contained Chinese characters)
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
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 public ScanConfiguration setScanPeriodMillis(int scanPeriodMillis) {
 // Comment removed (contained Chinese characters)
 if (scanPeriodMillis >= 1000) {
 this.scanPeriodMillis = scanPeriodMillis;
 }
 return this;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 public ScanConfiguration setAcceptSysConnectedDevice(boolean acceptSysConnectedDevice) {
 this.acceptSysConnectedDevice = acceptSysConnectedDevice;
 return this;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
 public ScanConfiguration setScanSettings(ScanSettings scanSettings) {
 Inspector.requireNonNull(scanSettings, "scanSettings can't be null");
 this.scanSettings = scanSettings;
 return this;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 public ScanConfiguration setOnlyAcceptBleDevice(boolean onlyAcceptBleDevice) {
 this.onlyAcceptBleDevice = onlyAcceptBleDevice;
 return this;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 public ScanConfiguration setRssiLowLimit(int rssiLowLimit) {
 this.rssiLowLimit = rssiLowLimit;
 return this;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
 public ScanConfiguration setFilters(List<ScanFilter> filters) {
 this.filters = filters;
 return this;
 }
}
