package android.yt.jni;

import android.util.Log;

/**
 * Utility class.
 * 
 * @author brilliantzhao
 * @since 2022.9.8 10:25
 */
public class Usbjni {

    /**
     * Private method description.
     */
    private static final String TAG = "Usbjni";

    static {
        try {
            System.loadLibrary("usb3803_hub");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            Log.e(TAG, "Couldn't load lib: - " + e.getMessage());
        }
    }

     * usb3803
     * sensor
    /**
     * Method description.
     *
     * @param parameter parameter description
     * @return return value
     */
    /**
     * Method description.
     */
    public static int setUSB3803Mode(boolean isPowerOn) {
        if (isPowerOn) {
            return usb3803_mode_setting(1);
        } else {
            return usb3803_mode_setting(0);
        }
    }

    /**
     * Method description.
     *
     * @param parameter parameter description
     * @return return value
     */
    /**
     * Method description.
     */
    public static int readUSB3803Parameter(int i) {
        return usb3803_read_parameter(i);
    }


    static native int usb3803_mode_setting(int i);

    static native int usb3803_read_parameter(int i);
}
