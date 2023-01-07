package org.akvo.foundation.ioc.anontations;

import java.lang.annotation.*;

@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
}
