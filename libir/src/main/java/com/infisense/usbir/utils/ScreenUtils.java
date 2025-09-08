package com.infisense.usbir.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;


import com.energy.iruvc.utils.CommonParams;

import java.lang.reflect.Method;

public class ScreenUtils {

    /**
     * Private method description.
     */
    private ScreenUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

     * @param context
     * @return
    /**
     * Method description.
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

     * @param context
     * @return
    /**
     * Method description.
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

     * @param context
     * @return
    /**
     * Method description.
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
            e.printStackTrace();
        }
        return statusHeight;
    }

     * @param activity
     * @return
    /**
     * Method description.
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

     * dpi
    /**
     * Method description.
     */
    public static int getScreenDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

     * @param activity
     * @return
    /**
     * Method description.
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

     * @param context
     * @return
    /**
     * Method description.
     */
    public static int getBottomStatusHeight(Context context) {
        int totalHeight = getDpi(context);

        int contentHeight = getScreenHeight(context);

        return totalHeight - contentHeight;
    }

    /**
     * Method description.
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
            e.printStackTrace();
        }
        return dpi;
    }

     * dppx
     * 16dp - 48px
     * 17dp - 51px
    /**
     * Method description.
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = getScreenDendity(context);
        return (int) ((dpValue * scale) + 0.5f);
    }

    /**
     * Method description.
     */
    public static float getScreenDendity(Context context) {
        return context.getResources().getDisplayMetrics().density;//3
    }

     * @param info
     * @setIcon
     * @setTitle
     * @setMessage
     * setXXXDialog
    /**
     * Method description.
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
                        //...To-do
                        dismissListener.onDismiss();
                    }
                });
        return normalDialog.show();
    }

     * @return
    /**
     * Method description.
     */
    public static int getPreviewFPSByDataFlowMode(CommonParams.DataFlowMode defaultDataFlowMode) {
        if (defaultDataFlowMode == CommonParams.DataFlowMode.IMAGE_AND_TEMP_OUTPUT) {
            return 25;
        }
        return 50;
    }

}
