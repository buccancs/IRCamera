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

import com.infisense.iruvc.utils.SynchronizedBitmap;

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
        runnable = new Runnable() {
            @Override
            public void run() {
                Canvas canvas = null;
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
                            if (canvas == null)
                                continue;

                             * getWidth()getHeight()
                            Bitmap mScaledBitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true);
                            canvas.drawBitmap(mScaledBitmap, 0, 0, null);

                            Paint paint = new Paint(); //
                            paint.setStrokeWidth(2); //。
                            paint.setAntiAlias(true); //
                            paint.setDither(true); //
                            paint.setColor(Color.WHITE); //

                            int cross_len = 20;
                            canvas.drawLine(getWidth() / 2 - cross_len, getHeight() / 2,
                                    getWidth() / 2 + cross_len, getHeight() / 2, paint);
                            canvas.drawLine(getWidth() / 2, getHeight() / 2 - cross_len,
                                    getWidth() / 2, getHeight() / 2 + cross_len, paint);
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

     * @param bitmap
    /**
     * Method description.
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

     * @param syncimage
    /**
     * Method description.
     */
    public void setSyncimage(SynchronizedBitmap syncimage) {
        this.syncimage = syncimage;
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
    public void stop() {
        cameraThread.interrupt();
        try {
            cameraThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}




