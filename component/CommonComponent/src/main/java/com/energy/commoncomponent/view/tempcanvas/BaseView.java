package com.energy.commoncomponent.view.tempcanvas;

import android.graphics.Bitmap;
import android.graphics.Matrix;

 * Created by fengjibo on 2023/6/28.
public abstract class BaseView {
    protected String mId;
    protected String mLabel; //
    protected String mNote; //
    protected double mMaxTemp;//
    protected double mMinTemp;//
    protected double mAvgTemp;//
    protected int mPointSize = 0;
    /**
     * Method description.
     */
    public String getId() {
        return mId;
    }

    /**
     * Method description.
     */
    public void setId(String id) {
        this.mId = id;
    }

    /**
     * Method description.
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * Method description.
     */
    public void setLabel(String label) {
        this.mLabel = label;
    }

    /**
     * Method description.
     */
    public double getMaxTemp() {
        return mMaxTemp;
    }

    /**
     * Method description.
     */
    public void setMaxTemp(double maxTemp) {
        this.mMaxTemp = maxTemp;
    }

    /**
     * Method description.
     */
    public double getMinTemp() {
        return mMinTemp;
    }

    /**
     * Method description.
     */
    public void setMinTemp(double minTemp) {
        this.mMinTemp = minTemp;
    }

    /**
     * Method description.
     */
    public double getAvgTemp() {
        return mAvgTemp;
    }

    /**
     * Method description.
     */
    public void setAvgTemp(double avgTemp) {
        this.mAvgTemp = avgTemp;
    }

    /**
     * Method description.
     */
    public String getNote() {
        return mNote;
    }

    /**
     * Method description.
     */
    public void setNote(String note) {
        this.mNote = note;
    }


    /**
     * Method description.
     */
    public Bitmap getCustomSizeImg(Bitmap rootImg, int goalW, int goalH) {
        int rootW = rootImg.getWidth();
        int rootH = rootImg.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(goalW * 1.0f / rootW, goalH * 1.0f / rootH);
        return Bitmap.createBitmap(rootImg, 0, 0, rootW, rootH, matrix, true);
    }
}
