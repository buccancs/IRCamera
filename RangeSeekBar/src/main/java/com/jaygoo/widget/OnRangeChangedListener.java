package com.jaygoo.widget;

/**
 * ================================================
 *     ：JayGoo
 *     ：
 * ：2018/5/8
 *     :
 * ================================================
 */
public interface OnRangeChangedListener {
    void onRangeChanged(DefRangeSeekBar view, float leftValue, float rightValue, boolean isFromUser);

    void onStartTrackingTouch(DefRangeSeekBar view, boolean isLeft);

    void onStopTrackingTouch(DefRangeSeekBar view, boolean isLeft);
}
