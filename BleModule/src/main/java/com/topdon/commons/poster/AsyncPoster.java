package com.topdon.commons.poster;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * date: 2019/8/7 10:57
 * author: chuanfeng.bi
 */
final class AsyncPoster implements Runnable, Poster {
    /**
     * Private method description.
     */
    private final ExecutorService executorService;
    private final Queue<Runnable> queue;

    AsyncPoster(@NonNull ExecutorService executorService) {
        this.executorService = executorService;
        queue = new ConcurrentLinkedQueue<>();
    }
    
    @Override
    /**
     * Method description.
     */
    public void enqueue(@NonNull Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null, cannot be enqueued");
        queue.add(runnable);
        executorService.execute(this);
    }

    @Override
    /**
     * Method description.
     */
    public void clear() {
        synchronized (this) {
            queue.clear();
        }
    }

    @Override
    /**
     * Method description.
     */
    public void run() {
        Runnable runnable = queue.poll();
        if (runnable != null) {
            runnable.run();
        }
    }
}
