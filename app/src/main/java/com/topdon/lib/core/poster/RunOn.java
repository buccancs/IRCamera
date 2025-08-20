package com.topdon.lib.core.poster;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RunOn {
    ThreadMode value() default ThreadMode.UNSPECIFIED;
}
