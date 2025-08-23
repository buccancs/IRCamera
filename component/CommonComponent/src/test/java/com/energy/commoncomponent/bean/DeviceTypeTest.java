package com.energy.commoncomponent.bean;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Unit tests for DeviceType enum
 */
@RunWith(RobolectricTestRunner.class)
public class DeviceTypeTest {

    @Test
    public void deviceType_hasCorrectValues() {
        // Test that all expected device types are defined
        DeviceType[] types = DeviceType.values();
        
        assertThat(types).hasLength(8);
        
        // Verify all device types exist
        assertThat(types).asList().containsExactly(
            DeviceType.DEVICE_TYPE_TC2C,
            DeviceType.DEVICE_TYPE_WN2256,
            DeviceType.DEVICE_TYPE_WN2384,
            DeviceType.DEVICE_TYPE_WN2640,
            DeviceType.DEVICE_TYPE_X3,
            DeviceType.DEVICE_TYPE_P2L,
            DeviceType.DEVICE_TYPE_X2PRO,
            DeviceType.DEVICE_TYPE_GL1280
        );
    }

    @Test
    public void deviceType_canBeConvertedToString() {
        // Test enum to string conversion
        assertThat(DeviceType.DEVICE_TYPE_TC2C.toString()).isEqualTo("DEVICE_TYPE_TC2C");
        assertThat(DeviceType.DEVICE_TYPE_WN2256.toString()).isEqualTo("DEVICE_TYPE_WN2256");
        assertThat(DeviceType.DEVICE_TYPE_WN2384.toString()).isEqualTo("DEVICE_TYPE_WN2384");
        assertThat(DeviceType.DEVICE_TYPE_WN2640.toString()).isEqualTo("DEVICE_TYPE_WN2640");
        assertThat(DeviceType.DEVICE_TYPE_X3.toString()).isEqualTo("DEVICE_TYPE_X3");
        assertThat(DeviceType.DEVICE_TYPE_P2L.toString()).isEqualTo("DEVICE_TYPE_P2L");
        assertThat(DeviceType.DEVICE_TYPE_X2PRO.toString()).isEqualTo("DEVICE_TYPE_X2PRO");
        assertThat(DeviceType.DEVICE_TYPE_GL1280.toString()).isEqualTo("DEVICE_TYPE_GL1280");
    }

    @Test
    public void deviceType_canBeConvertedFromString() {
        // Test string to enum conversion
        assertThat(DeviceType.valueOf("DEVICE_TYPE_TC2C")).isEqualTo(DeviceType.DEVICE_TYPE_TC2C);
        assertThat(DeviceType.valueOf("DEVICE_TYPE_WN2256")).isEqualTo(DeviceType.DEVICE_TYPE_WN2256);
        assertThat(DeviceType.valueOf("DEVICE_TYPE_WN2384")).isEqualTo(DeviceType.DEVICE_TYPE_WN2384);
        assertThat(DeviceType.valueOf("DEVICE_TYPE_WN2640")).isEqualTo(DeviceType.DEVICE_TYPE_WN2640);
        assertThat(DeviceType.valueOf("DEVICE_TYPE_X3")).isEqualTo(DeviceType.DEVICE_TYPE_X3);
        assertThat(DeviceType.valueOf("DEVICE_TYPE_P2L")).isEqualTo(DeviceType.DEVICE_TYPE_P2L);
        assertThat(DeviceType.valueOf("DEVICE_TYPE_X2PRO")).isEqualTo(DeviceType.DEVICE_TYPE_X2PRO);
        assertThat(DeviceType.valueOf("DEVICE_TYPE_GL1280")).isEqualTo(DeviceType.DEVICE_TYPE_GL1280);
    }

    @Test
    public void deviceType_hasConsistentOrdinal() {
        // Test that ordinal values are consistent
        assertThat(DeviceType.DEVICE_TYPE_TC2C.ordinal()).isEqualTo(0);
        assertThat(DeviceType.DEVICE_TYPE_WN2256.ordinal()).isEqualTo(1);
        assertThat(DeviceType.DEVICE_TYPE_WN2384.ordinal()).isEqualTo(2);
        assertThat(DeviceType.DEVICE_TYPE_WN2640.ordinal()).isEqualTo(3);
        assertThat(DeviceType.DEVICE_TYPE_X3.ordinal()).isEqualTo(4);
        assertThat(DeviceType.DEVICE_TYPE_P2L.ordinal()).isEqualTo(5);
        assertThat(DeviceType.DEVICE_TYPE_X2PRO.ordinal()).isEqualTo(6);
        assertThat(DeviceType.DEVICE_TYPE_GL1280.ordinal()).isEqualTo(7);
    }

    @Test
    public void deviceType_equalityWorks() {
        // Test enum equality
        assertThat(DeviceType.DEVICE_TYPE_TC2C).isEqualTo(DeviceType.DEVICE_TYPE_TC2C);
        assertThat(DeviceType.DEVICE_TYPE_TC2C).isNotEqualTo(DeviceType.DEVICE_TYPE_WN2256);
        assertThat(DeviceType.DEVICE_TYPE_TC2C).isNotEqualTo(null);
        
        // Test with valueOf
        assertThat(DeviceType.valueOf("DEVICE_TYPE_TC2C")).isEqualTo(DeviceType.DEVICE_TYPE_TC2C);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deviceType_throwsExceptionForInvalidValue() {
        DeviceType.valueOf("INVALID_DEVICE_TYPE");
    }

    @Test(expected = NullPointerException.class)
    public void deviceType_throwsExceptionForNullValue() {
        DeviceType.valueOf(null);
    }
}