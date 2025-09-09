package com.topdon.tc001.gsr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.csl.irCamera.R

/**
 * Enhanced Session Export Activity with modern UI patterns and accessibility
 * 
 * Export session data in various research formats with comprehensive UI and progress tracking.
 * Enhanced in Phase 11 with proper XML layout, accessibility support, and export controls.
 */
class SessionExportActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_SESSION_ID = "session_id"

        fun startActivity(
            context: Context,
            sessionId: String,
        ) {
            val intent =
                Intent(context, SessionExportActivity::class.java).apply {
                    putExtra(EXTRA_SESSION_ID, sessionId)
                }
            context.startActivity(intent)
        }
    }

    private lateinit var exportCsvCheckbox: CheckBox
    private lateinit var exportJsonCheckbox: CheckBox
    private lateinit var exportMetadataCheckbox: CheckBox
    private lateinit var exportSessionIdText: TextView
    private lateinit var exportEstimatedSizeText: TextView
    private lateinit var startExportButton: Button
    private lateinit var exportProgressBar: ProgressBar
    private lateinit var exportStatusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_export)

        initializeViews()
        setupActionBar()
        loadSessionInfo()
        setupExportControls()
    }

    private fun initializeViews() {
        exportCsvCheckbox = findViewById(R.id.export_csv_checkbox)
        exportJsonCheckbox = findViewById(R.id.export_json_checkbox)
        exportMetadataCheckbox = findViewById(R.id.export_metadata_checkbox)
        exportSessionIdText = findViewById(R.id.export_session_id_text)
        exportEstimatedSizeText = findViewById(R.id.export_estimated_size_text)
        startExportButton = findViewById(R.id.start_export_button)
        exportProgressBar = findViewById(R.id.export_progress_bar)
        exportStatusText = findViewById(R.id.export_status_text)
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.export_session_title)
        }
    }

    private fun loadSessionInfo() {
        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID) ?: "Unknown"
        
        exportSessionIdText.text = getString(R.string.session_id_format, sessionId)
        exportEstimatedSizeText.text = getString(R.string.estimated_export_size_placeholder)
    }

    private fun setupExportControls() {
        // Update estimated size when format options change
        val formatChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            updateEstimatedSize()
        }
        
        exportCsvCheckbox.setOnCheckedChangeListener(formatChangeListener)
        exportJsonCheckbox.setOnCheckedChangeListener(formatChangeListener)
        exportMetadataCheckbox.setOnCheckedChangeListener(formatChangeListener)

        startExportButton.setOnClickListener {
            startExport()
        }

        // Initial size calculation
        updateEstimatedSize()
    }

    private fun updateEstimatedSize() {
        var estimatedSizeMB = 0.0f
        
        if (exportCsvCheckbox.isChecked) {
            estimatedSizeMB += 8.5f // Estimated CSV size
        }
        
        if (exportJsonCheckbox.isChecked) {
            estimatedSizeMB += 12.2f // Estimated JSON size (larger due to structure)
        }
        
        if (exportMetadataCheckbox.isChecked) {
            estimatedSizeMB += 0.5f // Metadata overhead
        }
        
        if (estimatedSizeMB == 0.0f) {
            exportEstimatedSizeText.text = getString(R.string.no_format_selected)
            startExportButton.isEnabled = false
        } else {
            exportEstimatedSizeText.text = getString(R.string.estimated_size_format, estimatedSizeMB)
            startExportButton.isEnabled = true
        }
    }

    private fun startExport() {
        // Show progress UI
        startExportButton.isEnabled = false
        exportProgressBar.visibility = View.VISIBLE
        exportStatusText.visibility = View.VISIBLE
        exportStatusText.text = getString(R.string.export_starting)
        
        // TODO: Implement actual export functionality
        // This is a placeholder for the export implementation
        exportStatusText.text = getString(R.string.export_implementation_pending)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
