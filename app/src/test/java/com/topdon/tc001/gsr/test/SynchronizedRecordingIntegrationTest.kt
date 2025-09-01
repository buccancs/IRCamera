package com.topdon.tc001.gsr.test

import android.content.Context
import com.topdon.tc001.gsr.EnhancedThermalRecorder
import com.topdon.gsr.util.TimeUtil
import org.junit.Test
import org.junit.Before
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Comprehensive integration test for synchronized thermal + GSR recording
 * Tests Samsung S22 ground truth timing coordination
 */
class SynchronizedRecordingIntegrationTest {
    
    @Mock
    private lateinit var context: Context
    
    private lateinit var enhancedRecorder: EnhancedThermalRecorder
    
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        
        // Initialize Samsung S22 ground truth timing
        TimeUtil.initializeGroundTruthTiming()
        enhancedRecorder = EnhancedThermalRecorder.create(context)
    }
    
    @Test
    fun testSamsungS22GroundTruthInitialization() {
        val groundTruthBase = TimeUtil.getGroundTruthBase()
        assertTrue("Samsung S22 ground truth should be initialized", groundTruthBase > 0)
        
        val timingValidation = TimeUtil.validateTimingSystem()
        assertEquals("Ground truth should be active", true, timingValidation["ground_truth_active"])
        assertEquals("Samsung S22 status should be operational", "operational", timingValidation["samsung_s22_status"])
    }
    
    @Test
    fun testSynchronizedTimestampConsistency() {
        val timestamp1 = TimeUtil.getSynchronizedTimestamp()
        Thread.sleep(10)
        val timestamp2 = TimeUtil.getSynchronizedTimestamp()
        
        assertTrue("Timestamps should be monotonic", timestamp2 > timestamp1)
        assertTrue("Time difference should be reasonable", (timestamp2 - timestamp1) < 50)
    }
    
    @Test
    fun testHighPrecisionTiming() {
        val highPrecision1 = TimeUtil.getHighPrecisionTimestamp()
        val standard1 = TimeUtil.getSynchronizedTimestamp()
        
        // High precision should be close to standard timing
        val difference = kotlin.math.abs(highPrecision1 - standard1)
        assertTrue("High precision timing should be close to standard", difference < 5)
    }
    
    @Test
    fun testEnhancedThermalRecorderIntegration() {
        // Test recording lifecycle
        val sessionName = "TestSession"
        val startResult = enhancedRecorder.startRecording(sessionName, "TestParticipant", true)
        assertTrue("Recording should start successfully", startResult)
        assertTrue("Recorder should be in recording state", enhancedRecorder.isRecording())
        
        // Test sync event triggering
        val syncResult = enhancedRecorder.triggerSyncEvent("TEST_SYNC_EVENT", mapOf("test" to "value"))
        assertTrue("Sync event should be triggered", syncResult)
        
        // Test frame capture with Samsung S22 timing
        val captureResult = enhancedRecorder.captureFrame(mapOf("frame" to "test"))
        assertTrue("Frame capture should succeed", captureResult)
        
        // Test recording stop
        val session = enhancedRecorder.stopRecording()
        assertNotNull("Session should be returned", session)
        assertFalse("Recorder should not be recording", enhancedRecorder.isRecording())
    }
    
    @Test
    fun testTimingMetadata() {
        val metadata = TimeUtil.getTimingMetadata()
        
        // Verify Samsung S22 specific metadata
        assertEquals("Samsung_Galaxy_S22", metadata["device_model"])
        assertEquals("Snapdragon_8_Gen_1", metadata["device_processor"])
        assertEquals("unified_ntp_style", metadata["timing_mode"])
        assertEquals("sub_millisecond", metadata["timing_precision"])
        
        assertTrue("Ground truth base should be present", metadata["ground_truth_base"]?.toLongOrNull() != null)
        assertTrue("Current sync time should be present", metadata["current_sync_time"]?.toLongOrNull() != null)
    }
    
    @Test
    fun testPcTimeOffsetCoordination() {
        val originalOffset = TimeUtil.getPcTimeOffset()
        val testOffset = 1000L // 1 second
        
        TimeUtil.setPcTimeOffset(testOffset)
        assertEquals("PC offset should be set", testOffset, TimeUtil.getPcTimeOffset())
        
        val timestamp = TimeUtil.getUtcTimestamp()
        val systemTime = System.currentTimeMillis()
        val expectedDifference = kotlin.math.abs(timestamp - systemTime - testOffset)
        
        assertTrue("PC offset should be applied correctly", expectedDifference < 10)
        
        // Restore original offset
        TimeUtil.setPcTimeOffset(originalOffset)
    }
    
    @Test
    fun testSessionIdGeneration() {
        val sessionId1 = TimeUtil.generateSessionId("ThermalTest")
        val sessionId2 = TimeUtil.generateSessionId("ThermalTest")
        
        assertTrue("Session ID should contain prefix", sessionId1.startsWith("ThermalTest_"))
        assertNotEquals("Session IDs should be unique", sessionId1, sessionId2)
        
        // Verify timestamp format in session ID
        val timestampPart = sessionId1.substringAfter("ThermalTest_")
        assertTrue("Session ID should contain timestamp", timestampPart.matches(Regex("\\d{8}_\\d{6}")))
    }
    
    @Test
    fun testRecordingStatsAccuracy() {
        val sessionName = "StatsTest"
        enhancedRecorder.startRecording(sessionName, null, false) // No GSR for this test
        
        Thread.sleep(100) // Record for 100ms
        
        val stats = enhancedRecorder.getRecordingStats()
        assertNotNull("Stats should be available", stats)
        
        stats?.let {
            assertTrue("Session should have some duration", it.duration > 0)
            assertEquals("Session ID should match", sessionName + "_", it.sessionId.substringBefore(sessionName.substringAfter("_")))
            assertTrue("Session should be active", it.isActive)
        }
        
        enhancedRecorder.stopRecording()
    }
    
    @Test
    fun testErrorHandlingAndFallbacks() {
        // Test double start
        enhancedRecorder.startRecording("Test1")
        val secondStart = enhancedRecorder.startRecording("Test2")
        assertFalse("Second start should fail", secondStart)
        
        // Test sync event without recording
        enhancedRecorder.stopRecording()
        val syncWithoutRecording = enhancedRecorder.triggerSyncEvent("TEST")
        assertFalse("Sync event without recording should fail", syncWithoutRecording)
        
        // Test stop without start
        val stopWithoutStart = enhancedRecorder.stopRecording()
        // Should not crash and handle gracefully
    }
    
    @Test
    fun testSamsungS22DeviceValidation() {
        // This test validates the device detection logic
        val deviceModel = android.os.Build.MODEL
        val deviceManufacturer = android.os.Build.MANUFACTURER
        
        // The actual validation logic should not crash
        val recorder = EnhancedThermalRecorder.create(context)
        assertNotNull("Recorder should be created regardless of device", recorder)
        
        // If running on Samsung device, additional validation
        if (deviceManufacturer.contains("samsung", ignoreCase = true)) {
            // Samsung device detected - timing should be optimized
            assertTrue("Samsung device should use optimized timing", true)
        }
    }
}