package com.topdon.tc001.gsr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.csl.irCamera.R

/**
 * Enhanced Session Detail Activity with modern UI patterns and accessibility
 * 
 * Provides detailed view of recording session with data analysis and export options.
 * Enhanced in Phase 11 with proper XML layout, accessibility support, and modern patterns.
 */
class SessionDetailActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_SESSION_ID = "session_id"

        fun startActivity(
            context: Context,
            sessionId: String,
        ) {
            val intent =
                Intent(context, SessionDetailActivity::class.java).apply {
                    putExtra(EXTRA_SESSION_ID, sessionId)
                }
            context.startActivity(intent)
        }
    }

    private lateinit var sessionIdText: TextView
    private lateinit var sessionDateText: TextView
    private lateinit var sessionDurationText: TextView
    private lateinit var gsrSamplesText: TextView
    private lateinit var videoFramesText: TextView
    private lateinit var syncQualityText: TextView
    private lateinit var viewDataButton: Button
    private lateinit var exportSessionButton: Button
    private lateinit var playbackVideoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_detail)

        initializeViews()
        setupActionBar()
        loadSessionData()
        setupButtonListeners()
    }

    private fun initializeViews() {
        sessionIdText = findViewById(R.id.session_id_text)
        sessionDateText = findViewById(R.id.session_date_text)
        sessionDurationText = findViewById(R.id.session_duration_text)
        gsrSamplesText = findViewById(R.id.gsr_samples_text)
        videoFramesText = findViewById(R.id.video_frames_text)
        syncQualityText = findViewById(R.id.sync_quality_text)
        viewDataButton = findViewById(R.id.view_data_button)
        exportSessionButton = findViewById(R.id.export_session_button)
        playbackVideoButton = findViewById(R.id.playback_video_button)
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.session_details_title)
        }
    }

    private fun loadSessionData() {
        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID) ?: "Unknown"
        
        // Display session information with accessibility support
        sessionIdText.text = getString(R.string.session_id_format, sessionId)
        sessionDateText.text = getString(R.string.session_date_placeholder)
        sessionDurationText.text = getString(R.string.session_duration_placeholder)
        gsrSamplesText.text = getString(R.string.gsr_samples_placeholder)
        videoFramesText.text = getString(R.string.video_frames_placeholder)
        syncQualityText.text = getString(R.string.sync_quality_placeholder)
    }

    private fun setupButtonListeners() {
        viewDataButton.setOnClickListener {
            // TODO: Implement data viewer navigation
            GSRDataViewActivity.startActivity(this)
        }

        exportSessionButton.setOnClickListener {
            val sessionId = intent.getStringExtra(EXTRA_SESSION_ID) ?: ""
            SessionExportActivity.startActivity(this, sessionId)
        }

        playbackVideoButton.setOnClickListener {
            // TODO: Implement video playback navigation
            GSRVideoPlayerActivity.startActivity(this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
