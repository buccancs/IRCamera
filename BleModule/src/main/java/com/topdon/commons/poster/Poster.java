package com.topdon.commons.poster;

import androidx.annotation.NonNull;

/**
 * date: 2019/8/7 09:44
 * author: chuanfeng.bi
 */
interface Poster {
    /**
     * [Chinese text]
     * 
     * @param runnable [Chinese text]
     */
    void enqueue(@NonNull Runnable runnable);

    /**
     * [Chinese text]
     */
    void clear();
}
