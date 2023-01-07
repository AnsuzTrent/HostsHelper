package org.akvo.foundation.reactive.subscriber;

import org.akvo.foundation.reactive.base.CoreSubscriber;
import org.akvo.foundation.util.throwing.operator.ThrowingOperator;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

public class BlockingValueSubscriber<T> extends CountDownLatch implements CoreSubscriber<T> {
    private final boolean isBlockLast;
    private T value;
    private Throwable t;
    private Flow.Subscription subscription;
    private volatile boolean cancelled;

    /**
     * the number of times {@link CountDownLatch#countDown} must be invoked
     * before threads can pass through {@link CountDownLatch#await}
     *
     * @throws IllegalArgumentException if {@code count} is negative
     */
    public BlockingValueSubscriber(boolean isBlockLast) {
        super(1);
        this.isBlockLast = isBlockLast;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        if (!cancelled) {
            subscription.request(Long.MAX_VALUE);
        }
    }

    @Override
    public void onNext(T item) {
        if (isBlockLast) {
            value = item;
            return;
        }
        if (value == null) {
            value = item;
            cancel();
            countDown();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (isBlockLast) {
            value = null;
            t = throwable;
        }
        if (value == null) {
            t = throwable;
        }
        countDown();
    }

    @Override
    public void onComplete() {
        countDown();
    }

    @Override
    public void cancel() {
        if (cancelled) {
            return;
        }
        cancelled = true;
        subscription.cancel();
    }

    public T blockingAndGet(Duration timeout) {
        if (getCount() != 0) {
            try {
                if (Duration.ZERO.equals(timeout)) {
                    await();
                } else {
                    if (!await(timeout.toNanos(), TimeUnit.NANOSECONDS)) {
                        cancel();
                        throw new IllegalStateException("Timeout on blocking read for %s nanos".formatted(timeout));
                    }
                }
            } catch (InterruptedException e) {
                cancel();
                Thread.currentThread().interrupt();
                ThrowingOperator.sneakyThrow(e);
            }
        }

        if (t != null) {
            RuntimeException e = ThrowingOperator.propagate(t);
            e.addSuppressed(new Exception("#block terminated with an error"));
            throw e;
        }

        return value;
    }
}
