package com.topdon.ble.util;

import android.util.Log;


/**
 * date: 2021/8/12 16:24
 * author: bichuanfeng
 */
public class DefaultLogger implements Logger {
    /**
     * Private method description.
     */
    private final String tag;
    private boolean isEnabled;

    /**
     * Method description.
     */
    public DefaultLogger(String tag) {
        this.tag = tag;
    }

    @Override
    /**
     * Method description.
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    /**
     * Method description.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    /**
     * Method description.
     */
    public void log(int priority, int type, String msg) {
        if (isEnabled) {
            Log.println(priority, tag, msg);
        }
    }

    @Override
    /**
     * Method description.
     */
    public void log(int priority, int type, String msg, Throwable th) {
        if (isEnabled) {
            if (msg != null) {
                log(priority, type, msg + "\n" + Log.getStackTraceString(th));
            } else {
                log(priority, type, Log.getStackTraceString(th));
            }
        }        
    }
}
