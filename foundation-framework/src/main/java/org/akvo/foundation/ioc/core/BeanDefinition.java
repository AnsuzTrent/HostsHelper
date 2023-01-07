package org.akvo.foundation.ioc.core;

import org.akvo.foundation.ioc.anontations.Scope;

import java.lang.reflect.Executable;

public record BeanDefinition<T>(
    String name,
    Class<T> type,
    Scope.Type scopeType,
    Executable executable) {
}
