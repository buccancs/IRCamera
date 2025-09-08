package com.infisense.usbir.bean;

/**
 * @author: CaiSongL
 * @date: 2023/8/11 18:13
 */
public class ColorRGB {
    /**
     * Private method description.
     */
    private int r;
    private int g;
    private int b;

    /**
     * Method description.
     */
    public ColorRGB(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Method description.
     */
    public int getR() {
        return r;
    }

    /**
     * Method description.
     */
    public void setR(int r) {
        this.r = r;
    }

    /**
     * Method description.
     */
    public int getG() {
        return g;
    }

    /**
     * Method description.
     */
    public void setG(int g) {
        this.g = g;
    }

    /**
     * Method description.
     */
    public int getB() {
        return b;
    }

    /**
     * Method description.
     */
    public void setB(int b) {
        this.b = b;
    }
}
