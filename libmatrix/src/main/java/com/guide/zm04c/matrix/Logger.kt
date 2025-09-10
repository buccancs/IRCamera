package com.guide.zm04c.matrix

import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Logger {

    fun f(
        tag: String,
        text: String,
    ) { // data
        val nowtime = Date()
        val needWriteFiel = logfile.format(nowtime)
        val needWriteMessage = myLogSdf.format(nowtime) + "    " + "    " + tag + "    " + text
        val dirsFile = File(MYLOG_PATH_SDCARD_DIR)
        if (!dirsFile.exists()) {
            dirsFile.mkdirs()
        }
        val file = File(dirsFile.toString(), needWriteFiel + MYLOGFILEName) // MYLOG_PATH_SDCARD_DIR
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: Exception) {
            }
        }
        try {
            val filerWriter = FileWriter(file, true) // data，data
            val bufWriter = BufferedWriter(filerWriter)
            bufWriter.write(needWriteMessage)
            bufWriter.newLine()
            bufWriter.close()
            filerWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
