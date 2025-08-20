package com.topdon.tc004.util;

import android.content.Context;
import android.preference.PreferenceManager;

public class SPUtil {

    private static final String KEY_AUTO_RECORD = "auto_record";

    public static boolean getAutoRecord(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_AUTO_RECORD, false);
    }

    public static void setAutoRecord(Context context, boolean isChecked) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_AUTO_RECORD, isChecked)
                .apply();
    }

    private static final String KEY_AUTO_AUDIO = "auto_audio";

    public static boolean getAutoAudio(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_AUTO_AUDIO, false);
    }

    public static void setAutoAudio(Context context, boolean isChecked) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_AUTO_AUDIO, isChecked)
                .apply();
    }

}
