package com.topdon.commons.poster;

import java.lang.annotation.*;

/**
 * markermethod执行line程
 * <p>
 * date: 2019/8/2 23:53
 * author: chuanfeng.bi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RunOn {
    /**
     * 运行line程
     */
    ThreadMode value() default ThreadMode.UNSPECIFIED;
}
