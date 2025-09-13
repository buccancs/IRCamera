package com.topdon.ble.callback;

import android.Manifest;

import com.topdon.ble.Device;

/**
 * Comment removed (contained Chinese characters)
 * <p>
 * date: 2021/8/12 09:17
* author: bichuanfeng
 */
public interface ScanListener {
 /**
 * Comment removed (contained Chinese characters)
 */
 int ERROR_LACK_LOCATION_PERMISSION = 0;
 /**
 * Comment removed (contained Chinese characters)
 */
 int ERROR_LOCATION_SERVICE_CLOSED = 1;
 /**
 * Comment removed (contained Chinese characters)
 */
 int ERROR_SCAN_FAILED = 2;
 /**
 * Comment removed (contained Chinese characters)
 */
 int ERROR_LACK_BLUETOOTH_PERMISSION = 3;

 /**
 * Comment removed (contained Chinese characters)
 */
 void onScanStart();

 /**
 * Comment removed (contained Chinese characters)
 */
 void onScanStop();

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 */
 @Deprecated
 default void onScanResult(Device device) {
 }

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 void onScanResult(Device device, boolean isConnectedBySys);

 /**
 * Comment removed (contained Chinese characters)
 *
 * @param errorCode {@link #ERROR_LACK_LOCATION_PERMISSION}, {@link #ERROR_LOCATION_SERVICE_CLOSED}, {@link #ERROR_LACK_BLUETOOTH_PERMISSION}
 */
 void onScanError(int errorCode, String errorMsg);
}
