package com.infisense.usbir.bean;

import android.graphics.Bitmap;

public class ImgBean {

    /**
     * Private method description.
     */
    private int pcColor;
    private int img;
    private String titleName;
    private Bitmap bitmap;

    /**
     * Method description.
     */
    public ImgBean(int pcColor, String titleName) {
        this.pcColor = pcColor;
        this.titleName = titleName;
    }

    /**
     * Method description.
     */
    public ImgBean(int img, int pcColor, String titleName) {
        this.img = img;
        this.pcColor = pcColor;
        this.titleName = titleName;
    }

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
     * Method description.
     */
    public int getPcColor() {
        return pcColor;
    }

    /**
     * Method description.
     */
    public void setPcColor(int pcColor) {
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
