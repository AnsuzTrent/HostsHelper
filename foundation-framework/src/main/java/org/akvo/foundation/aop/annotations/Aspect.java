package org.akvo.foundation.aop.annotations;

import java.lang.annotation.*;

/**
 * 标识这个类使用切面
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Aspect {
    String value();
}
