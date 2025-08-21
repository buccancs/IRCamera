package android.yt.jni

open class Usbjni {

    companion object {
        init {
            System.loadLibrary("usb3803_hub")
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