package com.topdon.lib.ui.widget.seekbar;

/**
 * ================================================
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * Comment removed (contained Chinese characters)
 * ================================================
 */
public interface OnRangeChangedListener {
 void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser,int tempMode);

 void onStartTrackingTouch(RangeSeekBar view, boolean isLeft);

 void onStopTrackingTouch(RangeSeekBar view, boolean isLeft);
}
