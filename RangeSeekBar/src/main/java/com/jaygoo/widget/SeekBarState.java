package com.jaygoo.widget;

/**
 * ================================================
 * [Chinese text]    [Chinese text]: JayGoo
 * [Chinese text]    [Chinese text]: 
 * [Chinese text]: 2018/5/9
 * [Chinese text]    [Chinese text]: it works for draw indicator text
 * ================================================
 */
public class SeekBarState {
    public String indicatorText;
    public float value; // now progress value
    public boolean isMin;
    public boolean isMax;

    @Override
    public String toString() {
        return "indicatorText: " + indicatorText + " ,isMin: " + isMin + " ,isMax: " + isMax;
    }
}
