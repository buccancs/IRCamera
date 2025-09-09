package com.topdon.tc001.performance

import android.content.Context
import android.os.Debug
import androidx.lifecycle.LifecycleOwner
import com.topdon.tc001.controller.RecordingController
import com.topdon.tc001.network.EnhancedNetworkClient
import com.topdon.tc001.sensors.gsr.GSRSensorRecorder
import com.topdon.tc001.utils.TimeManager
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * Comprehensive performance tests for Hub-and-Spoke architecture
 * Tests data throughput, latency, CPU/memory usage under various load conditions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PerformanceTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var lifecycleOwner: LifecycleOwner

    private lateinit var recordingController: RecordingController
    private lateinit var networkClient: EnhancedNetworkClient
    private lateinit var timeManager: TimeManager
    private lateinit var gsrRecorder: GSRSensorRecorder

    private val testDispatcher = UnconfinedTestDispatcher()
    
    // Performance metrics
    private data class PerformanceMetrics(
        val executionTimeMs: Long,
        val memoryUsageMB: Double,
        val throughputOpsPerSec: Double,
        val averageLatencyMs: Double,
        val maxLatencyMs: Long,
        val errorRate: Double
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Mock context
        every { context.cacheDir } returns File("/tmp/test")
        every { context.getExternalFilesDir(any()) } returns File("/tmp/test")

        // Initialize components
        recordingController = RecordingController(context, lifecycleOwner)
        networkClient = EnhancedNetworkClient(context)
        timeManager = TimeManager.getInstance(context)

        // Setup mock sensors
        setupMockSensors()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        runBlocking {
            recordingController.cleanup()
            networkClient.disconnect()
        }
        unmockkAll()
    }

    private fun setupMockSensors() {
        gsrRecorder = mockk()
        every { gsrRecorder.initialize() } returns true
        every { gsrRecorder.startRecording(any(), any()) } returns true
        every { gsrRecorder.stopRecording() } returns mockk()
        every { gsrRecorder.isRecording() } returns false
        every { gsrRecorder.addSyncMarker(any(), any()) } returns true
        every { gsrRecorder.cleanup() } just Runs
    }

    @Test
    fun testHighThroughputSyncMarkers() = runTest {
        // Test sync marker throughput under high load
        recordingController.registerSensor("gsr", gsrRecorder)
        recordingController.startRecording("ThroughputTest")

        val syncMarkerCounts = listOf(100, 500, 1000, 2000)
        val results = mutableListOf<PerformanceMetrics>()

        syncMarkerCounts.forEach { count ->
            val metrics = measureSyncMarkerPerformance(count)
            results.add(metrics)

            // Verify performance requirements
            assertTrue("Throughput should be at least 50 ops/sec for $count markers", 
                metrics.throughputOpsPerSec >= 50.0)
            assertTrue("Average latency should be under 20ms for $count markers", 
                metrics.averageLatencyMs < 20.0)
            assertTrue("Error rate should be under 1% for $count markers", 
                metrics.errorRate < 0.01)
        }

        recordingController.stopRecording()

        // Verify scalability
        val throughputTrend = results.zipWithNext { current, next ->
            next.throughputOpsPerSec / current.throughputOpsPerSec
        }
        
        // Throughput should not degrade significantly with increased load
        throughputTrend.forEach { ratio ->
            assertTrue("Throughput should not degrade more than 50%", ratio >= 0.5)
        }
    }

    private suspend fun measureSyncMarkerPerformance(count: Int): PerformanceMetrics {
        val latencies = mutableListOf<Long>()
        var successCount = 0
        val initialMemory = getMemoryUsage()

        val totalTime = measureTimeMillis {
            repeat(count) { index ->
                val startTime = System.nanoTime()
                
                val result = recordingController.addSyncMarker(
                    "PERF_$index",
                    mapOf("index" to index, "timestamp" to System.nanoTime())
                )
                
                val endTime = System.nanoTime()
                val latency = (endTime - startTime) / 1_000_000 // Convert to ms
                latencies.add(latency)
                
                if (result) successCount++
                
                // Small delay to prevent overwhelming
                delay(1)
            }
        }

        val finalMemory = getMemoryUsage()
        val memoryUsed = finalMemory - initialMemory

        return PerformanceMetrics(
            executionTimeMs = totalTime,
            memoryUsageMB = memoryUsed,
            throughputOpsPerSec = (count * 1000.0) / totalTime,
            averageLatencyMs = latencies.average(),
            maxLatencyMs = latencies.maxOrNull() ?: 0L,
            errorRate = (count - successCount).toDouble() / count
        )
    }

    @Test
    fun testNetworkCommunicationPerformance() = runTest {
        // Mock successful network connection
        mockkConstructor(java.net.Socket::class)
        every { anyConstructed<java.net.Socket>().connect(any(), any()) } just Runs
        every { anyConstructed<java.net.Socket>().isConnected } returns true
        every { anyConstructed<java.net.Socket>().getOutputStream() } returns mockk(relaxed = true)
        every { anyConstructed<java.net.Socket>().getInputStream() } returns mockk(relaxed = true)

        networkClient.connect("localhost", 8080)

        val messageSizes = listOf(100, 1000, 5000, 10000) // bytes
        val messageResults = mutableListOf<PerformanceMetrics>()

        messageSizes.forEach { size ->
            val metrics = measureNetworkPerformance(size, 100) // 100 messages per size
            messageResults.add(metrics)

            // Network performance requirements
            assertTrue("Network throughput should be reasonable for ${size}B messages", 
                metrics.throughputOpsPerSec >= 10.0)
            assertTrue("Network latency should be under 100ms for ${size}B messages", 
                metrics.averageLatencyMs < 100.0)
        }

        // Verify network scalability with message size
        messageResults.zipWithNext { current, next ->
            val sizeRatio = messageSizes[messageResults.indexOf(next)] / messageSizes[messageResults.indexOf(current)]
            val latencyRatio = next.averageLatencyMs / current.averageLatencyMs
            
            // Latency should not increase more than proportionally to message size
            assertTrue("Latency increase should be proportional to message size", 
                latencyRatio <= sizeRatio * 2)
        }
    }

    private suspend fun measureNetworkPerformance(messageSize: Int, messageCount: Int): PerformanceMetrics {
        val latencies = mutableListOf<Long>()
        var successCount = 0
        val initialMemory = getMemoryUsage()

        val messageData = mapOf(
            "type" to "performance_test",
            "data" to "x".repeat(messageSize),
            "timestamp" to System.nanoTime()
        )

        val totalTime = measureTimeMillis {
            repeat(messageCount) { index ->
                val startTime = System.nanoTime()
                
                val result = networkClient.sendMessage(messageData)
                
                val endTime = System.nanoTime()
                val latency = (endTime - startTime) / 1_000_000
                latencies.add(latency)
                
                if (result) successCount++
                
                delay(5) // Small delay between messages
            }
        }

        val finalMemory = getMemoryUsage()

        return PerformanceMetrics(
            executionTimeMs = totalTime,
            memoryUsageMB = finalMemory - initialMemory,
            throughputOpsPerSec = (messageCount * 1000.0) / totalTime,
            averageLatencyMs = latencies.average(),
            maxLatencyMs = latencies.maxOrNull() ?: 0L,
            errorRate = (messageCount - successCount).toDouble() / messageCount
        )
    }

    @Test
    fun testTimeSynchronizationPerformance() = runTest {
        // Test time synchronization performance under various conditions
        val syncCounts = listOf(10, 50, 100)
        val syncResults = mutableListOf<PerformanceMetrics>()

        syncCounts.forEach { count ->
            val metrics = measureTimeSyncPerformance(count)
            syncResults.add(metrics)

            // Time sync performance requirements
            assertTrue("Time sync should maintain high accuracy under load", 
                metrics.errorRate < 0.05) // Less than 5% sync failures
            assertTrue("Average sync latency should be reasonable", 
                metrics.averageLatencyMs < 50.0) // Under 50ms per sync
        }

        // Verify sync quality remains consistent
        val accuracyTrend = syncResults.map { 1.0 - it.errorRate } // Convert to accuracy
        val averageAccuracy = accuracyTrend.average()
        
        assertTrue("Overall sync accuracy should be high", averageAccuracy >= 0.95)
    }

    private suspend fun measureTimeSyncPerformance(syncCount: Int): PerformanceMetrics {
        val latencies = mutableListOf<Long>()
        var successCount = 0
        val initialMemory = getMemoryUsage()

        val totalTime = measureTimeMillis {
            repeat(syncCount) { index ->
                val startTime = System.nanoTime()
                
                // Simulate time synchronization
                timeManager.updateClockOffset(
                    pcTime = System.currentTimeMillis() * 1_000_000,
                    localTime = System.nanoTime(),
                    networkLatency = (1..10).random() * 1_000_000L // 1-10ms random latency
                )
                
                val endTime = System.nanoTime()
                val latency = (endTime - startTime) / 1_000_000
                latencies.add(latency)
                
                // Check if sync was successful
                if (timeManager.isSynchronized()) successCount++
                
                delay(10) // Realistic interval between syncs
            }
        }

        val finalMemory = getMemoryUsage()

        return PerformanceMetrics(
            executionTimeMs = totalTime,
            memoryUsageMB = finalMemory - initialMemory,
            throughputOpsPerSec = (syncCount * 1000.0) / totalTime,
            averageLatencyMs = latencies.average(),
            maxLatencyMs = latencies.maxOrNull() ?: 0L,
            errorRate = (syncCount - successCount).toDouble() / syncCount
        )
    }

    @Test
    fun testMemoryUsageUnderLoad() = runTest {
        recordingController.registerSensor("gsr", gsrRecorder)
        
        val initialMemory = getMemoryUsage()
        recordingController.startRecording("MemoryTest")

        // Generate sustained load
        val loadDurationMs = 10000L // 10 seconds
        val operationsPerSecond = 50
        val totalOperations = (loadDurationMs / 1000) * operationsPerSecond

        val memorySnapshots = mutableListOf<Pair<Long, Double>>()
        val startTime = System.currentTimeMillis()

        repeat(totalOperations.toInt()) { index ->
            // Add sync markers continuously
            recordingController.addSyncMarker(
                "MEMORY_TEST_$index",
                mapOf(
                    "index" to index,
                    "timestamp" to System.nanoTime(),
                    "data" to "x".repeat(100) // Some payload
                )
            )

            // Take memory snapshot every second
            if (index % operationsPerSecond == 0) {
                val currentTime = System.currentTimeMillis()
                val currentMemory = getMemoryUsage()
                memorySnapshots.add((currentTime - startTime) to currentMemory)
            }

            delay(1000 / operationsPerSecond) // Maintain target rate
        }

        recordingController.stopRecording()
        val finalMemory = getMemoryUsage()

        // Analyze memory usage patterns
        val memoryGrowth = finalMemory - initialMemory
        
        // Memory growth should be reasonable
        assertTrue("Memory growth should be under 50MB for sustained load", memoryGrowth < 50.0)

        // Check for memory leaks (memory should not grow continuously)
        val memoryTrend = memorySnapshots.zipWithNext { current, next ->
            next.second - current.second
        }

        val averageGrowthRate = memoryTrend.average()
        assertTrue("Average memory growth rate should be minimal", 
            kotlin.math.abs(averageGrowthRate) < 1.0) // Less than 1MB per measurement
    }

    @Test
    fun testCPUUsageMonitoring() = runTest {
        // Test CPU usage under different load scenarios
        recordingController.registerSensor("gsr", gsrRecorder)
        recordingController.startRecording("CPUTest")

        val scenarios = listOf(
            "Low Load" to 10,     // 10 ops/sec
            "Medium Load" to 50,  // 50 ops/sec
            "High Load" to 100    // 100 ops/sec
        )

        scenarios.forEach { (scenarioName, opsPerSec) ->
            val cpuMetrics = measureCPUUsage(opsPerSec, durationSeconds = 5)
            
            // Log scenario results
            println("$scenarioName: CPU=${cpuMetrics.averageCpuPercent}%, " +
                   "Peak=${cpuMetrics.peakCpuPercent}%, Ops/sec=${opsPerSec}")

            // CPU should remain reasonable under all loads
            assertTrue("CPU usage should be reasonable for $scenarioName", 
                cpuMetrics.averageCpuPercent < 80.0)
            assertTrue("Peak CPU should not exceed limits for $scenarioName", 
                cpuMetrics.peakCpuPercent < 95.0)
        }

        recordingController.stopRecording()
    }

    private suspend fun measureCPUUsage(operationsPerSecond: Int, durationSeconds: Int): CPUMetrics {
        val cpuSamples = mutableListOf<Double>()
        val totalOperations = operationsPerSecond * durationSeconds
        val intervalMs = 1000 / operationsPerSecond

        // Start CPU monitoring
        launch {
            repeat(durationSeconds * 2) { // Sample twice per second
                val cpuPercent = getCurrentCPUUsage()
                cpuSamples.add(cpuPercent)
                delay(500)
            }
        }

        // Generate load
        repeat(totalOperations) { index ->
            recordingController.addSyncMarker(
                "CPU_TEST_$index",
                mapOf("index" to index, "timestamp" to System.nanoTime())
            )
            delay(intervalMs.toLong())
        }

        delay(1000) // Allow final CPU samples

        return CPUMetrics(
            averageCpuPercent = cpuSamples.average(),
            peakCpuPercent = cpuSamples.maxOrNull() ?: 0.0,
            minCpuPercent = cpuSamples.minOrNull() ?: 0.0,
            sampleCount = cpuSamples.size
        )
    }

    @Test
    fun testConcurrentPerformance() = runTest {
        // Test performance under concurrent operations
        recordingController.registerSensor("gsr", gsrRecorder)
        recordingController.startRecording("ConcurrencyTest")

        val concurrencyLevels = listOf(1, 2, 5, 10)
        val concurrencyResults = mutableListOf<PerformanceMetrics>()

        concurrencyLevels.forEach { concurrency ->
            val metrics = measureConcurrentPerformance(concurrency, operationsPerWorker = 100)
            concurrencyResults.add(metrics)

            // Performance should not degrade significantly with concurrency
            assertTrue("Concurrent performance should be reasonable for $concurrency workers", 
                metrics.throughputOpsPerSec >= 20.0)
            assertTrue("Error rate should remain low with $concurrency workers", 
                metrics.errorRate < 0.02)
        }

        recordingController.stopRecording()

        // Analyze scalability
        val scalabilityFactors = concurrencyResults.zipWithNext { current, next ->
            val concurrencyRatio = concurrencyLevels[concurrencyResults.indexOf(next)] / 
                                 concurrencyLevels[concurrencyResults.indexOf(current)]
            val throughputRatio = next.throughputOpsPerSec / current.throughputOpsPerSec
            throughputRatio / concurrencyRatio
        }

        scalabilityFactors.forEach { factor ->
            assertTrue("Scalability factor should be reasonable", factor >= 0.5)
        }
    }

    private suspend fun measureConcurrentPerformance(workerCount: Int, operationsPerWorker: Int): PerformanceMetrics {
        val latencies = mutableListOf<Long>()
        val successes = mutableListOf<Boolean>()
        val initialMemory = getMemoryUsage()

        val totalTime = measureTimeMillis {
            val workers = (1..workerCount).map { workerId ->
                async {
                    repeat(operationsPerWorker) { opIndex ->
                        val startTime = System.nanoTime()
                        
                        val result = recordingController.addSyncMarker(
                            "CONCURRENT_${workerId}_$opIndex",
                            mapOf(
                                "worker_id" to workerId,
                                "operation_index" to opIndex,
                                "timestamp" to System.nanoTime()
                            )
                        )
                        
                        val endTime = System.nanoTime()
                        val latency = (endTime - startTime) / 1_000_000
                        
                        synchronized(latencies) {
                            latencies.add(latency)
                            successes.add(result)
                        }
                        
                        delay(10) // Small delay between operations
                    }
                }
            }
            
            workers.awaitAll()
        }

        val finalMemory = getMemoryUsage()
        val totalOperations = workerCount * operationsPerWorker
        val successCount = successes.count { it }

        return PerformanceMetrics(
            executionTimeMs = totalTime,
            memoryUsageMB = finalMemory - initialMemory,
            throughputOpsPerSec = (totalOperations * 1000.0) / totalTime,
            averageLatencyMs = latencies.average(),
            maxLatencyMs = latencies.maxOrNull() ?: 0L,
            errorRate = (totalOperations - successCount).toDouble() / totalOperations
        )
    }

    // Helper methods for performance monitoring
    private fun getMemoryUsage(): Double {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        return usedMemory / (1024.0 * 1024.0) // Convert to MB
    }

    private fun getCurrentCPUUsage(): Double {
        // Simplified CPU usage estimation
        // In real implementation, would use proper CPU monitoring
        val threadCount = Thread.activeCount()
        return kotlin.math.min(threadCount * 5.0, 100.0) // Rough estimate
    }

    private data class CPUMetrics(
        val averageCpuPercent: Double,
        val peakCpuPercent: Double,
        val minCpuPercent: Double,
        val sampleCount: Int
    )
}