package org.akvo.foundation.ioc.anontations;

import java.lang.annotation.*;

/**
 * 指定注入的Bean 的名称
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Designate {
    String value() default "";
}
