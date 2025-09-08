package android.yt.jni;

import android.util.Log;

 * @Description:    USB,usb3803_hubso
 * @Author:         brilliantzhao
 * @CreateDate:     2022.3.21 9:27
 * @UpdateUser:
 * @UpdateDate:     2022.3.21 9:27
 * @UpdateRemark:
public class Usbjni {

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
     * @param isPowerOn
     * @return
    public static int setUSB3803Mode(boolean isPowerOn) {
        if (isPowerOn) {
            return usb3803_mode_setting(1);
        } else {
            return usb3803_mode_setting(0);
        }
    }

     * @param i
     * @return
    public static int readUSB3803Parameter(int i) {
        return usb3803_read_parameter(i);
    }


    static native int usb3803_mode_setting(int i);

    static native int usb3803_read_parameter(int i);
}
