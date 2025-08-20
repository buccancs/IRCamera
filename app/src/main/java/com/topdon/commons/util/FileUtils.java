package com.topdon.commons.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.energy.iruvc.utils.CommonParams;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Consolidated file utility class combining functionality from multiple FileUtil classes
 * Replaces com.infisense.usbir.utils.FileUtil and com.topdon.tc004.util.FileUtil
 */
public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String DATA_SAVE_DIR = "InfiRay";
    private static final String EASY_PLAYER_PATH = Environment.getExternalStorageDirectory() + "/EasyPlayerRTSP";
    private static int sBufferSize = 524288;

    private FileUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get disk cache directory
     * @param context application context
     * @return cache directory path
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = context.getCacheDir().getPath();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            }
        }
        return cachePath;
    }

    /**
     * Copy assets file to SD card
     * @param context application context
     * @param srcFileName source file name in assets
     * @param strOutFileName output file path
     * @throws IOException if copy fails
     */
    public static void copyAssetsDataToSD(Context context, String srcFileName, String strOutFileName) throws IOException {
        File file = new File(strOutFileName);
        if (file.exists()) {
            file.delete();
        }
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = context.getAssets().open(srcFileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    /**
     * Copy large assets file to SD card
     * @param context application context
     * @param srcFileName source file name in assets
     * @param strOutFileName output file path
     */
    public static void copyAssetsBigDataToSD(Context context, String srcFileName, String strOutFileName) {
        try {
            File file = new File(strOutFileName);
            Log.i(TAG, "file.exists->getAbsolutePath = " + file.getAbsolutePath());
            if (file.exists()) {
                file.delete();
            }
            
            if (!file.createNewFile()) {
                Log.e(TAG, "创建文件 " + srcFileName + " 失败");
                return;
            }

            InputStream myInput;
            OutputStream myOutput = new FileOutputStream(strOutFileName);
            myInput = context.getAssets().open(srcFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save byte array to file
     * @param mContext application context
     * @param bytes byte array to save
     * @param fileTitle file title
     */
    public static void saveByteFile(Context mContext, byte[] bytes, String fileTitle) {
        try {
            String fileSaveDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            File path = new File(fileSaveDir);
            if (!path.exists() && path.isDirectory()) {
                path.mkdirs();
            }
            String fileName = fileTitle + new SimpleDateFormat("_HHmmss_yyMMdd").
                    format(new Date(System.currentTimeMillis())) + ".bin";
            File file = new File(fileSaveDir, fileName);
            Log.i(TAG, "fileSaveDir=" + fileSaveDir + " fileName=" + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save short array to file
     * @param bytes short array to save
     * @param fileTitle file title
     */
    public static void saveShortFile(short[] bytes, String fileTitle) {
        try {
            File path = new File("/sdcard");
            if (!path.exists() && path.isDirectory()) {
                path.mkdirs();
            }
            File file = new File("/sdcard/", fileTitle + new SimpleDateFormat("_HHmmss_yyMMdd").
                    format(new Date(System.currentTimeMillis())) + ".bin");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(toByteArray(bytes));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save short array to specific directory
     * @param fileDir target directory
     * @param bytes short array to save
     * @param fileTitle file title
     */
    public static void saveShortFile(String fileDir, short[] bytes, String fileTitle) {
        createOrExistsDir(fileDir);
        try {
            File file = new File(fileDir, fileTitle + ".bin");
            createOrExistsFile(file);
            Log.i(TAG, "getAbsolutePath = " + file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(toByteArray(bytes));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TC004 video file utilities
    
    /**
     * Get picture path for URL
     * @param url video URL
     * @return picture directory path
     */
    public static String getPicturePath(String url) {
        return EASY_PLAYER_PATH + "/" + urlDir(url) + "/picture";
    }

    /**
     * Get picture file for URL
     * @param url video URL
     * @return picture file
     */
    public static File getPictureName(String url) {
        File file = new File(getPicturePath(url));
        file.mkdirs();
        File res = new File(file, new SimpleDateFormat("yy_MM_dd HH_mm_ss").format(new Date()) + ".jpg");
        return res;
    }

    /**
     * Get movie path for URL
     * @param url video URL
     * @return movie directory path
     */
    public static String getMoviePath(String url) {
        return EASY_PLAYER_PATH + "/" + urlDir(url) + "/movie";
    }

    /**
     * Get movie file for URL
     * @param url video URL
     * @return movie file
     */
    public static File getMovieName(String url) {
        File file = new File(getMoviePath(url));
        file.mkdirs();
        File res = new File(file, new SimpleDateFormat("yy_MM_dd HH_mm_ss").format(new Date()) + ".mp4");
        return res;
    }

    /**
     * Get snapshot file for URL
     * @param url video URL
     * @return snapshot file
     */
    public static File getSnapFile(String url) {
        File file = new File(getPicturePath(url));
        file.mkdirs();
        File res = new File(file, "snap.jpg");
        return res;
    }

    /**
     * Convert URL to directory name
     * @param url video URL
     * @return sanitized directory name
     */
    private static String urlDir(String url) {
        url = url.replace("://", "");
        url = url.replace("/", "");
        url = url.replace(".", "");

        if (url.length() > 64) {
            url = url.substring(0, 63);
        }

        return url;
    }

    // Additional utility methods

    /**
     * Create file with parent directories
     * @param dirPath directory path
     * @param fileName file name
     * @return created file or null if failed
     */
    public static File createFile(String dirPath, String fileName) {
        try {
            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                if (!createFileDir(dirFile)) {
                    Log.e(TAG, "createFile dirFile.mkdirs fail");
                    return null;
                }
            } else if (!dirFile.isDirectory()) {
                boolean delete = dirFile.delete();
                if (delete) {
                    return createFile(dirPath, fileName);
                } else {
                    Log.e(TAG, "createFile dirFile !isDirectory and delete fail");
                    return null;
                }
            }
            File file = new File(dirPath, fileName);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Log.e(TAG, "createFile createNewFile fail");
                    return null;
                }
            }
            return file;
        } catch (Exception e) {
            Log.e(TAG, "createFile fail :" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create directory recursively
     * @param dirFile directory file
     * @return true if successful
     */
    public static boolean createFileDir(File dirFile) {
        if (dirFile == null) return true;
        if (dirFile.exists()) {
            return true;
        }
        File parentFile = dirFile.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            return createFileDir(parentFile) && createFileDir(dirFile);
        } else {
            boolean mkdirs = dirFile.mkdirs();
            boolean isSuccess = mkdirs || dirFile.exists();
            if (!isSuccess) {
                Log.e(TAG, "createFileDir fail " + dirFile);
            }
            return isSuccess;
        }
    }

    /**
     * Check if file exists
     * @param context application context
     * @param file file to check
     * @return true if exists
     */
    public static boolean isFileExists(Context context, final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExists(context, file.getAbsolutePath());
    }

    /**
     * Check if file exists by path
     * @param context application context
     * @param filePath file path
     * @return true if exists
     */
    public static boolean isFileExists(Context context, final String filePath) {
        File file = new File(filePath);
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        return isFileExistsApi29(context, filePath);
    }

    /**
     * Check file exists for API 29+
     * @param context application context
     * @param filePath file path
     * @return true if exists
     */
    private static boolean isFileExistsApi29(Context context, String filePath) {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                Uri uri = Uri.parse(filePath);
                ContentResolver cr = context.getContentResolver();
                AssetFileDescriptor afd = cr.openAssetFileDescriptor(uri, "r");
                if (afd == null) return false;
                try {
                    afd.close();
                } catch (IOException ignore) {
                }
            } catch (FileNotFoundException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Read file to byte array
     * @param context application context
     * @param file file to read
     * @return byte array or null if failed
     */
    public static byte[] readFile2BytesByStream(Context context, final File file) {
        if (!isFileExists(context, file)) {
            return null;
        }
        try {
            ByteArrayOutputStream os = null;
            InputStream is = new BufferedInputStream(new FileInputStream(file), sBufferSize);
            try {
                os = new ByteArrayOutputStream();
                byte[] b = new byte[sBufferSize];
                int len;
                while ((len = is.read(b, 0, sBufferSize)) != -1) {
                    os.write(b, 0, len);
                }
                return os.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get version name from context
     * @param context application context
     * @return version name
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * Get MD5 hash of string
     * @param string input string
     * @return MD5 hash
     */
    public static String getMD5Key(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Create directory if not exists
     * @param filePath directory path
     */
    public static void makeDirectory(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * Create or check if directory exists
     * @param fileDir directory path
     */
    private static void createOrExistsDir(String fileDir) {
        File file = new File(fileDir);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
    }

    /**
     * Create or check if file exists
     * @param file file to check/create
     */
    private static void createOrExistsFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Convert short array to byte array
     * @param src short array
     * @return byte array
     */
    private static byte[] toByteArray(short[] src) {
        int count = src.length;
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2] = (byte) ((src[i] >> 8) & 0xFF);
            dest[i * 2 + 1] = (byte) (src[i] & 0xFF);
        }
        return dest;
    }

    /**
     * Convert byte array to short array
     * @param src byte array
     * @return short array
     */
    public static short[] toShortArray(byte[] src) {
        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
            dest[i] = (short) ((src[i * 2] & 0xFF) << 8 | ((src[2 * i + 1] & 0xFF)));
        }
        return dest;
    }

    /**
     * Write byte array to file
     * @param bytes byte array
     * @param filePath file path
     * @param fileName file name
     * @return 0 if successful, -1 if failed
     */
    public static int writeTxtToFile(byte[] bytes, String filePath, String fileName) {
        int result = -1;
        FileChannel fc = null;
        File file = null;
        try {
            makeFile(filePath, fileName);
            file = new File(filePath + fileName);
            fc = new FileOutputStream(file, false).getChannel();
            if (fc == null) {
                Log.e(TAG, "fc is null.");
            }
            fc.position(fc.size());
            fc.write(ByteBuffer.wrap(bytes));
            result = 0;
        } catch (IOException e) {
            e.printStackTrace();
            result = -1;
        } finally {
            try {
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = -1;
            }
            return result;
        }
    }

    /**
     * Create file with directories
     * @param filePath file path
     * @param fileName file name
     * @return created file
     * @throws IOException if creation fails
     */
    private static File makeFile(String filePath, String fileName) throws IOException {
        makeDirectory(filePath);
        File file = new File(filePath + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}