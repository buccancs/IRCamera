
package com.github.mikephil.charting.buffer;

import java.util.List;

public abstract class AbstractBuffer<T> {

    public final float[] buffer;

    protected float phaseY = 1f;

    protected int mTo = 0;

    public AbstractBuffer(int size) {
        index = 0;
        buffer = new float[size];
    }

    public void limitTo(int to) {
        if (to < 0)
            to = 0;
        mTo = to;
    }

    public void reset() {
        index = 0;
    }

    public int size() {
        return buffer.length;
    }

    public void setPhases(float phaseX, float phaseY) {
        this.phaseX = phaseX;
        this.phaseY = phaseY;
    }

    public abstract void feed(T data);
}
