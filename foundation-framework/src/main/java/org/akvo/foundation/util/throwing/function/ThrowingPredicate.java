package org.akvo.foundation.util.throwing.function;


import org.akvo.foundation.util.throwing.WrappedException;
import org.akvo.foundation.util.throwing.operator.ThrowingOperator;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface ThrowingPredicate<T> extends ThrowingOperator<Predicate<T>> {
    static <T> ThrowingPredicate<T> isEqual(Object targetRef) {
        return (null == targetRef)
            ? Objects::isNull
            : targetRef::equals;
    }

    @SuppressWarnings("unchecked")
    static <T> ThrowingPredicate<T> not(ThrowingPredicate<? super T> target) {
        Objects.requireNonNull(target);
        return (ThrowingPredicate<T>) target.negate();
    }

    @SuppressWarnings("all")
    boolean test(T t) throws Throwable;

    default ThrowingPredicate<T> and(final ThrowingPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return t -> test(t) && other.test(t);
    }

    default ThrowingPredicate<T> negate() {
        return t -> !test(t);
    }

    default ThrowingPredicate<T> or(final ThrowingPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return t -> test(t) || other.test(t);
    }

    default ThrowingPredicate<T> xor(final ThrowingPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return t -> test(t) ^ other.test(t);
    }

    default ThrowingFunction<T, Boolean> asFunction() {
        return this::test;
    }

    @Override
    default Predicate<T> sneaky() {
        return t -> {
            try {
                return test(t);
            } catch (final Throwable e) {
                return ThrowingOperator.sneakyThrow(e);
            }
        };
    }

    @Override
    default Predicate<T> unchecked() {
        return t -> {
            try {
                return test(t);
            } catch (final Throwable e) {
                throw new WrappedException(e);
            }
        };
    }
}
