package android.yt.jni

import android.util.Log

open class Usbjni {

    companion object {
        private const val TAG = "Usbjni"

        init {
            try {
                System.loadLibrary("usb3803_hub")
            } catch (e: UnsatisfiedLinkError) {
                e.printStackTrace()
                Log.e(TAG, "Couldn't load lib: - ${e.message}")
            }
        }

        @JvmStatic
        fun setUSB3803Mode(isPowerOn: Boolean): Int {
            return if (isPowerOn) {
                usb3803_mode_setting(1)
            } else {
                usb3803_mode_setting(0)
            }
        }

        @JvmStatic
        fun readUSB3803Parameter(i: Int): Int {
            return usb3803_read_parameter(i)
        }

        @JvmStatic
        external fun usb3803_mode_setting(i: Int): Int

        @JvmStatic
        external fun usb3803_read_parameter(i: Int): Int
    }
}