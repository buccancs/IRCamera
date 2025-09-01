package com.topdon.tc001.camera.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.topdon.tc001.R

/**
 * Dialog for selecting which sensors to include in multi-modal recording
 * Allows any combination of Thermal (IR), RGB Camera, and GSR sensors
 */
class SensorSelectionDialog(
    context: Context,
    private val availableSensors: Set<SensorType>,
    private val onSensorsSelected: (Set<SensorType>) -> Unit
) : Dialog(context) {

    companion object {
        private const val TAG = "SensorSelectionDialog"
    }

    enum class SensorType(val displayName: String, val description: String) {
        THERMAL("Thermal Camera", "Infrared thermal imaging"),
        RGB("RGB Camera", "Color video recording"),
        GSR("GSR Sensor", "128Hz physiological data")
    }

    private lateinit var thermalCheckBox: CheckBox
    private lateinit var rgbCheckBox: CheckBox
    private lateinit var gsrCheckBox: CheckBox
    private lateinit var startButton: Button
    private lateinit var cancelButton: Button
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Select Recording Sensors")

        // Create layout
        val mainLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
        }

        // Title text
        val titleText = TextView(context).apply {
            text = "Choose sensors for synchronized recording:"
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            setPadding(0, 0, 0, 24)
        }
        mainLayout.addView(titleText)

        // Sensor checkboxes with descriptions
        createSensorCheckBox(SensorType.THERMAL).let {
            thermalCheckBox = it.first
            mainLayout.addView(it.second)
        }

        createSensorCheckBox(SensorType.RGB).let {
            rgbCheckBox = it.first  
            mainLayout.addView(it.second)
        }

        createSensorCheckBox(SensorType.GSR).let {
            gsrCheckBox = it.first
            mainLayout.addView(it.second)
        }

        // Status text
        statusText = TextView(context).apply {
            textSize = 12f
            setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 16)
        }
        updateStatusText()
        mainLayout.addView(statusText)

        // Buttons
        val buttonLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        cancelButton = Button(context).apply {
            text = "Cancel"
            setOnClickListener { 
                Log.d(TAG, "Sensor selection canceled")
                dismiss() 
            }
        }

        startButton = Button(context).apply {
            text = "Start Recording"
            isEnabled = false
            setOnClickListener { startRecording() }
        }

        buttonLayout.addView(cancelButton)
        val spacer = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(24, 0)
        }
        buttonLayout.addView(spacer)
        buttonLayout.addView(startButton)

        mainLayout.addView(buttonLayout)

        // Setup change listeners
        setupCheckBoxListeners()
        
        setContentView(mainLayout)

        // Dialog properties
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setCancelable(true)
        setCanceledOnTouchOutside(false)

        Log.i(TAG, "Sensor selection dialog created with available sensors: $availableSensors")
    }

    private fun createSensorCheckBox(sensorType: SensorType): Pair<CheckBox, LinearLayout> {
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 8, 0, 8)
        }

        val checkBox = CheckBox(context).apply {
            text = sensorType.displayName
            textSize = 14f
            isEnabled = availableSensors.contains(sensorType)
            
            // Default selections based on availability
            isChecked = when (sensorType) {
                SensorType.THERMAL -> availableSensors.contains(sensorType) // Always select thermal if available
                SensorType.RGB -> false // Let user choose
                SensorType.GSR -> false // Let user choose  
            }

            if (!isEnabled) {
                alpha = 0.5f
            }
        }

        val description = TextView(context).apply {
            text = if (availableSensors.contains(sensorType)) {
                sensorType.description
            } else {
                "${sensorType.description} (Not Available)"
            }
            textSize = 12f
            setTextColor(
                if (availableSensors.contains(sensorType)) {
                    ContextCompat.getColor(context, android.R.color.darker_gray)
                } else {
                    ContextCompat.getColor(context, android.R.color.tertiary_text_dark)
                }
            )
            setPadding(32, 0, 0, 0)
        }

        container.addView(checkBox)
        container.addView(description)

        return Pair(checkBox, container)
    }

    private fun setupCheckBoxListeners() {
        val listener = { _: CompoundButton, _: Boolean ->
            updateStatusText()
            startButton.isEnabled = getSelectedSensors().isNotEmpty()
        }

        thermalCheckBox.setOnCheckedChangeListener(listener)
        rgbCheckBox.setOnCheckedChangeListener(listener)
        gsrCheckBox.setOnCheckedChangeListener(listener)

        // Initial state
        startButton.isEnabled = getSelectedSensors().isNotEmpty()
    }

    private fun updateStatusText() {
        val selectedSensors = getSelectedSensors()
        statusText.text = when (selectedSensors.size) {
            0 -> "Select at least one sensor to start recording"
            1 -> "Single-modal recording: ${selectedSensors.first().displayName}"
            2 -> "Dual-modal recording: ${selectedSensors.map { it.displayName }.joinToString(" + ")}"
            3 -> "Tri-modal recording: All sensors synchronized"
            else -> "${selectedSensors.size} sensors selected"
        }
    }

    private fun getSelectedSensors(): Set<SensorType> {
        val selected = mutableSetOf<SensorType>()
        
        if (thermalCheckBox.isChecked) selected.add(SensorType.THERMAL)
        if (rgbCheckBox.isChecked) selected.add(SensorType.RGB) 
        if (gsrCheckBox.isChecked) selected.add(SensorType.GSR)
        
        return selected
    }

    private fun startRecording() {
        val selectedSensors = getSelectedSensors()
        if (selectedSensors.isEmpty()) {
            Toast.makeText(context, "Please select at least one sensor", Toast.LENGTH_SHORT).show()
            return
        }

        Log.i(TAG, "Starting recording with selected sensors: $selectedSensors")
        onSensorsSelected(selectedSensors)
        dismiss()
    }

    /**
     * Helper method to get available sensors for a context
     */
    companion object {
        fun detectAvailableSensors(context: Context): Set<SensorType> {
            val available = mutableSetOf<SensorType>()
            
            // Thermal camera is always available in this thermal camera app
            available.add(SensorType.THERMAL)
            
            // Check RGB camera availability
            if (context.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_CAMERA_ANY)) {
                available.add(SensorType.RGB)
            }
            
            // GSR sensor availability - for now assume always available with fallback to simulated data
            // In production, this would check for paired Shimmer devices
            available.add(SensorType.GSR)
            
            Log.d(TAG, "Detected available sensors: $available")
            return available
        }

        /**
         * Show sensor selection dialog with auto-detected available sensors
         */
        fun show(context: Context, onSensorsSelected: (Set<SensorType>) -> Unit) {
            val availableSensors = detectAvailableSensors(context)
            SensorSelectionDialog(context, availableSensors, onSensorsSelected).show()
        }
    }
}