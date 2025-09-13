package com.topdon.commons.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @ClassName FileSizeUtil
 * @Email 616862466@qq.com
* @Author
 * @Date 2022/12/14 18:40
 */

public class FileSizeUtil {
 public static final int SIZETYPE_B = 1;//UnitBdoubleValue
 public static final int SIZETYPE_KB = 2;//UnitKBdoubleValue
 public static final int SIZETYPE_MB = 3;//UnitMBdoubleValue
 public static final int SIZETYPE_GB = 4;//UnitGBdoubleValue

 public static double getFileOrFilesSize(String filePath, int sizeType) {
 File file = new File(filePath);
 long blockSize = 0;
 try {
 if (file.isDirectory()) {
 blockSize = getFileSizes(file);
 } else {
 blockSize = getFileSize(file);
 }
 } catch (Exception e) {
 e.printStackTrace();
 Log.e("bcf", "getFileOrFilesSize-1-Failed!");
 }
 return FormetFileSize(blockSize, sizeType);
 }

 /**
 *
 * @return String
 */
 public static String getUnit(int sizeType) {
 String memoryUnit;
 if (sizeType == SIZETYPE_B) {
 memoryUnit = "B";
 } else if (sizeType == SIZETYPE_KB) {
 memoryUnit = "KB";
 } else if (sizeType == SIZETYPE_MB) {
 memoryUnit = "MB";
 } else {
 memoryUnit = "GB";
 }
 return memoryUnit;
 }

 /**
 *
 */
 public static long getFilesSize(String filePath) {
 File file = new File(filePath);
 long blockSize = 0;
 try {
 if (file.isDirectory()) {
 blockSize = getFileSizes(file);
 } else {
 blockSize = getFileSize(file);
 }
 } catch (Exception e) {
 e.printStackTrace();
 System.out.println("bcf--getFilesSize-2-Failed!");
 }
 return blockSize;
 }

 /**
 *
 */
 public static String getAutoFileOrFilesSize(String filePath, int sizeType) {
 File file = new File(filePath);
 long blockSize = 0;
 try {
 if (file.isDirectory()) {
 blockSize = getFileSizes(file);
 } else {
 blockSize = getFileSize(file);
 }
 } catch (Exception e) {
 e.printStackTrace();
 Log.e("bcf", "getAutoFileOrFilesSize-3-Failed!");
 }
 return FormetFileSize(blockSize, sizeType) + getUnit(sizeType);
 }

 /**
 *
 */
 public static String getAutoFileOrFilesSize(String filePath) {
 File file = new File(filePath);
 long blockSize = 0;
 try {
 if (file.isDirectory()) {
 blockSize = getFileSizes(file);
 } else {
 blockSize = getFileSize(file);
 }
 } catch (Exception e) {
 e.printStackTrace();
 Log.e("bcf", "getAutoFileOrFilesSize-4-Failed!");
 }
 return FormetFileSize(blockSize);
 }

 /**
 *
 * @return
 * @throws Exception
 */
 private static long getFileSize(File file) throws Exception {
 FileChannel fc = null;
 try {
 if (file.exists() && file.isFile()) {
 FileInputStream fis = new FileInputStream(file);
 fc = fis.getChannel();
 if (fc.isOpen()) {
 return fc.size();
 }
 }
 } catch (Exception e) {
 System.out.println("bcf--getFilesSize-5-Failed!");
 e.printStackTrace();
 } finally {
 if (fc != null) {
 fc.close();
 }
 }
 return 0;
 }

 /**
 *
 * @param f
 * @return
 * @throws Exception
 */
 private static long getFileSizes(File f) throws Exception {
 long size = 0;
 File flist[] = f.listFiles();
 for (int i = 0; i < flist.length; i++) {
 if (flist[i].isDirectory()) {
 size = size + getFileSizes(flist[i]);
 } else {
 size = size + getFileSize(flist[i]);
 }
 }
 return size;
 }

 /**
 *
 * @param fileS
 * @return
 */
 public static String FormetFileSize(long fileS) {
 DecimalFormat df = new DecimalFormat("#.00");
 String fileSizeString = "";
 String wrongSize = "0B";
 if (fileS == 0) {
 return wrongSize;
 }
 if (fileS < 1024) {
 fileSizeString = df.format((double) fileS) + "B";
 } else if (fileS < 1048576) {
 fileSizeString = df.format((double) fileS / 1024) + "KB";
 } else if (fileS < 1073741824) {
 fileSizeString = df.format((double) fileS / 1048576) + "MB";
 } else {
 fileSizeString = df.format((double) fileS / 1073741824) + "GB";
 }
 return fileSizeString;
 }

 /**
 *
 * @param fileS
 * @param sizeType
 * @return
 */
 public static double FormetFileSize(long fileS, int sizeType) {
 Locale enlocale = new Locale("en", "US");
 DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(enlocale);
 df.applyPattern("#.00");
 double fileSizeLong = 0;
 switch (sizeType) {
 case SIZETYPE_B:
 fileSizeLong = Double.parseDouble(df.format((double) fileS));
 break;
 case SIZETYPE_KB:
 fileSizeLong = Double.parseDouble(df.format((double) fileS / 1024));
 break;
 case SIZETYPE_MB:
 fileSizeLong = Double.parseDouble(df.format((double) fileS / 1048576));
 break;
 case SIZETYPE_GB:
 fileSizeLong = Double.parseDouble(df.format((double) fileS / 1073741824));
 break;
 default:
 break;
 }
 return fileSizeLong;
 }

 /**
 *
 * @return long
 */
 public static long getFileSizeByWriteLog(String filename) {
 try {
 File file = new File(filename);
 if (!file.exists() || !file.isFile()) {
 System.out.println("bcf--getFileSize");
 return -1;
 }
 return file.length();
 } catch (Exception e) {
 e.printStackTrace();
 System.out.println("bcf--getFileSize--getFilesSize-5-Failed!");
 }
 return 0;
 }
}
