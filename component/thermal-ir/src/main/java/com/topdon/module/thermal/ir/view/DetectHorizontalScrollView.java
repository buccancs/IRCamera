package com.topdon.module.thermal.ir.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class DetectHorizontalScrollView extends HorizontalScrollView {
    /**
     * Private method description.
     */
    private Runnable scrollerTask;
    private int intitPosition;
    private int newCheck = 100;
    private int childWidth = 0;

    /**
     * Method description.
     */
    public interface OnScrollStopListner {
        /**
         * scroll have stoped
         */
        void onScrollStoped();

        /**
         * scroll have stoped, and is at left edge
         */
        void onScrollToLeftEdge();

        /**
         * scroll have stoped, and is at right edge
         */
        void onScrollToRightEdge();

        /**
         * scroll have stoped, and is at middle
         */
        void onScrollToMiddle();

        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    /**
     * Private method description.
     */
    private OnScrollStopListner onScrollstopListner;

    /**
     * Method description.
     */
    public DetectHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scrollerTask = new Runnable() {
            @Override
            public void run() {
                int newPosition = getScrollX();
                if (intitPosition - newPosition == 0) {
                    if (onScrollstopListner == null) {
                        return;
                    }
                    onScrollstopListner.onScrollStoped();
                    Rect outRect = new Rect();
                    getDrawingRect(outRect);
                    if (getScrollX() == 0) {
                        onScrollstopListner.onScrollToLeftEdge();
                    } else if (childWidth + getPaddingLeft() + getPaddingRight() == outRect.right) {
                        onScrollstopListner.onScrollToRightEdge();
                    } else {
                        onScrollstopListner.onScrollToMiddle();
                    }
                } else {
                    intitPosition = getScrollX();
                    postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    /**
     * Method description.
     */
    public void setOnScrollStopListner(OnScrollStopListner listner) {
        onScrollstopListner = listner;
    }

    /**
     * Method description.
     */
    public void startScrollerTask() {
        intitPosition = getScrollX();
        postDelayed(scrollerTask, newCheck);
        checkTotalWidth();
    }

    /**
     * Private method description.
     */
    private void checkTotalWidth() {
        if (childWidth > 0) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            childWidth += getChildAt(i).getWidth();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollstopListner != null) {
            onScrollstopListner.onScrollChanged(l, t, oldl, oldt);
        }
    }
}