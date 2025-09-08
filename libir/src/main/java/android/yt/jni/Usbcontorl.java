package android.yt.jni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

    /**
     * Usbjni[Chinese text]framework[Chinese text]jni[Chinese text], [Chinese text], [Chinese text]implementso[Chinese text]. 
     */
    public class Usbcontorl extends Usbjni {

    public static boolean isload = false;

    static {
        File file = new File("/proc/self/maps");
        if (file.exists() && file.isFile()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                // [Chinese text], [Chinese text]null[Chinese text]
                while ((tempString = reader.readLine()) != null) {
                    // [Chinese text]
                    if (tempString.contains("libusb3803_hub.so")) {
                        isload = true;
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
                    } catch (IOException e1) {
                    }
                }
            }
        }
    }
}
