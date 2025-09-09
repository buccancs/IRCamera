package com.topdon.tc001.controller

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.topdon.tc001.sensors.SensorRecorder
import com.topdon.tc001.sensors.rgb.RgbCameraRecorder
import com.topdon.tc001.sensors.thermal.ThermalCameraRecorder
import com.topdon.tc001.sensors.gsr.GSRSensorRecorder
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.File

/**
 * Comprehensive unit tests for RecordingController
 * Tests core functionality, error handling, and state management
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RecordingControllerTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var lifecycleOwner: LifecycleOwner

    @MockK
    private lateinit var mockRgbRecorder: RgbCameraRecorder

    @MockK
    private lateinit var mockThermalRecorder: ThermalCameraRecorder

    @MockK
    private lateinit var mockGsrRecorder: GSRSensorRecorder

    private lateinit var recordingController: RecordingController
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Mock context and file operations
        every { context.cacheDir } returns File("/tmp/test")
        every { context.getExternalFilesDir(any()) } returns File("/tmp/test")

        recordingController = RecordingController(context, lifecycleOwner)

        // Setup mock recorders
        setupMockRecorders()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun setupMockRecorders() {
        every { mockRgbRecorder.initialize() } returns true
        every { mockThermalRecorder.initialize() } returns true
        every { mockGsrRecorder.initialize() } returns true

        every { mockRgbRecorder.startRecording(any(), any()) } returns true
        every { mockThermalRecorder.startRecording(any(), any()) } returns true
        every { mockGsrRecorder.startRecording(any(), any()) } returns true

        every { mockRgbRecorder.stopRecording() } returns mockk()
        every { mockThermalRecorder.stopRecording() } returns mockk()
        every { mockGsrRecorder.stopRecording() } returns mockk()

        every { mockRgbRecorder.cleanup() } just Runs
        every { mockThermalRecorder.cleanup() } just Runs
        every { mockGsrRecorder.cleanup() } just Runs

        every { mockRgbRecorder.isRecording() } returns false
        every { mockThermalRecorder.isRecording() } returns false
        every { mockGsrRecorder.isRecording() } returns false

        every { mockRgbRecorder.addSyncMarker(any(), any()) } returns true
        every { mockThermalRecorder.addSyncMarker(any(), any()) } returns true
        every { mockGsrRecorder.addSyncMarker(any(), any()) } returns true
    }

    @Test
    fun testInitialization() {
        assertFalse("Controller should not be recording initially", recordingController.isRecording)
        assertNotNull("Controller should be initialized", recordingController)
    }

    @Test
    fun testSensorRegistration() = runTest {
        // Register sensors
        val rgbResult = recordingController.registerSensor("rgb", mockRgbRecorder)
        val thermalResult = recordingController.registerSensor("thermal", mockThermalRecorder)
        val gsrResult = recordingController.registerSensor("gsr", mockGsrRecorder)

        assertTrue("RGB sensor should register successfully", rgbResult)
        assertTrue("Thermal sensor should register successfully", thermalResult)
        assertTrue("GSR sensor should register successfully", gsrResult)

        // Verify initialization was called
        verify { mockRgbRecorder.initialize() }
        verify { mockThermalRecorder.initialize() }
        verify { mockGsrRecorder.initialize() }
    }

    @Test
    fun testDuplicateSensorRegistration() = runTest {
        // Register sensor twice
        val firstResult = recordingController.registerSensor("rgb", mockRgbRecorder)
        val secondResult = recordingController.registerSensor("rgb", mockRgbRecorder)

        assertTrue("First registration should succeed", firstResult)
        assertFalse("Duplicate registration should fail", secondResult)
    }

    @Test
    fun testSensorInitializationFailure() = runTest {
        // Mock initialization failure
        every { mockRgbRecorder.initialize() } returns false

        val result = recordingController.registerSensor("rgb", mockRgbRecorder)

        assertFalse("Registration should fail when initialization fails", result)
    }

    @Test
    fun testStartRecording() = runTest {
        // Setup sensors
        registerAllSensors()

        // Start recording
        val sessionName = "TestSession"
        val result = recordingController.startRecording(sessionName)

        assertTrue("Recording should start successfully", result)
        assertTrue("Controller should be in recording state", recordingController.isRecording)

        // Verify all sensors were started
        verify { mockRgbRecorder.startRecording(any(), any()) }
        verify { mockThermalRecorder.startRecording(any(), any()) }
        verify { mockGsrRecorder.startRecording(any(), any()) }
    }

    @Test
    fun testStartRecordingWithoutSensors() = runTest {
        val result = recordingController.startRecording("TestSession")

        assertFalse("Recording should fail without sensors", result)
        assertFalse("Controller should not be recording", recordingController.isRecording)
    }

    @Test
    fun testStartRecordingWhenAlreadyRecording() = runTest {
        registerAllSensors()

        // Start recording first time
        val firstResult = recordingController.startRecording("Session1")
        assertTrue("First start should succeed", firstResult)

        // Try to start again
        val secondResult = recordingController.startRecording("Session2")
        assertFalse("Second start should fail", secondResult)
    }

    @Test
    fun testStopRecording() = runTest {
        registerAllSensors()

        // Start and then stop recording
        recordingController.startRecording("TestSession")
        val result = recordingController.stopRecording()

        assertNotNull("Stop recording should return session data", result)
        assertFalse("Controller should not be recording", recordingController.isRecording)

        // Verify all sensors were stopped
        verify { mockRgbRecorder.stopRecording() }
        verify { mockThermalRecorder.stopRecording() }
        verify { mockGsrRecorder.stopRecording() }
    }

    @Test
    fun testStopRecordingWhenNotRecording() = runTest {
        val result = recordingController.stopRecording()

        assertNull("Stop recording should return null when not recording", result)
    }

    @Test
    fun testSyncMarkerDistribution() = runTest {
        registerAllSensors()
        recordingController.startRecording("TestSession")

        // Add sync marker
        val syncId = "TEST_SYNC"
        val metadata = mapOf("test" to "value")
        val result = recordingController.addSyncMarker(syncId, metadata)

        assertTrue("Sync marker should be added successfully", result)

        // Verify sync marker was distributed to all sensors
        verify { mockRgbRecorder.addSyncMarker(syncId, metadata) }
        verify { mockThermalRecorder.addSyncMarker(syncId, metadata) }
        verify { mockGsrRecorder.addSyncMarker(syncId, metadata) }
    }

    @Test
    fun testSyncMarkerWhenNotRecording() = runTest {
        registerAllSensors()

        val result = recordingController.addSyncMarker("TEST_SYNC", emptyMap())

        assertFalse("Sync marker should fail when not recording", result)
    }

    @Test
    fun testErrorRecovery() = runTest {
        registerAllSensors()

        // Mock sensor start failure
        every { mockRgbRecorder.startRecording(any(), any()) } returns false

        val result = recordingController.startRecording("TestSession")

        assertFalse("Recording should fail when sensor fails to start", result)
        assertFalse("Controller should not be recording", recordingController.isRecording)
    }

    @Test
    fun testRecordingStats() = runTest {
        registerAllSensors()

        // Start recording and wait a bit
        recordingController.startRecording("TestSession")
        delay(100)

        val stats = recordingController.getRecordingStats()

        assertNotNull("Stats should be available when recording", stats)
        stats?.let {
            assertTrue("Session should be active", it.isActive)
            assertTrue("Duration should be positive", it.duration > 0)
            assertTrue("Session ID should contain timestamp", it.sessionId.contains("TestSession"))
        }
    }

    @Test
    fun testRecordingStatsWhenNotRecording() = runTest {
        val stats = recordingController.getRecordingStats()

        assertNull("Stats should be null when not recording", stats)
    }

    @Test
    fun testCleanup() = runTest {
        registerAllSensors()
        recordingController.startRecording("TestSession")

        recordingController.cleanup()

        assertFalse("Controller should not be recording after cleanup", recordingController.isRecording)

        // Verify cleanup was called on all sensors
        verify { mockRgbRecorder.cleanup() }
        verify { mockThermalRecorder.cleanup() }
        verify { mockGsrRecorder.cleanup() }
    }

    @Test
    fun testSensorStatusMonitoring() = runTest {
        registerAllSensors()

        // Mock one sensor as not recording
        every { mockRgbRecorder.isRecording() } returns true
        every { mockThermalRecorder.isRecording() } returns true
        every { mockGsrRecorder.isRecording() } returns false

        recordingController.startRecording("TestSession")

        // Allow some time for status monitoring
        delay(1100) // STATUS_UPDATE_INTERVAL_MS + buffer

        // Should detect inconsistent state and attempt recovery
        assertTrue("Controller should still be recording", recordingController.isRecording)
    }

    @Test
    fun testConcurrentOperations() = runTest {
        registerAllSensors()

        // Start multiple operations concurrently
        val operations = (1..10).map { index ->
            async {
                if (index % 2 == 0) {
                    recordingController.startRecording("Session$index")
                } else {
                    recordingController.addSyncMarker("SYNC_$index", emptyMap())
                }
            }
        }

        // Wait for all operations to complete
        operations.awaitAll()

        // Only one should have succeeded in starting recording
        val recordingStarted = operations.filterIndexed { index, _ -> index % 2 == 0 }
            .count { it.getCompleted() == true }

        assertEquals("Only one recording should have started", 1, recordingStarted)
    }

    private suspend fun registerAllSensors() {
        recordingController.registerSensor("rgb", mockRgbRecorder)
        recordingController.registerSensor("thermal", mockThermalRecorder)
        recordingController.registerSensor("gsr", mockGsrRecorder)
    }
}