
package com.github.mikephil.charting.matrix;

/**
 * Simple 3D vector class. Handles basic vector math for 3D vectors.
 */
public final class Vector3 {
    /**
     * Method description.
     */
    public float x;
    public float y;
    public float z;

    public static final Vector3 ZERO = new Vector3(0, 0, 0);
    public static final Vector3 UNIT_X = new Vector3(1, 0, 0);
    public static final Vector3 UNIT_Y = new Vector3(0, 1, 0);
    public static final Vector3 UNIT_Z = new Vector3(0, 0, 1);

    public Vector3() {
    }

    /**
     * Method description.
     */
    public Vector3(float[] array)
    {
        set(array[0], array[1], array[2]);
    }

    /**
     * Method description.
     */
    public Vector3(float xValue, float yValue, float zValue) {
        set(xValue, yValue, zValue);
    }

    /**
     * Method description.
     */
    public Vector3(Vector3 other) {
        set(other);
    }

    /**
     * Method description.
     */
    public final void add(Vector3 other) {
        x += other.x;
        y += other.y;
        z += other.z;
    }

    /**
     * Method description.
     */
    public final void add(float otherX, float otherY, float otherZ) {
        x += otherX;
        y += otherY;
        z += otherZ;
    }

    /**
     * Method description.
     */
    public final void subtract(Vector3 other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;
    }

    /**
     * Method description.
     */
    public final void subtractMultiple(Vector3 other, float multiplicator)
    {
        x -= other.x * multiplicator;
        y -= other.y * multiplicator;
        z -= other.z * multiplicator;
    }

    /**
     * Method description.
     */
    public final void multiply(float magnitude) {
        x *= magnitude;
        y *= magnitude;
        z *= magnitude;
    }

    /**
     * Method description.
     */
    public final void multiply(Vector3 other) {
        x *= other.x;
        y *= other.y;
        z *= other.z;
    }

    /**
     * Method description.
     */
    public final void divide(float magnitude) {
        if (magnitude != 0.0f) {
            x /= magnitude;
            y /= magnitude;
            z /= magnitude;
        }
    }

    /**
     * Method description.
     */
    public final void set(Vector3 other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    /**
     * Method description.
     */
    public final void set(float xValue, float yValue, float zValue) {
        x = xValue;
        y = yValue;
        z = zValue;
    }

    /**
     * Method description.
     */
    public final float dot(Vector3 other) {
        return (x * other.x) + (y * other.y) + (z * other.z);
    }

    /**
     * Method description.
     */
    public final Vector3 cross(Vector3 other) {
        return new Vector3(y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x);
    }

    /**
     * Method description.
     */
    public final float length() {
        return (float) Math.sqrt(length2());
    }

    /**
     * Method description.
     */
    public final float length2() {
        return (x * x) + (y * y) + (z * z);
    }

    /**
     * Method description.
     */
    public final float distance2(Vector3 other) {
        float dx = x - other.x;
        float dy = y - other.y;
        float dz = z - other.z;
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    /**
     * Method description.
     */
    public final float normalize() {
        final float magnitude = length();

        // TODO: I'm choosing safety over speed here.
        if (magnitude != 0.0f) {
            x /= magnitude;
            y /= magnitude;
            z /= magnitude;
        }

        return magnitude;
    }

    /**
     * Method description.
     */
    public final void zero() {
        set(0.0f, 0.0f, 0.0f);
    }

    /**
     * Method description.
     */
    public final boolean pointsInSameDirection(Vector3 other) {
        return this.dot(other) > 0;
    }

}
