package com.topdon.lib.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.topdon.lib.core.config.FileConfig;
import com.topdon.lib.core.listener.BitmapViewListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

    /**
     * BitmapUtils class.
     *
     * Provides bitmaputils functionality.
     */
    public class BitmapUtils {

    public static Bitmap mirror(Bitmap rawBitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1f, 1f);
        return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.getWidth(), rawBitmap.getHeight(), matrix, true);
    }

    public static Bitmap rotateBitmap(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // [Chinese text], [Chinese text]
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // [Chinese text], [Chinese text]
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

        /**
     * [Chinese text]bitmap[Chinese text]bytes
     */
    public static byte[] bitmapToBytes(Bitmap bitmap, int quality) {
        if (bitmap == null) {
            return null;
        }
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

        /**
     * [Chinese text]in progress
     *
     * @param bitmap
     * @param file   [Chinese text]----[Chinese text]
     * @param path   [Chinese text]----[Chinese text]
     * @return
     */
    public static boolean saveBitmap(Bitmap bitmap, File file, File path) {
        boolean success = false;
        byte[] bytes = bitmapToBytes(bitmap, 100);
        OutputStream out = null;
        try {
            if (!file.exists() && file.isDirectory()) {
                file.mkdirs();
            }
            out = new FileOutputStream(path);
            out.write(bytes);
            out.flush();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

        /**
     * high[Chinese text]
     *
     * @param bitmap [Chinese text]
     * @param width  [Chinese text], [Chinese text]
     */
    public static Bitmap imageZoom(Bitmap bitmap, double width) {
        // [Chinese text]bitmap[Chinese text]in progress, [Chinese text]bitmap[Chinese text]([Chinese text])
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // [Chinese text], [Chinese text], [Chinese text]
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
        Bitmap newBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        // [Chinese text]bitmap[Chinese text] [Chinese text]
        return scaleWithWH(newBitmap, width,
                width * newBitmap.getHeight() / newBitmap.getWidth());
    }

    /**
     * Scale image with specified width and height.
     *
     * @param bitmap the bitmap to scale
     * @param w new width
     * @param h new height
     * @return scaled Bitmap
     */
    public static Bitmap scaleWithWH(Bitmap bitmap, double w, double h) {
        if (w == 0 || h == 0 || bitmap == null) {
            return bitmap;
        } else {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            Matrix matrix = new Matrix();
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);

            matrix.postScale(scaleWidth, scaleHeight);
            return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                    matrix, true);
        }
    }

        /**
     * bitmap[Chinese text]
     *
     * @param file [Chinese text]
     * @param file [Chinese text]
     * @return bitmap
     */
    public static boolean saveFile(String file, Bitmap bmp) {
        if (TextUtils.isEmpty(file) || bmp == null) return false;

        File f = new File(file);
        if (f.exists()) {
            f.delete();
        } else {
            File p = f.getParentFile();
            if (!p.exists()) {
                p.mkdirs();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

        /**
     * [Chinese text], [Chinese text]
     *
     * @param backBitmap  [Chinese text]
     * @param frontBitmap [Chinese text]
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap, int leftFront, int topFront) {
        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(backBitmap, 0, 0, null);
        canvas.drawBitmap(frontBitmap, leftFront, topFront, null);
//        if (!frontBitmap.isRecycled()){
//            frontBitmap.recycle();
//        }
        return bitmap;
    }
    public static Bitmap mergeBitmapAlpha(Bitmap backBitmap, Bitmap frontBitmap,Paint paint, int leftFront, int topFront) {
        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(backBitmap, 0, 0, null);
        canvas.drawBitmap(frontBitmap, leftFront, topFront, paint);
//        if (!frontBitmap.isRecycled()){
//            frontBitmap.recycle();
//        }
        return bitmap;
    }

    public static Bitmap mergeBitmapByView(Bitmap backBitmap, Bitmap frontBitmap, BitmapViewListener view) {
        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
            return null;
        }
        Paint paint = new Paint();
        paint.setAlpha((int) (view.getViewAlpha() * 255));
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(backBitmap, 0, 0, null);
        if (view.getViewScale() != 1){
            frontBitmap = scaleWithWH(frontBitmap,view.getViewWidth(),view.getViewHeight());
        }
        canvas.drawBitmap(frontBitmap, view.getViewX(),view.getViewY(), paint);
        frontBitmap.recycle();
        return bitmap;
    }

    @NonNull
    public static Bitmap mergeBitmapByViewNonNull(@NonNull Bitmap backBitmap, @Nullable Bitmap frontBitmap, BitmapViewListener view) {
        if (frontBitmap == null || frontBitmap.isRecycled()) {
            return backBitmap;
        }

        Bitmap bitmap;
        if (backBitmap.isRecycled()) {
            bitmap = Bitmap.createBitmap(backBitmap.getWidth(), backBitmap.getHeight(), backBitmap.getConfig());
        } else {
            bitmap = backBitmap;
        }
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAlpha((int) (view.getViewAlpha() * 255));

        if (view.getViewScale() != 1){
            frontBitmap = scaleWithWH(frontBitmap,view.getViewWidth(),view.getViewHeight());
        }
        canvas.drawBitmap(frontBitmap, view.getViewX(),view.getViewY(), paint);
        frontBitmap.recycle();
        return bitmap;
    }

    public static void mergeBitmapByView(Bitmap frontBitmap, BitmapViewListener view,Canvas canvas) {
        if (frontBitmap == null || frontBitmap.isRecycled()) {
            return;
        }
        Paint paint = new Paint();
        paint.setAlpha((int) (view.getViewAlpha() * 255));
        if (view.getViewScale() != 1){
            frontBitmap = scaleWithWH(frontBitmap,view.getViewWidth(),view.getViewHeight());
        }
        canvas.drawBitmap(frontBitmap, view.getViewX(),view.getViewY(), paint);
    }

        /**
     * [Chinese text], [Chinese text]
     * @param bytes  [Chinese text]
     * @param bytes2 [Chinese text]
     */
    public static void savaRawFile(byte[] bytes, byte[] bytes2) {
        try {
            File path = new File("/sdcard");
            if (!path.exists() && path.isDirectory()) {
                path.mkdirs();
            }
            File file = new File("/sdcard/", "xxx.raw");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.write(bytes2);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        /**
     * [Chinese text]
     * @param bmp
     * @param title
     * @param address
     * @param time
     * @param seekBarWidth : [Chinese text]Pseudo color[Chinese text], [Chinese text]
     * @return
     */
    public static Bitmap drawCenterLable(Bitmap bmp, String title,String address,String time,int seekBarWidth) {
        // [Chinese text]
        Bitmap newBmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        // [Chinese text]
        Canvas canvas = new Canvas(newBmp);
        canvas.drawBitmap(bmp, 0, 0, null);  // [Chinese text]
        canvas.save();
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE); // [Chinese text]
        paint.setTextSize(SizeUtils.sp2px(12));
        paint.setDither(true);
        paint.setFilterBitmap(true);
        Rect rectText = new Rect();  // [Chinese text]text[Chinese text]high,  [Chinese text]: [Chinese text]
        paint.getTextBounds("[Chinese text]high[Chinese text]", 0,"[Chinese text]high[Chinese text]".length(), rectText);
        double beginX = SizeUtils.dp2px(10);  // 45[Chinese text]1.414
        double beginY = bmp.getHeight() - SizeUtils.dp2px(10);
        if (!TextUtils.isEmpty(time)){
            beginY = beginY - (rectText.bottom - rectText.top);
            canvas.drawText(time, (int)beginX, (int)beginY, paint);
            beginY -= SizeUtils.dp2px(6);
        }
        int lineWidth = bmp.getWidth() - SizeUtils.dp2px(20) - seekBarWidth;// [Chinese text]
        if (!TextUtils.isEmpty(address)){
            int textHeight = (rectText.bottom - rectText.top);
            paint.getTextBounds(address, 0,address.length(), rectText);
            if (rectText.width() > lineWidth){
                // [Chinese text], [Chinese text]
                StaticLayout staticLayout = new StaticLayout(address,
                        paint, lineWidth,
                        Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                beginY = beginY - (textHeight+SizeUtils.dp2px(1f))*staticLayout.getLineCount();
                canvas.save();
                canvas.translate( (int)beginX, (int)beginY - textHeight);
                staticLayout.draw(canvas);
                canvas.restore();
            }else {
                beginY = beginY - textHeight;
                canvas.drawText(address, (int)beginX, (int)beginY, paint);
            }
            beginY -= SizeUtils.dp2px(6);
        }
        if (!TextUtils.isEmpty(title)){
            int textHeight = (rectText.bottom - rectText.top);
            paint.getTextBounds(title, 0,title.length(), rectText);
            if (rectText.width() > lineWidth){
                // [Chinese text], [Chinese text]
                StaticLayout staticLayout = new StaticLayout(title,
                        paint, lineWidth,
                        Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                beginY = beginY - textHeight*staticLayout.getLineCount();
                canvas.save();
                canvas.translate( (int)beginX, (int)beginY - textHeight);
                staticLayout.draw(canvas);
                canvas.restore();
            }else {
                beginY = beginY - textHeight;
                canvas.drawText(title, (int)beginX, (int)beginY, paint);
            }
            beginY -= SizeUtils.dp2px(6);
        }
        canvas.restore();
        return newBmp;
    }
}
