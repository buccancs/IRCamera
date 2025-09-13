package com.topdon.commons.poster;

import java.lang.annotation.*;

/**
 * 
 * <p>
 * date: 2019/8/2 23:53
 * author: chuanfeng.bi
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RunOn {
    /**
     * 
     */
    ThreadMode value() default ThreadMode.UNSPECIFIED;
}
