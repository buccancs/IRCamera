package com.topdon.tc001

import android.util.Log
import android.view.WindowManager
// Note: PDFView library dependency not included in current build configuration
import android.widget.TextView
import com.csl.irCamera.R

import com.topdon.lib.core.ktbase.BaseActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * create by fylder on 2018/8/9
 **/
// Legacy ARouter route annotation - now using NavigationManager
class PdfActivity : BaseActivity() {
    // Note: Using TextView placeholder until PDFView library is integrated
    private val pdfView: TextView by lazy { findViewById<TextView>(R.id.pdf_view) }

    override fun initContentView() = R.layout.activity_pdf

    override fun initView() {
        // Note: PDF functionality requires PDFView library integration
        val pdfFileName = if (intent.getBooleanExtra("isTS001", false)) "TC001.pdf" else "TS004.pdf"
        pdfView.text = "PDF functionality temporarily unavailable - $pdfFileName will be displayed here when PDF library is available"
        
        // Note: PDF viewer method calls require PDFView library integration
        /*
        pdfView.fromAsset(pdfFileName)
        .enableSwipe(true) // allows to block changing pages using swipe
        .swipeHorizontal(false)
        .enableDoubletap(true)
        .defaultPage(0)
        .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
        .password(null)
        .scrollHandle(null)
        .enableAntialiasing(true) // improve rendering a little bit on low-res screens
        .spacing(0) // spacing between pages in dp. To define spacing color, set view background
        .load()
         */
    }

    override fun initData() {
        val pdfDir = getExternalFilesDir(PDF_DIRECTORY) 
            ?: throw IllegalStateException("Unable to access external files directory")
            
        val tc001File = File(pdfDir, "TC001.pdf")
        if (!tc001File.exists()) {
            try {
                copyBigDataToSD("TC001.pdf", tc001File)
            } catch (e: IOException) {
                Log.e("PdfActivity", "Failed to copy TC001.pdf", e)
                // Graceful fallback - could show error to user
            }
        }

        val tc004File = File(pdfDir, "TS004.pdf")
        if (!tc004File.exists()) {
            try {
                copyBigDataToSD("TS004.pdf", tc004File)
            } catch (e: IOException) {
                Log.e("PdfActivity", "Failed to copy TS004.pdf", e)
                // Graceful fallback - could show error to user
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    companion object {
        private const val BUFFER_SIZE = 8192 // Optimized buffer size for better I/O performance
        private const val PDF_DIRECTORY = "pdf"
    }

    /**
     * Copies assets files to external storage using proper resource management
     * 
     * @param assetsName Name of the asset file to copy
     * @param targetFile Target file location for the copied asset
     * @throws IOException if file operations fail
     */
    @Throws(IOException::class)
    private fun copyBigDataToSD(
        assetsName: String,
        targetFile: File,
    ) {
        // Ensure parent directory exists
        targetFile.parentFile?.mkdirs()
        
        // Use try-with-resources pattern for automatic resource management
        assets.open(assetsName).use { inputStream ->
            FileOutputStream(targetFile).use { outputStream ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.flush()
            }
        }
    }
}
