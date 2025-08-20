package com.topdon.commons.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.elvishew.xlog.XLog;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Consolidated app utility class combining functionality from multiple app utilities
 * Replaces com.energy.commoncomponent.utils.AppUtils and com.topdon.lib.core.utils.AppUtil
 */
public class AppUtils {

    private AppUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Check if an app is installed
     * @param context application context
     * @param packageName package name to check
     * @return true if app is installed
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> listPackageInfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < listPackageInfo.size(); i++) {
            if (listPackageInfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Open an app by package name
     * @param context application context
     * @param packageName package name to open
     * @throws PackageManager.NameNotFoundException if app not found
     */
    public static void openApp(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);
        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
        if (apps == null || apps.size() <= 0) {
            return;
        }
        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String name = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(name, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /**
     * Install APK file
     * @param context application context
     * @param apkPath APK file path
     */
    public static void installApp(Context context, File apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkPath);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkPath), "application/vnd.android.package-archive");
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            XLog.e("Failed to install app", e);
        }
    }

    /**
     * Check if a process is running
     * @param context application context
     * @param serviceName process name to check
     * @return true if process is running
     */
    public static boolean isProcessRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.processName.equals(serviceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if a service is running
     * @param context application context
     * @param serviceName service name to check
     * @return true if service is running
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(Integer.MAX_VALUE);
        if (runningServices != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
                if (serviceInfo.service.getClassName().equals(serviceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get app version name
     * @param context application context
     * @return version name
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get app version code
     * @param context application context
     * @return version code
     */
    public static float getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Run method by reflection
     * @param className class name to invoke
     * @param methodName method name to invoke
     */
    public static void runMethodByReflectClass(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName);
            method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run method by reflection with parameters
     * @param className class name to invoke
     * @param methodName method name to invoke
     * @param paramTypes parameter types
     * @param params parameters
     */
    public static void runMethodByReflectClass(String className, String methodName, Class<?>[] paramTypes, Object[] params) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, paramTypes);
            method.invoke(null, params);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get package info
     * @param context application context
     * @param packageName package name
     * @return PackageInfo or null if not found
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get current app package info
     * @param context application context
     * @return PackageInfo or null if not found
     */
    public static PackageInfo getCurrentPackageInfo(Context context) {
        return getPackageInfo(context, context.getPackageName());
    }
}