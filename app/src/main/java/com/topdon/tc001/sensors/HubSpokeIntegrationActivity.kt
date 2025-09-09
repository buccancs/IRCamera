package com.topdon.tc001.sensors

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.topdon.tc001.controller.RecordingController
import com.topdon.tc001.controller.RecordingState
import com.topdon.tc001.network.EnhancedNetworkClient
import com.topdon.tc001.service.RecordingService
import com.topdon.tc001.utils.TimeManager
import com.csl.irCamera.R
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Hub-Spoke Integration Activity demonstrating the complete Multi-Modal Physiological Sensing Platform.
 * 
 * This activity provides a comprehensive interface for:
 * - PC Controller discovery and connection
 * - Multi-modal sensor recording coordination
 * - Real-time status monitoring and error handling
 * - Time synchronization visualization
 * - Background service management
 * 
 * Features:
 * - Complete Hub-Spoke system demonstration
 * - Real-time sensor status display
 * - Network connectivity and sync quality monitoring
 * - Coordinated recording session management
 * - Error recovery and status reporting
 * 
 * @author IRCamera Android Sensor Node (Spoke)
 */
class HubSpokeIntegrationActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HubSpokeIntegration"
        private const val DEFAULT_PC_CONTROLLER_PORT = 8080
    }

    // UI Components
    private lateinit var statusTextView: TextView
    private lateinit var connectionStatusTextView: TextView
    private lateinit var syncQualityTextView: TextView
    private lateinit var sensorStatusTextView: TextView
    private lateinit var pcAddressEditText: EditText
    private lateinit var connectButton: Button
    private lateinit var disconnectButton: Button
    private lateinit var startRecordingButton: Button
    private lateinit var stopRecordingButton: Button
    private lateinit var addSyncMarkerButton: Button
    private lateinit var sessionDirectoryEditText: EditText
    private lateinit var progressBar: ProgressBar

    // Core components
    private lateinit var recordingController: RecordingController
    private lateinit var networkClient: EnhancedNetworkClient
    private lateinit var timeManager: TimeManager
    
    // Service connection
    private var recordingService: RecordingService? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RecordingService.RecordingServiceBinder
            recordingService = binder.getService()
            recordingController = binder.getService().getRecordingController()
            isServiceBound = true
            
            Log.i(TAG, "Connected to RecordingService")
            setupRecordingMonitoring()
            updateUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            recordingService = null
            isServiceBound = false
            Log.i(TAG, "Disconnected from RecordingService")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub_spoke_integration)
        
        initializeViews()
        initializeComponents()
        setupClickListeners()
        bindToRecordingService()
        
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        
        lifecycleScope.launch {
            try {
                if (::networkClient.isInitialized) {
                    networkClient.cleanup()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during cleanup", e)
            }
        }
        
        if (isServiceBound) {
            unbindService(serviceConnection)
        }
    }

    private fun initializeViews() {
        statusTextView = findViewById(R.id.statusTextView)
        connectionStatusTextView = findViewById(R.id.connectionStatusTextView)
        syncQualityTextView = findViewById(R.id.syncQualityTextView)
        sensorStatusTextView = findViewById(R.id.sensorStatusTextView)
        pcAddressEditText = findViewById(R.id.pcAddressEditText)
        connectButton = findViewById(R.id.connectButton)
        disconnectButton = findViewById(R.id.disconnectButton)
        startRecordingButton = findViewById(R.id.startRecordingButton)
        stopRecordingButton = findViewById(R.id.stopRecordingButton)
        addSyncMarkerButton = findViewById(R.id.addSyncMarkerButton)
        sessionDirectoryEditText = findViewById(R.id.sessionDirectoryEditText)
        progressBar = findViewById(R.id.progressBar)
        
        // Set default session directory
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
        val defaultSessionDir = "${getExternalFilesDir(null)}/hub_spoke_sessions/session_$timestamp"
        sessionDirectoryEditText.setText(defaultSessionDir)
    }

    private fun initializeComponents() {
        timeManager = TimeManager.getInstance(this)
        
        // Initialize network client (will be connected to service later)
        recordingController = RecordingController(this, this)
        networkClient = EnhancedNetworkClient(this, recordingController)
    }

    private fun setupClickListeners() {
        connectButton.setOnClickListener {
            connectToPCController()
        }
        
        disconnectButton.setOnClickListener {
            disconnectFromPCController()
        }
        
        startRecordingButton.setOnClickListener {
            startCoordinatedRecording()
        }
        
        stopRecordingButton.setOnClickListener {
            stopCoordinatedRecording()
        }
        
        addSyncMarkerButton.setOnClickListener {
            addSyncMarker()
        }
    }

    private fun bindToRecordingService() {
        val intent = Intent(this, RecordingService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun connectToPCController() {
        val pcAddress = pcAddressEditText.text.toString().trim()
        if (pcAddress.isEmpty()) {
            Toast.makeText(this, "Please enter PC Controller IP address", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                statusTextView.text = "Connecting to PC Controller..."
                
                val connected = networkClient.connectToController(pcAddress, DEFAULT_PC_CONTROLLER_PORT)
                
                if (connected) {
                    statusTextView.text = "Connected to PC Controller successfully"
                    Toast.makeText(this@HubSpokeIntegrationActivity, "Connected successfully", Toast.LENGTH_SHORT).show()
                    setupNetworkMonitoring()
                } else {
                    statusTextView.text = "Failed to connect to PC Controller"
                    Toast.makeText(this@HubSpokeIntegrationActivity, "Connection failed", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Connection error", e)
                statusTextView.text = "Connection error: ${e.message}"
                Toast.makeText(this@HubSpokeIntegrationActivity, "Connection error", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
                updateUI()
            }
        }
    }

    private fun disconnectFromPCController() {
        lifecycleScope.launch {
            try {
                statusTextView.text = "Disconnecting from PC Controller..."
                networkClient.disconnect()
                statusTextView.text = "Disconnected from PC Controller"
                Toast.makeText(this@HubSpokeIntegrationActivity, "Disconnected", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Disconnect error", e)
                statusTextView.text = "Disconnect error: ${e.message}"
            } finally {
                updateUI()
            }
        }
    }

    private fun startCoordinatedRecording() {
        val sessionDirectory = sessionDirectoryEditText.text.toString().trim()
        if (sessionDirectory.isEmpty()) {
            Toast.makeText(this, "Please enter session directory", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                statusTextView.text = "Starting coordinated recording session..."
                
                // Create session directory
                val sessionDir = File(sessionDirectory)
                if (!sessionDir.exists()) {
                    sessionDir.mkdirs()
                }
                
                val success = if (networkClient.isConnected()) {
                    // Start coordinated session with PC Controller
                    networkClient.startCoordinatedSession(sessionDirectory)
                } else {
                    // Start local recording only
                    recordingController.startRecording(sessionDirectory)
                }
                
                if (success) {
                    statusTextView.text = "Coordinated recording session started"
                    Toast.makeText(this@HubSpokeIntegrationActivity, "Recording started", Toast.LENGTH_SHORT).show()
                } else {
                    statusTextView.text = "Failed to start recording session"
                    Toast.makeText(this@HubSpokeIntegrationActivity, "Recording failed to start", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Recording start error", e)
                statusTextView.text = "Recording start error: ${e.message}"
                Toast.makeText(this@HubSpokeIntegrationActivity, "Recording error", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
                updateUI()
            }
        }
    }

    private fun stopCoordinatedRecording() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                statusTextView.text = "Stopping coordinated recording session..."
                
                val success = if (networkClient.isConnected()) {
                    // Stop coordinated session
                    networkClient.stopCoordinatedSession()
                } else {
                    // Stop local recording only
                    recordingController.stopRecording()
                }
                
                if (success) {
                    statusTextView.text = "Coordinated recording session stopped"
                    Toast.makeText(this@HubSpokeIntegrationActivity, "Recording stopped", Toast.LENGTH_SHORT).show()
                } else {
                    statusTextView.text = "Failed to stop recording session"
                    Toast.makeText(this@HubSpokeIntegrationActivity, "Recording stop failed", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Recording stop error", e)
                statusTextView.text = "Recording stop error: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
                updateUI()
            }
        }
    }

    private fun addSyncMarker() {
        lifecycleScope.launch {
            try {
                val markerType = "manual_sync_${System.currentTimeMillis()}"
                val metadata = mapOf(
                    "source" to "HubSpokeIntegrationActivity",
                    "user_initiated" to "true"
                )
                
                if (networkClient.isConnected()) {
                    // Distribute sync marker through PC Controller
                    networkClient.distributeSyncMarker(markerType, metadata)
                } else {
                    // Add local sync marker only
                    val timestampNs = timeManager.getCurrentTimestampNs()
                    recordingController.addSyncMarker(markerType, timestampNs, metadata)
                }
                
                Toast.makeText(this@HubSpokeIntegrationActivity, "Sync marker added", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Sync marker added: $markerType")
                
            } catch (e: Exception) {
                Log.e(TAG, "Sync marker error", e)
                Toast.makeText(this@HubSpokeIntegrationActivity, "Sync marker failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecordingMonitoring() {
        if (!::recordingController.isInitialized) return
        
        // Monitor recording state
        recordingController.recordingStateFlow
            .onEach { state ->
                runOnUiThread {
                    when (state) {
                        RecordingState.STARTING -> statusTextView.text = "Starting sensors..."
                        RecordingState.RECORDING -> statusTextView.text = "Recording in progress"
                        RecordingState.STOPPING -> statusTextView.text = "Stopping sensors..."
                        RecordingState.STOPPED -> statusTextView.text = "Recording stopped"
                        RecordingState.ERROR -> statusTextView.text = "Recording error"
                    }
                    updateUI()
                }
            }
            .launchIn(lifecycleScope)
        
        // Monitor sensor status
        recordingController.sensorStatusFlow
            .onEach { statusList ->
                runOnUiThread {
                    val statusText = buildString {
                        statusList.forEach { status ->
                            append("${status.sensorType}: ")
                            append(if (status.isRecording) "Recording" else "Stopped")
                            append(" (${status.samplesRecorded} samples, ")
                            append("${String.format("%.1f", status.storageUsedMB)}MB)\n")
                        }
                    }
                    sensorStatusTextView.text = statusText.trim()
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setupNetworkMonitoring() {
        // Monitor connection state
        networkClient.connectionStateFlow
            .onEach { state ->
                runOnUiThread {
                    connectionStatusTextView.text = "Connection: $state"
                    updateUI()
                }
            }
            .launchIn(lifecycleScope)
        
        // Monitor time sync quality
        lifecycleScope.launch {
            while (networkClient.isConnected()) {
                val syncQuality = timeManager.getSyncQuality()
                runOnUiThread {
                    syncQualityTextView.text = buildString {
                        append("Sync: ${syncQuality.level}")
                        syncQuality.qualityMs?.let { append(" (${it}ms)") }
                        syncQuality.timeSinceSyncMs?.let { append(" - ${it / 1000}s ago") }
                    }
                }
                kotlinx.coroutines.delay(2000) // Update every 2 seconds
            }
        }
    }

    private fun updateUI() {
        val isConnected = ::networkClient.isInitialized && networkClient.isConnected()
        val isRecording = ::recordingController.isInitialized && recordingController.isRecording
        
        connectButton.isEnabled = !isConnected
        disconnectButton.isEnabled = isConnected
        startRecordingButton.isEnabled = !isRecording
        stopRecordingButton.isEnabled = isRecording
        addSyncMarkerButton.isEnabled = isRecording
        
        pcAddressEditText.isEnabled = !isConnected
        sessionDirectoryEditText.isEnabled = !isRecording
        
        if (!isConnected) {
            connectionStatusTextView.text = "Connection: Disconnected"
            syncQualityTextView.text = "Sync: Not Available"
        }
        
        if (!isRecording) {
            sensorStatusTextView.text = "Sensors: Idle"
        }
    }
}