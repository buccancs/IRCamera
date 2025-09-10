package com.topdon.commons.poster;

import androidx.annotation.NonNull;

/**
 * date: 2019/8/7 09:44
 * author: chuanfeng.bi
 */
interface Poster {
    /**
     * Queue
     * 
     * @param runnable 
     */
    void enqueue(@NonNull Runnable runnable);

    /**
     * Queue
     */
    void clear();
}
