package org.akvo.foundation.util.throwing.function;

import org.akvo.foundation.util.throwing.WrappedException;
import org.akvo.foundation.util.throwing.operator.ThrowingOperator;

import java.util.Optional;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<T> extends ThrowingOperator<Supplier<T>> {
    @SuppressWarnings("all")
    T get() throws Throwable;

    default ThrowingFunction<Void, T> asFunction() {
        return arg -> get();
    }

    @Override
    default Supplier<T> sneaky() {
        return () -> {
            try {
                return get();
            } catch (final Throwable ex) {
                return ThrowingOperator.sneakyThrow(ex);
            }
        };
    }

    @Override
    default Supplier<T> unchecked() {
        return () -> {
            try {
                return get();
            } catch (final Throwable e) {
                throw new WrappedException(e);
            }
        };
    }

    default Supplier<Optional<T>> lifted() {
        return () -> {
            try {
                return Optional.ofNullable(get());
            } catch (final Throwable e) {
                return Optional.empty();
            }
        };
    }
}
