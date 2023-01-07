package org.akvo.foundation.ioc.anontations;

import java.lang.annotation.*;

/**
 * 声明该方法返回的对象是Bean 实例，可以指定名称，不指定时名称默认为类型全名
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Bean {
    String value() default "";
}
