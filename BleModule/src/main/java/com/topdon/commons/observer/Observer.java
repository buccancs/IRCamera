package com.topdon.commons.observer;

/**
 * Observer
 * <p>
 * date: 2019/8/3 13:15
 * author: chuanfeng.bi
 */
public interface Observer {
    /**
     * data变化
     */
    @Observe
    default void onChanged(Object o) {}
}
