package com.topdon.ble.callback;

import android.Manifest;

import com.topdon.ble.Device;

/**
 * <p>
 * date: 2021/8/12 09:17
* author: bichuanfeng
 */
public interface ScanListener {
 /**
 */
 int ERROR_LACK_LOCATION_PERMISSION = 0;
 /**
 */
 int ERROR_LOCATION_SERVICE_CLOSED = 1;
 /**
 */
 int ERROR_SCAN_FAILED = 2;
 /**
 */
 int ERROR_LACK_BLUETOOTH_PERMISSION = 3;

 /**
 */
 void onScanStart();

 /**
 */
 void onScanStop();

 /**
 *
 */
 @Deprecated
 default void onScanResult(Device device) {
 }

 /**
 *
 */
 void onScanResult(Device device, boolean isConnectedBySys);

 /**
 *
 * @param errorCode {@link #ERROR_LACK_LOCATION_PERMISSION}, {@link #ERROR_LOCATION_SERVICE_CLOSED}, {@link #ERROR_LACK_BLUETOOTH_PERMISSION}
 */
 void onScanError(int errorCode, String errorMsg);
}
