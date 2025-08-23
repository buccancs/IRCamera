package com.topdon.module.thermal.camera.task;


import java.util.concurrent.ArrayBlockingQueue;

public class DeviceControlWorker {
    private Thread mThread;
    private ArrayBlockingQueue<BaseTask> mEventQueue = new ArrayBlockingQueue(2);
    private DeviceState mDeviceState = DeviceState.NONE;
    private IDeviceConnectListener mDeviceControlCallback;
    private boolean isStartPreviewing = false;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            BaseTask task;
            DeviceState previousState;

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    synchronized (mEventQueue) {
                        while (mEventQueue.isEmpty()) {
                            try {
                                mEventQueue.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                        task = mEventQueue.peek();
                    }
                    previousState = mDeviceState;
                    task.setDeviceState(mDeviceState);
                    if (task instanceof StartPreviewTask) {
                        ((StartPreviewTask) task).setDeviceControlCallback(mDeviceControlCallback);
                    }
                    task.run();
                    mEventQueue.poll();
                    //call back connect result
                    mDeviceState = task.getDeviceState();
                    if (mDeviceControlCallback != null) {
                        //防止重复回调
                        if (mDeviceState != previousState) {
                            if (mDeviceState == DeviceState.OPEN) {
                                mDeviceControlCallback.onConnected();
                            } else if (mDeviceState == DeviceState.CLOSED) {
                                mDeviceControlCallback.onDisconnected();
                            } else if (mDeviceState == DeviceState.RESUMED) {
                                mDeviceState = DeviceState.OPEN;
                                mDeviceControlCallback.onResumed();
                            } else if (mDeviceState == DeviceState.PAUSED) {
                                mDeviceControlCallback.onPaused();
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    };

    /**
     * start worker thread
     */
    public void startWork() {
        if (mThread == null) {
            mThread = new Thread(mRunnable);
            mThread.start();
        }
    }

    /**
     * stop worker thread
     */
    public void stopWork() {
        if (mThread != null) {
            try {
                mThread.interrupt();
//                mThread.join();
                mThread = null;
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * add on task
     *
     * @param task a runnable extends BaseTask
     */
    public void addTask(BaseTask task) {
        synchronized (mEventQueue) {
            if (mEventQueue.size() < 2) {
                mEventQueue.add(task);
            } else {
                BaseTask task1 = mEventQueue.poll();
                if (task1 instanceof StartPreviewTask) {
                } else if (task1 instanceof StopPreviewTask) {
                }
                mEventQueue.add(task);
            }
            mEventQueue.notify();
        }
    }

    public void setDeviceControlCallback(IDeviceConnectListener mDeviceControlCallback) {
        this.mDeviceControlCallback = mDeviceControlCallback;
    }

    public void release() {
        mDeviceControlCallback = null;
        stopWork();
    }

    public DeviceState getDeviceState() {
        return mDeviceState;
    }

    public boolean isStartPreviewing() {
        return isStartPreviewing;
    }
    
    /**
     * Check if worker is running
     */
    public boolean isRunning() {
        return mThread != null && mThread.isAlive();
    }
}
