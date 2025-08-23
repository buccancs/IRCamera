package com.topdon.module.thermal.ir.utils;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Unit tests for HexDump utility class
 * This class is critical for thermal camera data processing
 */
@RunWith(RobolectricTestRunner.class)
public class HexDumpTest {

    @Test
    public void dumpHexString_withNullArray_returnsNullString() {
        assertThat(HexDump.dumpHexString(null)).isEqualTo("(null)");
    }

    @Test
    public void dumpHexString_withEmptyArray_returnsEmptyHexDump() {
        byte[] emptyArray = {};
        String result = HexDump.dumpHexString(emptyArray);
        assertThat(result).contains("0x");
        assertThat(result).hasLength(5); // "\n0x00"
    }

    @Test
    public void dumpHexString_withSimpleArray_returnsFormattedHex() {
        byte[] testData = {0x01, 0x02, 0x03, 0x04};
        String result = HexDump.dumpHexString(testData);
        
        assertThat(result).contains("0x");
        assertThat(result).contains("01 02 03 04");
    }

    @Test
    public void dumpHexString_withOffsetAndLength_handlesPartialArray() {
        byte[] testData = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05};
        String result = HexDump.dumpHexString(testData, 2, 3);
        
        assertThat(result).contains("02 03 04");
        assertThat(result).doesNotContain("00");
        assertThat(result).doesNotContain("01");
        assertThat(result).doesNotContain("05");
    }

    @Test
    public void toHexString_withByte_returnsCorrectHex() {
        assertThat(HexDump.toHexString((byte) 0x00)).isEqualTo("00");
        assertThat(HexDump.toHexString((byte) 0x0F)).isEqualTo("0F");
        assertThat(HexDump.toHexString((byte) 0xFF)).isEqualTo("FF");
        assertThat(HexDump.toHexString((byte) 0x7F)).isEqualTo("7F");
    }

    @Test
    public void toHexString_withByteArray_returnsCorrectHex() {
        byte[] testData = {0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
        assertThat(HexDump.toHexString(testData)).isEqualTo("0123456789ABCDEF");
    }

    @Test
    public void toHexString_withUpperCaseFalse_returnsLowerCaseHex() {
        byte[] testData = {(byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
        assertThat(HexDump.toHexString(testData, false)).isEqualTo("abcdef");
        assertThat(HexDump.toHexString(testData, true)).isEqualTo("ABCDEF");
    }

    @Test
    public void toHexString_withInt_returnsCorrectHex() {
        assertThat(HexDump.toHexString(0x12345678)).isEqualTo("12345678");
        assertThat(HexDump.toHexString(0x00000000)).isEqualTo("00000000");
        assertThat(HexDump.toHexString(0xFFFFFFFF)).isEqualTo("FFFFFFFF");
    }

    @Test
    public void toByteArray_withByte_returnsCorrectArray() {
        byte testByte = (byte) 0x42;
        byte[] result = HexDump.toByteArray(testByte);
        
        assertThat(result).hasLength(1);
        assertThat(result[0]).isEqualTo(testByte);
    }

    @Test
    public void toByteArray_withInt_returnsCorrectBigEndianArray() {
        int testInt = 0x12345678;
        byte[] result = HexDump.toByteArray(testInt);
        
        assertThat(result).hasLength(4);
        assertThat(result[0]).isEqualTo((byte) 0x12);
        assertThat(result[1]).isEqualTo((byte) 0x34);
        assertThat(result[2]).isEqualTo((byte) 0x56);
        assertThat(result[3]).isEqualTo((byte) 0x78);
    }

    @Test
    public void hexStringToByteArray_withValidHex_returnsCorrectBytes() {
        String hexString = "0123456789ABCDEF";
        byte[] result = HexDump.hexStringToByteArray(hexString);
        
        assertThat(result).hasLength(8);
        assertThat(result[0]).isEqualTo((byte) 0x01);
        assertThat(result[1]).isEqualTo((byte) 0x23);
        assertThat(result[7]).isEqualTo((byte) 0xEF);
    }

    @Test
    public void hexStringToByteArray_withLowerCaseHex_returnsCorrectBytes() {
        String hexString = "abcdef";
        byte[] result = HexDump.hexStringToByteArray(hexString);
        
        assertThat(result).hasLength(3);
        assertThat(result[0]).isEqualTo((byte) 0xAB);
        assertThat(result[1]).isEqualTo((byte) 0xCD);
        assertThat(result[2]).isEqualTo((byte) 0xEF);
    }

    @Test
    public void hexStringToByteArray_withInvalidChar_throwsException() {
        assertThrows(RuntimeException.class, () -> {
            HexDump.hexStringToByteArray("XY");
        });
    }

    @Test
    public void appendByteAsHex_withStringBuilder_appendsCorrectHex() {
        StringBuilder sb = new StringBuilder();
        HexDump.appendByteAsHex(sb, (byte) 0xAB, true);
        HexDump.appendByteAsHex(sb, (byte) 0xCD, false);
        
        assertThat(sb.toString()).isEqualTo("ABcd");
    }

    @Test
    public void bytesToInt_withLittleEndianBytes_returnsCorrectInt() {
        // Little endian: low byte first
        byte[] bytes = {0x78, 0x56, 0x34, 0x12}; // Should be 0x12345678
        int result = HexDump.bytesToInt(bytes, 0);
        
        assertThat(result).isEqualTo(0x12345678);
    }

    @Test
    public void intToBytes_withInt_returnsCorrectLittleEndianBytes() {
        int testInt = 0x12345678;
        byte[] result = HexDump.intToBytes(testInt);
        
        assertThat(result).hasLength(4);
        // Little endian: low byte first
        assertThat(result[0]).isEqualTo((byte) 0x78);
        assertThat(result[1]).isEqualTo((byte) 0x56);
        assertThat(result[2]).isEqualTo((byte) 0x34);
        assertThat(result[3]).isEqualTo((byte) 0x12);
    }

    @Test
    public void intToBytes2_withInt_returnsCorrectBigEndianBytes() {
        int testInt = 0x12345678;
        byte[] result = HexDump.intToBytes2(testInt);
        
        assertThat(result).hasLength(4);
        // Big endian: high byte first
        assertThat(result[0]).isEqualTo((byte) 0x12);
        assertThat(result[1]).isEqualTo((byte) 0x34);
        assertThat(result[2]).isEqualTo((byte) 0x56);
        assertThat(result[3]).isEqualTo((byte) 0x78);
    }

    @Test
    public void float2byte_withFloat_convertsToLittleEndianBytes() {
        float testFloat = 3.14159f;
        byte[] result = new byte[4];
        HexDump.float2byte(testFloat, result);
        
        // Verify we got 4 bytes
        assertThat(result).hasLength(4);
        
        // Convert back to float to verify correctness
        int intBits = 0;
        for (int i = 0; i < 4; i++) {
            intBits |= (result[i] & 0xFF) << (i * 8);
        }
        float convertedBack = Float.intBitsToFloat(intBits);
        assertThat(convertedBack).isWithin(0.00001f).of(testFloat);
    }

    @Test
    public void intToBytes_roundTripConversion_maintainsValue() {
        int originalValue = 0x12345678;
        
        // Convert to bytes and back
        byte[] bytes = HexDump.intToBytes(originalValue);
        int convertedBack = HexDump.bytesToInt(bytes, 0);
        
        assertThat(convertedBack).isEqualTo(originalValue);
    }

    @Test
    public void hexStringConversion_roundTrip_maintainsData() {
        byte[] original = {0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
        
        // Convert to hex string and back
        String hexString = HexDump.toHexString(original);
        byte[] convertedBack = HexDump.hexStringToByteArray(hexString);
        
        assertThat(convertedBack).isEqualTo(original);
    }

    @Test
    public void dumpHexString_with16ByteLine_formatsCorrectly() {
        // Test with exactly 16 bytes to see line formatting
        byte[] testData = new byte[16];
        for (int i = 0; i < 16; i++) {
            testData[i] = (byte) i;
        }
        
        String result = HexDump.dumpHexString(testData);
        
        // Should contain hex values
        assertThat(result).contains("00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F");
        // Should have ASCII representation of printable chars (if any)
        assertThat(result).contains("0x");
    }

    @Test
    public void dumpHexString_withMultipleLines_formatsCorrectly() {
        // Test with more than 16 bytes to trigger multiple lines
        byte[] testData = new byte[32];
        for (int i = 0; i < 32; i++) {
            testData[i] = (byte) i;
        }
        
        String result = HexDump.dumpHexString(testData);
        
        // Should have multiple lines
        assertThat(result).contains("\n");
        // Should have multiple address markers
        int addressCount = result.split("0x").length - 1;
        assertThat(addressCount).isAtLeast(2);
    }
}