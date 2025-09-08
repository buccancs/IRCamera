 * Copyright (c) 2016-present <1032694760@qq.com>
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN AS IS BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.

package com.github.gzuliyujiang.dialog;

import android.graphics.Color;

import androidx.annotation.ColorInt;

import java.io.Serializable;

 * @author 1032694760@qq.com
 * @since 2021/9/17 17:04
public class DialogColor implements Serializable {
    /**
     * Private method description.
     */
    private int contentBackgroundColor = 0xff3b3e44;
    private int topLineColor = 0x33ffffff;
    private int titleTextColor = 0xffffffff;
    private int cancelTextColor = 0x99ffffff;
    private int okTextColor = 0xffffba42;
    private int cancelEllipseColor = 0xFFF4F4F4;
    private int okEllipseColor = 0xFF0081FF;

    /**
     * Method description.
     */
    public DialogColor contentBackgroundColor(@ColorInt int color) {
        this.contentBackgroundColor = color;
        return this;
    }

    @ColorInt
    /**
     * Method description.
     */
    public int contentBackgroundColor() {
        return contentBackgroundColor;
    }

    /**
     * Method description.
     */
    public DialogColor topLineColor(@ColorInt int color) {
        this.topLineColor = color;
        return this;
    }

    @ColorInt
    /**
     * Method description.
     */
    public int topLineColor() {
        return topLineColor;
    }

    /**
     * Method description.
     */
    public DialogColor titleTextColor(@ColorInt int color) {
        this.titleTextColor = color;
        return this;
    }

    @ColorInt
    /**
     * Method description.
     */
    public int titleTextColor() {
        return titleTextColor;
    }

    /**
     * Method description.
     */
    public DialogColor cancelTextColor(@ColorInt int color) {
        this.cancelTextColor = color;
        return this;
    }

    @ColorInt
    /**
     * Method description.
     */
    public int cancelTextColor() {
        return cancelTextColor;
    }

    /**
     * Method description.
     */
    public DialogColor okTextColor(@ColorInt int color) {
        this.okTextColor = color;
        return this;
    }

    @ColorInt
    /**
     * Method description.
     */
    public int okTextColor() {
        return okTextColor;
    }

    /**
     * Method description.
     */
    public DialogColor cancelEllipseColor(@ColorInt int color) {
        this.cancelEllipseColor = color;
        return this;
    }

    @ColorInt
    /**
     * Method description.
     */
    public int cancelEllipseColor() {
        return cancelEllipseColor;
    }

    /**
     * Method description.
     */
    public DialogColor okEllipseColor(@ColorInt int color) {
        this.okEllipseColor = color;
        return this;
    }

    @ColorInt
    /**
     * Method description.
     */
    public int okEllipseColor() {
        return okEllipseColor;
    }

}
