package com.topdon.lib.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

/**
 * Communication utilities - stub implementation for compilation
 */
class CommUtils {
    
    companion object {
        
        /**
         * Send file via intent
         */
        fun sendFile(context: Context, file: File, mimeType: String = "application/octet-stream") {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = mimeType
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            context.startActivity(Intent.createChooser(intent, "Send file"))
        }
        
        /**
         * Share text content
         */
        fun shareText(context: Context, text: String, title: String = "Share") {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, text)
            context.startActivity(Intent.createChooser(intent, title))
        }
        
        /**
         * Open file with external app
         */
        fun openFile(context: Context, file: File, mimeType: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), mimeType)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
        
        /**
         * Send email
         */
        fun sendEmail(context: Context, to: String, subject: String, body: String) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$to")
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, body)
            context.startActivity(Intent.createChooser(intent, "Send email"))
        }
        
        /**
         * Format byte size to human readable
         */
        fun formatFileSize(bytes: Long): String {
            val units = arrayOf("B", "KB", "MB", "GB")
            var size = bytes.toDouble()
            var unitIndex = 0
            
            while (size >= 1024 && unitIndex < units.size - 1) {
                size /= 1024
                unitIndex++
            }
            
            return String.format("%.1f %s", size, units[unitIndex])
        }
        
        /**
         * Get application name
         */
        fun getAppName(): String {
            return "IR Camera"
        }
    }
}