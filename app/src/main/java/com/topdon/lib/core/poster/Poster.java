package com.topdon.lib.core.poster;

import androidx.annotation.NonNull;

interface Poster {
    void enqueue(@NonNull Runnable runnable);

    void clear();
}
