package org.akvo.foundation.ioc.test;

import org.akvo.foundation.ioc.anontations.Autowired;
import org.akvo.foundation.ioc.anontations.Component;

@Component
public class BeanSelfDependency {
    private final BeanSelfDependency beanSelfDependency;

    @Autowired
    public BeanSelfDependency(BeanSelfDependency beanSelfDependency) {
        this.beanSelfDependency = beanSelfDependency;
    }
}
