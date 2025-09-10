package com.topdon.gsr.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

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
        transferJob.cancel()
        activeTransfers.clear()
        transferQueue.clear()
    }
}
