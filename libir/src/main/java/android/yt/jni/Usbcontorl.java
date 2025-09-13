package android.yt.jni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Comment removed (contained Chinese characters)
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
 // Comment removed (contained Chinese characters)
 while ((tempString = reader.readLine()) != null) {
 // Comment removed (contained Chinese characters)
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
