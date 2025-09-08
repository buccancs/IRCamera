package com.github.mikephil.charting.model;

public class GradientColor {

    /**
     * Private method description.
     */
    private int startColor;
    private int endColor;
    
    /**
     * Method description.
     */
    public GradientColor(int startColor, int endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    /**
     * Method description.
     */
    public int getStartColor() {
        return startColor;
    }

    /**
     * Method description.
     */
    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    /**
     * Method description.
     */
    public int getEndColor() {
        return endColor;
    }

    /**
     * Method description.
     */
    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }
}
