package com.infisense.usbir

import android.content.Context
import android.content.pm.PackageManager
import java.io.File
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class CrashHandler : Thread.UncaughtExceptionHandler {

    companion object {
        val instance = CrashHandler()
    }

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    private var context: Context? = null

    override fun uncaughtException(t: Thread, e: Throwable) {
        defaultHandler?.uncaughtException(t, e)
    }

    private fun uploadErrorFileToServer(errorFile: File) {
        // Implementation for uploading error file to server
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun collectInfoToSDCard(pw: PrintWriter, ex: Throwable): File? {
        context?.let { ctx ->
            val pm = ctx.packageManager
            val pi = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
            
            val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            pw.print("time : ")
            pw.println(time)
            
            pw.print("versionCode : ")
            pw.println(pi.versionCode)
            
            pw.print("versionName : ")
            pw.println(pi.versionName)
        }
        return null
    }
}