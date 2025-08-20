package android.yt.jni

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class Usbcontorl : Usbjni() {

    companion object {
        @JvmStatic
        var isLoad = false
            private set

        init {
            val file = File("/proc/self/maps")
            if (file.exists() && file.isFile) {
                try {
                    BufferedReader(FileReader(file)).use { reader ->
                        var tempString: String?
                        while (reader.readLine().also { tempString = it } != null) {
                            if (tempString?.contains("libusb3803_hub.so") == true) {
                                isLoad = true
                                break
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}