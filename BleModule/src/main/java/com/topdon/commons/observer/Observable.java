package com.topdon.commons.observer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.topdon.commons.poster.MethodInfo;
import com.topdon.commons.poster.PosterDispatcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * [Chinese text], [Chinese text]observation[Chinese text]
 * <p>
 * date: 2019/8/3 13:14
 * author: chuanfeng.bi
 */
public final class Observable {
    private final List<ObserverInfo> observerInfos = new ArrayList<>();
    private final PosterDispatcher posterDispatcher;
    private final ObserverMethodHelper helper;

    /**
     * @param posterDispatcher            [Chinese text]
     * @param isObserveAnnotationRequired [Chinese text]{@link Observe}[Chinese text]observation[Chinese text]. [Chinese text], [Chinese text]
     */
    public Observable(@NonNull PosterDispatcher posterDispatcher, boolean isObserveAnnotationRequired) {
        this.posterDispatcher = posterDispatcher;
        helper = new ObserverMethodHelper(isObserveAnnotationRequired);
    }

    /**
     * [Chinese text]
     */
    public PosterDispatcher getPosterDispatcher() {
        return posterDispatcher;
    }

    /**
     * [Chinese text]observation[Chinese text]
     *
     * @param observer [Chinese text]observation[Chinese text]
     */
    public void registerObserver(@NonNull Observer observer) {
        Objects.requireNonNull(observer, "observer can't be null");
        synchronized (observerInfos) {
            boolean registered = false;
            for (Iterator<ObserverInfo> it = observerInfos.iterator(); it.hasNext(); ) {
                ObserverInfo info = it.next();
                Observer o = info.weakObserver.get();
                if (o == null) {
                    it.remove();
                } else if (o == observer) {
                    registered = true;
                }
            }
            if (registered) {
                Log.e("Observable", "", new Error("Observer " + observer + " is already registered."));
                return;
            }
            Map<String, Method> methodMap = helper.findObserverMethod(observer);
            observerInfos.add(new ObserverInfo(observer, methodMap));
        }
    }

    /**
     * [Chinese text]observation[Chinese text]
     *
     * @param observer [Chinese text]observation[Chinese text]
     */
    public boolean isRegistered(@NonNull Observer observer) {
        synchronized (observerInfos) {
            for (ObserverInfo info : observerInfos) {
                if (info.weakObserver.get() == observer) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * [Chinese text]observation[Chinese text]
     *
     * @param observer [Chinese text]observation[Chinese text]
     */
    public void unregisterObserver(@NonNull Observer observer) {
        synchronized (observerInfos) {
            for (Iterator<ObserverInfo> it = observerInfos.iterator(); it.hasNext(); ) {
                ObserverInfo info = it.next();
                Observer o = info.weakObserver.get();
                if (o == null || observer == o) {
                    it.remove();
                }
            }
        }
    }

    /**
     * [Chinese text]observation[Chinese text]in progress[Chinese text]
     */
    public void unregisterAll() {
        synchronized (observerInfos) {
            observerInfos.clear();
        }
        helper.clearCache();
    }

    private List<ObserverInfo> getObserverInfos() {
        synchronized (observerInfos) {
            ArrayList<ObserverInfo> infos = new ArrayList<>();
            for (ObserverInfo info : observerInfos) {
                Observer observer = info.weakObserver.get();
                if (observer != null) {
                    infos.add(info);
                }
            }
            return infos;
        }
    }

    /**
     * [Chinese text]observation[Chinese text]event[Chinese text]
     *
     * @param methodName [Chinese text]observation[Chinese text]
     * @param parameters [Chinese text]
     */
    public void notifyObservers(@NonNull String methodName, @Nullable MethodInfo.Parameter... parameters) {
        notifyObservers(new MethodInfo(methodName, parameters));
    }

    /**
     * [Chinese text]observation[Chinese text]event[Chinese text]
     *
     * @param info [Chinese text]
     */
    public void notifyObservers(@NonNull MethodInfo info) {
        List<ObserverInfo> infos = getObserverInfos();
        for (ObserverInfo oi : infos) {
            Observer observer = oi.weakObserver.get();
            if (observer != null) {
                String key = helper.generateKey(info.getTag(), info.getName(), info.getParameterTypes());
                Method method = oi.methodMap.get(key);
                if (method != null) {
                    Runnable runnable = helper.generateRunnable(observer, method, info);
                    posterDispatcher.post(method, runnable);
                }
            }
        }
    }
}
