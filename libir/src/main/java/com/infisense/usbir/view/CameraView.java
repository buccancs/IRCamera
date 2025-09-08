package com.infisense.usbir.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.energy.iruvc.utils.SynchronizedBitmap;
import com.infisense.usbdual.Const;
import com.infisense.usbir.utils.OpencvTools;

 * TextureViewSurfaceView
public class CameraView extends TextureView {
    /**
     * Private method description.
     */
    private String TAG = "CameraView";
    private Bitmap bitmap;
    private SynchronizedBitmap syncimage;
    private Runnable runnable;
    private Thread cameraThread;
    private Canvas canvas = null;
    private Paint paint;
    private int cross_len = 20;
    private Paint greenPaint;
    private boolean drawLine = true;//
    /**
     * Method description.
     */
    public int productType = Const.TYPE_IR;
    private int irWidth = 192;
    private int irHeight = 256;

    private boolean isOpenAmplify = false;


    public boolean isOpenAmplify() {
        return isOpenAmplify;
    }

    /**
     * Method description.
     */
    public void setOpenAmplify(boolean openAmplify) {
        isOpenAmplify = openAmplify;
    }

    /**
     * Method description.
     */
    public void setImageSize(int irWidth, int irHeight){
        this.irWidth = irWidth;
        this.irHeight = irHeight;
    }

    /**
     * Method description.
     */
    public CameraView(Context context) {
        this(context, null, 0);
    }

    /**
     * Method description.
     */
    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

     * @param context
     * @param attrs
     * @param defStyleAttr
    /**
     * Method description.
     */
    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(); //
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
//        paint = new Paint();  //
        paint.setStrokeWidth(2); //。
        paint.setAntiAlias(true); //
        paint.setDither(true); //
        paint.setColor(Color.WHITE); //
        greenPaint = new Paint();
        greenPaint.setStrokeWidth(6);
        greenPaint.setTextSize(56);
        greenPaint.setColor(Color.GREEN);
        runnable = new Runnable() {
            @Override
            public void run() {
                while (!cameraThread.isInterrupted()) {
                    synchronized (syncimage.viewLock) {
                        if (syncimage.valid == false) {
                            try {
                                syncimage.viewLock.wait();
                            } catch (InterruptedException e) {
                                cameraThread.interrupt();
                                Log.e(TAG, "lock.wait(): catch an interrupted exception");
                            }
                        }
                        if (syncimage.valid == true) {
                            canvas = lockCanvas();
                            if (canvas == null) {
                                continue;
                            }
                            paint.setStrokeWidth(2); //。
                            paint.setAntiAlias(true); //
                            paint.setDither(true); //
                            paint.setColor(Color.WHITE); //
                             * getWidth()getHeight()
                            Bitmap mScaledBitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true);
                            canvas.drawBitmap(mScaledBitmap, 0, 0, null);

                            if (drawLine){
                                canvas.drawLine(getWidth() / 2 - cross_len, getHeight() / 2,
                                        getWidth() / 2 + cross_len, getHeight() / 2, paint);
                                canvas.drawLine(getWidth() / 2, getHeight() / 2 - cross_len,
                                        getWidth() / 2, getHeight() / 2 + cross_len, paint);
                            }
                            unlockCanvasAndPost(canvas);
                            syncimage.valid = false;
                        }
                    }
                    SystemClock.sleep(1);
                }
                Log.w(TAG, "DisplayThread exit:");
            }
        };
    }

    /**
     * Method description.
     */
    public boolean isDrawLine() {
        return drawLine;
    }

    /**
     * Method description.
     */
    public void setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
    }

     * @param bitmap
    /**
     * Method description.
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Nullable
    @Override
    /**
     * Method description.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

     * @param syncimage
    /**
     * Method description.
     */
    public void setSyncimage(SynchronizedBitmap syncimage) {
        this.syncimage = syncimage;
    }

    @NonNull
    /**
     * Method description.
     */
    public Bitmap getScaledBitmap() {
        synchronized (syncimage.viewLock) {
            Bitmap sBitmap = null;
            sBitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true);
            return sBitmap;
        }
    }
    /**
     * Method description.
     */
    public void start() {
        cameraThread = new Thread(runnable);
        cameraThread.start();
    }
    /**
     * Method description.
     */
    public void setShowCross(boolean isShow){
        try {
            cross_len = isShow ? 20 : 0;
            canvas.drawLine(getWidth() / 2f - cross_len, getHeight() / 2f,
                    getWidth() / 2f + cross_len, getHeight() / 2f, paint);
            canvas.drawLine(getWidth() / 2f, getHeight() / 2f - cross_len,
                    getWidth() / 2f, getHeight() / 2f + cross_len, paint);
        }catch (Exception e){
            Log.e(TAG,":"+e.getMessage());
        }
    }


    /**
     * Method description.
     */
    public void stop() {
        try {
            if (cameraThread != null){
                cameraThread.interrupt();
                cameraThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}




