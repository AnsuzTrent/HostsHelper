package org.akvo.foundation.util.throwing.function;

import org.akvo.foundation.util.throwing.WrappedException;
import org.akvo.foundation.util.throwing.operator.ThrowingOperator;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 一个可能抛出的消费者，相当于{@linkplain Consumer}
 *
 * @param <T> 提供给这个消费者的值类型
 */
@FunctionalInterface
public interface ThrowingConsumer<T> extends ThrowingOperator<Consumer<T>> {

    void accept(T t) throws Throwable;

    default ThrowingFunction<T, Void> asFunction() {
        return arg -> {
            accept(arg);
            return null;
        };
    }

    default ThrowingConsumer<T> butBeforeDo(final ThrowingConsumer<? super T> before) {
        Objects.requireNonNull(before);
        return t -> {
            before.accept(t);
            accept(t);
        };
    }

    default ThrowingConsumer<T> andAfterDo(final ThrowingConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return t -> {
            accept(t);
            after.accept(t);
        };
    }

    @Override
    default Consumer<T> sneaky() {
        return t -> {
            try {
                accept(t);
            } catch (final Throwable e) {
                ThrowingOperator.sneakyThrow(e);
            }
        };
    }

    @Override
    default Consumer<T> unchecked() {
        return t -> {
            try {
                accept(t);
            } catch (final Throwable e) {
                throw new WrappedException(e);
            }
        };
    }
}
