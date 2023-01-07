package org.akvo.foundation.util.throwing.function;

import org.akvo.foundation.util.throwing.WrappedException;
import org.akvo.foundation.util.throwing.operator.ThrowingOperator;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> extends ThrowingOperator<Function<T, R>> {
    static <T> ThrowingFunction<T, T> identity() {
        return t -> t;
    }

    @SuppressWarnings("all")
    R apply(T arg) throws Throwable;

    default <V> ThrowingFunction<V, R> butBeforeDo(final ThrowingFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return v -> apply(before.apply(v));
    }

    default <V> ThrowingFunction<T, V> andAfterDo(final ThrowingFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return t -> after.apply(apply(t));
    }

    @Override
    default Function<T, R> sneaky() {
        return t -> {
            try {
                return apply(t);
            } catch (final Throwable ex) {
                return ThrowingOperator.sneakyThrow(ex);
            }
        };
    }

    @Override
    default Function<T, R> unchecked() {
        return t -> {
            try {
                return apply(t);
            } catch (final Throwable e) {
                throw new WrappedException(e);
            }
        };
    }

    default Function<T, Optional<R>> lifted() {
        return t -> {
            try {
                return Optional.ofNullable(apply(t));
            } catch (final Throwable e) {
                return Optional.empty();
            }
        };
    }
}
