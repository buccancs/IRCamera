package com.topdon.gsr.network

import android.content.Context
import android.util.Log
import com.topdon.gsr.model.GSRSample
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Service for streaming GSR and other sensor data to PC Controller
 */
class DataStreamingService(
    private val context: Context,
    private val networkClient: NetworkClient
) {
    companion object {
        private const val TAG = "DataStreamingService"
        private const val MAX_QUEUE_SIZE = 1000
        private const val BATCH_SIZE = 10
        private const val STREAM_INTERVAL_MS = 100L
        private const val BATCH_TIMEOUT_MS = 1000L
        private const val RETRY_ATTEMPTS = 3
        private const val RETRY_DELAY_MS = 1000L
    }

    // State management
    private val isStreaming = AtomicBoolean(false)
    private val isConnected = AtomicBoolean(false)
    
    // Data queues  
    private val gsrDataQueue = ConcurrentLinkedQueue<GSRSample>()
    private val thermalQueue = ConcurrentLinkedQueue<ThermalSample>()
    private val videoMetadataQueue = ConcurrentLinkedQueue<VideoMetadata>()
    private val rgbMetadataQueue = ConcurrentLinkedQueue<RgbFrameMetadata>()
    private val thermalMetadataQueue = ConcurrentLinkedQueue<ThermalFrameMetadata>()
    
    // Session tracking
    private var currentSessionId: String? = null
    private var eventListener: StreamingEventListener? = null
    
    // Coroutine management
    private val streamingJob = SupervisorJob()
    private val streamingScope = CoroutineScope(Dispatchers.IO + streamingJob)
    private var batchingJob: Job? = null

    /**
     * Start streaming data to PC Controller
     */
    suspend fun startStreaming(sessionId: String): Boolean =
        withContext(Dispatchers.IO) {
            if (isStreaming.get()) {
                Log.w(TAG, "Data streaming already active")
                return@withContext false
            }

            if (!networkClient.isConnected()) {
                Log.w(TAG, "Cannot start streaming - not connected to PC Controller")
                return@withContext false
            }

            try {
                currentSessionId = sessionId
                isStreaming.set(true)
                isConnected.set(true)

                clearQueues()

                startBatchingProcess()

                // Notify PC Controller that streaming started
                val success = networkClient.startDataStreaming()
                if (success) {
                    eventListener?.onStreamingStarted(sessionId)
                    Log.i(TAG, "Data streaming started for session: $sessionId")
                    true
                } else {
                    stopStreaming()
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start data streaming", e)
                eventListener?.onStreamingError("Failed to start: ${e.message}")
                false
            }
        }

    /**
     * Stop data streaming
     */
    suspend fun stopStreaming() = withContext(Dispatchers.IO) {
        try {
            if (isStreaming.compareAndSet(true, false)) {
                Log.i(TAG, "Stopping data streaming...")
                
                // Cancel batching job
                batchingJob?.cancel()
                batchingJob = null
                
                // Clear queues
                gsrDataQueue.clear()
                thermalQueue.clear()
                videoMetadataQueue.clear()
                
                // Notify PC Controller
                val message = JSONObject().apply {
                    put("type", "stop_data_streaming")
                    put("timestamp", System.currentTimeMillis())
                }
                networkClient.sendMessage(message)
                
                currentSessionId = null
                eventListener?.onStreamingStopped()
                Log.i(TAG, "Data streaming stopped")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping data streaming", e)
        }
    }

    fun queueThermalSample(sample: ThermalSample) {
        if (!isStreaming.get()) return

        if (thermalQueue.size >= MAX_QUEUE_SIZE) {
            val dropped = minOf(BATCH_SIZE, thermalQueue.size / 2)
            repeat(dropped) { thermalQueue.poll() }
            eventListener?.onQueueFull("Thermal", dropped)
            Log.w(TAG, "Thermal queue full, dropped $dropped samples")
        }

        thermalQueue.offer(sample)
    }

    /**
     * Queue video metadata for streaming
     */
    fun queueVideoMetadata(metadata: VideoMetadata) {
        if (!isStreaming.get()) return

        if (videoMetadataQueue.size >= MAX_QUEUE_SIZE) {
            val dropped = minOf(BATCH_SIZE, videoMetadataQueue.size / 2)
            repeat(dropped) { videoMetadataQueue.poll() }
            eventListener?.onQueueFull("VideoMetadata", dropped)
            Log.w(TAG, "Video metadata queue full, dropped $dropped samples")
        }

        videoMetadataQueue.offer(metadata)
    }

    private fun startBatchingProcess() {
        batchingJob =
            streamingScope.launch {
                while (isStreaming.get() && isActive) {
                    try {

                        if (gsrDataQueue.size >= BATCH_SIZE) {
                            sendGSRBatch()
                        }

                        if (thermalQueue.size >= BATCH_SIZE) {
                            sendThermalBatch()
                        }

                        if (videoMetadataQueue.size >= BATCH_SIZE) {
                            sendVideoMetadataBatch()
                        }

                        // Timeout-based batching for partial batches
                        delay(BATCH_TIMEOUT_MS)
                    } catch (e: Exception) {
                        if (isActive) {
                            Log.e(TAG, "Error in batching process", e)
                            eventListener?.onStreamingError("Batching error: ${e.message}")
                            delay(1000) // Wait before retrying
                        }
                    }
                }
            }
    }

    private suspend fun sendGSRBatch() {
        val batch = mutableListOf<GSRSample>()
        repeat(minOf(BATCH_SIZE, gsrDataQueue.size)) {
            gsrDataQueue.poll()?.let { batch.add(it) }
        }

        if (batch.isNotEmpty()) {
            val batchData = createGSRBatchJson(batch)
            if (sendBatchWithRetry(batchData, "gsr")) {
                eventListener?.onBatchSent(batch.size, "GSR")
            }
        }
    }

    private suspend fun sendThermalBatch() {
        val batch = mutableListOf<ThermalSample>()
        repeat(minOf(BATCH_SIZE, thermalQueue.size)) {
            thermalQueue.poll()?.let { batch.add(it) }
        }

        if (batch.isNotEmpty()) {
            val batchData = createThermalBatchJson(batch)
            if (sendBatchWithRetry(batchData, "thermal")) {
                eventListener?.onBatchSent(batch.size, "Thermal")
            }
        }
    }

    private suspend fun sendVideoMetadataBatch() {
        val batch = mutableListOf<VideoMetadata>()
        repeat(minOf(BATCH_SIZE, videoMetadataQueue.size)) {
            videoMetadataQueue.poll()?.let { batch.add(it) }
        }

        if (batch.isNotEmpty()) {
            val batchData = createVideoMetadataBatchJson(batch)
            if (sendBatchWithRetry(batchData, "video_metadata")) {
                eventListener?.onBatchSent(batch.size, "VideoMetadata")
            }
        }
    }

    private suspend fun sendBatchWithRetry(
        batchData: JSONObject,
        dataType: String,
    ): Boolean {
        repeat(RETRY_ATTEMPTS) { attempt ->
            try {
                val success =
                    networkClient.sendMeasurementData(
                        currentSessionId ?: "unknown",
                        batchData,
                    )
                if (success) {
                    return true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Batch send attempt ${attempt + 1} failed for $dataType", e)
                if (attempt < RETRY_ATTEMPTS - 1) {
                    delay(RETRY_DELAY_MS)
                }
            }
        }

        Log.e(TAG, "Failed to send $dataType batch after $RETRY_ATTEMPTS attempts")
        eventListener?.onStreamingError("Failed to send $dataType batch")
        return false
    }

    private fun createGSRBatchJson(samples: List<GSRSample>): JSONObject {
        val samplesArray = JSONArray()
        samples.forEach { sample ->
            val sampleJson =
                JSONObject().apply {
                    put("timestamp", sample.timestamp)
                    put("utc_timestamp", sample.utcTimestamp)
                    put("sample_index", sample.sampleIndex)
                    put("conductance", sample.conductance)
                    put("resistance", sample.resistance)
                    put("raw_value", sample.sampleIndex)  // Use sampleIndex as raw value indicator
                    put("session_id", sample.sessionId)
                }
            samplesArray.put(sampleJson)
        }

        return JSONObject().apply {
            put("data_type", "gsr_batch")
            put("batch_size", samples.size)
            put("samples", samplesArray)
            put("synchronized_timestamp", networkClient.getSynchronizedTimestamp())
        }
    }

    private fun createThermalBatchJson(samples: List<ThermalSample>): JSONObject {
        val samplesArray = JSONArray()
        samples.forEach { sample ->
            val sampleJson =
                JSONObject().apply {
                    put("timestamp", sample.timestamp)
                    put("width", sample.width)
                    put("height", sample.height)
                    put("min_temp", sample.minTemp)
                    put("max_temp", sample.maxTemp)
                    put("session_id", sample.sessionId)
                    // data is ByteArray - could be encoded as base64 if needed
                    put("data_size", sample.data.size)
                }
            samplesArray.put(sampleJson)
        }

        return JSONObject().apply {
            put("data_type", "thermal_batch")
            put("batch_size", samples.size)
            put("samples", samplesArray)
            put("synchronized_timestamp", networkClient.getSynchronizedTimestamp())
        }
    }

    private fun createVideoMetadataBatchJson(samples: List<VideoMetadata>): JSONObject {
        val samplesArray = JSONArray()
        samples.forEach { sample ->
            val sampleJson =
                JSONObject().apply {
                    put("timestamp", sample.timestamp)
                    put("frame_id", sample.frameId)
                    put("format", sample.format)
                    put("quality", sample.quality)
                    put("session_id", sample.sessionId)
                }
            samplesArray.put(sampleJson)
        }

        return JSONObject().apply {
            put("data_type", "video_metadata_batch")
            put("batch_size", samples.size)
            put("samples", samplesArray)
            put("synchronized_timestamp", networkClient.getSynchronizedTimestamp())
        }
    }

    private suspend fun sendRemainingData() {
        // Send any remaining GSR data
        while (gsrDataQueue.isNotEmpty()) {
            sendGSRBatch()
        }

        // Send any remaining thermal data
        while (thermalQueue.isNotEmpty()) {
            sendThermalBatch()
        }

        // Send any remaining video metadata
        while (videoMetadataQueue.isNotEmpty()) {
            sendVideoMetadataBatch()
        }
    }

    private fun clearQueues() {
        gsrDataQueue.clear()
        thermalQueue.clear()
        videoMetadataQueue.clear()
    }

    /**
     * Get current queue sizes for monitoring
     */
    fun getQueueSizes(): Map<String, Int> {
        return mapOf(
            "gsr" to gsrDataQueue.size,
            "thermal" to thermalQueue.size,
            "video_metadata" to videoMetadataQueue.size,
        )
    }

    /**
     * Check if streaming is active
     */
    fun isStreamingActive(): Boolean = isStreaming.get()
    
    /**
     * Set event listener for streaming events
     */
    fun setEventListener(listener: StreamingEventListener?) {
        this.eventListener = listener
    }

    /**
     * Clean up resources
     */
    suspend fun cleanup() {

        stopStreaming()

        // Cancel all jobs after streaming is stopped
        streamingJob.cancel()
        clearQueues()
        eventListener = null
    }
}

/**
 * Interface for streaming event callbacks
 */
interface StreamingEventListener {
    fun onStreamingStarted()
    fun onStreamingStopped()
    fun onBatchSent(batchSize: Int)
    fun onStreamingError(error: String)
    fun onQueueFull(dataType: String)
}

/**
 * Data class for thermal sample data
 */
data class ThermalSample(
    val timestamp: Long,
    val width: Int,
    val height: Int,
    val data: ByteArray,
    val minTemp: Float,
    val maxTemp: Float,
    val sessionId: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ThermalSample

        if (timestamp != other.timestamp) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (!data.contentEquals(other.data)) return false
        if (minTemp != other.minTemp) return false
        if (maxTemp != other.maxTemp) return false
        if (sessionId != other.sessionId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + data.contentHashCode()
        result = 31 * result + minTemp.hashCode()
        result = 31 * result + maxTemp.hashCode()
        result = 31 * result + sessionId.hashCode()
        return result
    }
}

/**
 * Data class for video metadata
 */
data class VideoMetadata(
    val frameId: Long,
    val timestamp: Long,
    val format: String,
    val quality: Int,
    val sessionId: String
)

/**
 * Data class for RGB frame metadata
 */
data class RgbFrameMetadata(
    val frameId: Long,
    val timestamp: Long,
    val width: Int,
    val height: Int,
    val quality: Int
)

/**
 * Data class for thermal frame metadata
 */
data class ThermalFrameMetadata(
    val frameId: Long,
    val timestamp: Long,
    val width: Int,
    val height: Int,
    val minTemp: Float,
    val maxTemp: Float
)


