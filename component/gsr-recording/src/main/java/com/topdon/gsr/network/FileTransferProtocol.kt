package com.topdon.gsr.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Handles file transfer protocol for sending files to PC Controller
 */
class FileTransferProtocol(
    private val context: Context,
    private val networkClient: NetworkClient
) {
    companion object {
        private const val TAG = "FileTransferProtocol"
        private const val CHUNK_SIZE = 8192
        private const val MAX_CONCURRENT_TRANSFERS = 3
        private const val RETRY_ATTEMPTS = 3
    }

    // Transfer management
    private val activeTransfers = ConcurrentHashMap<String, TransferSession>()
    private val transferQueue = mutableListOf<TransferRequest>()
    private val completedTransfers = AtomicLong(0)
    
    // Coroutine scope
    private val transferScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Queue a file for transfer to PC Controller
     */
    suspend fun queueFileTransfer(
        filePath: String,
        priority: TransferPriority = TransferPriority.NORMAL,
        sessionId: String,
        metadata: Map<String, String> = emptyMap(),
    ): String =
        withContext(Dispatchers.IO) {
            val file = File(filePath)
            if (!file.exists()) {
                throw FileNotFoundException("File not found: $filePath")
            }

            val transferId = generateTransferId(filePath, sessionId)
            val request =
                TransferRequest(
                    transferId = transferId,
                    filePath = filePath,
                    fileSize = file.length(),
                    priority = priority,
                    sessionId = sessionId,
                    metadata = metadata,
                )

            synchronized(transferQueue) {
                transferQueue.add(request)
                transferQueue.sortByDescending { it.priority.weight }
            }

            Log.d(TAG, "Queued file transfer: $transferId, size: ${file.length()} bytes")
            processTransferQueue()
            transferId
        }

    private suspend fun startFileTransfer(request: TransferRequest): Unit =
        withContext(Dispatchers.IO) {
            val session =
                TransferSession(
                    request = request,
                    startTime = System.currentTimeMillis(),
                )

            activeTransfers[request.transferId] = session

            try {

                val resumeOffset = checkResumeCapability(request.transferId)
                session.resumeOffset = resumeOffset

                initializeTransfer(session)

                transferFileInChunks(session)

                // Verify transfer integrity
                verifyTransferIntegrity(session)

                Log.d(TAG, "Transfer completed: ${request.transferId}")
            } catch (e: Exception) {
                Log.e(TAG, "Transfer failed: ${request.transferId}", e)
                handleTransferError(session, e)
            } finally {
                activeTransfers.remove(request.transferId)

                transferScope.launch {
                    processTransferQueueAsync()
                }
            }
        }

    private suspend fun sendFileChunk(
        session: TransferSession,
        chunkIndex: Int,
        data: ByteArray,
    ) {
        val chunkMessage =
            JSONObject().apply {
                put("type", "file_chunk")
                put("transfer_id", session.request.transferId)
                put("chunk_index", chunkIndex)
                put("chunk_size", data.size)
            }

        // Send chunk metadata followed by binary data
        networkClient.sendMessage(chunkMessage)
        networkClient.sendBinaryData(data)

        // Wait for chunk acknowledgment
        val ack = networkClient.waitForResponse("chunk_ack", 5000L)
        if (ack.optString("transfer_id") != session.request.transferId ||
            ack.optInt("chunk_index") != chunkIndex
        ) {
            throw IOException("Invalid chunk acknowledgment")
        }
    }

    /**
     * Handle transfer error with cleanup and notification
     */
    private fun handleTransferError(session: TransferSession, error: Exception) {
        Log.e(TAG, "Transfer error for ${session.request.transferId}: ${error.message}", error)
        
        try {
            // Send error notification to PC Controller
            val errorMessage = JSONObject().apply {
                put("type", "file_transfer_error")
                put("transfer_id", session.request.transferId)
                put("error", error.message ?: "Unknown transfer error")
                put("timestamp", System.currentTimeMillis())
            }
            
            // Best effort to notify controller of error
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    networkClient.sendMessage(errorMessage)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to send error notification", e)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error while handling transfer error", e)
        }
    }

    suspend fun cancelTransfer(transferId: String): Boolean {
        val session = activeTransfers[transferId] ?: return false

        val cancelMessage =
            JSONObject().apply {
                put("type", "file_transfer_cancel")
                put("transfer_id", transferId)
            }

        networkClient.sendMessage(cancelMessage)
        activeTransfers.remove(transferId)

        Log.d(TAG, "Transfer cancelled: $transferId")
        return true
    }

    fun cleanup() {
        transferScope.cancel()
        activeTransfers.clear()
        transferQueue.clear()
    }
    
    /**
     * Process transfer queue
     */
    private fun processTransferQueue() {
        transferScope.launch {
            processTransferQueueAsync()
        }
    }
    
    /**
     * Process transfer queue asynchronously
     */
    private suspend fun processTransferQueueAsync() {
        synchronized(transferQueue) {
            if (transferQueue.isEmpty()) return
            
            // Start transfers up to max concurrent limit
            val activeCount = activeTransfers.size
            val availableSlots = MAX_CONCURRENT_TRANSFERS - activeCount
            
            if (availableSlots > 0) {
                val toStart = transferQueue.take(availableSlots)
                transferQueue.removeAll(toStart)
                
                toStart.forEach { request ->
                    transferScope.launch {
                        startFileTransfer(request)
                    }
                }
            }
        }
    }
    
    /**
     * Check resume capability for a transfer
     */
    private suspend fun checkResumeCapability(transferId: String): Long {
        return try {
            val checkMessage = JSONObject().apply {
                put("type", "file_transfer_check_resume")
                put("transfer_id", transferId)
            }
            
            networkClient.sendMessage(checkMessage)
            // In a real implementation, we'd wait for response
            // For now, return 0 (no resume)
            0L
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check resume capability", e)
            0L
        }
    }
    
    /**
     * Initialize transfer
     */
    private suspend fun initializeTransfer(session: TransferSession) {
        val initMessage = JSONObject().apply {
            put("type", "file_transfer_init")
            put("transfer_id", session.request.transferId)
            put("file_path", session.request.filePath)
            put("file_size", session.request.fileSize)
            put("session_id", session.request.sessionId)
            put("resume_offset", session.resumeOffset)
        }
        
        networkClient.sendMessage(initMessage)
        Log.d(TAG, "Transfer initialized: ${session.request.transferId}")
    }
    
    /**
     * Transfer file in chunks
     */
    private suspend fun transferFileInChunks(session: TransferSession) {
        val file = File(session.request.filePath)
        val buffer = ByteArray(CHUNK_SIZE)
        
        file.inputStream().use { inputStream ->
            // Skip to resume offset if needed
            inputStream.skip(session.resumeOffset)
            
            var chunkIndex = (session.resumeOffset / CHUNK_SIZE).toInt()
            var bytesRead: Int
            
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                val chunk = if (bytesRead < CHUNK_SIZE) {
                    buffer.copyOf(bytesRead)
                } else {
                    buffer
                }
                
                sendFileChunk(session, chunkIndex, chunk)
                session.bytesTransferred += bytesRead
                chunkIndex++
            }
        }
    }
    
    /**
     * Verify transfer integrity
     */
    private suspend fun verifyTransferIntegrity(session: TransferSession) {
        val file = File(session.request.filePath)
        val checksum = calculateFileChecksum(file)
        
        val verifyMessage = JSONObject().apply {
            put("type", "file_transfer_verify")
            put("transfer_id", session.request.transferId)
            put("checksum", checksum)
            put("file_size", file.length())
        }
        
        networkClient.sendMessage(verifyMessage)
        Log.d(TAG, "Transfer verification sent: ${session.request.transferId}")
    }
    
    /**
     * Calculate file checksum
     */
    private fun calculateFileChecksum(file: File): String {
        val digest = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(8192)
        
        file.inputStream().use { inputStream ->
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Generate transfer ID
     */
    private fun generateTransferId(filePath: String, sessionId: String): String {
        val input = "$filePath-$sessionId-${System.currentTimeMillis()}"
        return MessageDigest.getInstance("MD5").digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}

/**
 * Transfer priority levels
 */
enum class TransferPriority(val weight: Int) {
    LOW(0), NORMAL(1), HIGH(2), URGENT(3)
}

/**
 * Transfer request data class
 */
data class TransferRequest(
    val transferId: String,
    val filePath: String,
    val fileSize: Long,
    val priority: TransferPriority,
    val sessionId: String,
    val metadata: Map<String, String>,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Transfer session data class
 */
data class TransferSession(
    val request: TransferRequest,
    val startTime: Long,
    var bytesTransferred: Long = 0,
    var resumeOffset: Long = 0,
    val isActive: Boolean = true
)
