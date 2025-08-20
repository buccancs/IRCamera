package com.topdon.lib.core.util;

/**
 * Simple math utilities
 */
public class MathUtils {
    
    private MathUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Clamp a value between min and max
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Clamp a float value between min and max
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}