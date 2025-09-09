package com.infisense.usbir.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Modern SharedPreferences utility with enhanced security and performance patterns
 * 
 * Enhanced with:
 * - Non-blocking apply() operations for better performance
 * - Proper null safety annotations and error handling
 * - Type-safe data operations with validation
 * - Improved documentation and security practices
 * 
 * Created by fengjibo on 2023/4/12.
 * Enhanced for Phase 11 systematic code quality improvements.
 */
public class SharedPreferencesUtil {
    private static final String TAG = "SharedPreferencesUtil";
    private static final String FILE_NAME = "usb_ir";

    /**
     * Modern non-blocking data save operation with enhanced type safety and error handling
     *
     * @param context Application context for SharedPreferences access
     * @param key Preference key for data storage
     * @param data Data object to be saved (Integer, Boolean, String, Float, Long)
     * @return true if save operation was initiated successfully, false otherwise
     */
    public static boolean saveData(@NonNull Context context, @NonNull String key, @NonNull Object data) {
        if (context == null || key == null || data == null) {
            Log.w(TAG, "Invalid parameters provided to saveData - context, key, or data is null");
            return false;
        }
        
        try {
            String type = data.getClass().getSimpleName();
            SharedPreferences sharedPreferences = context
                    .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            switch (type) {
                case "Integer":
                    editor.putInt(key, (Integer) data);
                    break;
                case "Boolean":
                    editor.putBoolean(key, (Boolean) data);
                    break;
                case "String":
                    editor.putString(key, (String) data);
                    break;
                case "Float":
                    editor.putFloat(key, (Float) data);
                    break;
                case "Long":
                    editor.putLong(key, (Long) data);
                    break;
                default:
                    Log.w(TAG, "Unsupported data type: " + type);
                    return false;
            }
            
            // Use apply() for non-blocking asynchronous operation (modern best practice)
            editor.apply();
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving data for key: " + key, e);
            return false;
        }
    }

    /**
     * Enhanced data retrieval with type safety and proper error handling
     *
     * @param context Application context for SharedPreferences access  
     * @param key Preference key for data retrieval
     * @param defValue Default value with type information for safe retrieval
     * @return Retrieved data object or default value if key doesn't exist or error occurs
     */
    @Nullable
    public static Object getData(@NonNull Context context, @NonNull String key, @NonNull Object defValue) {
        if (context == null || key == null || defValue == null) {
            Log.w(TAG, "Invalid parameters provided to getData - context, key, or defValue is null");
            return defValue;
        }
        
        try {
            String type = defValue.getClass().getSimpleName();
            SharedPreferences sharedPreferences = context.getSharedPreferences
                    (FILE_NAME, Context.MODE_PRIVATE);

            switch (type) {
                case "Integer":
                    return sharedPreferences.getInt(key, (Integer) defValue);
                case "Boolean":
                    return sharedPreferences.getBoolean(key, (Boolean) defValue);
                case "String":
                    return sharedPreferences.getString(key, (String) defValue);
                case "Float":
                    return sharedPreferences.getFloat(key, (Float) defValue);
                case "Long":
                    return sharedPreferences.getLong(key, (Long) defValue);
                default:
                    Log.w(TAG, "Unsupported data type for retrieval: " + type);
                    return defValue;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving data for key: " + key, e);
            return defValue;
        }
    }

    /**
     * Enhanced byte array storage with security validation and error handling
     * 
     * Uses Base64 encoding for safe binary data storage in SharedPreferences.
     * Implements modern non-blocking apply() for better performance.
     *
     * @param context Application context for SharedPreferences access
     * @param key Preference key for byte data storage
     * @param data Byte array to be encoded and stored
     * @return true if save operation was initiated successfully, false otherwise
     */
    public static boolean saveByteData(@NonNull Context context, @NonNull String key, @NonNull byte[] data) {
        if (context == null || key == null || data == null) {
            Log.w(TAG, "Invalid parameters provided to saveByteData - context, key, or data is null");
            return false;
        }
        
        try {
            SharedPreferences sharedPreferences = context
                    .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String encodedData = Base64.encodeToString(data, Base64.DEFAULT);
            editor.putString(key, encodedData);

            // Use apply() for non-blocking asynchronous operation
            editor.apply();
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving byte data for key: " + key, e);
            return false;
        }
    }

    /**
     * Enhanced byte array retrieval with proper validation and error handling
     * 
     * Safely decodes Base64-encoded binary data from SharedPreferences with
     * comprehensive error handling and null safety.
     *
     * @param context Application context for SharedPreferences access
     * @param key Preference key for byte data retrieval
     * @return Decoded byte array or empty array if key doesn't exist or error occurs
     */
    @NonNull
    public static byte[] getByteData(@NonNull Context context, @NonNull String key) {
        if (context == null || key == null) {
            Log.w(TAG, "Invalid parameters provided to getByteData - context or key is null");
            return new byte[0];
        }
        
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences
                    (FILE_NAME, Context.MODE_PRIVATE);

            String encodedString = sharedPreferences.getString(key, "");
            if (encodedString == null || encodedString.isEmpty()) {
                Log.d(TAG, "No data found for key: " + key);
                return new byte[0];
            }
            
            return Base64.decode(encodedString, Base64.DEFAULT);
            
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving byte data for key: " + key, e);
            return new byte[0];
        }
    }
}
