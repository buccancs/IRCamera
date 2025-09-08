package com.topdon.ble.util;

/**
 * date: 2019/8/2 23:56
 * author: bichuanfeng
 */
public interface Logger {
    /**
     * [Chinese text]
     */
    int TYPE_GENERAL = 0;
    /**
     * [Chinese text]
     */
    int TYPE_SCAN_STATE = 1;
    /**
     * [Chinese text]
     */
    int TYPE_CONNECTION_STATE = 2;
    /**
     * [Chinese text]
     */
    int TYPE_CHARACTERISTIC_READ = 3;
    /**
     * [Chinese text]
     */
    int TYPE_CHARACTERISTIC_CHANGED = 4;
    /**
     * [Chinese text]
     */
    int TYPE_READ_REMOTE_RSSI = 5;
    /**
     * [Chinese text]
     */
    int TYPE_MTU_CHANGED = 6;
    /**
     * [Chinese text]
     */
    int TYPE_REQUEST_FAILED = 7;
    int TYPE_DESCRIPTOR_READ = 8;
    int TYPE_NOTIFICATION_CHANGED = 9;
    int TYPE_INDICATION_CHANGED = 10;
    int TYPE_CHARACTERISTIC_WRITE = 11;
    int TYPE_PHY_CHANGE = 12;

    /**
     * [Chinese text]
     *
     * @param priority [Chinese text]. {@link android.util.Log#DEBUG}[Chinese text]
     * @param type     [Chinese text]. {@link #TYPE_CONNECTION_STATE}[Chinese text]
     * @param msg      [Chinese text]
     */
    void log(int priority, int type, String msg);

    /**
     * [Chinese text]
     *
     * @param priority [Chinese text]. {@link android.util.Log#DEBUG}[Chinese text]
     * @param type     [Chinese text]. {@link #TYPE_CONNECTION_STATE}[Chinese text]
     * @param msg      [Chinese text]
     * @param th       [Chinese text]
     */
    void log(int priority, int type, String msg, Throwable th);
    
    /**
     * [Chinese text]
     */
    void setEnabled(boolean isEnabled);

    /**
     * [Chinese text]
     */
    boolean isEnabled();
}
