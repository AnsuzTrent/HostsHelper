package org.akvo.foundation.ioc.test;

import org.akvo.foundation.ioc.anontations.Autowired;
import org.akvo.foundation.ioc.anontations.Component;

@Component
public class BeanA {
    private final BeanD d;

    @Autowired
    public BeanA(BeanD d) {
        this.d = d;
    }
}
