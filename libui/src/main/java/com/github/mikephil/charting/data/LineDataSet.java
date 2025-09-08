
package com.github.mikephil.charting.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.util.Log;

import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class LineDataSet extends LineRadarDataSet<Entry> implements ILineDataSet {

    /**
     * Drawing mode for this line dataset
     **/
    /**
     * Private method description.
     */
    private Mode mMode = Mode.LINEAR;

    /**
     * List representing all colors that are used for the circles
     */
    private List<Integer> mCircleColors = null;

    /**
     * the color of the inner circles
     */
    private int mCircleHoleColor = Color.WHITE;

    /**
     * the radius of the circle-shaped value indicators
     */
    private float mCircleRadius = 8f;

    /**
     * the hole radius of the circle-shaped value indicators
     */
    private float mCircleHoleRadius = 4f;

    /**
     * sets the intensity of the cubic lines
     */
    private float mCubicIntensity = 0.2f;

    /**
     * the path effect of this DataSet that makes dashed lines possible
     */
    private DashPathEffect mDashPathEffect = null;

    /**
     * formatter for customizing the position of the fill-line
     */
    private IFillFormatter mFillFormatter = new DefaultFillFormatter();

    /**
     * if true, drawing circles is enabled
     */
    private boolean mDrawCircles = true;

    private boolean mDrawCircleHole = true;


    /**
     * Method description.
     */
    public LineDataSet(List<Entry> yVals, String label) {
        super(yVals, label);

        // mCircleRadius = Utils.convertDpToPixel(4f);
        // mLineWidth = Utils.convertDpToPixel(1f);

        if (mCircleColors == null) {
            mCircleColors = new ArrayList<Integer>();
        }
        mCircleColors.clear();

        // default colors
        // mColors.add(Color.rgb(192, 255, 140));
        // mColors.add(Color.rgb(255, 247, 140));
        mCircleColors.add(Color.rgb(140, 234, 255));
    }

    @Override
    /**
     * Method description.
     */
    public DataSet<Entry> copy() {
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < mValues.size(); i++) {
            entries.add(mValues.get(i).copy());
        }
        LineDataSet copied = new LineDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(LineDataSet lineDataSet) {
        super.copy(lineDataSet);
        lineDataSet.mCircleColors = mCircleColors;
        lineDataSet.mCircleHoleColor = mCircleHoleColor;
        lineDataSet.mCircleHoleRadius = mCircleHoleRadius;
        lineDataSet.mCircleRadius = mCircleRadius;
        lineDataSet.mCubicIntensity = mCubicIntensity;
        lineDataSet.mDashPathEffect = mDashPathEffect;
        lineDataSet.mDrawCircleHole = mDrawCircleHole;
        lineDataSet.mDrawCircles = mDrawCircleHole;
        lineDataSet.mFillFormatter = mFillFormatter;
        lineDataSet.mMode = mMode;
    }

    /**
     * Returns the drawing mode for this line dataset
     *
     * @return
     */
    @Override
    /**
     * Method description.
     */
    public Mode getMode() {
        return mMode;
    }

    /**
     * Returns the drawing mode for this LineDataSet
     *
     * @return
     */
    /**
     * Method description.
     */
    public void setMode(Mode mode) {
        mMode = mode;
    }

    /**
     * Sets the intensity for cubic lines (if enabled). Max = 1f = very cubic,
     * Min = 0.05f = low cubic effect, Default: 0.2f
     *
     * @param intensity
     */
    /**
     * Method description.
     */
    public void setCubicIntensity(float intensity) {

        if (intensity > 1f)
            intensity = 1f;
        if (intensity < 0.05f)
            intensity = 0.05f;

        mCubicIntensity = intensity;
    }

    @Override
    /**
     * Method description.
     */
    public float getCubicIntensity() {
        return mCubicIntensity;
    }


    /**
     * Sets the radius of the drawn circles.
     * Default radius = 4f, Min = 1f
     *
     * @param radius
     */
    /**
     * Method description.
     */
    public void setCircleRadius(float radius) {

        if (radius >= 1f) {
            mCircleRadius = Utils.convertDpToPixel(radius);
        } else {
            Log.e("LineDataSet", "Circle radius cannot be < 1");
        }
    }

    @Override
    /**
     * Method description.
     */
    public float getCircleRadius() {
        return mCircleRadius;
    }

    /**
     * Sets the hole radius of the drawn circles.
     * Default radius = 2f, Min = 0.5f
     *
     * @param holeRadius
     */
    /**
     * Method description.
     */
    public void setCircleHoleRadius(float holeRadius) {

        if (holeRadius >= 0.5f) {
            mCircleHoleRadius = Utils.convertDpToPixel(holeRadius);
        } else {
            Log.e("LineDataSet", "Circle radius cannot be < 0.5");
        }
    }

    @Override
    /**
     * Method description.
     */
    public float getCircleHoleRadius() {
        return mCircleHoleRadius;
    }

    /**
     * sets the size (radius) of the circle shpaed value indicators,
     * default size = 4f
     * <p/>
     * This method is deprecated because of unclarity. Use setCircleRadius instead.
     *
     * @param size
     */
    @Deprecated
    /**
     * Method description.
     */
    public void setCircleSize(float size) {
        setCircleRadius(size);
    }

    /**
     * This function is deprecated because of unclarity. Use getCircleRadius instead.
     */
    @Deprecated
    /**
     * Method description.
     */
    public float getCircleSize() {
        return getCircleRadius();
    }

    /**
     * Enables the line to be drawn in dashed mode, e.g. like this
     * "- - - - - -". THIS ONLY WORKS IF HARDWARE-ACCELERATION IS TURNED OFF.
     * Keep in mind that hardware acceleration boosts performance.
     *
     * @param lineLength  the length of the line pieces
     * @param spaceLength the length of space in between the pieces
     * @param phase       offset, in degrees (normally, use 0)
     */
    /**
     * Method description.
     */
    public void enableDashedLine(float lineLength, float spaceLength, float phase) {
        mDashPathEffect = new DashPathEffect(new float[]{
                lineLength, spaceLength
        }, phase);
    }

    /**
     * Disables the line to be drawn in dashed mode.
     */
    /**
     * Method description.
     */
    public void disableDashedLine() {
        mDashPathEffect = null;
    }

    @Override
    /**
     * Method description.
     */
    public boolean isDashedLineEnabled() {
        return mDashPathEffect == null ? false : true;
    }

    @Override
    /**
     * Method description.
     */
    public DashPathEffect getDashPathEffect() {
        return mDashPathEffect;
    }

    /**
     * set this to true to enable the drawing of circle indicators for this
     * DataSet, default true
     *
     * @param enabled
     */
    /**
     * Method description.
     */
    public void setDrawCircles(boolean enabled) {
        this.mDrawCircles = enabled;
    }

    @Override
    /**
     * Method description.
     */
    public boolean isDrawCirclesEnabled() {
        return mDrawCircles;
    }

    @Deprecated
    @Override
    /**
     * Method description.
     */
    public boolean isDrawCubicEnabled() {
        return mMode == Mode.CUBIC_BEZIER;
    }

    @Deprecated
    @Override
    /**
     * Method description.
     */
    public boolean isDrawSteppedEnabled() {
        return mMode == Mode.STEPPED;
    }

    /** ALL CODE BELOW RELATED TO CIRCLE-COLORS */

    /**
     * returns all colors specified for the circles
     *
     * @return
     */
    /**
     * Method description.
     */
    public List<Integer> getCircleColors() {
        return mCircleColors;
    }

    @Override
    /**
     * Method description.
     */
    public int getCircleColor(int index) {
        return mCircleColors.get(index);
    }

    @Override
    /**
     * Method description.
     */
    public int getCircleColorCount() {
        return mCircleColors.size();
    }

    /**
     * Sets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. Make sure that the colors
     * are already prepared (by calling getResources().getColor(...)) before
     * adding them to the DataSet.
     *
     * @param colors
     */
    /**
     * Method description.
     */
    public void setCircleColors(List<Integer> colors) {
        mCircleColors = colors;
    }

    /**
     * Sets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. Make sure that the colors
     * are already prepared (by calling getResources().getColor(...)) before
     * adding them to the DataSet.
     *
     * @param colors
     */
    /**
     * Method description.
     */
    public void setCircleColors(int... colors) {
        this.mCircleColors = ColorTemplate.createColors(colors);
    }

    /**
     * ets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. You can use
     * "new String[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     *
     * @param colors
     */
    /**
     * Method description.
     */
    public void setCircleColors(int[] colors, Context c) {

        List<Integer> clrs = mCircleColors;
        if (clrs == null) {
            clrs = new ArrayList<>();
        }
        clrs.clear();

        for (int color : colors) {
            clrs.add(c.getResources().getColor(color));
        }

        mCircleColors = clrs;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     *
     * @param color
     */
    /**
     * Method description.
     */
    public void setCircleColor(int color) {
        resetCircleColors();
        mCircleColors.add(color);
    }

    /**
     * resets the circle-colors array and creates a new one
     */
    /**
     * Method description.
     */
    public void resetCircleColors() {
        if (mCircleColors == null) {
            mCircleColors = new ArrayList<Integer>();
        }
        mCircleColors.clear();
    }

    /**
     * Sets the color of the inner circle of the line-circles.
     *
     * @param color
     */
    /**
     * Method description.
     */
    public void setCircleHoleColor(int color) {
        mCircleHoleColor = color;
    }

    @Override
    /**
     * Method description.
     */
    public int getCircleHoleColor() {
        return mCircleHoleColor;
    }

    /**
     * Set this to true to allow drawing a hole in each data circle.
     *
     * @param enabled
     */
    /**
     * Method description.
     */
    public void setDrawCircleHole(boolean enabled) {
        mDrawCircleHole = enabled;
    }

    @Override
    /**
     * Method description.
     */
    public boolean isDrawCircleHoleEnabled() {
        return mDrawCircleHole;
    }

    /**
     * Sets a custom IFillFormatter to the chart that handles the position of the
     * filled-line for each DataSet. Set this to null to use the default logic.
     *
     * @param formatter
     */
    /**
     * Method description.
     */
    public void setFillFormatter(IFillFormatter formatter) {

        if (formatter == null)
            mFillFormatter = new DefaultFillFormatter();
        else
            mFillFormatter = formatter;
    }

    @Override
    /**
     * Method description.
     */
    public IFillFormatter getFillFormatter() {
        return mFillFormatter;
    }

    /**
     * Method description.
     */
    public enum Mode {
        LINEAR,
        STEPPED,
        CUBIC_BEZIER,
        HORIZONTAL_BEZIER
    }
}
