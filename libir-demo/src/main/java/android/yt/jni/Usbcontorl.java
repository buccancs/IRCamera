package android.yt.jni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class.
 * 
 * @author brilliantzhao
 * @since 2022.9.8 10:25
 */
public class Usbcontorl extends Usbjni {

    /**
     * Method description.
     */
    public static boolean isLoad = false;

    static {
        File file = new File("/proc/self/maps");
        if (file.exists() && file.isFile()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                // null
                while ((tempString = reader.readLine()) != null) {
                    if (tempString.contains("libusb3803_hub.so")) {
                        isLoad = true;
                        break;
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}