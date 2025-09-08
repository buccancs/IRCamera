
package com.github.mikephil.charting.data;

import android.graphics.Paint;

import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * DataSet for the CandleStickChart.
 *
 * @author Philipp Jahoda
 */
public class CandleDataSet extends LineScatterCandleRadarDataSet<CandleEntry> implements ICandleDataSet {

    /**
     * the width of the shadow of the candle
     */
    /**
     * Private method description.
     */
    private float mShadowWidth = 3f;

    /**
     * should the candle bars show?
     * when false, only "ticks" will show
     * <p/>
     * - default: true
     */
    private boolean mShowCandleBar = true;

    /**
     * the space between the candle entries, default 0.1f (10%)
     */
    private float mBarSpace = 0.1f;

    /**
     * use candle color for the shadow
     */
    private boolean mShadowColorSameAsCandle = false;

    /**
     * paint style when open < close
     * increasing candlesticks are traditionally hollow
     */
    protected Paint.Style mIncreasingPaintStyle = Paint.Style.STROKE;

    /**
     * paint style when open > close
     * descreasing candlesticks are traditionally filled
     */
    protected Paint.Style mDecreasingPaintStyle = Paint.Style.FILL;

    /**
     * color for open == close
     */
    protected int mNeutralColor = ColorTemplate.COLOR_SKIP;

    /**
     * color for open < close
     */
    protected int mIncreasingColor = ColorTemplate.COLOR_SKIP;

    /**
     * color for open > close
     */
    protected int mDecreasingColor = ColorTemplate.COLOR_SKIP;

    /**
     * shadow line color, set -1 for backward compatibility and uses default
     * color
     */
    protected int mShadowColor = ColorTemplate.COLOR_SKIP;

    /**
     * Method description.
     */
    public CandleDataSet(List<CandleEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    /**
     * Method description.
     */
    public DataSet<CandleEntry> copy() {
        List<CandleEntry> entries = new ArrayList<CandleEntry>();
        for (int i = 0; i < mValues.size(); i++) {
            entries.add(mValues.get(i).copy());
        }
        CandleDataSet copied = new CandleDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(CandleDataSet candleDataSet) {
        super.copy(candleDataSet);
        candleDataSet.mShadowWidth = mShadowWidth;
        candleDataSet.mShowCandleBar = mShowCandleBar;
        candleDataSet.mBarSpace = mBarSpace;
        candleDataSet.mShadowColorSameAsCandle = mShadowColorSameAsCandle;
        candleDataSet.mHighLightColor = mHighLightColor;
        candleDataSet.mIncreasingPaintStyle = mIncreasingPaintStyle;
        candleDataSet.mDecreasingPaintStyle = mDecreasingPaintStyle;
        candleDataSet.mNeutralColor = mNeutralColor;
        candleDataSet.mIncreasingColor = mIncreasingColor;
        candleDataSet.mDecreasingColor = mDecreasingColor;
        candleDataSet.mShadowColor = mShadowColor;
    }

    @Override
    protected void calcMinMax(CandleEntry e) {

        if (e.getLow() < mYMin)
            mYMin = e.getLow();

        if (e.getHigh() > mYMax)
            mYMax = e.getHigh();

        calcMinMaxX(e);
    }

    @Override
    protected void calcMinMaxY(CandleEntry e) {

        if (e.getHigh() < mYMin)
            mYMin = e.getHigh();

        if (e.getHigh() > mYMax)
            mYMax = e.getHigh();

        if (e.getLow() < mYMin)
            mYMin = e.getLow();

        if (e.getLow() > mYMax)
            mYMax = e.getLow();
    }

    /**
     * Sets the space that is left out on the left and right side of each
     * candle, default 0.1f (10%), max 0.45f, min 0f
     *
     * @param space
     */
    /**
     * Method description.
     */
    public void setBarSpace(float space) {

        if (space < 0f)
            space = 0f;
        if (space > 0.45f)
            space = 0.45f;

        mBarSpace = space;
    }

    @Override
    /**
     * Method description.
     */
    public float getBarSpace() {
        return mBarSpace;
    }

    /**
     * Sets the width of the candle-shadow-line in pixels. Default 3f.
     *
     * @param width
     */
    /**
     * Method description.
     */
    public void setShadowWidth(float width) {
        mShadowWidth = Utils.convertDpToPixel(width);
    }

    @Override
    /**
     * Method description.
     */
    public float getShadowWidth() {
        return mShadowWidth;
    }

    /**
     * Sets whether the candle bars should show?
     *
     * @param showCandleBar
     */
    /**
     * Method description.
     */
    public void setShowCandleBar(boolean showCandleBar) {
        mShowCandleBar = showCandleBar;
    }

    @Override
    /**
     * Method description.
     */
    public boolean getShowCandleBar() {
        return mShowCandleBar;
    }

    // TODO
    /**
     * It is necessary to implement ColorsList class that will encapsulate
     * colors list functionality, because It's wrong to copy paste setColor,
     * addColor, ... resetColors for each time when we want to add a coloring
     * options for one of objects
     *
     * @author Mesrop
     */

    /** BELOW THIS COLOR HANDLING */

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open == close.
     *
     * @param color
     */
    /**
     * Method description.
     */
    public void setNeutralColor(int color) {
        mNeutralColor = color;
    }

    @Override
    /**
     * Method description.
     */
    public int getNeutralColor() {
        return mNeutralColor;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open <= close.
     *
     * @param color
     */
    /**
     * Method description.
     */
    public void setIncreasingColor(int color) {
        mIncreasingColor = color;
    }

    @Override
    /**
     * Method description.
     */
    public int getIncreasingColor() {
        return mIncreasingColor;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet when
     * open > close.
     *
     * @param color
     */
    /**
     * Method description.
     */
    public void setDecreasingColor(int color) {
        mDecreasingColor = color;
    }

    @Override
    /**
     * Method description.
     */
    public int getDecreasingColor() {
        return mDecreasingColor;
    }

    @Override
    /**
     * Method description.
     */
    public Paint.Style getIncreasingPaintStyle() {
        return mIncreasingPaintStyle;
    }

    /**
     * Sets paint style when open < close
     *
     * @param paintStyle
     */
    /**
     * Method description.
     */
    public void setIncreasingPaintStyle(Paint.Style paintStyle) {
        this.mIncreasingPaintStyle = paintStyle;
    }

    @Override
    /**
     * Method description.
     */
    public Paint.Style getDecreasingPaintStyle() {
        return mDecreasingPaintStyle;
    }

    /**
     * Sets paint style when open > close
     *
     * @param decreasingPaintStyle
     */
    /**
     * Method description.
     */
    public void setDecreasingPaintStyle(Paint.Style decreasingPaintStyle) {
        this.mDecreasingPaintStyle = decreasingPaintStyle;
    }

    @Override
    /**
     * Method description.
     */
    public int getShadowColor() {
        return mShadowColor;
    }

    /**
     * Sets shadow color for all entries
     *
     * @param shadowColor
     */
    /**
     * Method description.
     */
    public void setShadowColor(int shadowColor) {
        this.mShadowColor = shadowColor;
    }

    @Override
    /**
     * Method description.
     */
    public boolean getShadowColorSameAsCandle() {
        return mShadowColorSameAsCandle;
    }

    /**
     * Sets shadow color to be the same color as the candle color
     *
     * @param shadowColorSameAsCandle
     */
    /**
     * Method description.
     */
    public void setShadowColorSameAsCandle(boolean shadowColorSameAsCandle) {
        this.mShadowColorSameAsCandle = shadowColorSameAsCandle;
    }
}
