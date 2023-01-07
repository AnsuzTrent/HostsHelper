package org.akvo.foundation.ioc.anontations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Scope {
    Type value() default Type.SINGLETON;

    /**
     * 作用域类型
     */
    enum Type {
        /**
         * 原型
         */
        PROTOTYPE,
        /**
         * 单例
         */
        SINGLETON
    }
}
