package com.github.mikephil.charting.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp Jahoda on 21/10/15.
 * This is the base dataset of all DataSets. It's purpose is to implement critical methods
 * provided by the IDataSet interface.
 */
public abstract class BaseDataSet<T extends Entry> implements IDataSet<T> {

    /**
     * List representing all colors that are used for this DataSet
     */
    protected List<Integer> mColors = null;

    protected GradientColor mGradientColor = null;

    protected List<GradientColor> mGradientColors = null;

    /**
     * List representing all colors that are used for drawing the actual values for this DataSet
     */
    protected List<Integer> mValueColors = null;

    /**
     * label that describes the DataSet or the data the DataSet represents
     */
    /**
     * Private method description.
     */
    private String mLabel = "DataSet";

    /**
     * this specifies which axis this DataSet should be plotted against
     */
    protected YAxis.AxisDependency mAxisDependency = YAxis.AxisDependency.LEFT;

    /**
     * if true, value highlightning is enabled
     */
    protected boolean mHighlightEnabled = true;

    /**
     * custom formatter that is used instead of the auto-formatter if set
     */
    protected transient ValueFormatter mValueFormatter;

    /**
     * the typeface used for the value text
     */
    protected Typeface mValueTypeface;

    private Legend.LegendForm mForm = Legend.LegendForm.DEFAULT;
    private float mFormSize = Float.NaN;
    private float mFormLineWidth = Float.NaN;
    private DashPathEffect mFormLineDashEffect = null;

    /**
     * if true, y-values are drawn on the chart
     */
    protected boolean mDrawValues = true;

    /**
     * if true, y-icons are drawn on the chart
     */
    protected boolean mDrawIcons = true;

    /**
     * the offset for drawing icons (in dp)
     */
    protected MPPointF mIconsOffset = new MPPointF();

    /**
     * the size of the value-text labels
     */
    protected float mValueTextSize = 17f;

    /**
     * flag that indicates if the DataSet is visible or not
     */
    protected boolean mVisible = true;

    /**
     * Default constructor.
     */
    /**
     * Method description.
     */
    public BaseDataSet() {
        mColors = new ArrayList<Integer>();
        mValueColors = new ArrayList<Integer>();

        // default color
        mColors.add(Color.rgb(140, 234, 255));
        mValueColors.add(Color.BLACK);
    }

    /**
     * Constructor with label.
     *
     * @param label
     */
    /**
     * Method description.
     */
    public BaseDataSet(String label) {
        this();
        this.mLabel = label;
    }

    /**
     * Use this method to tell the data set that the underlying data has changed.
     */
    /**
     * Method description.
     */
    public void notifyDataSetChanged() {
        calcMinMax();
    }


    /**
     * ###### ###### COLOR GETTING RELATED METHODS ##### ######
     */

    @Override
    /**
     * Method description.
     */
    public List<Integer> getColors() {
        return mColors;
    }

    /**
     * Method description.
     */
    public List<Integer> getValueColors() {
        return mValueColors;
    }

    @Override
    /**
     * Method description.
     */
    public int getColor() {
        return mColors.get(0);
    }

    @Override
    /**
     * Method description.
     */
    public int getColor(int index) {
        return mColors.get(index % mColors.size());
    }

    @Override
    /**
     * Method description.
     */
    public GradientColor getGradientColor() {
        return mGradientColor;
    }

    @Override
    /**
     * Method description.
     */
    public List<GradientColor> getGradientColors() {
        return mGradientColors;
    }

    @Override
    /**
     * Method description.
     */
    public GradientColor getGradientColor(int index) {
        return mGradientColors.get(index % mGradientColors.size());
    }

