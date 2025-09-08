package com.topdon.commons.util;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * MathUtils class.
 * 
 * Provides functionality for mathutils operations.
 */
public class MathUtils {
    /**
     * [Chinese text], [Chinese text]4[Chinese text]5[Chinese text]
     *
     * @param num   [Chinese text]
     * @param scale [Chinese text]
     */
    public static double setDoubleAccuracy(double num, int scale) {
        return ((int) (num * Math.pow(10, scale))) / Math.pow(10, scale);
    }

    /**
     * [Chinese text], [Chinese text]100%
     *
     * @param scale [Chinese text]. 12.3%[Chinese text]1[Chinese text]
     */
    public static float[] getPercents(int scale, @NonNull float... values) {
        float total = 0;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {
                list.add(i);
            }
            total += values[i];
        }

        if (total == 0) {
            return new float[values.length];
        }

        float[] fs = new float[values.length];
        int sc = (int) Math.pow(10, scale + 2);
        float sum = 0;
        for (int i = 0; i < list.size(); i++) {
            int index = list.get(i);
            if (i == list.size() - 1) {
                fs[index] = 1 - sum;
            } else {
                // [Chinese text]int[Chinese text]4[Chinese text]5[Chinese text], [Chinese text]float[Chinese text]
                fs[index] = (int) (values[index] / total * sc) / (float) sc;
                sum += fs[index];
            }
        }
        return fs;
    }

    /**
     * [Chinese text]
     *
     * @param bigEndian true[Chinese text]high[Chinese text], false[Chinese text]low[Chinese text]
     * @param value     [Chinese text], short, int, long
     * @param len       [Chinese text], [Chinese text]high[Chinese text], [Chinese text]; [Chinese text]low[Chinese text], [Chinese text]
     */
    @NonNull
    public static byte[] numberToBytes(boolean bigEndian, long value, int len) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            int j = bigEndian ? 7 - i : i;
            bytes[i] = (byte) (value >> 8 * j & 0xff);
        }
        if (len > 8) {
            return bytes;
        } else {
            return Arrays.copyOfRange(bytes, bigEndian ? 8 - len : 0, bigEndian ? 8 : len);
        }
    }

    /**
     * [Chinese text]
     *
     * @param bigEndian true[Chinese text]high[Chinese text], false[Chinese text]low[Chinese text]
     * @param cls       [Chinese text]
     * @param src       [Chinese text]
     */
    @SuppressWarnings("unchecked")
    public static <T> T bytesToNumber(boolean bigEndian, Class<T> cls, @NonNull byte... src) {
        int len = Math.min(8, src.length);
        byte[] bs = new byte[8];
        System.arraycopy(src, 0, bs, bigEndian ? 8 - len : 0, len);
        long value = 0;
        // [Chinese text]long[Chinese text]8[Chinese text]
        for (int i = 0; i < 8; i++) {
            int shift = (bigEndian ? 7 - i : i) << 3;
            value = value | ((long) 0xff << shift & ((long) bs[i] << shift));
        }
        if (src.length == 1) {
            value = (byte) value;
        } else if (src.length == 2) {
            value = (short) value;
        } else if (src.length <= 4) {
            value = (int) value;
        }
        if (cls == short.class || cls == Short.class) {
            return (T) Short.valueOf((short) value);
        } else if (cls == int.class || cls == Integer.class) {
            return (T) Integer.valueOf((int) value);
        } else if (cls == long.class || cls == Long.class) {
            return (T) Long.valueOf(value);
        }
        throw new IllegalArgumentException("cls must be one of short, int and long");
    }

    /**
     * [Chinese text], [Chinese text]bit. [Chinese text]10000110 00110001[Chinese text]10001100 01100001
     */
    public static byte[] reverseBitAndByte(byte[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        byte[] target = new byte[src.length];
        // [Chinese text]byte[Chinese text]bit
        for (int i = 0; i < src.length; i++) {
            // [Chinese text]bit
            int value = 0;
            int tmp = src[src.length - 1 - i];
            for (int j = 7; j >= 0; j--) {
                value |= (tmp & 0x01) << j;
                tmp >>= 1;
            }
            target[i] = (byte) value;
        }
        return target;
    }

    /**
     * [Chinese text]
     *
     * @param src  [Chinese text]
     * @param size [Chinese text], [Chinese text]
     * @return [Chinese text]
     */
    @NonNull
    public static List<byte[]> splitPackage(@NonNull byte[] src, int size) {
        List<byte[]> list = new ArrayList<>();
        int loop = src.length / size + (src.length % size == 0 ? 0 : 1);
        for (int i = 0; i < loop; i++) {
            int from = i * size;
            int to = Math.min(src.length, from + size);
            list.add(Arrays.copyOfRange(src, i * size, to));
        }
        return list;
    }

    /**
     * [Chinese text]
     *
     * @param src [Chinese text]
     * @return [Chinese text]
     */
    @NonNull
    public static byte[] joinPackage(@NonNull byte[]... src) {
        byte[] bytes = new byte[0];
        for (byte[] bs : src) {
            bytes = Arrays.copyOf(bytes, bytes.length + bs.length);
            System.arraycopy(bs, 0, bytes, bytes.length - bs.length, bs.length);
        }
        return bytes;
    }

    /**
     * Name:    CRC-8   x8+x2+x+1
     * Poly:    0x07
     * Init:    0x00
     * Refin:   False
     * Refout:  False
     * Xorout:  0x00
     */
    /**
 * Calccrc8 operation.
 * * @param bytes the bytes parameter
 * @return the result
 */
public static int calcCrc8(byte[] bytes) {
        int crc = 0;
        for (byte b : bytes) {
            crc ^= b;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80) != 0) {
                    crc = (crc << 1) ^ 0x07;
                } else {
                    crc <<= 1;
                }
            }
        }
        return crc & 0xff;
    }

    /**
     * CRC16[Chinese text], Modbus
     */
    /**
 * Calccrc16 Modbus operation.
 * * @param data the data parameter
 * @return the result
 */
