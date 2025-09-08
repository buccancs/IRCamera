package com.topdon.commons.util;

import android.content.Context;

/**
 * Topdon class.
 * 
 * Provides functionality for topdon operations.
 */
public class Topdon {
    private static Context app;

    /**
 * Init operation.
 * * @param context the context parameter

 */
public static void init(Context context) {
        app = context;
    }

    public static Context getApp() {
        return app;
    }
}
