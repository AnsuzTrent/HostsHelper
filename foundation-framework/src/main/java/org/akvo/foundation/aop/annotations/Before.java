package org.akvo.foundation.aop.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Before {
    /**
     * 包.类.方法(...)
     * <p>
     * 包名.*.方法(int, String)
     *
     * @return 切面标识
     */
    String value();

    /**
     * @return 优先级，默认最低
     */
    int order() default 0;
}
