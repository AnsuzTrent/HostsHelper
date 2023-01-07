package org.akvo.foundation.ioc.test;

import org.akvo.foundation.ioc.anontations.Autowired;
import org.akvo.foundation.ioc.anontations.Component;
import org.akvo.foundation.ioc.anontations.Scope;

@Component
@Scope
public class BeanC {
    private final BeanA a;
    private final BeanB b;

    @Autowired
    public BeanC(BeanA a, BeanB b) {
        this.a = a;
        this.b = b;
    }
}
