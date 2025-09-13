package com.topdon.commons.poster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * taskallocate
 * <p>
 * date: 2019/8/7 10:18
 * author: chuanfeng.bi
 */
public class PosterDispatcher {
    private final ThreadMode defaultMode;
    private final Poster backgroundPoster;
    private final Poster mainThreadPoster;
    private final ExecutorService executorService;
    private final Poster asyncPoster;

    public PosterDispatcher(@NonNull ExecutorService executorService, @NonNull ThreadMode defaultMode) {
        this.defaultMode = defaultMode;
        this.executorService = executorService;
        backgroundPoster = new BackgroundPoster(executorService);
        mainThreadPoster = new MainThreadPoster();
        asyncPoster = new AsyncPoster(executorService);
    }

    /**
     * Get/Retrieve默认运行line程
     */
    public ThreadMode getDefaultMode() {
        return defaultMode;
    }

    /**
     * Get/Retrieveline程池
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Clear所有queue中task，存在执行的无法stop
     */
    public void clearTasks() {
        backgroundPoster.clear();
        mainThreadPoster.clear();
        asyncPoster.clear();
    }

    /**
     * 根据method上带的{@link RunOn}注解，将taskpost到指定line程执行。如果method上没有带注解，使用configuration的默认值
     *
     * @param method   method
     * @param runnable 要执行的task
     */
    public void post(@Nullable Method method, @NonNull Runnable runnable) {
        if (method != null) {
            RunOn annotation = method.getAnnotation(RunOn.class);
            ThreadMode mode = defaultMode;
            if (annotation != null) {
                mode = annotation.value();
            }
            post(mode, runnable);
        }
    }

    /**
     * 将taskpost到指定line程执行。
     *
     * @param mode     指定task执行line程
     * @param runnable 要执行的task
     */
    public void post(@NonNull ThreadMode mode, @NonNull Runnable runnable) {
        if (mode == ThreadMode.UNSPECIFIED) {
            mode = defaultMode;
        }
        switch (mode) {
            case MAIN:
                mainThreadPoster.enqueue(runnable);
                break;
            case POSTING:
                runnable.run();
                break;
            case BACKGROUND:
                backgroundPoster.enqueue(runnable);
                break;
            case ASYNC:
                asyncPoster.enqueue(runnable);
                break;
        }
    }

    /**
     * 将taskpost到指定line程执行
     *
     * @param owner      method的所在的对象实例
     * @param methodName method名
     * @param tag        {@link Tag#value()}
     * @param parameters parameterinfo
     */
    public void post(@NonNull Object owner, @NonNull String methodName, @NonNull String tag,
                     @Nullable MethodInfo.Parameter... parameters) {
        Class<?>[] classes = new Class[0];
        Object[] params = new Object[0];
        if (parameters != null) {
            params = new Object[parameters.length];
            classes = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                MethodInfo.Parameter parameter = parameters[i];
                classes[i] = parameter.getType();
                params[i] = parameter.getValue();
            }
        }
        Method[] methods = owner.getClass().getDeclaredMethods();
        Method tm = null;
        Method mm = null;
        for (Method method : methods) {
            Tag annotation = method.getAnnotation(Tag.class);
            if (annotation != null && !annotation.value().isEmpty() && annotation.value().equals(tag) &&
                    equalParamTypes(method.getParameterTypes(), classes)) {
                tm = method;
            }
            if (tm == null) {
                if (method.getName().equals(methodName) && equalParamTypes(method.getParameterTypes(), classes)) {
                    mm = method;
                }
            } else {
                break;
            }
        }
        Method method = tm == null ? mm : tm;
        if (method == null) {
            return;
        }
        try {
            Object[] finalParams = params;
            post(method, () -> {
                try {
                    method.invoke(owner, finalParams);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ignore) {
        }
    }

    private boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
        if (params1.length == params2.length) {
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] != params2[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 将taskpost到指定line程执行
     *
     * @param owner      method的所在的对象实例
     * @param methodName method名
     * @param parameters parameterinfo
     */
    public void post(@NonNull final Object owner, @NonNull String methodName, @Nullable MethodInfo.Parameter... parameters) {
        post(owner, methodName, "", parameters);
    }

    /**
     * 将taskpost到指定line程执行
     *
     * @param owner      method的所在的对象实例
     * @param methodInfo methodinfo实例
     */
    public void post(@NonNull Object owner, @NonNull MethodInfo methodInfo) {
        post(owner, methodInfo.getName(), methodInfo.getTag(), methodInfo.getParameters());
    }
}
