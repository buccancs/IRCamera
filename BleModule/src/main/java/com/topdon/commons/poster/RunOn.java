package com.topdon.commons.poster;

import java.lang.annotation.*;

/**
 * [Chinese text]line[Chinese text]
 * <p>
 * date: 2019/8/2 23:53
 * author: chuanfeng.bi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @/**
 * RunOn class.
 * 
 * Provides runon functionality.
 */
interface RunOn {
    /**
     * [Chinese text]line[Chinese text]
     */
    ThreadMode value() default ThreadMode.UNSPECIFIED;
}
