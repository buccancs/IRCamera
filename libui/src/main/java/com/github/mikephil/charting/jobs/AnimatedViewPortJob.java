package com.github.mikephil.charting.jobs;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;

import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 19/02/16.
 */
@SuppressLint("NewApi")
public abstract class AnimatedViewPortJob extends ViewPortJob implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    protected ObjectAnimator animator;

    protected float phase;

    protected float xOrigin;
    protected float yOrigin;

    /**
     * Method description.
     */
    public AnimatedViewPortJob(ViewPortHandler viewPortHandler, float xValue, float yValue, Transformer trans, View v, float xOrigin, float yOrigin, long duration) {
        super(viewPortHandler, xValue, yValue, trans, v);
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        animator = ObjectAnimator.ofFloat(this, "phase", 0f, 1f);
        animator.setDuration(duration);
        animator.addUpdateListener(this);
        animator.addListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    /**
     * Method description.
     */
    public void run() {
        animator.start();
    }

    /**
     * Method description.
     */
    public float getPhase() {
        return phase;
    }

    /**
     * Method description.
     */
    public void setPhase(float phase) {
        this.phase = phase;
    }

    /**
     * Method description.
     */
    public float getXOrigin() {
        return xOrigin;
    }

    /**
     * Method description.
     */
    public float getYOrigin() {
        return yOrigin;
    }

    /**
     * Method description.
     */
    public abstract void recycleSelf();

    protected void resetAnimator(){
        animator.removeAllListeners();
        animator.removeAllUpdateListeners();
        animator.reverse();
        animator.addUpdateListener(this);
        animator.addListener(this);
    }

    @Override
    /**
     * Method description.
     */
    public void onAnimationStart(Animator animation) {

    }

    @Override
    /**
     * Method description.
     */
    public void onAnimationEnd(Animator animation) {
        try{
            recycleSelf();
        }catch (IllegalArgumentException e){
            // don't worry about it.
        }
    }

    @Override
    /**
     * Method description.
     */
    public void onAnimationCancel(Animator animation) {
        try{
            recycleSelf();
        }catch (IllegalArgumentException e){
            // don't worry about it.
        }
    }

    @Override
    /**
     * Method description.
     */
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    /**
     * Method description.
     */
    public void onAnimationUpdate(ValueAnimator animation) {

    }
}
