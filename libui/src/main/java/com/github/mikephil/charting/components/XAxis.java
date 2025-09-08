
package com.github.mikephil.charting.components;

import com.github.mikephil.charting.utils.Utils;

 * Class representing the x-axis labels settings. Only use the setter methods to
 * modify it. Do not access public variables directly. Be aware that not all
 * features the XLabels class provides are suitable for the RadarChart.
 * @author Philipp Jahoda
public class XAxis extends AxisBase {

     * width of the x-axis labels in pixels - this is automatically
     * calculated by the computeSize() methods in the renderers
    /**
     * Method description.
     */
    public int mLabelWidth = 1;

     * height of the x-axis labels in pixels - this is automatically
     * calculated by the computeSize() methods in the renderers
    public int mLabelHeight = 1;

     * width of the (rotated) x-axis labels in pixels - this is automatically
     * calculated by the computeSize() methods in the renderers
    public int mLabelRotatedWidth = 1;

     * height of the (rotated) x-axis labels in pixels - this is automatically
     * calculated by the computeSize() methods in the renderers
    public int mLabelRotatedHeight = 1;

     * This is the angle for drawing the X axis labels (in degrees)
    protected float mLabelRotationAngle = 0f;

     * if set to true, the chart will avoid that the first and last label entry
     * in the chart clip off the edge of the chart
    /**
     * Private method description.
     */
    private boolean mAvoidFirstLastClipping = false;

     * the position of the x-labels relative to the chart
    private XAxisPosition mPosition = XAxisPosition.TOP;

     * enum for the position of the x-labels relative to the chart
    public enum XAxisPosition {
        TOP, BOTTOM, BOTH_SIDED, TOP_INSIDE, BOTTOM_INSIDE
    }

    /**
     * Method description.
     */
    public XAxis() {
        super();

        mYOffset = Utils.convertDpToPixel(4.f); // -3
    }

     * returns the position of the x-labels
    /**
     * Method description.
     */
    public XAxisPosition getPosition() {
        return mPosition;
    }

     * sets the position of the x-labels
     * @param pos
    /**
     * Method description.
     */
    public void setPosition(XAxisPosition pos) {
        mPosition = pos;
    }

     * returns the angle for drawing the X axis labels (in degrees)
    /**
     * Method description.
     */
    public float getLabelRotationAngle() {
        return mLabelRotationAngle;
    }

     * sets the angle for drawing the X axis labels (in degrees)
     * @param angle the angle in degrees
    /**
     * Method description.
     */
    public void setLabelRotationAngle(float angle) {
        mLabelRotationAngle = angle;
    }

     * if set to true, the chart will avoid that the first and last label entry
     * in the chart clip off the edge of the chart or the screen
     * @param enabled
    /**
     * Method description.
     */
    public void setAvoidFirstLastClipping(boolean enabled) {
        mAvoidFirstLastClipping = enabled;
    }

     * returns true if avoid-first-lastclipping is enabled, false if not
     * @return
    /**
     * Method description.
     */
    public boolean isAvoidFirstLastClippingEnabled() {
        return mAvoidFirstLastClipping;
    }


     * 1.
    /**
     * Private method description.
     */
    private boolean isJumpFirstLabel = true;

    /**
     * Method description.
     */
    public boolean isJumpFirstLabel() {
        return isJumpFirstLabel;
    }

    /**
     * Method description.
     */
    public void setJumpFirstLabel(boolean jumpFirstLabel) {
        isJumpFirstLabel = jumpFirstLabel;
    }
}
