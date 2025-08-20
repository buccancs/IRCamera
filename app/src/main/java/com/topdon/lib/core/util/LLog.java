package com.topdon.lib.core.util;

import android.util.Log;

/**
 * Simple logging utility
 */
public class LLog {
    
    private LLog() {
        // Private constructor to prevent instantiation
    }
    
    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }
    
    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }
    
    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }
    
    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }
    
    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }
}