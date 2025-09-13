package com.topdon.ble.util;

/**
 * date: 2019/8/2 23:56
 * author: bichuanfeng
 */
public interface Logger {
    /**
     * 一般的
     */
    int TYPE_GENERAL = 0;
    /**
     * Searchstate
     */
    int TYPE_SCAN_STATE = 1;
    /**
     * connectionstate
     */
    int TYPE_CONNECTION_STATE = 2;
    /**
     * 读特征
     */
    int TYPE_CHARACTERISTIC_READ = 3;
    /**
     * 特征值变化
     */
    int TYPE_CHARACTERISTIC_CHANGED = 4;
    /**
     * 信号强度
     */
    int TYPE_READ_REMOTE_RSSI = 5;
    /**
     * 最大传输单元变化
     */
    int TYPE_MTU_CHANGED = 6;
    /**
     * 请求failed
     */
    int TYPE_REQUEST_FAILED = 7;
    int TYPE_DESCRIPTOR_READ = 8;
    int TYPE_NOTIFICATION_CHANGED = 9;
    int TYPE_INDICATION_CHANGED = 10;
    int TYPE_CHARACTERISTIC_WRITE = 11;
    int TYPE_PHY_CHANGE = 12;

    /**
     * 打印Log
     *
     * @param priority Log级别。{@link android.util.Log#DEBUG}等
     * @param type     Logtype。{@link #TYPE_CONNECTION_STATE}等
     * @param msg      Log内容
     */
    void log(int priority, int type, String msg);

    /**
     * 打印Log
     *
     * @param priority Log级别。{@link android.util.Log#DEBUG}等
     * @param type     Logtype。{@link #TYPE_CONNECTION_STATE}等
     * @param msg      Log内容
     * @param th       exception
     */
    void log(int priority, int type, String msg, Throwable th);
    
    /**
     * Log输出控制
     */
    void setEnabled(boolean isEnabled);

    /**
     * Log输出是否使能
     */
    boolean isEnabled();
}
