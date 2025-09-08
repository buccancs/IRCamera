
package com.github.mikephil.charting.utils;

import java.util.List;

/**
 * Class for describing width and height dimensions in some arbitrary
 * unit. Replacement for the android.Util.SizeF which is available only on API >= 21.
 */
public final class FSize extends ObjectPool.Poolable{

    // TODO : Encapsulate width & height

    /**
     * Method description.
     */
    public float width;
    public float height;

    /**
     * Private method description.
     */
    private static ObjectPool<FSize> pool;

    static {
        pool = ObjectPool.create(256, new FSize(0,0));
        pool.setReplenishPercentage(0.5f);
    }


    protected ObjectPool.Poolable instantiate(){
        return new FSize(0,0);
    }

    /**
     * Method description.
     */
    public static FSize getInstance(final float width, final float height){
        FSize result = pool.get();
        result.width = width;
        result.height = height;
        return result;
    }

    /**
     * Method description.
     */
    public static void recycleInstance(FSize instance){
        pool.recycle(instance);
    }

    /**
     * Method description.
     */
    public static void recycleInstances(List<FSize> instances){
        pool.recycle(instances);
    }

    /**
     * Method description.
     */
    public FSize() {
    }

    /**
     * Method description.
     */
    public FSize(final float width, final float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    /**
     * Method description.
     */
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof FSize) {
            final FSize other = (FSize) obj;
            return width == other.width && height == other.height;
        }
        return false;
    }

    @Override
    /**
     * Method description.
     */
    public String toString() {
        return width + "x" + height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    /**
     * Method description.
     */
    public int hashCode() {
        return Float.floatToIntBits(width) ^ Float.floatToIntBits(height);
    }
}
