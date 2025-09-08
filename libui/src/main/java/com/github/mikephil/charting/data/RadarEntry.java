package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;

/**
 * Created by philipp on 13/06/16.
 */
@SuppressLint("ParcelCreator")
public class RadarEntry extends Entry {

    /**
     * Method description.
     */
    public RadarEntry(float value) {
        super(0f, value);
    }

    /**
     * Method description.
     */
    public RadarEntry(float value, Object data) {
        super(0f, value, data);
    }

    /**
     * This is the same as getY(). Returns the value of the RadarEntry.
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
    public RadarEntry copy() {
        RadarEntry e = new RadarEntry(getY(), getData());
        return e;
    }

    @Deprecated
    @Override
    /**
     * Method description.
     */
    public void setX(float x) {
        super.setX(x);
    }

    @Deprecated
    @Override
    /**
     * Method description.
     */
    public float getX() {
        return super.getX();
    }
}
