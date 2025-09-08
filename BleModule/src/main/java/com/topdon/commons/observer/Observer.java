package com.topdon.commons.observer;

/**
 * observation[Chinese text]
 * <p>
 * date: 2019/8/3 13:15
 * author: chuanfeng.bi
 */
public interface Observer {
    /**
     * [Chinese text]
     */
    @Observe
    default void onChanged(Object o) {}
}
