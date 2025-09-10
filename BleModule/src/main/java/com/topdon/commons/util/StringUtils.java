package com.topdon.commons.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.UUID;

/**
 * date: 2019/8/7 11:15
 * author: chuanfeng.bi
 */
public class StringUtils {
    /**
     * uuid，
     */
    public static String randomUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 
     *
     * @param src       
     * @param targetLen 
     * @param head      
     */
    public static String fillZero(String src, int targetLen, boolean head) {
        if (src == null) return null;
        StringBuilder sb = new StringBuilder(src);
        while (sb.length() % targetLen != 0) {
            if (head) {
                sb.insert(0, "0");
            } else {
                sb.append("0");
            }
        }
        return sb.toString();
    }

    /**
     * 16，2
     */
    public static String toHex(int num) {
        return fillZero(Integer.toHexString(num), 2, true);
    }

    /**
     * 16，2
     */
    public static String toHex(long num) {
        return fillZero(Long.toHexString(num), 2, true);
    }

    /**
     * 2，8
     */
    public static String toBinary(int num) {
        return fillZero(Integer.toBinaryString(num), 8, true);
    }

    /**
     * 2，8
     */
    public static String toBinary(long num) {
        return fillZero(Long.toBinaryString(num), 8, true);
    }

    /**
     * byteArray16
     *
     * @return bytesnullnull，bytes0""，
     */
    public static String toHex(byte[] bytes) {
        return toHex(bytes, " ");
    }

    /**
     * byteArray16
     *
     * @param separator 
     * @return bytesnullnull，bytes0""，
     */
    public static String toHex(byte[] bytes, String separator) {
        if (bytes == null) {
            return null;
        } else if (bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte aSrc : bytes) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
            if (!TextUtils.isEmpty(separator)) {
                sb.append(separator);
            }
        }
        String s = sb.toString().toUpperCase(Locale.ENGLISH);
        if (!TextUtils.isEmpty(separator)) {
            s = s.substring(0, s.length() - separator.length());
        }
        return s;
    }

    /**
     * byteArray2
     *
     * @return bytesnullnull，bytes0""，
     */
    public static String toBinary(byte[] bytes) {
        return toBinary(bytes, " ");
    }

    /**
     * byteArray2
     *
     * @param separator 
     * @return bytesnullnull，bytes0""，
     */
    public static String toBinary(byte[] bytes, String separator) {
        if (bytes == null) {
            return null;
        } else if (bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte aSrc : bytes) {
            int v = aSrc & 0xFF;
            String hv = Integer.toBinaryString(v);
            int loop = 8 - hv.length();
            for (int i = 0; i < loop; i++) {
                sb.append(0);
            }
            sb.append(hv);
            if (!TextUtils.isEmpty(separator)) {
                sb.append(separator);
            }
        }
        String s = sb.toString();
        if (!TextUtils.isEmpty(separator)) {
            s = s.substring(0, s.length() - separator.length());
        }
        return s;
    }

    /**
     * java0，.
     */
    public static String subZeroAndDot(String number) {
        if (TextUtils.isEmpty(number)) return number;
        if (number.indexOf(".") > 0) {
            number = number.replace("0+?$", "");//0  
            number = number.replace("[.]$", "");//.  
        }
        return number;
    }

    /**
     * 00:00:00
     *
     * @param duration ，：
     */
    @NonNull
    public static String toDuration(int duration) {
        return toDuration(duration, null);
    }

    /**
     * 
     *
     * @param duration ，：
     */
    @NonNull
    public static String toDuration(int duration, String format) {
        if (format != null) {
            return String.format(Locale.ENGLISH, format, duration / 3600, duration % 3600 / 60, duration % 60);
        } else {
            return String.format(Locale.ENGLISH, "%02d:%02d:%02d", duration / 3600, duration % 3600 / 60, duration % 60);
        }
    }

    /**
     * 16Array
     *
     * @param hexStr    16
     * @param separator 
     */
    public static byte[] toByteArray(String hexStr, String separator) {
        String s = hexStr.replaceAll(separator, "");
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }
}
