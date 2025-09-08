package com.topdon.commons.base.entity;


import com.topdon.commons.base.interfaces.Checkable;

/**
 * date: 2019/8/6 12:35
 * author: chuanfeng.bi
 */
public class CheckableItem<T> implements Checkable<CheckableItem<T>> {
    /**
     * Private method description.
     */
    private T data;
    private boolean isChecked;

    /**
     * Method description.
     */
    public CheckableItem() {
    }

    /**
     * Method description.
     */
    public CheckableItem(T data) {
        this.data = data;
    }

    /**
     * Method description.
     */
    public CheckableItem(T data, boolean isChecked) {
        this.data = data;
        this.isChecked = isChecked;
    }

    /**
     * Method description.
     */
    public T getData() {
        return data;
    }

    /**
     * Method description.
     */
    public void setData(T data) {
        this.data = data;
    }

    @Override
    /**
     * Method description.
     */
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    /**
     * Method description.
     */
    public CheckableItem<T> setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }
}
