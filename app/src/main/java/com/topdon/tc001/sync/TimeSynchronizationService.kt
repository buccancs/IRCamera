package com.topdon.tc001.sync

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

/**
 * NTP-like time synchronization service for precise temporal alignment in the Multi-Modal Physiological Sensing Platform.
 * 
 * Implements custom time synchronization protocol between Android Sensor Node (Spoke) and PC Controller (Hub)
 * to achieve millisecond-accurate timestamp alignment across all sensor data streams.
 * 
 * Features:
 * - NTP-like handshake for clock offset calculation
 * - Continuous clock drift monitoring and correction
 * - Network latency compensation
 * - Precision timestamp generation with nanosecond resolution
 * - Real-time synchronization quality monitoring
 * 
 * @author IRCamera Android Sensor Node (Spoke)
 */
class TimeSynchronizationService {
    companion object {
        private const val TAG = "TimeSyncService"
        private const val SYNC_PORT = 12345
        private const val SYNC_TIMEOUT_MS = 5000
        private const val SYNC_PACKET_SIZE = 48 // NTP-like packet size
        private const val MAX_SYNC_ATTEMPTS = 3
        private const val SYNC_INTERVAL_MS = 60000L // 1 minute
        private const val DRIFT_CHECK_INTERVAL_MS = 10000L // 10 seconds
        private const val MAX_ACCEPTABLE_DRIFT_MS = 10L // 10ms max drift
        private const val SYNC_QUALITY_WINDOW = 10 // Number of samples for quality calculation
    }
    
    // Synchronization state
    private var _isInitialized = AtomicBoolean(false)
    private var _isSyncing = AtomicBoolean(false)
    private var clockOffset = AtomicLong(0) // Offset in nanoseconds (Android - PC)
    private var networkLatency = AtomicLong(0) // Round-trip time in nanoseconds
    private var lastSyncTime = AtomicLong(0)
    private var syncQuality = AtomicLong(100) // Quality percentage (0-100)
    
    // Network configuration
    private var hubAddress: InetAddress? = null
    private var syncSocket: DatagramSocket? = null
    
    // Coroutine management
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var syncJob: Job? = null
    private var driftMonitorJob: Job? = null
    
    // Data flows for monitoring
    private val _syncStatusFlow = MutableSharedFlow<SyncStatus>()
    val syncStatusFlow: SharedFlow<SyncStatus> = _syncStatusFlow.asSharedFlow()
    
    private val _syncQualityFlow = MutableSharedFlow<SyncQuality>()
    val syncQualityFlow: SharedFlow<SyncQuality> = _syncQualityFlow.asSharedFlow()
    
    // Quality tracking
    private val recentSyncDeltas = mutableListOf<Long>()
    
    /**
     * Initialize time synchronization with PC Controller (Hub)
     */
    suspend fun initialize(hubIpAddress: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Initializing time synchronization with hub: $hubIpAddress")
            
            // Resolve hub address
            hubAddress = InetAddress.getByName(hubIpAddress)
            
            // Create UDP socket for sync communication
            syncSocket = DatagramSocket()
            syncSocket?.soTimeout = SYNC_TIMEOUT_MS
            
            // Perform initial synchronization
            val initialSyncSuccess = performInitialSync()
            
            if (initialSyncSuccess) {
                _isInitialized.set(true)
                startContinuousSync()
                startDriftMonitoring()
                
                Log.i(TAG, "Time synchronization initialized successfully")
                Log.i(TAG, "Initial clock offset: ${clockOffset.get() / 1_000_000}ms")
                Log.i(TAG, "Network latency: ${networkLatency.get() / 1_000_000}ms")
                
                emitSyncStatus(SyncStatusType.SYNCHRONIZED, "Initial synchronization completed")
                true
            } else {
                Log.e(TAG, "Failed to perform initial synchronization")
                emitSyncStatus(SyncStatusType.FAILED, "Initial synchronization failed")
                false
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize time synchronization", e)
            emitSyncStatus(SyncStatusType.ERROR, "Initialization error: ${e.message}")
            false
        }
    }
    
