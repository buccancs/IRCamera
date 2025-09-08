
package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.interfaces.dataprovider.BubbleDataProvider;
import com.github.mikephil.charting.renderer.BubbleChartRenderer;

/**
 * The BubbleChart. Draws bubbles. Bubble chart implementation: Copyright 2015
 * Pierre-Marc Airoldi Licensed under Apache License 2.0. In the BubbleChart, it
 * is the area of the bubble, not the radius or diameter of the bubble that
 * conveys the data.
 *
 * @author Philipp Jahoda
 */
public class BubbleChart extends BarLineChartBase<BubbleData> implements BubbleDataProvider {

    /**
     * Method description.
     */
    public BubbleChart(Context context) {
        super(context);
    }

    /**
     * Method description.
     */
    public BubbleChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Method description.
     */
    public BubbleChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new BubbleChartRenderer(this, mAnimator, mViewPortHandler);
    }

    /**
     * Method description.
     */
    public BubbleData getBubbleData() {
        return mData;
    }
}
