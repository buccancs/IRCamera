package com.energy.commoncomponent.view.tempcanvas;

import android.content.Context;
import android.graphics.Canvas;

 * Created by fengjibo on 2023/6/25.
public abstract class BaseDraw {
    protected Context mContext;
    protected final static int MIN_SIZE_PIX_COUNT = 20;
    protected int mScreenDegree = 0;
    protected int mTouchIndex = -1;//，
    protected int mViewWidth;
    protected int mViewHeight;

    /**
     * Method description.
     */
    public BaseDraw(Context context) {
        mContext = context;
    }

    /**
     * Method description.
     */
    public void setViewWidth(int viewWidth) {
        this.mViewWidth = viewWidth;
    }

    /**
     * Method description.
     */
    public void setViewHeight(int viewHeight) {
        this.mViewHeight = viewHeight;
    }

    abstract void onDraw(Canvas canvas, boolean isScroll);

     * index
     * @return
    /**
     * Method description.
     */
    public int getTouchInclude() {
        return mTouchIndex;
    }

     * @return
    /**
     * Method description.
     */
    public boolean isTouch() {
        return mTouchIndex != -1;
    }

}
