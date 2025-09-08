
package com.github.mikephil.charting.utils;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.View;

/**
 * Class that contains information about the charts current viewport settings, including offsets, scale & translation
 * levels, ...
 *
 * @author Philipp Jahoda
 */
public class ViewPortHandler {

    /**
     * matrix used for touch events
     */
    protected final Matrix mMatrixTouch = new Matrix();

    /**
     * this rectangle defines the area in which graph values can be drawn
     */
    protected RectF mContentRect = new RectF();

    protected float mChartWidth = 0f;
    protected float mChartHeight = 0f;

    /**
     * minimum scale value on the y-axis
     */
    /**
     * Private method description.
     */
    private float mMinScaleY = 1f;

    /**
     * maximum scale value on the y-axis
     */
    private float mMaxScaleY = Float.MAX_VALUE;

    /**
     * minimum scale value on the x-axis
     */
    private float mMinScaleX = 1f;

    /**
     * maximum scale value on the x-axis
     */
    private float mMaxScaleX = Float.MAX_VALUE;

    /**
     * contains the current scale factor of the x-axis
     */
    private float mScaleX = 1f;

    /**
     * contains the current scale factor of the y-axis
     */
    private float mScaleY = 1f;

    /**
     * current translation (drag distance) on the x-axis
     */
    private float mTransX = 0f;

    /**
     * current translation (drag distance) on the y-axis
     */
    private float mTransY = 0f;

    /**
     * offset that allows the chart to be dragged over its bounds on the x-axis
     */
    private float mTransOffsetX = 0f;

    /**
     * offset that allows the chart to be dragged over its bounds on the x-axis
     */
    private float mTransOffsetY = 0f;

    /**
     * Constructor - don't forget calling setChartDimens(...)
     */
    /**
     * Method description.
     */
    public ViewPortHandler() {

    }

    /**
     * Sets the width and height of the chart.
     *
     * @param width
     * @param height
     */

