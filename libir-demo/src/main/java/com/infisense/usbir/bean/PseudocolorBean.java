package com.infisense.usbir.bean;

import android.graphics.Bitmap;

import com.infisense.iruvc.utils.CommonParams;

public class PseudocolorBean {

    /**
     * Private method description.
     */
    private CommonParams.PseudoColorType pcColor;

    private int img;

    /**
     * Method description.
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Method description.
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Private method description.
     */
    private Bitmap bitmap;

    /**
     * Method description.
     */
    public PseudocolorBean(CommonParams.PseudoColorType pcColor, String titleName) {
        this.pcColor = pcColor;
        this.titleName = titleName;
    }

    /**
     * Method description.
     */
    public PseudocolorBean(int img, CommonParams.PseudoColorType pcColor, String titleName) {
        this.img = img;
        this.pcColor = pcColor;
        this.titleName = titleName;
    }

    /**
     * Method description.
     */
    public CommonParams.PseudoColorType getPcColor() {
        return pcColor;
    }

    /**
     * Method description.
     */
    public void setPcColor(CommonParams.PseudoColorType pcColor) {
        this.pcColor = pcColor;
    }

    /**
     * Method description.
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * Method description.
     */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    /**
     * Private method description.
     */
    private String titleName;

    /**
     * Method description.
     */
    public int getImg() {
        return img;
    }

    /**
     * Method description.
     */
    public void setImg(int img) {
        this.img = img;
    }
}
