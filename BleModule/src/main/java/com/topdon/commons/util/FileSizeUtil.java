package com.topdon.commons.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * FileSizeUtil class.
 * 
 * Provides functionality for filesizeutil operations.
 */
public class FileSizeUtil {
    public static final int SIZETYPE_B = 1;// [Chinese text]B[Chinese text]double[Chinese text]
    public static final int SIZETYPE_KB = 2;// [Chinese text]KB[Chinese text]double[Chinese text]
    public static final int SIZETYPE_MB = 3;// [Chinese text]MB[Chinese text]double[Chinese text]
    public static final int SIZETYPE_GB = 4;// [Chinese text]GB[Chinese text]double[Chinese text]

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
            Log.e("bcf[Chinese text]", "getFileOrFilesSize-1-[Chinese text]!");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * [Chinese text]
     *
     * @param sizeType [Chinese text]
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
     * [Chinese text]
     *
     * @param filePath [Chinese text]
     * @return [Chinese text]B, KB, MB, GB[Chinese text]
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
            System.out.println("bcf[Chinese text]--getFilesSize-2-[Chinese text]!");
//            Log.e("[Chinese text]", "getFilesSize-2-[Chinese text]!");
        }
        return blockSize;
    }

    /**
     * [Chinese text]
     *
     * @param filePath [Chinese text]
     * @return [Chinese text]B, KB, MB, GB[Chinese text]
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
            Log.e("bcf[Chinese text]", "getAutoFileOrFilesSize-3-[Chinese text]!");
        }
        return FormetFileSize(blockSize, sizeType) + getUnit(sizeType);
    }

    /**
     * [Chinese text]
     *
     * @param filePath [Chinese text]
     * @return [Chinese text]B, KB, MB, GB[Chinese text]
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
            Log.e("bcf[Chinese text]", "getAutoFileOrFilesSize-4-[Chinese text]!");
        }
        return FormetFileSize(blockSize);
    }

    /**
     * [Chinese text]
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
            System.out.println("bcf[Chinese text]--getFilesSize-5-[Chinese text]!");
//            Log.e("[Chinese text]", "getFileSize-5-[Chinese text]!");
            e.printStackTrace();
        } finally {
            if (fc != null) {
                fc.close();
            }
        }
        return 0;
    }

    /**
     * [Chinese text]
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
     * [Chinese text]
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
     * [Chinese text],[Chinese text]
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
     * [Chinese text]
     * [Chinese text]
     *
     * @param filename [Chinese text]
     * @return long
     */
    public static long getFileSizeByWriteLog(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists() || !file.isFile()) {
                System.out.println("bcf--getFileSize[Chinese text]");
                return -1;
            }
            return file.length();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("bcf--getFileSize[Chinese text]--getFilesSize-5-[Chinese text]!");
        }
        return 0;
    }
}
