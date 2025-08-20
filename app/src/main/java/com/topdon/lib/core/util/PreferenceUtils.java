package com.topdon.lib.core.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Simple preference utilities
 */
public class PreferenceUtils {
    
    private PreferenceUtils() {
        // Private constructor to prevent instantiation
    }
    
    public static void putString(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }
    
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }
    
    public static void putInt(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putInt(key, value).apply();
    }
    
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }
    
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }
    
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defaultValue);
    }
}