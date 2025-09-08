package com.topdon.commons.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.UUID;

/**
 * StringUtils class.
 * 
 * Provides functionality for stringutils operations.
 */
public class StringUtils {
    /**
     * [Chinese text]uuid[Chinese text], [Chinese text]
     */
    /**
 * Randomuuid operation.
 *
 * @return the result
 */
public static String randomUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * [Chinese text]
     *
     * @param src       [Chinese text]
     * @param targetLen target[Chinese text]
     * @param head      [Chinese text]
     */
    /**
 * Fillzero operation.
 * * @param src the src parameter
 * @param targetLen the targetLen parameter
 * @param head the head parameter
 * @return the result
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
     * [Chinese text]16[Chinese text], [Chinese text]2[Chinese text]
     */
    /**
 * Tohex operation.
 * * @param num the num parameter
 * @return the result
 */
public static String toHex(int num) {
        return fillZero(Integer.toHexString(num), 2, true);
    }

    /**
     * [Chinese text]16[Chinese text], [Chinese text]2[Chinese text]
     */
    /**
 * Tohex operation.
 * * @param num the num parameter
 * @return the result
 */
public static String toHex(long num) {
        return fillZero(Long.toHexString(num), 2, true);
    }

    /**
     * [Chinese text]2[Chinese text], [Chinese text]8[Chinese text]
     */
    /**
 * Tobinary operation.
 * * @param num the num parameter
 * @return the result
 */
public static String toBinary(int num) {
        return fillZero(Integer.toBinaryString(num), 8, true);
    }

    /**
     * [Chinese text]2[Chinese text], [Chinese text]8[Chinese text]
     */
    /**
 * Tobinary operation.
 * * @param num the num parameter
 * @return the result
 */
public static String toBinary(long num) {
        return fillZero(Long.toBinaryString(num), 8, true);
    }

    /**
     * byte[Chinese text]16[Chinese text]
     *
     * @return [Chinese text]bytes[Chinese text]null[Chinese text]null, [Chinese text]bytes[Chinese text]0[Chinese text]"", [Chinese text]
     */
    /**
 * Tohex operation.
 * * @param bytes the bytes parameter
 * @return the result
 */
public static String toHex(byte[] bytes) {
        return toHex(bytes, " ");
    }

    /**
     * byte[Chinese text]16[Chinese text]
     *
     * @param separator [Chinese text]
     * @return [Chinese text]bytes[Chinese text]null[Chinese text]null, [Chinese text]bytes[Chinese text]0[Chinese text]"", [Chinese text]
     */
    /**
 * Tohex operation.
 * * @param bytes the bytes parameter
 * @param separator the separator parameter
 * @return the result
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
     * byte[Chinese text]2[Chinese text]
     *
     * @return [Chinese text]bytes[Chinese text]null[Chinese text]null, [Chinese text]bytes[Chinese text]0[Chinese text]"", [Chinese text]
     */
    /**
 * Tobinary operation.
 * * @param bytes the bytes parameter
 * @return the result
 */
public static String toBinary(byte[] bytes) {
        return toBinary(bytes, " ");
    }

    /**
     * byte[Chinese text]2[Chinese text]
     *
     * @param separator [Chinese text]
     * @return [Chinese text]bytes[Chinese text]null[Chinese text]null, [Chinese text]bytes[Chinese text]0[Chinese text]"", [Chinese text]
     */
    /**
 * Tobinary operation.
 * * @param bytes the bytes parameter
 * @param separator the separator parameter
 * @return the result
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
     * [Chinese text]java[Chinese text]point[Chinese text]0, [Chinese text].[Chinese text]
     */
    /**
 * Subzeroanddot operation.
 * * @param number the number parameter
 * @return the result
 */
public static String subZeroAndDot(String number) {
        if (TextUtils.isEmpty(number)) return number;
        if (number.indexOf(".") > 0) {
            number = number.replace("0+?$", "");// [Chinese text]0  
            number = number.replace("[.]$", "");// [Chinese text].[Chinese text]  
        }
        return number;
    }

    /**
     * [Chinese text]00:00:00
     *
     * @param duration [Chinese text], [Chinese text]: [Chinese text]
     */
    @NonNull
    /**
 * Toduration operation.
 * * @param duration the duration parameter
 * @return the result
 */
public static String toDuration(int duration) {
        return toDuration(duration, null);
    }

    /**
     * [Chinese text]
     *
     * @param duration [Chinese text], [Chinese text]: [Chinese text]
     */
    @NonNull
    /**
 * Toduration operation.
 * * @param duration the duration parameter
 * @param format the format parameter
 * @return the result
 */
public static String toDuration(int duration, String format) {
        if (format != null) {
            return String.format(Locale.ENGLISH, format, duration / 3600, duration % 3600 / 60, duration % 60);
        } else {
            return String.format(Locale.ENGLISH, "%02d:%02d:%02d", duration / 3600, duration % 3600 / 60, duration % 60);
        }
    }

    /**
     * [Chinese text]16[Chinese text]
     *
     * @param hexStr    16[Chinese text]
     * @param separator [Chinese text]
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
