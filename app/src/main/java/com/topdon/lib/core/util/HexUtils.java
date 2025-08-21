package com.topdon.lib.core.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Consolidated hex utility class combining functionality from multiple hex utilities
 * Replaces com.infisense.usbir.utils.HexUtils and com.topdon.ble.util.HexUtil
 */
public class HexUtils {
    private static FileInputStream in;

    /**
     * Convert byte array to hex string (uppercase, no spaces)
     * @param bArray byte array to convert
     * @return hex string representation
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null || bArray.length <= 0)
            return "BYTE IS NULL";
        int length = bArray.length;
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * Convert byte array to hex string with spaces between bytes (uppercase)
     * @param bytes byte array to convert
     * @return hex string with spaces
     */
    public static String binaryToHexString(byte[] bytes) {
        String hexStr = "0123456789ABCDEF";
        String result = "";
        String hex = "";
        for (byte b : bytes) {
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            result += hex + " ";
        }
        return result;
    }

    /**
     * Convert single byte to hex string
     * @param byte1 byte to convert
     * @return hex string representation
     */
    public static String byteToHex(byte byte1) {
        StringBuffer sb = new StringBuffer(1);
        String sTemp;
        for (int i = 0; i < 1; i++) {
            sTemp = Integer.toHexString(0xFF & byte1);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * Convert hex string to byte array
     * @param hexStr hex string to convert
     * @return byte array
     */
    public static byte[] toByteArray(String hexStr) {
        String s = hexStr.replaceAll(" ", "");
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    /**
     * Convert hex string to byte array with validation for 2-byte result
     * @param hexStr hex string to convert
     * @return byte array (padded to 2 bytes if needed)
     */
    public static byte[] toByteArray1(String hexStr) {
        String s = hexStr.replaceAll(" ", "");
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
        }
        if (bytes.length != 2) {
            byte v = bytes[0];
            bytes = new byte[2];
            bytes[0] = 0;
            bytes[1] = v;
        }
        return bytes;
    }

    /**
     * Convert string to hex bytes
     * @param src source string
     * @return byte array
     */
    public static byte[] getString2HexBytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * Convert hex string to byte array
     * @param src hex string
     * @return byte array
     */
    public static byte[] HexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < len; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * Unite two bytes
     * @param src0 first byte
     * @param src1 second byte
     * @return united byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * Convert hex string to byte array
     * @param hex hex string
     * @return byte array
     */
    public static byte[] hexToByte(String hex) {
        int m = 0, n = 0;
        int byteLen = hex.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte) intVal);
        }
        return ret;
    }

    /**
     * Convert hex to string
     * @param bytes hex string
     * @return converted string
     */
    public static String hexToString(String bytes) {
        bytes = bytes.toUpperCase();
        String hexString = "0123456789ABCDEFabcdef";
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    /**
     * Read file to byte array
     * @param path file path
     * @return byte array from file
     */
    public static byte[] readFileToByteArray(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            in = new FileInputStream(file);
            long inSize = in.getChannel().size();
            if (inSize == 0) {
                return null;
            }

            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            return buffer;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                return null;
            }
        }
    }

    /**
     * Extract sub-array from byte array
     * @param data source data
     * @param start start index
     * @param length length to extract
     * @return sub-array
     */
    public static byte[] byteSub(byte[] data, int start, int length) {
        byte[] bt = new byte[length];
        if (start + length > data.length) {
            bt = new byte[data.length - start];
        }
        for (int i = 0; i < length && (i + start) < data.length; i++) {
            bt[i] = data[i + start];
        }
        return bt;
    }

    // HexDump functionality consolidated from multiple HexDump classes
    private final static char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private final static char[] HEX_LOWER_CASE_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Dump byte array as hex string with ASCII representation
     * @param array byte array to dump
     * @return formatted hex dump string
     */
    public static String dumpHexString(byte[] array) {
        if (array == null) return "(null)";
        return dumpHexString(array, 0, array.length);
    }

    /**
     * Dump byte array as hex string with ASCII representation
     * @param array byte array to dump
     * @param offset start offset
     * @param length length to dump
     * @return formatted hex dump string
     */
    public static String dumpHexString(byte[] array, int offset, int length) {
        if (array == null) return "(null)";
        StringBuilder result = new StringBuilder();

        byte[] line = new byte[16];
        int lineIndex = 0;

        result.append("\n0x");
        result.append(toHexString(offset));

        for (int i = offset; i < offset + length; i++) {
            if (lineIndex == 16) {
                result.append(" ");

                for (int j = 0; j < 16; j++) {
                    if (line[j] > ' ' && line[j] < '~') {
                        result.append(new String(line, j, 1));
                    } else {
                        result.append(".");
                    }
                }

                result.append("\n0x");
                result.append(toHexString(i));
                lineIndex = 0;
            }

            byte b = array[i];
            result.append(" ");
            result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
            result.append(HEX_DIGITS[b & 0x0F]);

            line[lineIndex++] = b;
        }

        if (lineIndex != 16) {
            int count = (16 - lineIndex) * 3;
            count++;
            for (int i = 0; i < count; i++) {
                result.append(" ");
            }

            for (int i = 0; i < lineIndex; i++) {
                if (line[i] > ' ' && line[i] < '~') {
                    result.append(new String(line, i, 1));
                } else {
                    result.append(".");
                }
            }
        }

        result.append("\n");
        return result.toString();
    }

    /**
     * Convert integer to hex string
     * @param i integer to convert
     * @return hex string
     */
    public static String toHexString(int i) {
        return toHexString(toByteArray(i));
    }

    /**
     * Convert byte array to hex string
     * @param array byte array
     * @return hex string
     */
    public static String toHexString(byte[] array) {
        return toHexString(array, 0, array.length, true);
    }

    /**
     * Convert byte array to hex string
     * @param array byte array
     * @param offset start offset
     * @param length length
     * @param upperCase whether to use uppercase
     * @return hex string
     */
    public static String toHexString(byte[] array, int offset, int length, boolean upperCase) {
        char[] digits = upperCase ? HEX_DIGITS : HEX_LOWER_CASE_DIGITS;
        char[] buf = new char[length * 2];
        int bufIndex = 0;
        for (int i = offset; i < offset + length; i++) {
            byte b = array[i];
            buf[bufIndex++] = digits[(b >>> 4) & 0x0F];
            buf[bufIndex++] = digits[b & 0x0F];
        }
        return new String(buf);
    }

    /**
     * Convert integer to 4-byte array
     * @param i integer value
     * @return byte array
     */
    public static byte[] toByteArray(int i) {
        byte[] array = new byte[4];
        array[3] = (byte) (i & 0xFF);
        array[2] = (byte) ((i >> 8) & 0xFF);
        array[1] = (byte) ((i >> 16) & 0xFF);
        array[0] = (byte) ((i >> 24) & 0xFF);
        return array;
    }
}