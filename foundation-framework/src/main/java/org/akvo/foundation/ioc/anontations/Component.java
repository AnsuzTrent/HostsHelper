package org.akvo.foundation.ioc.anontations;

import java.lang.annotation.*;

/**
 * 标记这个类的实例化由容器处理
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {
    String value() default "";
}
