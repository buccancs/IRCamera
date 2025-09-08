package com.topdon.commons.poster;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

 * <p>
 * date: 2019/8/7 10:40
 * author: chuanfeng.bi
final class BackgroundPoster implements Runnable, Poster {
    /**
     * Private method description.
     */
    private final ExecutorService executorService;
    private final Queue<Runnable> queue;
    private volatile boolean executorRunning;

    BackgroundPoster(@NonNull ExecutorService executorService) {
        this.executorService = executorService;
        queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    /**
     * Method description.
     */
    public void enqueue(@NonNull Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null, cannot be enqueued");
        synchronized (this) {
            queue.add(runnable);
            if (!executorRunning) {
                executorRunning = true;
                executorService.execute(this);
            }
        }
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
        try {
            while (true) {
                Runnable runnable = queue.poll();
                if (runnable == null) {
                    synchronized (this) {
                        runnable = queue.poll();
                        if (runnable == null) {
                            executorRunning = false;
                            return;
                        }
                    }
                }
                runnable.run();
            }
        } finally {
            executorRunning = false;
        }
    }
}
