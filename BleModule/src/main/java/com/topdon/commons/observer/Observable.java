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
 * messageRelease者、被Observer
 * <p>
 * date: 2019/8/3 13:14
 * author: chuanfeng.bi
 */
public final class Observable {
    private final List<ObserverInfo> observerInfos = new ArrayList<>();
    private final PosterDispatcher posterDispatcher;
    private final ObserverMethodHelper helper;

    /**
     * @param posterDispatcher            method分发者
     * @param isObserveAnnotationRequired 是否强制使用{@link Observe}注解才会收到被Observer的message。强制使用的话，性能会好一些
     */
    public Observable(@NonNull PosterDispatcher posterDispatcher, boolean isObserveAnnotationRequired) {
        this.posterDispatcher = posterDispatcher;
        helper = new ObserverMethodHelper(isObserveAnnotationRequired);
    }

    /**
     * method分发者
     */
    public PosterDispatcher getPosterDispatcher() {
        return posterDispatcher;
    }

    /**
     * 将Observeradd到Register集合里
     *
     * @param observer 需要Register的Observer
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
     * 查询Observer是否Register
     *
     * @param observer 要查询的Observer
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
     * 将Observer从Register集合里移除
     *
     * @param observer 需要CancelRegister的Observer
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
     * 将所有Observer从Register集合中移除
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
     * notification所有ObserverEvent变化
     *
     * @param methodName 要调用Observer的method名
     * @param parameters methodparameterinfo对
     */
    public void notifyObservers(@NonNull String methodName, @Nullable MethodInfo.Parameter... parameters) {
        notifyObservers(new MethodInfo(methodName, parameters));
    }

    /**
     * notification所有ObserverEvent变化
     *
     * @param info methodinfo实例
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
