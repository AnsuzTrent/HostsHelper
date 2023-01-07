package org.akvo.foundation.util.throwing.function;

import org.akvo.foundation.util.throwing.WrappedException;
import org.akvo.foundation.util.throwing.operator.ThrowingOperator;

@FunctionalInterface
public interface ThrowingRunnable extends ThrowingOperator<Runnable> {
    @SuppressWarnings("all")
    void run() throws Throwable;

    default ThrowingFunction<Void, Void> asFunction() {
        return arg -> {
            run();
            return null;
        };
    }

    default ThrowingSupplier<Void> asSupplier() {
        return () -> {
            run();
            return null;
        };
    }

    default ThrowingConsumer<Void> asConsumer() {
        return t -> run();
    }

    @Override
    default Runnable sneaky() {
        return () -> {
            try {
                run();
            } catch (final Throwable e) {
                ThrowingOperator.sneakyThrow(e);
            }
        };
    }

    @Override
    default Runnable unchecked() {
        return () -> {
            try {
                run();
            } catch (final Throwable e) {
                throw new WrappedException(e);
            }
        };
    }
}
