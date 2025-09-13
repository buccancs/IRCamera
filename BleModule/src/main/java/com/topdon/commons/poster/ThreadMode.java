package com.topdon.commons.poster;

/**
 * line程Strategy
 * <p>
 * date: 2019/8/2 23:53
 * author: chuanfeng.bi
 */
public enum ThreadMode {
    /**
     * 和调用者同一line程
     */
    POSTING,
    /**
     * 主line程，UIline程
     */
    MAIN,
    /**
     * 后台line程，Synchronize的
     */
    BACKGROUND,
    /**
     * Asynchronousline程
     */
    ASYNC,
    /**
     * 未指定
     */
    UNSPECIFIED
}
