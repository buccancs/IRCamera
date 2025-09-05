package com.topdon.lms.sdk;

/**
 * Stub implementation for LMS SDK compatibility
 */
public class LMS {
    public static final int SUCCESS = 0;
    
    private static LMS instance;
    
    public static LMS getInstance() {
        if (instance == null) {
            instance = new LMS();
        }
        return instance;
    }
    
    public String getLoginName() {
        return "default_user";
    }
}