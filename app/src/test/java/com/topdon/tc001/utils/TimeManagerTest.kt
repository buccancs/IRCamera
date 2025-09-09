package com.topdon.tc001.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.SystemClock
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.net.InetAddress
import kotlin.math.abs

/**
 * Comprehensive unit tests for TimeManager
 * Tests time synchronization, NTP protocol, and clock management
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TimeManagerTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var connectivityManager: ConnectivityManager

    @MockK
    private lateinit var networkCapabilities: NetworkCapabilities

    private lateinit var timeManager: TimeManager
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Mock context and network
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns mockk()
        every { connectivityManager.getNetworkCapabilities(any()) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true

        timeManager = TimeManager(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun testInitialization() {
        assertNotNull("TimeManager should be initialized", timeManager)
        assertFalse("Should not be synchronized initially", timeManager.isSynchronized())
    }

    @Test
    fun testSingletonPattern() {
        val instance1 = TimeManager.getInstance(context)
        val instance2 = TimeManager.getInstance(context)

        assertSame("Should return same instance", instance1, instance2)
    }

    @Test
    fun testMonotonicTimestamp() {
        val timestamp1 = timeManager.getMonotonicTimestamp()
        Thread.sleep(1)
        val timestamp2 = timeManager.getMonotonicTimestamp()

        assertTrue("Monotonic timestamps should be increasing", timestamp2 > timestamp1)
        assertTrue("Timestamp difference should be reasonable", (timestamp2 - timestamp1) < 1000000) // 1ms in nanos
    }

    @Test
    fun testHighPrecisionTimestamp() {
        val timestamp1 = timeManager.getHighPrecisionTimestamp()
        val timestamp2 = timeManager.getHighPrecisionTimestamp()

        assertTrue("High precision timestamps should be increasing", timestamp2 >= timestamp1)
        
        // Should be based on SystemClock.elapsedRealtimeNanos()
        val systemTime = SystemClock.elapsedRealtimeNanos()
        val timeDiff = abs(timestamp2 - systemTime)
        assertTrue("Should be close to system time", timeDiff < 1000000) // 1ms tolerance
    }

    @Test
    fun testClockOffsetCalculation() {
        val pcTime = System.currentTimeMillis() * 1000000 // Convert to nanoseconds
        val localTime = SystemClock.elapsedRealtimeNanos()
        val networkLatency = 50000000L // 50ms in nanoseconds

        timeManager.updateClockOffset(pcTime, localTime, networkLatency)

        assertTrue("Should be synchronized after offset calculation", timeManager.isSynchronized())
        
        val offset = timeManager.getClockOffset()
        assertNotNull("Clock offset should be calculated", offset)
    }

    @Test
    fun testSynchronizedTimestamp() = runTest {
        // Setup synchronization
        val pcTime = System.currentTimeMillis() * 1000000
        val localTime = SystemClock.elapsedRealtimeNanos()
        timeManager.updateClockOffset(pcTime, localTime, 0)

        val syncTimestamp = timeManager.getSynchronizedTimestamp()

        assertTrue("Synchronized timestamp should be positive", syncTimestamp > 0)
        
        // Should be reasonably close to current time
        val currentTime = System.currentTimeMillis()
        val timestampMs = syncTimestamp / 1000000
        val timeDiff = abs(timestampMs - currentTime)
        assertTrue("Should be close to current time", timeDiff < 1000) // 1 second tolerance
    }

    @Test
    fun testSynchronizedTimestampWhenNotSynced() {
        // Don't synchronize first
        val timestamp = timeManager.getSynchronizedTimestamp()

        // Should fall back to monotonic timestamp
        val monotonicTimestamp = timeManager.getMonotonicTimestamp()
        val diff = abs(timestamp - monotonicTimestamp)
        assertTrue("Should fall back to monotonic when not synced", diff < 1000000) // 1ms tolerance
    }

    @Test
    fun testNetworkLatencyMeasurement() = runTest {
        val startTime = System.nanoTime()
        
        // Mock successful ping
        mockkStatic(InetAddress::class)
        every { InetAddress.getByName("192.168.1.100").isReachable(any()) } returns true

        val latency = timeManager.measureNetworkLatency("192.168.1.100")

        assertTrue("Latency should be positive", latency >= 0)
        
        unmockkStatic(InetAddress::class)
    }

    @Test
    fun testNetworkLatencyTimeout() = runTest {
        // Mock failed ping
        mockkStatic(InetAddress::class)
        every { InetAddress.getByName("192.168.1.100").isReachable(any()) } returns false

        val latency = timeManager.measureNetworkLatency("192.168.1.100")

        assertEquals("Should return -1 for timeout", -1L, latency)
        
        unmockkStatic(InetAddress::class)
    }

    @Test
    fun testSyncQualityCalculation() {
        // Test high quality sync
        timeManager.updateClockOffset(
            pcTime = System.currentTimeMillis() * 1000000,
            localTime = SystemClock.elapsedRealtimeNanos(),
            networkLatency = 1000000L // 1ms
        )

        val highQualitySync = timeManager.getSyncQuality()
        assertTrue("High quality sync should be > 0.8", highQualitySync > 0.8)

        // Test low quality sync
        timeManager.updateClockOffset(
            pcTime = System.currentTimeMillis() * 1000000,
            localTime = SystemClock.elapsedRealtimeNanos(),
            networkLatency = 10000000L // 10ms (high latency)
        )

        val lowQualitySync = timeManager.getSyncQuality()
        assertTrue("Low quality sync should be < 0.8", lowQualitySync < 0.8)
    }

    @Test
    fun testClockDriftDetection() = runTest {
        // Initial sync
        timeManager.updateClockOffset(
            System.currentTimeMillis() * 1000000,
            SystemClock.elapsedRealtimeNanos(),
            1000000L
        )

        val initialOffset = timeManager.getClockOffset()

        // Simulate time passing and drift
        delay(100)
        
        // Update with slightly different offset (simulating drift)
        timeManager.updateClockOffset(
            System.currentTimeMillis() * 1000000,
            SystemClock.elapsedRealtimeNanos(),
            1000000L
        )

        val newOffset = timeManager.getClockOffset()
        
        // Should detect if drift is significant
        assertNotNull("Both offsets should be calculated", initialOffset)
        assertNotNull("Both offsets should be calculated", newOffset)
    }

    @Test
    fun testSessionIdGeneration() {
        val sessionId1 = timeManager.generateSessionId("TEST")
        val sessionId2 = timeManager.generateSessionId("TEST")

        assertTrue("Session ID should start with prefix", sessionId1.startsWith("TEST_"))
        assertTrue("Session ID should contain timestamp", sessionId1.contains("_"))
        assertNotEquals("Session IDs should be unique", sessionId1, sessionId2)

        // Verify timestamp format
        val timestampPart = sessionId1.substring(sessionId1.indexOf("_") + 1)
        assertTrue("Should contain valid timestamp", timestampPart.matches(Regex("\\d+")))
    }

    @Test
    fun testTimestampConversion() {
        val nanoTimestamp = System.nanoTime()
        
        val milliTimestamp = timeManager.nanosToMillis(nanoTimestamp)
        val convertedBack = timeManager.millisToNanos(milliTimestamp)

        assertEquals("Conversion should be consistent", 
            nanoTimestamp / 1000000, milliTimestamp)
        assertEquals("Back conversion should match", 
            milliTimestamp * 1000000, convertedBack)
    }

    @Test
    fun testSyncStatistics() = runTest {
        // Perform multiple sync operations
        repeat(5) { index ->
            timeManager.updateClockOffset(
                System.currentTimeMillis() * 1000000,
                SystemClock.elapsedRealtimeNanos(),
                (index + 1) * 1000000L // Varying latency
            )
            delay(10)
        }

        val stats = timeManager.getSyncStatistics()

        assertNotNull("Sync statistics should be available", stats)
        stats?.let {
            assertTrue("Should have sync count", it.syncCount > 0)
            assertTrue("Should have average latency", it.averageLatency >= 0)
            assertTrue("Should have last sync time", it.lastSyncTime > 0)
        }
    }

    @Test
    fun testConcurrentSyncOperations() = runTest {
        val operations = (1..10).map { index ->
            async {
                timeManager.updateClockOffset(
                    System.currentTimeMillis() * 1000000 + index,
                    SystemClock.elapsedRealtimeNanos(),
                    index * 1000000L
                )
            }
        }

        // Wait for all operations to complete
        operations.awaitAll()

        // Should handle concurrent operations gracefully
        assertTrue("Should be synchronized after concurrent operations", 
            timeManager.isSynchronized())
    }

    @Test
    fun testErrorHandling() {
        // Test with invalid inputs
        timeManager.updateClockOffset(-1, -1, -1)
        
        // Should handle gracefully and not crash
        assertFalse("Should not be synchronized with invalid inputs", 
            timeManager.isSynchronized())
    }

    @Test
    fun testSyncTimeout() = runTest {
        // Mock network unavailable
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false

        val result = timeManager.syncWithPcController("192.168.1.100")

        assertFalse("Sync should fail without network", result)
        assertFalse("Should not be synchronized", timeManager.isSynchronized())
    }

    @Test
    fun testClockAccuracy() {
        val beforeSync = SystemClock.elapsedRealtimeNanos()
        
        timeManager.updateClockOffset(
            System.currentTimeMillis() * 1000000,
            SystemClock.elapsedRealtimeNanos(),
            0 // No network latency for accuracy test
        )

        val syncTimestamp = timeManager.getSynchronizedTimestamp()
        val afterSync = SystemClock.elapsedRealtimeNanos()

        // The synchronized timestamp should be within a reasonable range
        assertTrue("Sync timestamp should be reasonable", 
            syncTimestamp >= beforeSync && syncTimestamp <= afterSync + 1000000) // 1ms tolerance
    }
}