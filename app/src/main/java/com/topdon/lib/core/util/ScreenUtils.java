package com.topdon.lib.core.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.energy.iruvc.utils.CommonParams;

import java.lang.reflect.Method;

/**
 * Consolidated screen utility class combining functionality from multiple ScreenUtils
 * Replaces com.infisense.usbir.utils.ScreenUtils and com.energy.commoncomponent.utils.ScreenUtils
 */
public class ScreenUtils {
    
    private ScreenUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get screen width
     * @param context application context
     * @return screen width in pixels
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * Get screen height
     * @param context application context
     * @return screen height in pixels
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * Get status bar height
     * @param context application context
     * @return status bar height in pixels
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
        }
        return statusHeight;
    }

    /**
     * Take screenshot with status bar
     * @param activity activity to capture
     * @return screenshot bitmap
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * Get screen density DPI
     * @param context application context
     * @return density DPI
     */
    public static int getScreenDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * Take screenshot without status bar
     * @param activity activity to capture
     * @return screenshot bitmap without status bar
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * Get bottom status bar height (navigation bar)
     * @param context application context
     * @return bottom status bar height
     */
    public static int getBottomStatusHeight(Context context) {
        int totalHeight = getDpi(context);
        int contentHeight = getScreenHeight(context);
        return totalHeight - contentHeight;
    }

    /**
     * Get real screen height including navigation bar
     * @param context application context
     * @return real screen height
     */
    public static int getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
        }
        return dpi;
    }

    /**
     * Convert dp to pixels (with context)
     * @param context application context
     * @param dpValue dp value
     * @return pixel value
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = getScreenDensity(context);
        return (int) ((dpValue * scale) + 0.5f);
    }

    /**
     * Convert dp to pixels (system resources)
     * @param dpValue dp value
     * @return pixel value
     */
    public static int dp2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Convert sp to pixels
     * @param spValue sp value
     * @return pixel value
     */
    public static int sp2px(final float spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * Get screen density
     * @param context application context
     * @return screen density
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * Show normal dialog with OK button
     * @param context application context
     * @param info dialog message
     * @param dismissListener dismiss listener
     * @return created dialog
     */
    public static Dialog showNormalDialog(Context context, String info, PopupWindow.OnDismissListener dismissListener) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle("Info");
        normalDialog.setMessage(info);
        normalDialog.setCancelable(false);
        normalDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissListener.onDismiss();
                    }
                });
        return normalDialog.show();
    }

    /**
     * Get preview FPS based on data flow mode
     * @param defaultDataFlowMode data flow mode
     * @return FPS value
     */
    public static int getPreviewFPSByDataFlowMode(CommonParams.DataFlowMode defaultDataFlowMode) {
        if (defaultDataFlowMode == CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT) {
            return 25;
        }
        return 50;
    }
}