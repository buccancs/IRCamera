package com.infisense.usbir.view;//package com.infisense.usbir.view;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.FrameLayout;
//
//import androidx.core.content.ContextCompat;
//
//import com.infisense.usbir.R;
//
//public class TemperatureSeeklBar extends View implements View.OnTouchListener {
//
//    private static final String TAG = "TemperatureToolBar";
//    private int TOUCH_TOLERANCE = 48;
//    private final int HORIZONAL_MARGIN = 48;
//    private final int VERTICAL_MARGIN = 24;
//    private float TEMPERATURE_MARGIN = 2f;
//    private float TEMPERATURE_MIN_RANGE = 5f;
//    private static final int[] STATE_NORMAL = {};
//    private static final int[] STATE_PRESSED = {
//            android.R.attr.state_pressed, android.R.attr.state_window_focused,
//    };
//
//    private float maxTemperature = 60f;
//    private float minTemperature = -10f;
//
//    private Drawable scrollBarBackground;
//    private Drawable thumbLow;
//    private Drawable thumbHigh;
//
//    private int scollBarWidth;
//    private int scollBarHeight;
//    private int backgroundWidth;
//    private int backgroundHeight;
//    private int thumbWidth;
//    private int thumbHeight;
//    private float thumbLowLocation;
//    private float thumbHighLocation;
//
//    private int startY;
//    private int endY;
//    private boolean movingThumbLow;
//
//    public float getLowTemperature() {
//        return lowTemperature;
//    }
//
//    public float getHighTemperature() {
//        return highTemperature;
//    }
//
//    private boolean movingThumbHigh;
//
//    private int leftMargin;
//    private int topMargin;
//    private FrameLayout.LayoutParams layoutParams;
//    private float lowTemperature;
//    private float highTemperature;
//
//    public TemperatureSeeklBar(Context context) {
//        this(context, null, 0);
//    }
//
//    public TemperatureSeeklBar(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public TemperatureSeeklBar(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//
//        scrollBarBackground = ContextCompat.getDrawable(context, R.drawable.pseudocolor);
//        thumbLow = ContextCompat.getDrawable(context, R.drawable.low);
//        thumbHigh = ContextCompat.getDrawable(context, R.drawable.high);
//        thumbLow.setState(STATE_NORMAL);
//        thumbHigh.setState(STATE_NORMAL);
//
//        setOnTouchListener(this);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.w(TAG, "onMeasure");
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(scollBarWidth, scollBarHeight);
//    }
//
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        Log.w(TAG, "onDraw");
//        super.onDraw(canvas);
//
//        scrollBarBackground.setBounds((scollBarWidth - backgroundWidth) / 2 + TOUCH_TOLERANCE, thumbHeight / 2, (scollBarWidth + backgroundWidth) / 2 + TOUCH_TOLERANCE, scollBarHeight - thumbHeight / 2);
//        scrollBarBackground.draw(canvas);
//        if (movingThumbLow) {
//            float lowRate = (lowTemperature - minTemperature) / (maxTemperature - minTemperature);
//            int thumbLowLocation = (int) (backgroundHeight * (1 - lowRate));
//            Log.w(TAG, "thumbLowLocation = " + thumbLowLocation);
//            thumbLow.setBounds((scollBarWidth - thumbWidth) / 2 + TOUCH_TOLERANCE, thumbLowLocation, (scollBarWidth + thumbWidth) / 2 + TOUCH_TOLERANCE, thumbLowLocation + thumbHeight);
//        } else {
//            thumbLow.setBounds((scollBarWidth - thumbWidth) / 2 + TOUCH_TOLERANCE, (int) thumbLowLocation, (scollBarWidth + thumbWidth) / 2 + TOUCH_TOLERANCE, (int) thumbLowLocation + thumbHeight);
//        }
//        thumbLow.draw(canvas);
//        if (movingThumbHigh) {
//            float highRate = (highTemperature - minTemperature) / (maxTemperature - minTemperature);
//            int thumbHighLocation = (int) (backgroundHeight * (1 - highRate));
//            Log.w(TAG, "thumbHighLocation = " + thumbHighLocation);
//            thumbHigh.setBounds((scollBarWidth - thumbWidth) / 2 + TOUCH_TOLERANCE, thumbHighLocation, (scollBarWidth + thumbWidth) / 2 + TOUCH_TOLERANCE, thumbHighLocation + thumbHeight);
//        } else {
//            thumbHigh.setBounds((scollBarWidth - thumbWidth) / 2 + TOUCH_TOLERANCE, (int) thumbHighLocation, (scollBarWidth + thumbWidth) / 2 + TOUCH_TOLERANCE, (int) thumbHighLocation + thumbHeight);
//        }
//        thumbHigh.draw(canvas);
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            Log.w(TAG, "ACTION_DOWN");
//            startY = (int) (event.getY());
//            Log.w(TAG, "startY = " + startY + "; thumbLowLocation = " + thumbLowLocation + "; TOUCH_TOLERANCE = " + TOUCH_TOLERANCE);
//            if (startY > thumbLowLocation - TOUCH_TOLERANCE && startY < thumbLowLocation + thumbHeight + TOUCH_TOLERANCE) {
//                movingThumbLow = true;
//            } else if (startY > thumbHighLocation - TOUCH_TOLERANCE && startY < thumbHighLocation + thumbHeight + TOUCH_TOLERANCE) {
//                movingThumbHigh = true;
//            }
//            invalidate();
//            return true;
//        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            Log.w(TAG, "ACTION_MOVE");
//            endY = (int) (event.getY());
//            if (movingThumbLow) {
//                Log.w(TAG, "thumbLowLocation = " + thumbLowLocation + "; thumbHighLocation = " + thumbHighLocation + "; thumbHeight = " + thumbHeight);
//                Log.w(TAG, "startY = " + startY + "; endY = " + endY);
//                if (thumbLowLocation + endY - startY > thumbHighLocation + thumbHeight) {
//                    lowTemperature = (1 - (thumbLowLocation + endY - startY) / backgroundHeight) * (maxTemperature - minTemperature) + minTemperature;
//                    if (lowTemperature < minTemperature) {
//                        lowTemperature = minTemperature;
//                    }
//                } else {
//                    lowTemperature = (1 - (thumbHighLocation + thumbHeight) / backgroundHeight) * (maxTemperature - minTemperature) + minTemperature;
//                }
//                Log.w(TAG, "lowTemperature = " + lowTemperature);
//            } else if (movingThumbHigh) {
//                if (thumbHighLocation + endY - startY < thumbLowLocation - thumbHeight) {
//                    highTemperature = (1 - (thumbHighLocation + endY - startY) / backgroundHeight) * (maxTemperature - minTemperature) + minTemperature;
//                    if (highTemperature > maxTemperature) {
//                        highTemperature = maxTemperature;
//                    }
//                } else {
//                    highTemperature = (1 - (thumbLowLocation - thumbHeight) / backgroundHeight) * (maxTemperature - minTemperature) + minTemperature;
//                }
//            }
//            invalidate();
//            return true;
//        } else if (event.getAction() == MotionEvent.ACTION_UP) {
//            Log.w(TAG, "ACTION_UP");
//            if (movingThumbLow) {
//                Log.w(TAG, "thumbLowLocation = " + thumbLowLocation + "; thumbHighLocation = " + thumbHighLocation + "; thumbHeight = " + thumbHeight);
//                Log.w(TAG, "startY = " + startY + "; endY = " + endY);
//                if (thumbLowLocation + endY - startY > thumbHighLocation + thumbHeight) {
//                    lowTemperature = (1 - (thumbLowLocation + endY - startY) / backgroundHeight) * (maxTemperature - minTemperature) + minTemperature;
//                    thumbLowLocation += endY - startY;
//                    if (lowTemperature < minTemperature) {
//                        lowTemperature = minTemperature;
//                        thumbLowLocation = backgroundHeight;
//                    }
//                } else {
//                    lowTemperature = (1 - (thumbHighLocation + thumbHeight) / backgroundHeight) * (maxTemperature - minTemperature) + minTemperature;
//                    thumbLowLocation = thumbHighLocation + thumbHeight;
//                }
//                movingThumbLow = false;
//                Log.w(TAG, "lowTemperature = " + lowTemperature);
//            } else if (movingThumbHigh) {
//                if (thumbHighLocation + endY - startY < thumbLowLocation - thumbHeight) {
//                    highTemperature = (1 - (thumbHighLocation + endY - startY) / backgroundHeight) * (maxTemperature - minTemperature) + minTemperature;
//                    thumbHighLocation += (endY - startY);
//                    if (highTemperature > maxTemperature) {
//                        highTemperature = maxTemperature;
//                        thumbHighLocation = 0;
//                    }
//                } else {
//                    highTemperature = (1 - (thumbLowLocation - thumbHeight) / backgroundHeight) * (maxTemperature - minTemperature) + minTemperature;
//                    thumbHighLocation = thumbLowLocation - thumbHeight;
//                }
//                movingThumbHigh = false;
//            }
//            invalidate();
//            return false;
//        } else {
//            return false;
//        }
//    }
//
//    public void setViewLayout(int bigWindowLeftMargin, int bigWindowTopMargin, int bigWindowWidth, int bigWindowHeight) {
//        scollBarHeight = bigWindowHeight - VERTICAL_MARGIN * 2;
//        Log.w(TAG, "setViewLayout: scollBarHeight = " + scollBarHeight);
//
//        backgroundWidth = scrollBarBackground.getIntrinsicWidth();
//        backgroundHeight = scrollBarBackground.getIntrinsicHeight();
//        thumbWidth = thumbLow.getIntrinsicWidth();
//        thumbHeight = thumbLow.getIntrinsicHeight();
//        float rate = (float) scollBarHeight / (float) (backgroundHeight + thumbHeight);
//        backgroundWidth = (int) (backgroundWidth * rate);
//        backgroundHeight = (int) (backgroundHeight * rate);
//        thumbWidth = (int) (thumbWidth * rate);
//        thumbHeight = (int) (thumbHeight * rate);
//        scollBarWidth = Math.max(backgroundWidth, thumbWidth) + 2 * TOUCH_TOLERANCE;
////        Log.w(TAG, "rate =" + rate + "; scollBarHeight = " + scollBarHeight + "; backgroundHeight = " + backgroundHeight + "; thumbHeight = " + thumbHeight);
//
//        float lowRate = (lowTemperature - minTemperature) / (maxTemperature - minTemperature);
//        thumbLowLocation = (int) (backgroundHeight * (1 - lowRate));
//        float highRate = (highTemperature - minTemperature) / (maxTemperature - minTemperature);
//        thumbHighLocation = (int) (backgroundHeight * (1 - highRate));
//
//        leftMargin = bigWindowLeftMargin + bigWindowWidth - scollBarWidth - HORIZONAL_MARGIN;
//        topMargin = bigWindowTopMargin + (bigWindowHeight - scollBarHeight) / 2;
//        layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
//        layoutParams.setMargins(leftMargin, topMargin, 0, 0);
//        setLayoutParams(layoutParams);
//        invalidate();
//    }
//
//    public void setTemperatureRange(float minTemperature, float maxTemperature) {
//        if (!movingThumbLow && !movingThumbHigh) {
//            if ((Math.abs(this.minTemperature - minTemperature) > TEMPERATURE_MARGIN || Math.abs(this.maxTemperature - maxTemperature) > TEMPERATURE_MARGIN) && maxTemperature - minTemperature > TEMPERATURE_MIN_RANGE) {
//                this.minTemperature = minTemperature - TEMPERATURE_MARGIN;
//                this.maxTemperature = maxTemperature + TEMPERATURE_MARGIN;
//                if (lowTemperature < minTemperature - TEMPERATURE_MARGIN) {
//                    lowTemperature = minTemperature - TEMPERATURE_MARGIN;
//                }
//                if (highTemperature < minTemperature - TEMPERATURE_MARGIN) {
//                    highTemperature = maxTemperature + TEMPERATURE_MARGIN;
//                }
//                if (highTemperature > maxTemperature + TEMPERATURE_MARGIN) {
//                    highTemperature = maxTemperature + TEMPERATURE_MARGIN;
//                }
//                if (lowTemperature > maxTemperature + TEMPERATURE_MARGIN) {
//                    lowTemperature = minTemperature - TEMPERATURE_MARGIN;
//                }
//                float lowRate = (lowTemperature - this.minTemperature) / (this.maxTemperature - this.minTemperature);
//                thumbLowLocation = (int) (backgroundHeight * (1 - lowRate));
//                float highRate = (highTemperature - this.minTemperature) / (this.maxTemperature - this.minTemperature);
//                thumbHighLocation = (int) (backgroundHeight * (1 - highRate));
//                invalidate();
//            }
//        }
//    }
//}
