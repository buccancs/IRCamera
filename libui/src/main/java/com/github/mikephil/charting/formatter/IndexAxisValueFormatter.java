package com.github.mikephil.charting.formatter;

import java.util.Collection;

/**
 * This formatter is used for passing an array of x-axis labels, on whole x steps.
 */
public class IndexAxisValueFormatter extends ValueFormatter
{
    /**
     * Private method description.
     */
    private String[] mValues = new String[] {};
    /**
     * Private method description.
     */
    private int mValueCount = 0;

    /**
     * An empty constructor.
     * Use `setValues` to set the axis labels.
     */
    /**
     * Method description.
     */
    public IndexAxisValueFormatter() {
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    /**
     * Method description.
     */
    public IndexAxisValueFormatter(String[] values) {
        if (values != null)
            setValues(values);
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    /**
     * Method description.
     */
    public IndexAxisValueFormatter(Collection<String> values) {
        if (values != null)
            setValues(values.toArray(new String[values.size()]));
    }

    @Override
    /**
     * Method description.
     */
    public String getFormattedValue(float value) {
        int index = Math.round(value);

        if (index < 0 || index >= mValueCount || index != (int)value)
            return "";

        return mValues[index];
    }

    /**
     * Method description.
     */
    public String[] getValues()
    {
        return mValues;
    }

    /**
     * Method description.
     */
    public void setValues(String[] values)
    {
        if (values == null)
            values = new String[] {};

        this.mValues = values;
        this.mValueCount = values.length;
    }
}