    /**
     * Perform initial time synchronization handshake
     */
    private suspend fun performInitialSync(): Boolean = withContext(Dispatchers.IO) {
        var bestOffset = Long.MAX_VALUE
        var bestLatency = Long.MAX_VALUE
        var successfulSyncs = 0
        
        repeat(MAX_SYNC_ATTEMPTS) { attempt ->
            try {
                Log.d(TAG, "Sync attempt ${attempt + 1}/$MAX_SYNC_ATTEMPTS")
                
                val syncResult = performSyncHandshake()
                
                if (syncResult != null) {
                    successfulSyncs++
                    
                    // Use the sync with lowest latency for better accuracy
                    if (syncResult.latency < bestLatency) {
                        bestOffset = syncResult.offset
                        bestLatency = syncResult.latency
                        
                        Log.d(TAG, "New best sync: offset=${bestOffset / 1_000_000}ms, latency=${bestLatency / 1_000_000}ms")
                    }
                }
                
                // Small delay between attempts
                delay(100)
                
            } catch (e: Exception) {
                Log.w(TAG, "Sync attempt ${attempt + 1} failed", e)
            }
        }
        
        if (successfulSyncs > 0) {
            clockOffset.set(bestOffset)
            networkLatency.set(bestLatency)
            lastSyncTime.set(System.nanoTime())
            
            Log.i(TAG, "Initial sync completed: $successfulSyncs/$MAX_SYNC_ATTEMPTS attempts successful")
            return@withContext true
        }
        
        return@withContext false
    }
    
    /**
     * Perform single NTP-like synchronization handshake
     */
    private suspend fun performSyncHandshake(): SyncResult? = withContext(Dispatchers.IO) {
        return@withContext try {
            val socket = syncSocket ?: return@withContext null
            val hubAddr = hubAddress ?: return@withContext null
            
            // Create sync request packet (NTP-like format)
            val requestPacket = createSyncRequestPacket()
            val request = DatagramPacket(requestPacket, requestPacket.size, hubAddr, SYNC_PORT)
            
            // Record send time with high precision
            val t1 = System.nanoTime() // Client send time
            socket.send(request)
            
            // Receive response
            val responseBuffer = ByteArray(SYNC_PACKET_SIZE)
            val response = DatagramPacket(responseBuffer, responseBuffer.size)
            socket.receive(response)
            val t4 = System.nanoTime() // Client receive time
            
            // Parse response packet
            val syncResponse = parseSyncResponsePacket(responseBuffer)
            if (syncResponse != null) {
                val t2 = syncResponse.serverReceiveTime // Server receive time
                val t3 = syncResponse.serverSendTime     // Server send time
                
                // Calculate clock offset and network delay using NTP algorithm
                val networkDelay = ((t4 - t1) - (t3 - t2)) / 2
                val clockOffsetCalc = ((t2 - t1) + (t3 - t4)) / 2
                
                Log.d(TAG, "Sync calculation: t1=$t1, t2=$t2, t3=$t3, t4=$t4")
                Log.d(TAG, "Network delay: ${networkDelay / 1_000_000}ms, Offset: ${clockOffsetCalc / 1_000_000}ms")
                
                SyncResult(
                    offset = clockOffsetCalc,
                    latency = t4 - t1,
                    networkDelay = networkDelay,
                    quality = calculateSyncQuality(networkDelay, clockOffsetCalc)
                )
            } else {
                Log.w(TAG, "Failed to parse sync response packet")
                null
            }
            
        } catch (e: Exception) {
            Log.w(TAG, "Sync handshake failed", e)
            null
        }
    }
    
    /**
     * Create NTP-like sync request packet
     */
    private fun createSyncRequestPacket(): ByteArray {
        val buffer = ByteBuffer.allocate(SYNC_PACKET_SIZE)
        
        // NTP-like header
        buffer.put(0x1B.toByte()) // Version 3, Mode 3 (client)
        buffer.put(0x00.toByte()) // Stratum
        buffer.put(0x06.toByte()) // Poll interval
        buffer.put(0xEC.toByte()) // Precision
        
        // Root delay and dispersion (4 bytes each)
        buffer.putInt(0)
        buffer.putInt(0)
        
        // Reference identifier (4 bytes)
        buffer.putInt(0)
        
        // Timestamps (8 bytes each)
        buffer.putLong(0) // Reference timestamp
        buffer.putLong(0) // Origin timestamp
        buffer.putLong(0) // Receive timestamp
        buffer.putLong(System.nanoTime()) // Transmit timestamp (t1)
        
        return buffer.array()
    }
    
