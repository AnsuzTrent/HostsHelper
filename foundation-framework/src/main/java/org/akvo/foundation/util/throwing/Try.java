package org.akvo.foundation.util.throwing;

import org.akvo.foundation.util.throwing.function.ThrowingConsumer;
import org.akvo.foundation.util.throwing.function.ThrowingFunction;
import org.akvo.foundation.util.throwing.function.ThrowingPredicate;
import org.akvo.foundation.util.throwing.function.ThrowingSupplier;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Try<T> {
    private static final Try<?> EMPTY = new Try<>();
    private T value;
    private Throwable throwable;

    private Try() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Try<T> empty() {
        return (Try<T>) EMPTY;
    }

    public static <T> Try<T> success(T value) {
        Try<T> tTry = new Try<>();
        tTry.value = value;
        return tTry;
    }

    public static <T> Try<T> fail(Throwable throwable) {
        Try<T> tTry = new Try<>();
        tTry.throwable = throwable;
        return tTry;
    }

    public static <T> Try<T> of(ThrowingSupplier<T> supplier) {
        try {
            return success(supplier.get());
        } catch (Throwable e) {
            return fail(e);
        }
    }

    public static <T> Try<T> from(ThrowingSupplier<Try<T>> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            return fail(e);
        }
    }


    public <E> Try<E> map(ThrowingFunction<? super T, ? extends E> mapper) {
        Objects.requireNonNull(mapper);
        if (isFail()) {
            return fail(throwable);
        }
        if (isEmpty()) {
            return empty();
        }
        return of(() -> mapper.apply(value));
    }

    public <E> Try<E> flatMap(ThrowingFunction<? super T, ? extends Try<E>> mapper) {
        Objects.requireNonNull(mapper);
        if (isFail()) {
            return fail(throwable);
        }
        if (isEmpty()) {
            return empty();
        }
        return from(() -> mapper.apply(value));
    }

    public Try<T> filter(ThrowingPredicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (isNotEmpty()) {
            return from(() -> predicate.test(value) ? this : empty());
        }
        return this;
    }

    public Try<T> doOnEmpty(Runnable runnable) {
        Objects.requireNonNull(runnable);
        if (isEmpty()) {
            runnable.run();
        }
        return this;
    }

    public Try<T> doOnError(Runnable runnable) {
        Objects.requireNonNull(runnable);
        if (isFail()) {
            runnable.run();
        }
        return this;
    }

    public Try<T> switchIfEmpty(Try<T> other) {
        if (isEmpty()) {
            return other;
        }
        return this;
    }

    public Try<T> switchIfThrow(Try<T> other) {
        if (isFail()) {
            return other;
        }
        return this;
    }

    public Try<T> onEmptyResume(Supplier<? extends T> other) {
        Objects.requireNonNull(other);
        if (isEmpty()) {
            return of(other::get);
        }
        return this;
    }

    public Try<T> onErrorResume(ThrowingFunction<Throwable, ? extends T> mapper) {
        Objects.requireNonNull(mapper);
        if (isFail()) {
            return of(() -> mapper.apply(throwable));
        }
        return this;
    }

    public void ifPresent(ThrowingConsumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        if (isNotEmpty()) {
            try {
                consumer.accept(value);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void ifThrow(Consumer<? super Throwable> consumer) {
        Objects.requireNonNull(consumer);
        if (isFail()) {
            consumer.accept(throwable);
        }
    }

    public T orEmptyGet(Supplier<? extends T> other) {
        Objects.requireNonNull(other);
        if (isEmpty()) {
            return other.get();
        }
        return value;
    }

    public T orThrowGet(Supplier<? extends T> other) {
        Objects.requireNonNull(other);
        if (isFail()) {
            return other.get();
        }
        return value;
    }

    public <X extends Throwable> T throwIfEmpty(Supplier<? extends X> exceptionSupplier) throws X {
        Objects.requireNonNull(exceptionSupplier);
        if (isNotEmpty()) {
            return getValue();
        }
        throw exceptionSupplier.get();
    }

    public T reThrow() throws Throwable {
        if (isFail()) {
            throw throwable;
        }
        return getValue();
    }

    public T getValue() {
        return value;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isSuccess() {
        return throwable == null;
    }

    public boolean isFail() {
        return !isSuccess();
    }

    public boolean isEmpty() {
        return isSuccess() && value == null;
    }

    public boolean isNotEmpty() {
        return isSuccess() && value != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Try<?> aTry
            && Objects.equals(value, aTry.value)
            && Objects.equals(throwable, aTry.throwable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, throwable);
    }

    @Override
    public String toString() {
        return "Try%s".formatted(throwable == null
            ? (value == null ? ".empty" : "{%s}".formatted(value))
            : "{%s:%s}".formatted(throwable.getClass().getName(),
            throwable.getMessage()));
    }
}