public static int calcCRC16_Modbus(byte[] data) {
        int crc = 0xffff;// 16[Chinese text]
        for (byte b : data) {
            if (b < 0) {
                crc ^= (int) b + 256; // XOR byte into least sig. byte of
            } else {
                crc ^= (int) b; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
            }
        }
        return crc & 0xffff;
    }

    /**
     * CRC[Chinese text], CRC-CCITT (XModem)
     */
    /**
 * Calccrc Ccitt Xmodem operation.
 * * @param bytes the bytes parameter
 * @return the result
 */
public static int calcCRC_CCITT_XModem(byte[] bytes) {
        int crc = 0;          // initial value
        int polynomial = 0x1021;
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        return crc & 0xffff;
    }

    /**
     * CRC[Chinese text], CRC-CCITT (XModem)
     */
    /**
 * Calccrc Ccitt Xmodem operation.
 * * @param bytes the bytes parameter
 * @param offset the offset parameter
 * @param len the len parameter
 * @return the result
 */
public static int calcCRC_CCITT_XModem(byte[] bytes, int offset, int len) {
        int crc = 0;          // initial value
        int polynomial = 0x1021;
        for (int i = offset; i < offset + len; i++) {
            byte b = bytes[i];
            for (int j = 0; j < 8; j++) {
                boolean bit = ((b >> (7 - j) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        return crc & 0xffff;
    }

    /**
     * CRC[Chinese text], CRC-CCITT (0xFFFF)
     */
    /**
 * Calccrc Ccitt 0Xffff operation.
 * * @param bytes the bytes parameter
 * @return the result
 */
public static int calcCRC_CCITT_0xFFFF(byte[] bytes) {
        int crc = 0xffff; // initial value
        int polynomial = 0x1021; // poly value
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        return crc & 0xffff;
    }

    /**
     * CRC[Chinese text], CRC-CCITT (0xFFFF)
     */
    /**
 * Calccrc Ccitt 0Xffff operation.
 * * @param bytes the bytes parameter
 * @param offset the offset parameter
 * @param len the len parameter
 * @return the result
 */
public static int calcCRC_CCITT_0xFFFF(byte[] bytes, int offset, int len) {
        int crc = 0xffff; // initial value
        int polynomial = 0x1021; // poly value
        for (int i = offset; i < offset + len; i++) {
            byte b = bytes[i];
            for (int j = 0; j < 8; j++) {
                boolean bit = ((b >> (7 - j) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        return crc & 0xffff;
    }
}
