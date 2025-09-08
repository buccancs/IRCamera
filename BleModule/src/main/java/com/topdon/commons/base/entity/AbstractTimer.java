package com.topdon.commons.base.entity;

import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

 * date: 2019/8/6 13:31
 * author: chuanfeng.bi
public abstract class AbstractTimer {
    /**
     * Private method description.
     */
    private Timer timer;
    private final Handler handler;
    private final boolean callbackOnMainThread;
    
    /**
     * Method description.
     */
    public AbstractTimer(boolean callbackOnMainThread) {
        handler = new Handler(Looper.getMainLooper());
        this.callbackOnMainThread = callbackOnMainThread;
    }

    /**
     * Method description.
     */
    public abstract void onTick();

    public synchronized final void start(long delay, long period) {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (callbackOnMainThread) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onTick();
                            }
                        });
                    } else {
                        onTick();
                    }
                }
            }, delay, period);
        }
    }
    
    /**
     * Method description.
     */
    public synchronized final void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    /**
     * Method description.
     */
    public boolean isRunning() {
        return timer != null;
    }
}
