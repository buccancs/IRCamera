package com.topdon.commons.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import java.lang.ref.WeakReference;

/**
 * Consolidated preferences utility class combining functionality from multiple preference utilities
 * Replaces com.infisense.usbir.utils.SharedPreferencesUtil and com.topdon.tc004.util.SPUtil
 * Enhances com.topdon.commons.util.PreUtil
 */
public class PreferenceUtils {
    
    // File names for different preference files
    private static final String USB_IR_FILE = "usb_ir";
    
    // Keys for TC004 specific preferences
    private static final String KEY_AUTO_RECORD = "auto_record";
    private static final String KEY_AUTO_AUDIO = "auto_audio";
    
    private WeakReference<Context> mContext;
    private SharedPreferences preferences;
    private String fileName;
    private static PreferenceUtils instance;

    private PreferenceUtils(Context context) {
        this(context, "default_prefs");
    }

    private PreferenceUtils(Context context, String fileName) {
        this.mContext = new WeakReference<>(context);
        this.fileName = fileName;
        this.preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * Get instance with default preferences file
     * @param context application context
     * @return PreferenceUtils instance
     */
    public static PreferenceUtils getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceUtils(context);
        }
        return instance;
    }

    /**
     * Get instance with specific preferences file
     * @param context application context
     * @param fileName preferences file name
     * @return PreferenceUtils instance
     */
    public static PreferenceUtils getInstance(Context context, String fileName) {
        return new PreferenceUtils(context, fileName);
    }

    // Generic save/get methods (from SharedPreferencesUtil)
    
    /**
     * Save data to preferences
     * @param context application context
     * @param key preference key
     * @param data data to save
     */
    public static void saveData(Context context, String key, Object data) {
        saveData(context, USB_IR_FILE, key, data);
    }

    /**
     * Save data to specific preferences file
     * @param context application context
     * @param fileName preferences file name
     * @param key preference key
     * @param data data to save
     */
    public static void saveData(Context context, String fileName, String key, Object data) {
        String type = data.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) data);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) data);
        } else if ("String".equals(type)) {
            editor.putString(key, (String) data);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) data);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) data);
        }
        editor.apply();
    }

    /**
     * Get data from preferences
     * @param context application context
     * @param key preference key
     * @param defValue default value
     * @return stored value or default
     */
    public static Object getData(Context context, String key, Object defValue) {
        return getData(context, USB_IR_FILE, key, defValue);
    }

    /**
     * Get data from specific preferences file
     * @param context application context
     * @param fileName preferences file name
     * @param key preference key
     * @param defValue default value
     * @return stored value or default
     */
    public static Object getData(Context context, String fileName, String key, Object defValue) {
        String type = defValue.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

        if ("Integer".equals(type)) {
            return sharedPreferences.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return sharedPreferences.getBoolean(key, (Boolean) defValue);
        } else if ("String".equals(type)) {
            return sharedPreferences.getString(key, (String) defValue);
        } else if ("Float".equals(type)) {
            return sharedPreferences.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return sharedPreferences.getLong(key, (Long) defValue);
        }
        return null;
    }

    /**
     * Save byte array data as Base64
     * @param context application context
     * @param key preference key
     * @param data byte array to save
     */
    public static void saveByteData(Context context, String key, byte[] data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USB_IR_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String imageString = new String(Base64.encode(data, Base64.DEFAULT));
        editor.putString(key, imageString);
        editor.apply();
    }

    /**
     * Get byte array data from Base64
     * @param context application context
     * @param key preference key
     * @return decoded byte array
     */
    public static byte[] getByteData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USB_IR_FILE, Context.MODE_PRIVATE);
        String string = sharedPreferences.getString(key, "");
        return Base64.decode(string.getBytes(), Base64.DEFAULT);
    }

    // TC004 specific methods (from SPUtil)
    
    /**
     * Get auto record setting
     * @param context application context
     * @return auto record enabled status
     */
    public static boolean getAutoRecord(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_AUTO_RECORD, false);
    }

    /**
     * Set auto record setting
     * @param context application context
     * @param isChecked auto record enabled
     */
    public static void setAutoRecord(Context context, boolean isChecked) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_AUTO_RECORD, isChecked)
                .apply();
    }

    /**
     * Get auto audio setting
     * @param context application context
     * @return auto audio enabled status
     */
    public static boolean getAutoAudio(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_AUTO_AUDIO, false);
    }

    /**
     * Set auto audio setting
     * @param context application context
     * @param isChecked auto audio enabled
     */
    public static void setAutoAudio(Context context, boolean isChecked) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_AUTO_AUDIO, isChecked)
                .apply();
    }

    // Instance methods for enhanced functionality (from PreUtil)
    
    /**
     * Put string value
     * @param key preference key
     * @param value string value
     */
    public void put(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        preferences.edit().putString(key, value).apply();
    }

    /**
     * Put boolean value
     * @param key preference key
     * @param value boolean value
     */
    public void put(String key, boolean value) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        preferences.edit().putBoolean(key, value).apply();
    }

    /**
     * Put int value
     * @param key preference key
     * @param value int value
     */
    public void put(String key, int value) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        preferences.edit().putInt(key, value).apply();
    }

    /**
     * Put float value
     * @param key preference key
     * @param value float value
     */
    public void put(String key, float value) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        preferences.edit().putFloat(key, value).apply();
    }

    /**
     * Put long value
     * @param key preference key
     * @param value long value
     */
    public void put(String key, long value) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        preferences.edit().putLong(key, value).apply();
    }

    /**
     * Get string value
     * @param key preference key
     * @return string value or empty string
     */
    public String get(String key) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        return preferences.getString(key, "");
    }

    /**
     * Get string value with default
     * @param key preference key
     * @param defValue default value
     * @return string value or default
     */
    public String get(String key, String defValue) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        return preferences.getString(key, defValue);
    }

    /**
     * Get boolean value with default
     * @param key preference key
     * @param defValue default value
     * @return boolean value or default
     */
    public boolean get(String key, boolean defValue) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        return preferences.getBoolean(key, defValue);
    }

    /**
     * Get int value with default
     * @param key preference key
     * @param defValue default value
     * @return int value or default
     */
    public int get(String key, int defValue) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        return preferences.getInt(key, defValue);
    }

    /**
     * Get float value with default
     * @param key preference key
     * @param defValue default value
     * @return float value or default
     */
    public float get(String key, float defValue) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        return preferences.getFloat(key, defValue);
    }

    /**
     * Get long value with default
     * @param key preference key
     * @param defValue default value
     * @return long value or default
     */
    public long get(String key, long defValue) {
        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
        }
        return preferences.getLong(key, defValue);
    }

    /**
     * Clear all preferences
     */
    public void clearAll() {
        try {
            if (mContext.get() != null) {
                String filePath = mContext.get().getApplicationInfo().dataDir + "/shared_prefs/" + fileName + ".xml";
                java.io.File file = new java.io.File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}