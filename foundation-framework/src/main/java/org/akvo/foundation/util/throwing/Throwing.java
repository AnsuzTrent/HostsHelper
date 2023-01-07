package org.akvo.foundation.util.throwing;

import org.akvo.foundation.util.throwing.function.*;

import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

@SuppressWarnings("unused")
public interface Throwing {
    static <T> ThrowingSupplier<T> supplier(ThrowingSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return supplier;
    }

    static <T> ThrowingConsumer<T> consumer(ThrowingConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        return consumer;
    }

    static <T, R> ThrowingFunction<T, R> function(ThrowingFunction<T, R> function) {
        Objects.requireNonNull(function);
        return function;
    }

    static <T> ThrowingUnaryOperator<T> function(ThrowingUnaryOperator<T> unaryOperator) {
        Objects.requireNonNull(unaryOperator);
        return unaryOperator;
    }

    static <T> ThrowingPredicate<T> predicate(ThrowingPredicate<T> predicate) {
        Objects.requireNonNull(predicate);
        return predicate;
    }

    static ThrowingRunnable runnable(ThrowingRunnable runnable) {
        Objects.requireNonNull(runnable);
        return runnable;
    }


    static <T> Supplier<T> sneakySupplier(ThrowingSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return supplier.sneaky();
    }

    static <T> Consumer<T> sneakyConsumer(ThrowingConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        return consumer.sneaky();
    }

    static <T, R> Function<T, R> sneakyFunction(ThrowingFunction<T, R> function) {
        Objects.requireNonNull(function);
        return function.sneaky();
    }

    static <T> UnaryOperator<T> sneakyUnaryOperator(ThrowingUnaryOperator<T> unaryOperator) {
        Objects.requireNonNull(unaryOperator);
        return unaryOperator.sneaky();
    }

    static <T> Predicate<T> sneakyPredicate(ThrowingPredicate<T> predicate) {
        Objects.requireNonNull(predicate);
        return predicate.sneaky();
    }

    static Runnable sneakyRunnable(ThrowingRunnable runnable) {
        Objects.requireNonNull(runnable);
        return runnable.sneaky();
    }


    static <T> Supplier<T> uncheckedSupplier(ThrowingSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return supplier.unchecked();
    }

    static <T> Consumer<T> uncheckedConsumer(ThrowingConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        return consumer.unchecked();
    }

    static <T, R> Function<T, R> uncheckedFunction(ThrowingFunction<T, R> function) {
        Objects.requireNonNull(function);
        return function.unchecked();
    }

    static <T> UnaryOperator<T> uncheckedUnaryOperator(ThrowingUnaryOperator<T> unaryOperator) {
        Objects.requireNonNull(unaryOperator);
        return unaryOperator.unchecked();
    }

    static <T> Predicate<T> uncheckedPredicate(ThrowingPredicate<T> predicate) {
        Objects.requireNonNull(predicate);
        return predicate.unchecked();
    }

    static Runnable uncheckedRunnable(ThrowingRunnable runnable) {
        Objects.requireNonNull(runnable);
        return runnable.unchecked();
    }


    static <T> Supplier<Optional<T>> liftedSupplier(ThrowingSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return supplier.lifted();
    }

    static <T, R> Function<T, Optional<R>> liftedFunction(ThrowingFunction<T, R> function) {
        Objects.requireNonNull(function);
        return function.lifted();
    }

}
