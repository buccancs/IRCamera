package com.topdon.ble.util;

/**
 * date: 2019/8/2 23:56
* author: bichuanfeng
 */
public interface Logger {
 /**
 * Comment removed (contained Chinese characters)
 */
 int TYPE_GENERAL = 0;
 /**
 * Comment removed (contained Chinese characters)
 */
 int TYPE_SCAN_STATE = 1;
 /**
 * ConnectionStatus
 */
 int TYPE_CONNECTION_STATE = 2;
 /**
 * Comment removed (contained Chinese characters)
 */
 int TYPE_CHARACTERISTIC_READ = 3;
 /**
 * Comment removed (contained Chinese characters)
 */
 int TYPE_CHARACTERISTIC_CHANGED = 4;
 /**
 * Comment removed (contained Chinese characters)
 */
 int TYPE_READ_REMOTE_RSSI = 5;
 /**
 * Comment removed (contained Chinese characters)
 */
 int TYPE_MTU_CHANGED = 6;
 /**
 * Comment removed (contained Chinese characters)
 */
 int TYPE_REQUEST_FAILED = 7;
 int TYPE_DESCRIPTOR_READ = 8;
 int TYPE_NOTIFICATION_CHANGED = 9;
 int TYPE_INDICATION_CHANGED = 10;
 int TYPE_CHARACTERISTIC_WRITE = 11;
 int TYPE_PHY_CHANGE = 12;

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 void log(int priority, int type, String msg);

 /**
 * Comment removed (contained Chinese characters)
 *
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 */
 void log(int priority, int type, String msg, Throwable th);
 
 /**
 * Comment removed (contained Chinese characters)
 */
 void setEnabled(boolean isEnabled);

 /**
 * Comment removed (contained Chinese characters)
 */
 boolean isEnabled();
}
