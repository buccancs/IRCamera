package com.topdon.tc001.network

import android.content.Context
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.Socket
import java.net.ServerSocket

/**
 * Comprehensive unit tests for EnhancedNetworkClient
 * Tests network communication, JSON protocol, and error handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EnhancedNetworkClientTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var mockSocket: Socket

    private lateinit var networkClient: EnhancedNetworkClient
    private val testDispatcher = UnconfinedTestDispatcher()
    private val inputStream = ByteArrayOutputStream()
    private val outputStream = ByteArrayOutputStream()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Mock socket streams
        every { mockSocket.getInputStream() } returns ByteArrayInputStream(byteArrayOf())
        every { mockSocket.getOutputStream() } returns outputStream
        every { mockSocket.isConnected } returns true
        every { mockSocket.close() } just Runs

        networkClient = EnhancedNetworkClient(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun testInitialization() {
        assertNotNull("NetworkClient should be initialized", networkClient)
        assertFalse("Should not be connected initially", networkClient.isConnected())
    }

    @Test
    fun testSuccessfulConnection() = runTest {
        // Mock successful socket creation
        mockkConstructor(Socket::class)
        every { anyConstructed<Socket>().connect(any(), any()) } just Runs
        every { anyConstructed<Socket>().getInputStream() } returns ByteArrayInputStream("""{"type":"connection_ack","status":"ok"}""".toByteArray())
        every { anyConstructed<Socket>().getOutputStream() } returns outputStream

        val result = networkClient.connect("192.168.1.100", 8080)

        assertTrue("Connection should succeed", result)
        assertTrue("Should be connected after successful connection", networkClient.isConnected())
    }

    @Test
    fun testConnectionFailure() = runTest {
        // Mock failed socket creation
        mockkConstructor(Socket::class)
        every { anyConstructed<Socket>().connect(any(), any()) } throws Exception("Connection failed")

        val result = networkClient.connect("192.168.1.100", 8080)

        assertFalse("Connection should fail", result)
        assertFalse("Should not be connected after failed connection", networkClient.isConnected())
    }

    @Test
    fun testSendMessage() = runTest {
        // Setup connected state
        setupConnectedClient()

        val message = mapOf(
            "type" to "sync_marker",
            "id" to "TEST_SYNC",
            "timestamp" to System.currentTimeMillis()
        )

        val result = networkClient.sendMessage(message)

        assertTrue("Message should be sent successfully", result)

        // Verify JSON was written to output stream
        val sentData = outputStream.toString()
        assertTrue("Should contain message type", sentData.contains("sync_marker"))
        assertTrue("Should contain sync ID", sentData.contains("TEST_SYNC"))
    }

    @Test
    fun testSendMessageWhenNotConnected() = runTest {
        val message = mapOf("type" to "test")

        val result = networkClient.sendMessage(message)

        assertFalse("Message should fail when not connected", result)
    }

    @Test
    fun testReceiveMessage() = runTest {
        val testMessage = """{"type":"status_update","recording":true,"sensors":["rgb","thermal","gsr"]}"""
        setupConnectedClient(testMessage)

        val message = networkClient.receiveMessage()

        assertNotNull("Should receive message", message)
        message?.let {
            assertEquals("Should parse message type", "status_update", it["type"])
            assertEquals("Should parse recording status", true, it["recording"])
            assertTrue("Should parse sensor array", it["sensors"] is List<*>)
        }
    }

    @Test
    fun testReceiveInvalidMessage() = runTest {
        val invalidMessage = "invalid json message"
        setupConnectedClient(invalidMessage)

        val message = networkClient.receiveMessage()

        assertNull("Should return null for invalid JSON", message)
    }

    @Test
    fun testSyncWithPcController() = runTest {
        val syncResponse = """{"type":"sync_response","pc_time":${System.currentTimeMillis() * 1000000},"latency":1000000}"""
        setupConnectedClient(syncResponse)

        val result = networkClient.syncWithPcController()

        assertTrue("Sync should succeed", result)

        // Verify sync request was sent
        val sentData = outputStream.toString()
        assertTrue("Should send sync request", sentData.contains("sync_request"))
    }

    @Test
    fun testStartSession() = runTest {
        val sessionResponse = """{"type":"session_response","status":"started","session_id":"TEST_SESSION_123"}"""
        setupConnectedClient(sessionResponse)

        val result = networkClient.startSession("TestSession", "TestParticipant")

        assertTrue("Session should start successfully", result)

        // Verify session request was sent
        val sentData = outputStream.toString()
        assertTrue("Should send session request", sentData.contains("session_request"))
        assertTrue("Should include session name", sentData.contains("TestSession"))
    }

    @Test
    fun testStopSession() = runTest {
        val stopResponse = """{"type":"session_response","status":"stopped"}"""
        setupConnectedClient(stopResponse)

        val result = networkClient.stopSession()

        assertTrue("Session should stop successfully", result)

        // Verify stop request was sent
        val sentData = outputStream.toString()
        assertTrue("Should send stop session request", sentData.contains("stop_session"))
    }

    @Test
    fun testAddSyncMarker() = runTest {
        val syncResponse = """{"type":"sync_marker_response","status":"added"}"""
        setupConnectedClient(syncResponse)

        val metadata = mapOf("event" to "stimulus_presentation", "value" to 123)
        val result = networkClient.addSyncMarker("STIMULUS_1", metadata)

        assertTrue("Sync marker should be added successfully", result)

        // Verify sync marker was sent
        val sentData = outputStream.toString()
        assertTrue("Should send sync marker", sentData.contains("sync_marker"))
        assertTrue("Should include marker ID", sentData.contains("STIMULUS_1"))
    }

    @Test
    fun testFileTransfer() = runTest {
        val transferResponse = """{"type":"file_transfer_response","status":"ready","chunk_size":8192}"""
        setupConnectedClient(transferResponse)

        val testData = "test file content".toByteArray()
        val result = networkClient.sendFileChunk("test.csv", testData, 0, testData.size)

        assertTrue("File transfer should succeed", result)

        // Verify file transfer request was sent
        val sentData = outputStream.toString()
        assertTrue("Should send file transfer request", sentData.contains("file_transfer"))
    }

    @Test
    fun testConnectionHeartbeat() = runTest {
        val heartbeatResponse = """{"type":"heartbeat_response","status":"ok"}"""
        setupConnectedClient(heartbeatResponse)

        val result = networkClient.sendHeartbeat()

        assertTrue("Heartbeat should succeed", result)

        // Verify heartbeat was sent
        val sentData = outputStream.toString()
        assertTrue("Should send heartbeat", sentData.contains("heartbeat"))
    }

    @Test
    fun testDisconnection() = runTest {
        setupConnectedClient()

        networkClient.disconnect()

        assertFalse("Should not be connected after disconnect", networkClient.isConnected())
    }

    @Test
    fun testReconnection() = runTest {
        // Setup initial connection
        setupConnectedClient()
        networkClient.disconnect()

        // Mock successful reconnection
        mockkConstructor(Socket::class)
        every { anyConstructed<Socket>().connect(any(), any()) } just Runs
        every { anyConstructed<Socket>().getInputStream() } returns ByteArrayInputStream("""{"type":"connection_ack","status":"ok"}""".toByteArray())
        every { anyConstructed<Socket>().getOutputStream() } returns outputStream

        val result = networkClient.connect("192.168.1.100", 8080)

        assertTrue("Reconnection should succeed", result)
        assertTrue("Should be connected after reconnection", networkClient.isConnected())
    }

    @Test
    fun testNetworkTimeout() = runTest {
        // Mock socket timeout
        mockkConstructor(Socket::class)
        every { anyConstructed<Socket>().connect(any(), any()) } throws Exception("Connection timeout")

        val result = networkClient.connect("192.168.1.100", 8080, timeout = 1000)

        assertFalse("Connection should fail on timeout", result)
    }

    @Test
    fun testConcurrentOperations() = runTest {
        setupConnectedClient()

        // Send multiple messages concurrently
        val operations = (1..10).map { index ->
            async {
                networkClient.sendMessage(mapOf("type" to "test", "index" to index))
            }
        }

        val results = operations.awaitAll()

        // All operations should succeed (assuming no conflicts)
        assertTrue("All concurrent operations should succeed", results.all { it })
    }

    @Test
    fun testErrorRecovery() = runTest {
        setupConnectedClient()

        // Simulate network error
        every { mockSocket.getOutputStream() } throws Exception("Network error")

        val result = networkClient.sendMessage(mapOf("type" to "test"))

        assertFalse("Should handle network errors gracefully", result)
        // Client should detect disconnection
        assertFalse("Should be disconnected after error", networkClient.isConnected())
    }

    @Test
    fun testConnectionStatus() = runTest {
        setupConnectedClient()

        val status = networkClient.getConnectionStatus()

        assertNotNull("Connection status should be available", status)
        status?.let {
            assertTrue("Should be connected", it.isConnected)
            assertTrue("Should have valid connection time", it.connectionTime > 0)
            assertEquals("Should have correct host", "test_host", it.remoteHost)
        }
    }

    @Test
    fun testDataValidation() = runTest {
        setupConnectedClient()

        // Test with null message
        val nullResult = networkClient.sendMessage(null)
        assertFalse("Should reject null messages", nullResult)

        // Test with empty message
        val emptyResult = networkClient.sendMessage(emptyMap())
        assertFalse("Should reject empty messages", emptyResult)

        // Test with valid message
        val validResult = networkClient.sendMessage(mapOf("type" to "valid"))
        assertTrue("Should accept valid messages", validResult)
    }

    @Test
    fun testJsonSerialization() = runTest {
        setupConnectedClient()

        val complexMessage = mapOf(
            "type" to "complex_data",
            "timestamp" to System.currentTimeMillis(),
            "sensors" to listOf("rgb", "thermal", "gsr"),
            "metadata" to mapOf(
                "participant" to "P001",
                "session" to "S123",
                "quality" to 0.95
            )
        )

        val result = networkClient.sendMessage(complexMessage)

        assertTrue("Should serialize complex JSON successfully", result)

        val sentData = outputStream.toString()
        assertTrue("Should contain all message components", 
            sentData.contains("complex_data") && 
            sentData.contains("sensors") && 
            sentData.contains("metadata"))
    }

    private fun setupConnectedClient(responseMessage: String = """{"type":"ack","status":"ok"}""") {
        mockkConstructor(Socket::class)
        every { anyConstructed<Socket>().connect(any(), any()) } just Runs
        every { anyConstructed<Socket>().getInputStream() } returns ByteArrayInputStream(responseMessage.toByteArray())
        every { anyConstructed<Socket>().getOutputStream() } returns outputStream
        every { anyConstructed<Socket>().isConnected } returns true

        runBlocking {
            networkClient.connect("test_host", 8080)
        }
    }
}