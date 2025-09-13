package com.topdon.commons.poster;

import androidx.annotation.NonNull;

/**
 * date: 2019/8/7 09:44
 * author: chuanfeng.bi
 */
interface Poster {
    /**
     * 将要执行的task加入queue
     * 
     * @param runnable 要执行的task
     */
    void enqueue(@NonNull Runnable runnable);

    /**
     * Clearqueuetask
     */
    void clear();
}
