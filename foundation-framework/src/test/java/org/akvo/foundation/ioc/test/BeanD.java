package org.akvo.foundation.ioc.test;

import org.akvo.foundation.ioc.anontations.Bean;
import org.akvo.foundation.ioc.anontations.Component;
import org.akvo.foundation.ioc.anontations.Scope;

@Component
@Scope(Scope.Type.PROTOTYPE)
public class BeanD {
    @Bean
    public void beanVoid() {
    }

    @Bean("cc")
    public BeanC beanC(BeanA a, BeanB b) {
        return new BeanC(a, b);
    }
}
