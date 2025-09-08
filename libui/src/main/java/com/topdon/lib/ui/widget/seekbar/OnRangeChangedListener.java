package com.topdon.lib.ui.widget.seekbar;

/**
 * ================================================
 * [Chinese text]    [Chinese text]: JayGoo
 * [Chinese text]    [Chinese text]: 
 * [Chinese text]: 2018/5/8
 * [Chinese text]    [Chinese text]:
 * ================================================
 */
public interface OnRangeChangedListener {
    void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser,int tempMode);

    void onStartTrackingTouch(RangeSeekBar view, boolean isLeft);

    void onStopTrackingTouch(RangeSeekBar view, boolean isLeft);
}
