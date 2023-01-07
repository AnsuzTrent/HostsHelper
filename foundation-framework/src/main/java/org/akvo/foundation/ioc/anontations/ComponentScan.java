package org.akvo.foundation.ioc.anontations;

import java.lang.annotation.*;

/**
 * 根据传入的path 进行扫描，不传入时默认使用注解所在类的包
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ComponentScan {
    String value() default "";

    /**
     * 排除一些路径，这些路径下不进行扫描
     */
    String[] excludes() default {};
}
