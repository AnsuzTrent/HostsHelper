package org.akvo.foundation.util.throwing.function;

import org.akvo.foundation.util.throwing.WrappedException;
import org.akvo.foundation.util.throwing.operator.ThrowingOperator;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface ThrowingUnaryOperator<T> extends ThrowingFunction<T, T> {
    static <T> ThrowingUnaryOperator<T> identity() {
        return t -> t;
    }

    @Override
    default UnaryOperator<T> sneaky() {
        return t -> {
            try {
                return apply(t);
            } catch (final Throwable ex) {
                return ThrowingOperator.sneakyThrow(ex);
            }
        };
    }

    @Override
    default UnaryOperator<T> unchecked() {
        return t -> {
            try {
                return apply(t);
            } catch (final Throwable e) {
                throw new WrappedException(e);
            }
        };
    }
}
