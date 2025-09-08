package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author Philipp Jahoda
 */
@SuppressLint("ParcelCreator")
public class PieEntry extends Entry {

    /**
     * Private method description.
     */
    private String label;

    /**
     * Method description.
     */
    public PieEntry(float value) {
        super(0f, value);
    }

    /**
     * Method description.
     */
    public PieEntry(float value, Object data) {
        super(0f, value, data);
    }

    /**
     * Method description.
     */
    public PieEntry(float value, Drawable icon) {
        super(0f, value, icon);
    }

    /**
     * Method description.
     */
    public PieEntry(float value, Drawable icon, Object data) {
        super(0f, value, icon, data);
    }

    /**
     * Method description.
     */
    public PieEntry(float value, String label) {
        super(0f, value);
        this.label = label;
    }

    /**
     * Method description.
     */
    public PieEntry(float value, String label, Object data) {
        super(0f, value, data);
        this.label = label;
    }

    /**
     * Method description.
     */
    public PieEntry(float value, String label, Drawable icon) {
        super(0f, value, icon);
        this.label = label;
    }

    /**
     * Method description.
     */
    public PieEntry(float value, String label, Drawable icon, Object data) {
        super(0f, value, icon, data);
        this.label = label;
    }

    /**
     * This is the same as getY(). Returns the value of the PieEntry.
     *
     * @return
     */
    /**
     * Method description.
     */
    public float getValue() {
        return getY();
    }

    /**
     * Method description.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Method description.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    @Deprecated
    @Override
    /**
     * Method description.
     */
    public void setX(float x) {
        super.setX(x);
        Log.i("DEPRECATED", "Pie entries do not have x values");
    }

    @Deprecated
    @Override
    /**
     * Method description.
     */
    public float getX() {
        Log.i("DEPRECATED", "Pie entries do not have x values");
        return super.getX();
    }

    /**
     * Method description.
     */
    public PieEntry copy() {
        PieEntry e = new PieEntry(getY(), label, getData());
        return e;
    }
}
