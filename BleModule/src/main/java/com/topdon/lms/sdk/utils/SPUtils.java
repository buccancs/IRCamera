package com.topdon.lms.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stub implementation for LMS SDK compatibility
 */
public class SPUtils {
    private SharedPreferences sharedPreferences;
    
    private SPUtils(Context context) {
        sharedPreferences = context.getSharedPreferences("lms_prefs", Context.MODE_PRIVATE);
    }
    
    public static SPUtils getInstance(Context context) {
        return new SPUtils(context);
    }
    
    public Object get(String key, Object defaultValue) {
        if (defaultValue instanceof String) {
            return sharedPreferences.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultValue);
        }
        return defaultValue;
    }
    
    public void put(String key, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        editor.apply();
    }
}