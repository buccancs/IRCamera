package com.infisense.usbir;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
* @author: CaiSongL
 * @date: 2023/5/24 9:47
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

 private static final String TAG = "CrashHandler";
 private Thread.UncaughtExceptionHandler mDefaultHandler;
 private static CrashHandler crashHandler = new CrashHandler();

 private Context mContext;
 /* Comment removed (contained Chinese characters) */
 private File logFile ;

 private CrashHandler() {

 }

 public static CrashHandler getInstance() {
 if (crashHandler == null) {
 synchronized (CrashHandler.class) {
 if (crashHandler == null) {
 crashHandler = new CrashHandler();
 }
 }
 }
 return crashHandler;
 }

 public void init(Context context) {
 mContext = context;
 logFile = new File(mContext.getCacheDir(),"crashLog.trace");
 mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
 // Comment removed (contained Chinese characters)
 Thread.setDefaultUncaughtExceptionHandler(this);
 }

 @Override
 public void uncaughtException(Thread thread, Throwable ex) {
 // Comment removed (contained Chinese characters)
 ex.printStackTrace();
 // Comment removed (contained Chinese characters)
 if (!handlelException(ex) && mDefaultHandler != null) {
 // Comment removed (contained Chinese characters)
 mDefaultHandler.uncaughtException(thread, ex);
 } else {
 try {
 Thread.sleep(3 * 1000);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }
 try {
 // Comment removed (contained Chinese characters)
 upLoadErrorFileToServer(logFile);
 } catch (Exception e) {
 e.printStackTrace();
 }
// Intent intent = new Intent(mContext, SplashActivity.class);
// Comment removed (contained Chinese characters)
// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
// mContext.startActivity(intent);
// Comment removed (contained Chinese characters)
// Timer timer = new Timer();
// timer.schedule(new TimerTask() {
//
// @Override
// public void run() {
// Process.killProcess(Process.myPid());
// }
// }, 2 * 1000);

 }
 }

 private boolean handlelException(Throwable ex) {
 if (ex == null) {
 return false;
 }

 // Comment removed (contained Chinese characters)
 new Thread() {
 @Override
 public void run() {
 Looper.prepare();
// Comment removed (contained Chinese characters)
 Toast.makeText(mContext, "，", Toast.LENGTH_LONG)
 .show();
 Looper.loop();
 }
 }.start();

 PrintWriter pw = null;
 try {
 if (!logFile.exists()) {
 logFile.createNewFile();
 }
 pw = new PrintWriter(logFile);
 // Comment removed (contained Chinese characters)
 logFile = collectInfoToSDCard(pw, ex);
 pw.close();
 } catch (Exception e) {
 e.printStackTrace();
 }

 return true;
 }

 /**
 * Comment removed (contained Chinese characters)
 */
 private void upLoadErrorFileToServer(File errorFile) {

 }

 /**
 * Comment removed (contained Chinese characters)
 *
 */
 private File collectInfoToSDCard(PrintWriter pw, Throwable ex)
 throws PackageManager.NameNotFoundException {

 PackageManager pm = mContext.getPackageManager();
 PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);
 // Comment removed (contained Chinese characters)
 String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
 pw.print("time : ");
 pw.println(time);
 // Comment removed (contained Chinese characters)
 pw.print("versionCode : ");
 pw.println(pi.versionCode);
 // Comment removed (contained Chinese characters)
 pw.print("versionName : ");
 pw.println(pi.versionName);
 try {
 /* Comment removed (contained Chinese characters) */
 Field[] Fields = Build.class.getDeclaredFields();
 for (Field field : Fields) {
 field.setAccessible(true);
 pw.print(field.getName() + " : ");
 pw.println(field.get(null).toString());
 }
 } catch (Exception e) {
 Log.i(TAG, "an error occured when collect crash info" + e);
 }

 // Comment removed (contained Chinese characters)
 ex.printStackTrace(pw);
 return logFile;
 }
}