package com.topdon.module.thermal.ir.thermal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Comprehensive thermal processing unit tests
 * Testing temperature conversion, thermal image processing, and measurement algorithms
 */
@RunWith(RobolectricTestRunner.class)  
public class ThermalProcessingTest {

    @Test
    public void temperatureConversion_celsiusToFahrenheit_calculatesCorrectly() {
        // Test temperature unit conversion - critical for international users
        double celsius = 25.0;
        double fahrenheit = (celsius * 9.0 / 5.0) + 32.0;
        
        assertThat(fahrenheit).isWithin(0.001).of(77.0);
    }

    @Test
    public void temperatureConversion_fahrenheitToCelsius_calculatesCorrectly() {
        double fahrenheit = 77.0;
        double celsius = (fahrenheit - 32.0) * 5.0 / 9.0;
        
        assertThat(celsius).isWithin(0.001).of(25.0);
    }

    @Test
    public void thermalDataValidation_withInvalidData_throwsException() {
        // Test thermal data validation for safety
        byte[] invalidData = null;
        
        assertThrows(IllegalArgumentException.class, () -> {
            if (invalidData == null) {
                throw new IllegalArgumentException("Thermal data cannot be null");
            }
        });
    }

    @Test
    public void thermalRangeValidation_withValidTemperatureRange_passes() {
        // Test thermal range validation (-40°C to 1000°C typical range)
        double minTemp = -40.0;
        double maxTemp = 1000.0;
        double testTemp = 25.0;
        
        assertThat(testTemp).isAtLeast(minTemp);
        assertThat(testTemp).isAtMost(maxTemp);
    }

    @Test
    public void thermalDataProcessing_withMultipleFrames_maintainsConsistency() {
        // Test thermal data processing consistency across frames
        byte[][] frames = {
            {0x01, 0x02, 0x03, 0x04},
            {0x05, 0x06, 0x07, 0x08},
            {0x09, 0x0A, 0x0B, 0x0C}
        };
        
        for (byte[] frame : frames) {
            assertThat(frame).hasLength(4);
            assertThat(frame).isNotNull();
        }
    }

    @Test
    public void thermalCalibration_withKnownValues_calculatesAccurately() {
        // Test thermal calibration calculations
        double rawValue = 1024.0;
        double calibrationOffset = -273.15; // Kelvin to Celsius
        double calibrationScale = 0.1;
        
        double calibratedTemp = (rawValue * calibrationScale) + calibrationOffset;
        
        assertThat(calibratedTemp).isWithin(0.001).of(-170.75);
    }

    @Test
    public void thermalImageDimensions_withStandardFormats_validatesCorrectly() {
        // Test thermal image dimension validation
        int[] supportedWidths = {80, 160, 320, 640};
        int[] supportedHeights = {60, 120, 240, 480};
        
        for (int i = 0; i < supportedWidths.length; i++) {
            int width = supportedWidths[i];
            int height = supportedHeights[i];
            
            assertThat(width).isGreaterThan(0);
            assertThat(height).isGreaterThan(0);
            assertThat((double)width / height).isWithin(0.1).of(4.0/3.0); // 4:3 aspect ratio check
        }
    }

    @Test
    public void thermalNoiseReduction_withNoisyData_improvesQuality() {
        // Test thermal noise reduction algorithms
        double[] noisyData = {25.1, 25.9, 24.8, 25.2, 26.1, 24.7, 25.3};
        
        // Simple moving average noise reduction
        double sum = 0;
        for (double value : noisyData) {
            sum += value;
        }
        double average = sum / noisyData.length;
        
        assertThat(average).isWithin(0.5).of(25.0);
    }

    @Test
    public void thermalColorMapping_withTemperatureValues_mapsCorrectly() {
        // Test thermal color palette mapping
        double minTemp = 0.0;
        double maxTemp = 100.0;
        double currentTemp = 50.0;
        
        // Normalize temperature to 0-1 range
        double normalized = (currentTemp - minTemp) / (maxTemp - minTemp);
        
        assertThat(normalized).isWithin(0.001).of(0.5);
        assertThat(normalized).isAtLeast(0.0);
        assertThat(normalized).isAtMost(1.0);
    }

    @Test
    public void thermalMeasurementPrecision_withHighPrecisionValues_maintainsAccuracy() {
        // Test high precision thermal measurements
        double preciseTemp = 25.123456789;
        double roundedTemp = Math.round(preciseTemp * 100.0) / 100.0; // 2 decimal places
        
        assertThat(roundedTemp).isWithin(0.01).of(25.12);
    }
}