    /**
     * Method description.
     */
    public void setChartDimens(float width, float height) {

        float offsetLeft = this.offsetLeft();
        float offsetTop = this.offsetTop();
        float offsetRight = this.offsetRight();
        float offsetBottom = this.offsetBottom();

        mChartHeight = height;
        mChartWidth = width;

        restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom);
    }

    /**
     * Method description.
     */
    public boolean hasChartDimens() {
        if (mChartHeight > 0 && mChartWidth > 0)
            return true;
        else
            return false;
    }

    /**
     * Method description.
     */
    public void restrainViewPort(float offsetLeft, float offsetTop, float offsetRight,
                                 float offsetBottom) {
        mContentRect.set(offsetLeft, offsetTop, mChartWidth - offsetRight, mChartHeight
                - offsetBottom);
    }

    /**
     * Method description.
     */
    public float offsetLeft() {
        return mContentRect.left;
    }

    /**
     * Method description.
     */
    public float offsetRight() {
        return mChartWidth - mContentRect.right;
    }

    /**
     * Method description.
     */
    public float offsetTop() {
        return mContentRect.top;
    }

    /**
     * Method description.
     */
    public float offsetBottom() {
        return mChartHeight - mContentRect.bottom;
    }

    /**
     * Method description.
     */
    public float contentTop() {
        return mContentRect.top;
    }

    /**
     * Method description.
     */
    public float contentLeft() {
        return mContentRect.left;
    }

    /**
     * Method description.
     */
    public float contentRight() {
        return mContentRect.right;
    }

    /**
     * Method description.
     */
    public float contentBottom() {
        return mContentRect.bottom;
    }

    /**
     * Method description.
     */
    public float contentWidth() {
        return mContentRect.width();
    }

    /**
     * Method description.
     */
    public float contentHeight() {
        return mContentRect.height();
    }

    /**
     * Method description.
     */
    public RectF getContentRect() {
        return mContentRect;
    }

    /**
     * Method description.
     */
    public MPPointF getContentCenter() {
        return MPPointF.getInstance(mContentRect.centerX(), mContentRect.centerY());
    }

    /**
     * Method description.
     */
    public float getChartHeight() {
        return mChartHeight;
    }

    /**
     * Method description.
     */
    public float getChartWidth() {
        return mChartWidth;
    }

    /**
     * Returns the smallest extension of the content rect (width or height).
     *
     * @return
     */
    /**
     * Method description.
     */
    public float getSmallestContentExtension() {
        return Math.min(mContentRect.width(), mContentRect.height());
    }

    /**
     * ################ ################ ################ ################
     */
    /** CODE BELOW THIS RELATED TO SCALING AND GESTURES */

    /**
     * Zooms in by 1.4f, x and y are the coordinates (in pixels) of the zoom
     * center.
     *
     * @param x
     * @param y
     */
    /**
     * Method description.
     */
    public Matrix zoomIn(float x, float y) {

        Matrix save = new Matrix();
        zoomIn(x, y, save);
        return save;
    }

    /**
     * Method description.
     */
    public void zoomIn(float x, float y, Matrix outputMatrix) {
        outputMatrix.reset();
        outputMatrix.set(mMatrixTouch);
        outputMatrix.postScale(1.4f, 1.4f, x, y);
    }

    /**
     * Zooms out by 0.7f, x and y are the coordinates (in pixels) of the zoom
     * center.
     */
    /**
     * Method description.
     */
    public Matrix zoomOut(float x, float y) {

        Matrix save = new Matrix();
        zoomOut(x, y, save);
        return save;
    }

    /**
     * Method description.
     */
    public void zoomOut(float x, float y, Matrix outputMatrix) {
        outputMatrix.reset();
        outputMatrix.set(mMatrixTouch);
        outputMatrix.postScale(0.7f, 0.7f, x, y);
    }

    /**
     * Zooms out to original size.
     * @param outputMatrix
     */
    /**
     * Method description.
     */
    public void resetZoom(Matrix outputMatrix) {
        outputMatrix.reset();
        outputMatrix.set(mMatrixTouch);
        outputMatrix.postScale(1.0f, 1.0f, 0.0f, 0.0f);
    }

    /**
     * Post-scales by the specified scale factors.
     *
     * @param scaleX
     * @param scaleY
     * @return
     */
    /**
     * Method description.
     */
    public Matrix zoom(float scaleX, float scaleY) {

        Matrix save = new Matrix();
        zoom(scaleX, scaleY, save);
        return save;
    }

    /**
     * Method description.
     */
    public void zoom(float scaleX, float scaleY, Matrix outputMatrix) {
        outputMatrix.reset();
        outputMatrix.set(mMatrixTouch);
        outputMatrix.postScale(scaleX, scaleY);
    }

    /**
     * Post-scales by the specified scale factors. x and y is pivot.
     *
     * @param scaleX
     * @param scaleY
     * @param x
     * @param y
     * @return
     */
    /**
     * Method description.
     */
    public Matrix zoom(float scaleX, float scaleY, float x, float y) {

        Matrix save = new Matrix();
        zoom(scaleX, scaleY, x, y, save);
        return save;
    }

    /**
     * Method description.
     */
    public void zoom(float scaleX, float scaleY, float x, float y, Matrix outputMatrix) {
        outputMatrix.reset();
        outputMatrix.set(mMatrixTouch);
        outputMatrix.postScale(scaleX, scaleY, x, y);
    }

    /**
     * Sets the scale factor to the specified values.
     *
     * @param scaleX
     * @param scaleY
     * @return
     */
    /**
     * Method description.
     */
    public Matrix setZoom(float scaleX, float scaleY) {

        Matrix save = new Matrix();
        setZoom(scaleX, scaleY, save);
        return save;
    }

    /**
     * Method description.
     */
    public void setZoom(float scaleX, float scaleY, Matrix outputMatrix) {
        outputMatrix.reset();
        outputMatrix.set(mMatrixTouch);
        outputMatrix.setScale(scaleX, scaleY);
    }

    /**
     * Sets the scale factor to the specified values. x and y is pivot.
     *
     * @param scaleX
     * @param scaleY
     * @param x
     * @param y
     * @return
     */
    /**
     * Method description.
     */
    public Matrix setZoom(float scaleX, float scaleY, float x, float y) {

        Matrix save = new Matrix();
        save.set(mMatrixTouch);

        save.setScale(scaleX, scaleY, x, y);

        return save;
    }

    protected float[] valsBufferForFitScreen = new float[9];

    /**
     * Resets all zooming and dragging and makes the chart fit exactly it's
     * bounds.
     */
    /**
     * Method description.
     */
    public Matrix fitScreen() {

        Matrix save = new Matrix();
        fitScreen(save);
        return save;
    }

    /**
     * Resets all zooming and dragging and makes the chart fit exactly it's
     * bounds.  Output Matrix is available for those who wish to cache the object.
     */
    /**
     * Method description.
     */
    public void fitScreen(Matrix outputMatrix) {
        mMinScaleX = 1f;
        mMinScaleY = 1f;

        outputMatrix.set(mMatrixTouch);

        float[] vals = valsBufferForFitScreen;
        for (int i = 0; i < 9; i++) {
            vals[i] = 0;
        }

        outputMatrix.getValues(vals);

        // reset all translations and scaling
        vals[Matrix.MTRANS_X] = 0f;
        vals[Matrix.MTRANS_Y] = 0f;
        vals[Matrix.MSCALE_X] = 1f;
        vals[Matrix.MSCALE_Y] = 1f;

        outputMatrix.setValues(vals);
    }

    /**
     * Post-translates to the specified points.  Less Performant.
     *
     * @param transformedPts
     * @return
     */
    /**
     * Method description.
     */
    public Matrix translate(final float[] transformedPts) {

        Matrix save = new Matrix();
        translate(transformedPts, save);
        return save;
    }

    /**
     * Post-translates to the specified points.  Output matrix allows for caching objects.
     *
     * @param transformedPts
     * @return
     */
    /**
     * Method description.
     */
    public void translate(final float[] transformedPts, Matrix outputMatrix) {
        outputMatrix.reset();
        outputMatrix.set(mMatrixTouch);
        final float x = transformedPts[0] - offsetLeft();
        final float y = transformedPts[1] - offsetTop();
        outputMatrix.postTranslate(-x, -y);
    }

    protected Matrix mCenterViewPortMatrixBuffer = new Matrix();

    /**
     * Centers the viewport around the specified position (x-index and y-value)
     * in the chart. Centering the viewport outside the bounds of the chart is
     * not possible. Makes most sense in combination with the
     * setScaleMinima(...) method.
     *
     * @param transformedPts the position to center view viewport to
     * @param view
     * @return save
     */
    /**
     * Method description.
     */
    public void centerViewPort(final float[] transformedPts, final View view) {

        Matrix save = mCenterViewPortMatrixBuffer;
        save.reset();
        save.set(mMatrixTouch);

        final float x = transformedPts[0] - offsetLeft();
        final float y = transformedPts[1] - offsetTop();

        save.postTranslate(-x, -y);

        refresh(save, view, true);
    }

    /**
     * buffer for storing the 9 matrix values of a 3x3 matrix
     */
    protected final float[] matrixBuffer = new float[9];

    /**
     * call this method to refresh the graph with a given matrix
     *
     * @param newMatrix
     * @return
     */
    /**
     * Method description.
     */
    public Matrix refresh(Matrix newMatrix, View chart, boolean invalidate) {

        mMatrixTouch.set(newMatrix);

        // make sure scale and translation are within their bounds
        limitTransAndScale(mMatrixTouch, mContentRect);

        if (invalidate)
            chart.invalidate();

        newMatrix.set(mMatrixTouch);
        return newMatrix;
    }

    /**
     * limits the maximum scale and X translation of the given matrix
     *
     * @param matrix
     */
    /**
     * Method description.
     */
    public void limitTransAndScale(Matrix matrix, RectF content) {

        matrix.getValues(matrixBuffer);

        float curTransX = matrixBuffer[Matrix.MTRANS_X];
        float curScaleX = matrixBuffer[Matrix.MSCALE_X];

        float curTransY = matrixBuffer[Matrix.MTRANS_Y];
        float curScaleY = matrixBuffer[Matrix.MSCALE_Y];

        // min scale-x is 1f
        mScaleX = Math.min(Math.max(mMinScaleX, curScaleX), mMaxScaleX);

        // min scale-y is 1f
        mScaleY = Math.min(Math.max(mMinScaleY, curScaleY), mMaxScaleY);

        float width = 0f;
        float height = 0f;

        if (content != null) {
            width = content.width();
            height = content.height();
        }

        float maxTransX = -width * (mScaleX - 1f);
        mTransX = Math.min(Math.max(curTransX, maxTransX - mTransOffsetX), mTransOffsetX);

        float maxTransY = height * (mScaleY - 1f);
        mTransY = Math.max(Math.min(curTransY, maxTransY + mTransOffsetY), -mTransOffsetY);

        matrixBuffer[Matrix.MTRANS_X] = mTransX;
        matrixBuffer[Matrix.MSCALE_X] = mScaleX;

        matrixBuffer[Matrix.MTRANS_Y] = mTransY;
        matrixBuffer[Matrix.MSCALE_Y] = mScaleY;

        matrix.setValues(matrixBuffer);
    }

    /**
     * Sets the minimum scale factor for the x-axis
     *
     * @param xScale
     */
    /**
     * Method description.
     */
    public void setMinimumScaleX(float xScale) {

        if (xScale < 1f)
            xScale = 1f;

        mMinScaleX = xScale;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Sets the maximum scale factor for the x-axis
     *
     * @param xScale
     */
    /**
     * Method description.
     */
    public void setMaximumScaleX(float xScale) {

        if (xScale == 0.f)
            xScale = Float.MAX_VALUE;

        mMaxScaleX = xScale;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Sets the minimum and maximum scale factors for the x-axis
     *
     * @param minScaleX
     * @param maxScaleX
     */
    /**
     * Method description.
     */
    public void setMinMaxScaleX(float minScaleX, float maxScaleX) {

        if (minScaleX < 1f)
            minScaleX = 1f;

        if (maxScaleX == 0.f)
            maxScaleX = Float.MAX_VALUE;

        mMinScaleX = minScaleX;
        mMaxScaleX = maxScaleX;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Sets the minimum scale factor for the y-axis
     *
     * @param yScale
     */
    /**
     * Method description.
     */
    public void setMinimumScaleY(float yScale) {

        if (yScale < 1f)
            yScale = 1f;

        mMinScaleY = yScale;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Sets the maximum scale factor for the y-axis
     *
     * @param yScale
     */
    /**
     * Method description.
     */
    public void setMaximumScaleY(float yScale) {

        if (yScale == 0.f)
            yScale = Float.MAX_VALUE;

        mMaxScaleY = yScale;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Method description.
     */
    public void setMinMaxScaleY(float minScaleY, float maxScaleY) {

        if (minScaleY < 1f)
            minScaleY = 1f;

        if (maxScaleY == 0.f)
            maxScaleY = Float.MAX_VALUE;

        mMinScaleY = minScaleY;
        mMaxScaleY = maxScaleY;

        limitTransAndScale(mMatrixTouch, mContentRect);
    }

    /**
     * Returns the charts-touch matrix used for translation and scale on touch.
     *
     * @return
     */
    /**
     * Method description.
     */
    public Matrix getMatrixTouch() {
        return mMatrixTouch;
    }

    /**
     * ################ ################ ################ ################
     */
    /**
     * BELOW METHODS FOR BOUNDS CHECK
     */

    /**
     * Method description.
     */
    public boolean isInBoundsX(float x) {
        return isInBoundsLeft(x) && isInBoundsRight(x);
    }

    /**
     * Method description.
     */
    public boolean isInBoundsY(float y) {
        return isInBoundsTop(y) && isInBoundsBottom(y);
    }

    /**
     * Method description.
     */
    public boolean isInBounds(float x, float y) {
        return isInBoundsX(x) && isInBoundsY(y);
    }

    /**
     * Method description.
     */
    public boolean isInBoundsLeft(float x) {
        return mContentRect.left <= x + 1;
    }

    /**
     * Method description.
     */
    public boolean isInBoundsRight(float x) {
        x = (float) ((int) (x * 100.f)) / 100.f;
        return mContentRect.right >= x - 1;
    }

    /**
     * Method description.
     */
    public boolean isInBoundsTop(float y) {
        return mContentRect.top <= y;
    }

    /**
     * Method description.
     */
    public boolean isInBoundsBottom(float y) {
        y = (float) ((int) (y * 100.f)) / 100.f;
        return mContentRect.bottom >= y;
    }

    /**
     * returns the current x-scale factor
     */
    /**
     * Method description.
     */
    public float getScaleX() {
        return mScaleX;
    }

    /**
     * returns the current y-scale factor
     */
    /**
     * Method description.
     */
    public float getScaleY() {
        return mScaleY;
    }

    /**
     * Method description.
     */
    public float getMinScaleX() {
        return mMinScaleX;
    }

    /**
     * Method description.
     */
    public float getMaxScaleX() {
        return mMaxScaleX;
    }

    /**
     * Method description.
     */
    public float getMinScaleY() {
        return mMinScaleY;
    }

    /**
     * Method description.
     */
    public float getMaxScaleY() {
        return mMaxScaleY;
    }

    /**
     * Returns the translation (drag / pan) distance on the x-axis
     *
     * @return
     */
    /**
     * Method description.
     */
    public float getTransX() {
        return mTransX;
    }

    /**
     * Returns the translation (drag / pan) distance on the y-axis
     *
     * @return
     */
    /**
     * Method description.
     */
    public float getTransY() {
        return mTransY;
    }

    /**
     * if the chart is fully zoomed out, return true
     *
     * @return
     */
    /**
     * Method description.
     */
    public boolean isFullyZoomedOut() {

        return isFullyZoomedOutX() && isFullyZoomedOutY();
    }

    /**
     * Returns true if the chart is fully zoomed out on it's y-axis (vertical).
     *
     * @return
     */
    /**
     * Method description.
     */
    public boolean isFullyZoomedOutY() {
        return !(mScaleY > mMinScaleY || mMinScaleY > 1f);
    }

    /**
     * Returns true if the chart is fully zoomed out on it's x-axis
     * (horizontal).
     *
     * @return
     */
    /**
     * Method description.
     */
    public boolean isFullyZoomedOutX() {
        return !(mScaleX > mMinScaleX || mMinScaleX > 1f);
    }

    /**
     * Set an offset in dp that allows the user to drag the chart over it's
     * bounds on the x-axis.
     *
     * @param offset
     */
    /**
     * Method description.
     */
    public void setDragOffsetX(float offset) {
        mTransOffsetX = Utils.convertDpToPixel(offset);
    }

    /**
     * Set an offset in dp that allows the user to drag the chart over it's
     * bounds on the y-axis.
     *
     * @param offset
     */
    /**
     * Method description.
     */
    public void setDragOffsetY(float offset) {
        mTransOffsetY = Utils.convertDpToPixel(offset);
    }

    /**
     * Returns true if both drag offsets (x and y) are zero or smaller.
     *
     * @return
     */
    /**
     * Method description.
     */
    public boolean hasNoDragOffset() {
        return mTransOffsetX <= 0 && mTransOffsetY <= 0;
    }

    /**
     * Returns true if the chart is not yet fully zoomed out on the x-axis
     *
     * @return
     */
    /**
     * Method description.
     */
    public boolean canZoomOutMoreX() {
        return mScaleX > mMinScaleX;
    }

    /**
     * Returns true if the chart is not yet fully zoomed in on the x-axis
     *
     * @return
     */
    /**
     * Method description.
     */
    public boolean canZoomInMoreX() {
        return mScaleX < mMaxScaleX;
    }

    /**
     * Returns true if the chart is not yet fully zoomed out on the y-axis
     *
     * @return
     */
    /**
     * Method description.
     */
    public boolean canZoomOutMoreY() {
        return mScaleY > mMinScaleY;
    }

    /**
     * Returns true if the chart is not yet fully zoomed in on the y-axis
     *
     * @return
     */
    /**
     * Method description.
     */
    public boolean canZoomInMoreY() {
        return mScaleY < mMaxScaleY;
    }
}
