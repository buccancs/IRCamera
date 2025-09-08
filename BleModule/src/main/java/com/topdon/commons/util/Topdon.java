package com.topdon.commons.util;

import android.content.Context;

public class Topdon {
    /**
     * Private method description.
     */
    private static Context app;

    /**
     * Method description.
     */
    public static void init(Context context) {
        app = context;
    }

    /**
     * Method description.
     */
    public static Context getApp() {
        return app;
    }
}
