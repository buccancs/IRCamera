package com.topdon.commons.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class AppHolder implements Application.ActivityLifecycleCallbacks {
    //Activity
    /**
     * Private method description.
     */
    private final List<RunningActivity> runningActivities = new CopyOnWriteArrayList<>();
    private boolean isCompleteExit = false;
    private Application application;
    private Looper mainLooper;
    private RunningActivity topActivity;

    private AppHolder() {
        mainLooper = Looper.getMainLooper();
        //application
        application = tryGetApplication();
        if (application != null) {
            application.registerActivityLifecycleCallbacks(this);
        }
    }

    /**
     * Private method description.
     */
    private static final class Holder {
        private static final AppHolder INSTANCE = new AppHolder();
    }
    
    /**
     * Private method description.
     */
    private static class RunningActivity {
        String name;
        WeakReference<Activity> weakActivity;

        RunningActivity(String name, WeakReference<Activity> weakActivity) {
            this.name = name;
            this.weakActivity = weakActivity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RunningActivity)) return false;
            RunningActivity runningActivity = (RunningActivity) o;
            return name.equals(runningActivity.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
    
    @NonNull
    /**
     * Method description.
     */
    public static AppHolder getInstance() {
        return Holder.INSTANCE;
    }
    
    @SuppressLint("PrivateApi")
    @Nullable
    /**
     * Private method description.
     */
    private Application tryGetApplication() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Method catMethod = cls.getMethod("currentActivityThread");
            catMethod.setAccessible(true);
            Object aThread = catMethod.invoke(null);
            Method method = aThread.getClass().getMethod("getApplication");
            return (Application) method.invoke(aThread);
        } catch (Exception e) {
            return null;
        }
    }

    @CallSuper
    @Override
    /**
     * Method description.
     */
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        RunningActivity a = new RunningActivity(activity.getClass().getName(), new WeakReference<>(activity));
        if (!runningActivities.contains(a)) {
            runningActivities.add(a);
        }
        topActivity = a;
    }

    @CallSuper
    @Override
    /**
     * Method description.
     */
    public void onActivityStarted(Activity activity) {

    }

    @CallSuper
    @Override
    /**
     * Method description.
     */
    public void onActivityResumed(Activity activity) {

    }

    @CallSuper
    @Override
    /**
     * Method description.
     */
    public void onActivityPaused(Activity activity) {

    }

    @CallSuper
    @Override
    /**
     * Method description.
     */
    public void onActivityStopped(Activity activity) {

    }

    @CallSuper
    @Override
    /**
     * Method description.
     */
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @CallSuper
    @Override
    /**
     * Method description.
     */
    public void onActivityDestroyed(Activity activity) {
        if (runningActivities.isEmpty()) {
            topActivity = null;
        }
        RunningActivity a = new RunningActivity(activity.getClass().getName(), new WeakReference<>(activity));
        runningActivities.remove(a);
        if (isCompleteExit && runningActivities.isEmpty()) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }
    
    /**
     * Method description.
     */
    public static void initialize(@NonNull Application application) {
        Objects.requireNonNull(application, "application is null");
        //Application
        if (Holder.INSTANCE.application != null && Holder.INSTANCE.application != application) {
            Holder.INSTANCE.application.unregisterActivityLifecycleCallbacks(Holder.INSTANCE);
            application.registerActivityLifecycleCallbacks(Holder.INSTANCE);
        }
        Holder.INSTANCE.application = application;
    }
    
    /**
     * Method description.
     */
    public boolean isMainThread() {
        return Looper.myLooper() == mainLooper;
    }
    
    @NonNull
    /**
     * Method description.
     */
    public Looper getMainLooper() {
        if (mainLooper == null) {
            mainLooper = Looper.getMainLooper();
        }
        return mainLooper;
    }
    
    @NonNull
    /**
     * Method description.
     */
    public Context getContext() {
        Objects.requireNonNull(application, "The AppHolder has not been initialized, make sure to call AppHolder.initialize(app) first.");
        return application;
    }
    
    @Nullable
    /**
     * Method description.
     */
    public PackageInfo getPackageInfo() {
        try {
            PackageManager pm = application.getPackageManager();
            return pm.getPackageInfo(application.getPackageName(), 0);
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * Method description.
     */
    public boolean isAppOnForeground() {
        ActivityManager am = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
            if (processes != null) {
                for (ActivityManager.RunningAppProcessInfo process : processes) {
                    if (application.getPackageName().equals(process.processName) &&
                            ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == process.importance) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Private method description.
     */
    private boolean contains(Object[] array, Object obj) {
        if (array != null && array.length > 0) {
            for (Object o : array) {
                if (o.equals(obj)) {
                    return true;
                }
            }
        }
        return false;
    }
        
     * finishActivity
    /**
     * Method description.
     */
    public void finish(String className, String... classNames) {
        List<RunningActivity> list = new ArrayList<>(runningActivities);
        Collections.reverse(list);//，finish
        for (RunningActivity runningActivity : list) {
            Activity activity = runningActivity.weakActivity.get();
            if (activity != null) {
                String name = activity.getClass().getName();
                if (name.equals(className) || contains(classNames, name)) {
                    activity.finish();
                }
            }
        }
    }

     * finishActivity
     * @param classNames ActivitynullfinishActivity
    /**
     * Method description.
     */
    public void finishAllWithout(@Nullable String className, String... classNames) {
        List<RunningActivity> list = new ArrayList<>(runningActivities);
        Collections.reverse(list);//，finish
        for (RunningActivity runningActivity : list) {
            Activity activity = runningActivity.weakActivity.get();
            if (activity != null) {
                String name = activity.getClass().getName();
                if (!name.equals(className) && !contains(classNames, name)) {
                    activity.finish();
                }
            }
        }
    }

     * finishActivity
    /**
     * Method description.
     */
    public void finishAll() {
        finishAllWithout(null);
    }

     * Activity
     * @param className
    /**
     * Method description.
     */
    public void backTo(String className) {
        List<RunningActivity> list = new ArrayList<>(runningActivities);
        Collections.reverse(list);//，finish
        for (RunningActivity runningActivity : list) {
            Activity activity = runningActivity.weakActivity.get();
            if (activity != null) {
                String name = activity.getClass().getName();
                if (name.equals(className)) {
                    activity.finish();
                    return;
                }
            }
        }
    }
    
    @Nullable
    /**
     * Method description.
     */
    public Activity getActivity(String className) {
        for (RunningActivity runningActivity : runningActivities) {
            if (runningActivity.name.equals(className)) {
                return runningActivity.weakActivity.get();
            }
        }
        return null;
    }
    
    /**
     * Method description.
     */
    public boolean isAllFinished() {
        return runningActivities.isEmpty();
    }
    
    /**
     * Method description.
     */
    public List<Activity> getAllActivities() {
        List<Activity> activities = new ArrayList<>();
        for (RunningActivity runningActivity : runningActivities) {
            Activity activity = runningActivity.weakActivity.get();
            if (activity != null) {
                activities.add(activity);
            }
        }
        return activities;
    }

     * finishActivity
    /**
     * Method description.
     */
    public void completeExit() {
        isCompleteExit = true;
        List<RunningActivity> list = new ArrayList<>(runningActivities);
        Collections.reverse(list);//，finish
        for (RunningActivity runningActivity : list) {
            Activity activity = runningActivity.weakActivity.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }
    
    /**
     * Method description.
     */
    public Activity getTopActivity() {
        return topActivity == null ? null : topActivity.weakActivity.get();
    }
}