    /**
     * Parse sync response packet from PC Controller
     */
    private fun parseSyncResponsePacket(packet: ByteArray): SyncResponse? {
        return try {
            val buffer = ByteBuffer.wrap(packet)
            
            // Skip header (4 bytes)
            buffer.position(4)
            
            // Skip root delay, dispersion, reference ID (12 bytes)
            buffer.position(16)
            
            // Skip reference timestamp (8 bytes)
            buffer.position(24)
            
            val originTimestamp = buffer.long    // Our original t1
            val receiveTimestamp = buffer.long   // Server t2
            val transmitTimestamp = buffer.long  // Server t3
            
            SyncResponse(
                serverReceiveTime = receiveTimestamp,
                serverSendTime = transmitTimestamp,
                clientOriginTime = originTimestamp
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse sync response", e)
            null
        }
    }
    
    /**
     * Calculate synchronization quality based on network conditions
     */
    private fun calculateSyncQuality(networkDelay: Long, offset: Long): Int {
        // Quality decreases with higher network delay and offset variability
        val delayMs = networkDelay / 1_000_000
        val offsetMs = abs(offset) / 1_000_000
        
        val quality = when {
            delayMs < 1 && offsetMs < 1 -> 100      // Excellent
            delayMs < 5 && offsetMs < 5 -> 90       // Very good
            delayMs < 10 && offsetMs < 10 -> 80     // Good
            delayMs < 20 && offsetMs < 20 -> 70     // Fair
            delayMs < 50 && offsetMs < 50 -> 60     // Poor
            else -> 50                               // Very poor
        }
        
        return quality.coerceIn(0, 100)
    }
    
    /**
     * Start continuous synchronization monitoring
     */
    private fun startContinuousSync() {
        syncJob = syncScope.launch {
            while (isActive && _isInitialized.get()) {
                try {
                    delay(SYNC_INTERVAL_MS)
                    
                    if (!_isSyncing.compareAndSet(false, true)) {
                        continue // Skip if already syncing
                    }
                    
                    Log.d(TAG, "Performing periodic sync")
                    val syncResult = performSyncHandshake()
                    
                    if (syncResult != null) {
                        updateClockOffset(syncResult)
                        emitSyncStatus(SyncStatusType.SYNCHRONIZED, "Periodic sync completed")
                    } else {
                        emitSyncStatus(SyncStatusType.DEGRADED, "Periodic sync failed")
                    }
                    
                    _isSyncing.set(false)
                    
                } catch (e: Exception) {
                    Log.w(TAG, "Continuous sync error", e)
                    _isSyncing.set(false)
                }
            }
        }
    }
    
    /**
     * Start drift monitoring to detect clock drift
     */
    private fun startDriftMonitoring() {
        driftMonitorJob = syncScope.launch {
            while (isActive && _isInitialized.get()) {
                try {
                    delay(DRIFT_CHECK_INTERVAL_MS)
                    
                    val timeSinceLastSync = (System.nanoTime() - lastSyncTime.get()) / 1_000_000
                    
                    if (timeSinceLastSync > SYNC_INTERVAL_MS * 2) {
                        Log.w(TAG, "Sync timeout detected: ${timeSinceLastSync}ms since last sync")
                        emitSyncStatus(SyncStatusType.TIMEOUT, "No sync for ${timeSinceLastSync}ms")
                    }
                    
                    updateSyncQuality()
                    
                } catch (e: Exception) {
                    Log.w(TAG, "Drift monitoring error", e)
                }
            }
        }
    }
    
    /**
     * Update clock offset with new sync result
     */
    private fun updateClockOffset(syncResult: SyncResult) {
        val previousOffset = clockOffset.get()
        val offsetDelta = abs(syncResult.offset - previousOffset)
        
        // Update offset (using simple average for now, could use more sophisticated filtering)
        val newOffset = (previousOffset + syncResult.offset) / 2
        clockOffset.set(newOffset)
        networkLatency.set(syncResult.latency)
        lastSyncTime.set(System.nanoTime())
        
        // Track offset changes for quality calculation
        recentSyncDeltas.add(offsetDelta)
        if (recentSyncDeltas.size > SYNC_QUALITY_WINDOW) {
            recentSyncDeltas.removeAt(0)
        }
        
        Log.d(TAG, "Clock offset updated: ${newOffset / 1_000_000}ms (delta: ${offsetDelta / 1_000_000}ms)")
        
        // Check for excessive drift
        if (offsetDelta > MAX_ACCEPTABLE_DRIFT_MS * 1_000_000) {
            Log.w(TAG, "Excessive clock drift detected: ${offsetDelta / 1_000_000}ms")
            emitSyncStatus(SyncStatusType.DRIFT_WARNING, "Clock drift: ${offsetDelta / 1_000_000}ms")
        }
    }
    
    /**
     * Update synchronization quality metrics
     */
    private fun updateSyncQuality() {
        if (recentSyncDeltas.isEmpty()) {
            return
        }
        
        val avgDelta = recentSyncDeltas.average()
        val maxDelta = recentSyncDeltas.maxOrNull() ?: 0L
        
        val qualityScore = when {
            avgDelta < 1_000_000 && maxDelta < 5_000_000 -> 100  // Excellent stability
            avgDelta < 5_000_000 && maxDelta < 10_000_000 -> 90  // Good stability
            avgDelta < 10_000_000 && maxDelta < 20_000_000 -> 80 // Fair stability
            else -> 70 // Poor stability
        }
        
        syncQuality.set(qualityScore.toLong())
        
        val quality = SyncQuality(
            qualityPercentage = qualityScore,
            averageDriftMs = avgDelta / 1_000_000,
            maxDriftMs = maxDelta / 1_000_000,
            networkLatencyMs = networkLatency.get() / 1_000_000,
            timeSinceLastSyncMs = (System.nanoTime() - lastSyncTime.get()) / 1_000_000
        )
        
        syncScope.launch {
            _syncQualityFlow.emit(quality)
        }
    }
    
    /**
     * Get synchronized timestamp (local time + offset)
     */
    fun getSynchronizedTimestamp(): Long {
        return System.nanoTime() + clockOffset.get()
    }
    
    /**
     * Get local timestamp
     */
    fun getLocalTimestamp(): Long {
        return System.nanoTime()
    }
    
    /**
     * Get clock offset in nanoseconds
     */
    fun getClockOffset(): Long = clockOffset.get()
    
    /**
     * Get clock offset in milliseconds
     */
    fun getClockOffsetMs(): Double = clockOffset.get() / 1_000_000.0
    
    /**
     * Get network latency in milliseconds
     */
    fun getNetworkLatencyMs(): Double = networkLatency.get() / 1_000_000.0
    
    /**
     * Check if synchronization is active and healthy
     */
    fun isSynchronized(): Boolean {
        if (!_isInitialized.get()) return false
        
        val timeSinceLastSync = (System.nanoTime() - lastSyncTime.get()) / 1_000_000
        return timeSinceLastSync < SYNC_INTERVAL_MS * 2
    }
    
    /**
     * Get current synchronization quality (0-100)
     */
    fun getSyncQuality(): Int = syncQuality.get().toInt()
    
    /**
     * Shutdown time synchronization service
     */
    suspend fun shutdown() {
        Log.i(TAG, "Shutting down time synchronization service")
        
        _isInitialized.set(false)
        
        syncJob?.cancel()
        driftMonitorJob?.cancel()
        
        syncSocket?.close()
        syncSocket = null
        
        syncScope.cancel()
        
        Log.i(TAG, "Time synchronization service shut down")
    }
    
    /**
     * Emit synchronization status update
     */
    private suspend fun emitSyncStatus(statusType: SyncStatusType, message: String) {
        val status = SyncStatus(
            statusType = statusType,
            message = message,
            clockOffsetMs = getClockOffsetMs(),
            networkLatencyMs = getNetworkLatencyMs(),
            qualityPercentage = getSyncQuality(),
            timestampNs = System.nanoTime()
        )
        
        _syncStatusFlow.emit(status)
    }
    
    /**
     * Force immediate synchronization
     */
    suspend fun forceSynchronization(): Boolean {
        if (!_isInitialized.get()) {
            Log.w(TAG, "Cannot force sync - not initialized")
            return false
        }
        
        if (_isSyncing.get()) {
            Log.w(TAG, "Sync already in progress")
            return false
        }
        
        return try {
            _isSyncing.set(true)
            val syncResult = performSyncHandshake()
            
            if (syncResult != null) {
                updateClockOffset(syncResult)
                emitSyncStatus(SyncStatusType.SYNCHRONIZED, "Manual sync completed")
                true
            } else {
                emitSyncStatus(SyncStatusType.FAILED, "Manual sync failed")
                false
            }
        } finally {
            _isSyncing.set(false)
        }
    }
}

/**
 * Synchronization result from a single handshake
 */
data class SyncResult(
    val offset: Long,        // Clock offset in nanoseconds
    val latency: Long,       // Round-trip time in nanoseconds
    val networkDelay: Long,  // Network delay in nanoseconds
    val quality: Int         // Quality score 0-100
)

/**
 * Sync response packet data
 */
data class SyncResponse(
    val serverReceiveTime: Long,
    val serverSendTime: Long,
    val clientOriginTime: Long
)

/**
 * Synchronization status information
 */
data class SyncStatus(
    val statusType: SyncStatusType,
    val message: String,
    val clockOffsetMs: Double,
    val networkLatencyMs: Double,
    val qualityPercentage: Int,
    val timestampNs: Long
)

/**
 * Synchronization quality metrics
 */
data class SyncQuality(
    val qualityPercentage: Int,
    val averageDriftMs: Double,
    val maxDriftMs: Double,
    val networkLatencyMs: Double,
    val timeSinceLastSyncMs: Double
)

/**
 * Synchronization status types
 */
enum class SyncStatusType {
    INITIALIZING,
    SYNCHRONIZED,
    DEGRADED,
    FAILED,
    TIMEOUT,
    DRIFT_WARNING,
    ERROR
}