    /**
     * ###### ###### COLOR SETTING RELATED METHODS ##### ######
     */

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     *
     * @param colors
     */
    /**
     * Method description.
     */
    public void setColors(List<Integer> colors) {
        this.mColors = colors;
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     *
     * @param colors
     */
    /**
     * Method description.
     */
    public void setColors(int... colors) {
        this.mColors = ColorTemplate.createColors(colors);
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. You can use
     * "new int[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     *
     * @param colors
     */
    /**
     * Method description.
     */
    public void setColors(int[] colors, Context c) {

        if (mColors == null) {
            mColors = new ArrayList<>();
        }

        mColors.clear();

        for (int color : colors) {
            mColors.add(c.getResources().getColor(color));
        }
    }

    /**
     * Adds a new color to the colors array of the DataSet.
     *
     * @param color
     */
    /**
     * Method description.
     */
    public void addColor(int color) {
        if (mColors == null)
            mColors = new ArrayList<Integer>();
        mColors.add(color);
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
    public void setColor(int color) {
        resetColors();
        mColors.add(color);
    }

    /**
     * Sets the start and end color for gradient color, ONLY color that should be used for this DataSet.
     *
     * @param startColor
     * @param endColor
     */
    /**
     * Method description.
     */
    public void setGradientColor(int startColor, int endColor) {
        mGradientColor = new GradientColor(startColor, endColor);
    }

    /**
     * Sets the start and end color for gradient colors, ONLY color that should be used for this DataSet.
     *
     * @param gradientColors
     */
    /**
     * Method description.
     */
    public void setGradientColors(List<GradientColor> gradientColors) {
        this.mGradientColors = gradientColors;
    }

    /**
     * Sets a color with a specific alpha value.
     *
     * @param color
     * @param alpha from 0-255
     */
    /**
     * Method description.
     */
    public void setColor(int color, int alpha) {
        setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
    }

    /**
     * Sets colors with a specific alpha value.
     *
     * @param colors
     * @param alpha
     */
    /**
     * Method description.
     */
    public void setColors(int[] colors, int alpha) {
        resetColors();
        for (int color : colors) {
            addColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
        }
    }

    /**
     * Resets all colors of this DataSet and recreates the colors array.
     */
    /**
     * Method description.
     */
    public void resetColors() {
        if (mColors == null) {
            mColors = new ArrayList<Integer>();
        }
        mColors.clear();
    }

    /**
     * ###### ###### OTHER STYLING RELATED METHODS ##### ######
     */

    @Override
    /**
     * Method description.
     */
    public void setLabel(String label) {
        mLabel = label;
    }

    @Override
    /**
     * Method description.
     */
    public String getLabel() {
        return mLabel;
    }

    @Override
    /**
     * Method description.
     */
    public void setHighlightEnabled(boolean enabled) {
        mHighlightEnabled = enabled;
    }

    @Override
    /**
     * Method description.
     */
    public boolean isHighlightEnabled() {
        return mHighlightEnabled;
    }

    @Override
    /**
     * Method description.
     */
    public void setValueFormatter(ValueFormatter f) {

        if (f == null)
            return;
        else
            mValueFormatter = f;
    }

    @Override
    /**
     * Method description.
     */
    public ValueFormatter getValueFormatter() {
        if (needsFormatter())
            return Utils.getDefaultValueFormatter();
        return mValueFormatter;
    }

    @Override
    /**
     * Method description.
     */
    public boolean needsFormatter() {
        return mValueFormatter == null;
    }

    @Override
    /**
     * Method description.
     */
    public void setValueTextColor(int color) {
        mValueColors.clear();
        mValueColors.add(color);
    }

    @Override
    /**
     * Method description.
     */
    public void setValueTextColors(List<Integer> colors) {
        mValueColors = colors;
    }

    @Override
    /**
     * Method description.
     */
    public void setValueTypeface(Typeface tf) {
        mValueTypeface = tf;
    }

    @Override
    /**
     * Method description.
     */
    public void setValueTextSize(float size) {
        mValueTextSize = Utils.convertDpToPixel(size);
    }

    @Override
    /**
     * Method description.
     */
    public int getValueTextColor() {
        return mValueColors.get(0);
    }

    @Override
    /**
     * Method description.
     */
    public int getValueTextColor(int index) {
        return mValueColors.get(index % mValueColors.size());
    }

    @Override
    /**
     * Method description.
     */
    public Typeface getValueTypeface() {
        return mValueTypeface;
    }

    @Override
    /**
     * Method description.
     */
    public float getValueTextSize() {
        return mValueTextSize;
    }

    /**
     * Method description.
     */
    public void setForm(Legend.LegendForm form) {
        mForm = form;
    }

    @Override
    /**
     * Method description.
     */
    public Legend.LegendForm getForm() {
        return mForm;
    }

    /**
     * Method description.
     */
    public void setFormSize(float formSize) {
        mFormSize = formSize;
    }

    @Override
    /**
     * Method description.
     */
    public float getFormSize() {
        return mFormSize;
    }

    /**
     * Method description.
     */
    public void setFormLineWidth(float formLineWidth) {
        mFormLineWidth = formLineWidth;
    }

    @Override
    /**
     * Method description.
     */
    public float getFormLineWidth() {
        return mFormLineWidth;
    }

    /**
     * Method description.
     */
    public void setFormLineDashEffect(DashPathEffect dashPathEffect) {
        mFormLineDashEffect = dashPathEffect;
    }

    @Override
    /**
     * Method description.
     */
    public DashPathEffect getFormLineDashEffect() {
        return mFormLineDashEffect;
    }

    @Override
    /**
     * Method description.
     */
    public void setDrawValues(boolean enabled) {
        this.mDrawValues = enabled;
    }

    @Override
    /**
     * Method description.
     */
    public boolean isDrawValuesEnabled() {
        return mDrawValues;
    }

    @Override
    /**
     * Method description.
     */
    public void setDrawIcons(boolean enabled) {
        mDrawIcons = enabled;
    }

    @Override
    /**
     * Method description.
     */
    public boolean isDrawIconsEnabled() {
        return mDrawIcons;
    }

    @Override
    /**
     * Method description.
     */
    public void setIconsOffset(MPPointF offsetDp) {

        mIconsOffset.x = offsetDp.x;
        mIconsOffset.y = offsetDp.y;
    }

    @Override
    /**
     * Method description.
     */
    public MPPointF getIconsOffset() {
        return mIconsOffset;
    }

    @Override
    /**
     * Method description.
     */
    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    @Override
    /**
     * Method description.
     */
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    /**
     * Method description.
     */
    public YAxis.AxisDependency getAxisDependency() {
        return mAxisDependency;
    }

    @Override
    /**
     * Method description.
     */
    public void setAxisDependency(YAxis.AxisDependency dependency) {
        mAxisDependency = dependency;
    }


    /**
     * ###### ###### DATA RELATED METHODS ###### ######
     */

    @Override
    /**
     * Method description.
     */
    public int getIndexInEntries(int xIndex) {

        for (int i = 0; i < getEntryCount(); i++) {
            if (xIndex == getEntryForIndex(i).getX())
                return i;
        }

        return -1;
    }

    @Override
    /**
     * Method description.
     */
    public boolean removeFirst() {

        if (getEntryCount() > 0) {

            T entry = getEntryForIndex(0);
            return removeEntry(entry);
        } else
            return false;
    }

    @Override
    /**
     * Method description.
     */
    public boolean removeLast() {

        if (getEntryCount() > 0) {

            T e = getEntryForIndex(getEntryCount() - 1);
            return removeEntry(e);
        } else
            return false;
    }

    @Override
    /**
     * Method description.
     */
    public boolean removeEntryByXValue(float xValue) {

        T e = getEntryForXValue(xValue, Float.NaN);
        return removeEntry(e);
    }

    @Override
    /**
     * Method description.
     */
    public boolean removeEntry(int index) {

        T e = getEntryForIndex(index);
        return removeEntry(e);
    }

    @Override
    /**
     * Method description.
     */
    public boolean contains(T e) {

        for (int i = 0; i < getEntryCount(); i++) {
            if (getEntryForIndex(i).equals(e))
                return true;
        }

        return false;
    }

    protected void copy(BaseDataSet baseDataSet) {
        baseDataSet.mAxisDependency = mAxisDependency;
        baseDataSet.mColors = mColors;
        baseDataSet.mDrawIcons = mDrawIcons;
        baseDataSet.mDrawValues = mDrawValues;
        baseDataSet.mForm = mForm;
        baseDataSet.mFormLineDashEffect = mFormLineDashEffect;
        baseDataSet.mFormLineWidth = mFormLineWidth;
        baseDataSet.mFormSize = mFormSize;
        baseDataSet.mGradientColor = mGradientColor;
        baseDataSet.mGradientColors = mGradientColors;
        baseDataSet.mHighlightEnabled = mHighlightEnabled;
        baseDataSet.mIconsOffset = mIconsOffset;
        baseDataSet.mValueColors = mValueColors;
        baseDataSet.mValueFormatter = mValueFormatter;
        baseDataSet.mValueColors = mValueColors;
        baseDataSet.mValueTextSize = mValueTextSize;
        baseDataSet.mVisible = mVisible;
    }
}
