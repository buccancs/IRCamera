
package com.github.gzuliyujiang.dialog;


import androidx.annotation.NonNull;

public final class DialogLog {
    private static boolean enable = false;

    private DialogLog() {
        super();
    }

    public static void enable() {
        enable = true;
    }

    public static void print(@NonNull Object log) {
        if (!enable) {
            return;
        }
    }

}
