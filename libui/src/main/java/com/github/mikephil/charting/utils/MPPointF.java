package com.github.mikephil.charting.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Tony Patino on 6/24/16.
 */
public class MPPointF extends ObjectPool.Poolable {

    /**
     * Private method description.
     */
    private static ObjectPool<MPPointF> pool;

    /**
     * Method description.
     */
    public float x;
    public float y;

    static {
        pool = ObjectPool.create(32, new MPPointF(0,0));
        pool.setReplenishPercentage(0.5f);
    }

    /**
     * Method description.
     */
    public MPPointF() {
    }

    /**
     * Method description.
     */
    public MPPointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Method description.
     */
    public static MPPointF getInstance(float x, float y) {
        MPPointF result = pool.get();
        result.x = x;
        result.y = y;
        return result;
    }

    /**
     * Method description.
     */
    public static MPPointF getInstance() {
        return pool.get();
    }

    /**
     * Method description.
     */
    public static MPPointF getInstance(MPPointF copy) {
        MPPointF result = pool.get();
        result.x = copy.x;
        result.y = copy.y;
        return result;
    }

    /**
     * Method description.
     */
    public static void recycleInstance(MPPointF instance){
        pool.recycle(instance);
    }

    /**
     * Method description.
     */
    public static void recycleInstances(List<MPPointF> instances){
        pool.recycle(instances);
    }

    /**
     * Method description.
     */
    public static final Parcelable.Creator<MPPointF> CREATOR = new Parcelable.Creator<MPPointF>() {
        /**
         * Return a new point from the data in the specified parcel.
         */
        public MPPointF createFromParcel(Parcel in) {
            MPPointF r = new MPPointF(0,0);
            r.my_readFromParcel(in);
            return r;
        }

        /**
         * Return an array of rectangles of the specified size.
         */
        public MPPointF[] newArray(int size) {
            return new MPPointF[size];
        }
    };

    /**
     * Set the point's coordinates from the data stored in the specified
     * parcel. To write a point to a parcel, call writeToParcel().
     * Provided to support older Android devices.
     *
     * @param in The parcel to read the point's coordinates from
     */
    /**
     * Method description.
     */
    public void my_readFromParcel(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
    }

    /**
     * Method description.
     */
    public float getX(){
        return this.x;
    }

    /**
     * Method description.
     */
    public float getY(){
        return this.y;
    }

    @Override
    protected ObjectPool.Poolable instantiate() {
        return new MPPointF(0,0);
    }
}