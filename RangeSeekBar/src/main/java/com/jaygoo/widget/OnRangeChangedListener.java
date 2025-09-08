package com.jaygoo.widget;

/**
 * ================================================
 * [Chinese text]    [Chinese text]: JayGoo
 * [Chinese text]    [Chinese text]: 
 * [Chinese text]: 2018/5/8
 * [Chinese text]    [Chinese text]:
 * ================================================
 */
public interface OnRangeChangedListener {
    void onRangeChanged(DefRangeSeekBar view, float leftValue, float rightValue, boolean isFromUser);

    void onStartTrackingTouch(DefRangeSeekBar view, boolean isLeft);

    void onStopTrackingTouch(DefRangeSeekBar view, boolean isLeft);
}
