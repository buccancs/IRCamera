package com.topdon.gsr

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.shimmer.driver.ObjectCluster
import com.shimmer.driver.shimmer3.Shimmer3
import com.topdon.gsr.service.ShimmerGSRRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Comprehensive test suite for Shimmer3 GSR integration
 * Tests both simulated and real device interface scenarios
 */
@RunWith(MockitoJUnitRunner::class)
class ShimmerIntegrationTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var handler: Handler
    private lateinit var shimmerRecorder: ShimmerGSRRecorder

    @Before
    fun setup() {
        // Create handler for testing
        handler = Handler(Looper.getMainLooper())
        
        // Create ShimmerGSRRecorder with mocked context
        shimmerRecorder = ShimmerGSRRecorder(mockContext)
    }

    @Test
    fun testObjectClusterDataExtraction() {
        // Test GSR data extraction from ObjectCluster
        val objectCluster = ObjectCluster()
        
        // Test conductance extraction
        val conductanceData = objectCluster.getFormatClusterValue("GSR_Conductance", "CAL")
        assertNotNull(conductanceData)
        assertTrue(conductanceData.data > 0)
        assertTrue(conductanceData.data < 100) // Reasonable GSR range
        assertEquals("µS", conductanceData.unit)
        
        // Test resistance extraction
        val resistanceData = objectCluster.getFormatClusterValue("GSR_Resistance", "CAL")
        assertNotNull(resistanceData)
        assertTrue(resistanceData.data > 0)
        assertTrue(resistanceData.data < 1000) // Reasonable resistance range
        assertEquals("kΩ", resistanceData.unit)
    }

    @Test
    fun testShimmer3DeviceConnection() {
        val shimmer3 = Shimmer3(mockContext, handler)
        val connectionLatch = CountDownLatch(1)
        var connectionStatus = ""

        // Set up connection callback
        shimmer3.setBluetoothConnectionCallback { status ->
            connectionStatus = status
            connectionLatch.countDown()
        }

        // Test connection
        shimmer3.connect("00:11:22:33:44:55", "TestShimmer")
        
        // Wait for connection callback
        assertTrue(connectionLatch.await(5, TimeUnit.SECONDS))
        assertEquals("CONNECTED", connectionStatus)
        assertTrue(shimmer3.isConnected())
        assertEquals("00:11:22:33:44:55", shimmer3.getMacAddress())
        assertEquals("TestShimmer", shimmer3.getDeviceName())
        
        // Clean up
        shimmer3.disconnect()
    }

    @Test
    fun testShimmer3DataStreaming() {
        val shimmer3 = Shimmer3(mockContext, handler)
        val dataLatch = CountDownLatch(5) // Wait for 5 data samples
        var receivedData = 0

        // Set up data callback
        shimmer3.setShimmerDataCallback { objectCluster ->
            receivedData++
            dataLatch.countDown()
        }

        // Connect and start streaming
        shimmer3.connect("00:11:22:33:44:55", "TestShimmer")
        
        // Give connection time to establish
        Thread.sleep(1500)
        
        shimmer3.startStreaming()
        
        // Wait for data samples
        assertTrue(dataLatch.await(10, TimeUnit.SECONDS))
        assertTrue(receivedData >= 5)
        
        // Clean up
        shimmer3.stopStreaming()
        shimmer3.disconnect()
    }

    @Test
    fun testShimmerGSRRecorderIntegration() = runBlocking {
        val recorder = ShimmerGSRRecorder(mockContext, 128)
        val dataLatch = CountDownLatch(10) // Wait for 10 GSR samples
        val receivedSamples = mutableListOf<com.topdon.gsr.model.GSRSample>()

        // Add listener for recording events
        recorder.addListener(object : ShimmerGSRRecorder.GSRRecordingListener {
            override fun onRecordingStarted(session: com.topdon.gsr.model.SessionInfo) {
                println("Recording started: ${session.sessionId}")
            }

            override fun onRecordingStopped(session: com.topdon.gsr.model.SessionInfo) {
                println("Recording stopped: ${session.sessionId}, samples: ${session.sampleCount}")
            }

            override fun onSampleRecorded(sample: com.topdon.gsr.model.GSRSample) {
                receivedSamples.add(sample)
                dataLatch.countDown()
            }

            override fun onSyncMarkRecorded(syncMark: com.topdon.gsr.model.SyncMark) {
                println("Sync mark: ${syncMark.eventType}")
            }

            override fun onError(error: String) {
                println("Error: $error")
            }

            override fun onDeviceConnected() {
                println("Device connected")
            }

            override fun onDeviceDisconnected() {
                println("Device disconnected")
            }
        })

        // Test device initialization and recording
        val deviceInitialized = recorder.initializeDevice()
        assertTrue(deviceInitialized)

        val recordingStarted = recorder.startRecording("TestSession_${System.currentTimeMillis()}")
        assertTrue(recordingStarted)

        // Wait for some data samples
        assertTrue(dataLatch.await(15, TimeUnit.SECONDS))
        
        // Verify data quality
        assertTrue(receivedSamples.size >= 10)
        
        receivedSamples.forEach { sample ->
            assertTrue(sample.conductance > 0)
            assertTrue(sample.conductance < 100) // Reasonable physiological range
            assertTrue(sample.resistance > 0)
            assertTrue(sample.resistance < 1000) // Reasonable resistance range
            assertNotNull(sample.sessionId)
        }

        // Test sync event
        assertTrue(recorder.triggerSyncEvent("TEST_EVENT", "Integration test"))

        // Stop recording
        val session = recorder.stopRecording()
        assertNotNull(session)
        assertTrue(session.sampleCount > 0)
    }

    @Test
    fun testGSRConfigurationValidation() {
        val shimmer3 = Shimmer3(mockContext, handler)
        
        // Test configuration bytes creation and validation
        val config = ByteArray(12)
        config[0] = 0x04.toByte() // 128Hz sampling rate
        config[1] = 0x08.toByte() // GSR sensor enable
        
        // This should not throw an exception
        shimmer3.writeConfigurationBytes(config)
        
        // Test edge cases
        val emptyConfig = ByteArray(0)
        shimmer3.writeConfigurationBytes(emptyConfig) // Should handle gracefully
        
        val largeConfig = ByteArray(256) { 0xFF.toByte() }
        shimmer3.writeConfigurationBytes(largeConfig) // Should handle gracefully
    }

    @Test
    fun testErrorHandlingAndFallbacks() {
        val recorder = ShimmerGSRRecorder(mockContext)
        val errorLatch = CountDownLatch(1)
        var errorMessage = ""

        recorder.addListener(object : ShimmerGSRRecorder.GSRRecordingListener {
            override fun onRecordingStarted(session: com.topdon.gsr.model.SessionInfo) {}
            override fun onRecordingStopped(session: com.topdon.gsr.model.SessionInfo) {}
            override fun onSampleRecorded(sample: com.topdon.gsr.model.GSRSample) {}
            override fun onSyncMarkRecorded(syncMark: com.topdon.gsr.model.SyncMark) {}
            override fun onDeviceConnected() {}
            override fun onDeviceDisconnected() {}
            
            override fun onError(error: String) {
                errorMessage = error
                errorLatch.countDown()
            }
        })

        // Test recording without device initialization
        runBlocking {
            val result = recorder.startRecording("TestErrorSession")
            // Should fail gracefully
            // Either return false or trigger error callback
        }

        // Test with invalid device address
        runBlocking {
            val result = recorder.initializeDevice("invalid_address")
            // Should handle invalid addresses gracefully
        }
    }

    @Test
    fun testPerformanceAndThroughput() = runBlocking {
        val recorder = ShimmerGSRRecorder(mockContext, 256) // High sampling rate
        val startTime = System.currentTimeMillis()
        val dataLatch = CountDownLatch(256) // 1 second worth at 256Hz
        var sampleCount = 0

        recorder.addListener(object : ShimmerGSRRecorder.GSRRecordingListener {
            override fun onRecordingStarted(session: com.topdon.gsr.model.SessionInfo) {}
            override fun onRecordingStopped(session: com.topdon.gsr.model.SessionInfo) {}
            override fun onSyncMarkRecorded(syncMark: com.topdon.gsr.model.SyncMark) {}
            override fun onError(error: String) {}
            override fun onDeviceConnected() {}
            override fun onDeviceDisconnected() {}
            
            override fun onSampleRecorded(sample: com.topdon.gsr.model.GSRSample) {
                sampleCount++
                dataLatch.countDown()
            }
        })

        // Initialize and start high-rate recording
        assertTrue(recorder.initializeDevice())
        assertTrue(recorder.startRecording("PerformanceTest"))

        // Wait for target number of samples
        assertTrue(dataLatch.await(5, TimeUnit.SECONDS))
        
        val endTime = System.currentTimeMillis()
        val duration = (endTime - startTime) / 1000.0
        val actualRate = sampleCount / duration

        // Verify we achieved reasonable throughput
        assertTrue(actualRate > 100) // At least 100 Hz
        println("Achieved sampling rate: ${actualRate.toInt()} Hz")

        recorder.stopRecording()
    }
}