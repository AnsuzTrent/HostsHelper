package org.akvo.foundation.reactive;

import org.akvo.foundation.reactive.base.CorePublisher;
import org.akvo.foundation.reactive.base.CoreSubscriber;
import org.akvo.foundation.reactive.subscriber.BlockingIterableSubscriber;
import org.akvo.foundation.reactive.subscriber.BlockingValueSubscriber;
import org.akvo.foundation.reactive.subscriber.CompleteFutureSubscriber;
import org.akvo.foundation.reactive.subscriber.DefaultSubscriber;
import org.akvo.foundation.reactive.util.FlowableUtils;
import org.akvo.foundation.util.collection.StreamUtil;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public abstract class Flowable<T> implements CorePublisher<T> {
    protected Flowable() {
    }

    private static <T> Flowable<T> onAssembly(Flowable<T> source) {
        return source;
    }

    public static <T> Flowable<T> empty() {
        return onAssembly(null);
    }

    @SuppressWarnings("unchecked")
    public static <T> Flowable<T> from(Flow.Publisher<? extends T> source) {
        if (source instanceof Flowable<? extends T> f) {
            return onAssembly((Flowable<T>) f);
        }
        return onAssembly(empty());
    }


    public CompletableFuture<T> toFuture() {
        return subscribeWith(new CompleteFutureSubscriber<>());
    }

    public Iterable<T> toIterable() {
        return toIterable(FlowableUtils.DEFAULT_BATCH_SIZE);
    }

    public Iterable<T> toIterable(int batchSize) {
        return toIterable(batchSize, null);
    }

    public Iterable<T> toIterable(int batchSize, Supplier<Queue<T>> queueProvider) {
        if (queueProvider == null) {
            queueProvider = LinkedList::new;
        }
        return new BlockingIterableSubscriber<>(this, batchSize, queueProvider);
    }

    public Stream<T> toStream() {
        return toStream(FlowableUtils.DEFAULT_BATCH_SIZE);
    }

    public Stream<T> toStream(int batchSize) {
        return StreamUtil.streamIterable(toIterable(batchSize));
    }

    @SuppressWarnings("unchecked")
    public T[] toArray() {
        return (T[]) toStream().toArray();
    }

    public T blockFirst() {
        return blockFirst(Duration.ZERO);
    }

    public T blockFirst(Duration timeout) {
        return subscribeWith(new BlockingValueSubscriber<>(false))
            .blockingAndGet(timeout);
    }

    public T blockLast() {
        return blockLast(Duration.ZERO);
    }

    public T blockLast(Duration timeout) {
        return subscribeWith(new BlockingValueSubscriber<>(true))
            .blockingAndGet(timeout);
    }

    private <E extends CoreSubscriber<? super T>> E subscribeWith(E subscriber) {
        subscribe(subscriber);
        return subscriber;
    }

    public final Controllable subscribe() {
        return subscribe(null, null, null, null);
    }

    public final Controllable subscribe(Consumer<? super T> valueConsumer) {
        Objects.requireNonNull(valueConsumer, "valueConsumer");
        return subscribe(valueConsumer, null, null, null);
    }

    public final Controllable subscribe(Consumer<? super T> valueConsumer,
                                        Consumer<? super Throwable> errorConsumer) {
        Objects.requireNonNull(errorConsumer, "errorConsumer");
        return subscribe(valueConsumer, errorConsumer, null, null);
    }

    public final Controllable subscribe(Consumer<? super T> valueConsumer,
                                        Consumer<? super Throwable> errorConsumer,
                                        Runnable completeConsumer) {
        Objects.requireNonNull(completeConsumer, "completeConsumer");
        return subscribe(valueConsumer, errorConsumer, completeConsumer, null);
    }

    public final Controllable subscribe(Consumer<? super T> valueConsumer,
                                        Consumer<? super Throwable> errorConsumer,
                                        Runnable completeConsumer,
                                        Consumer<? super Flow.Subscription> subscriptionConsumer) {
        return subscribeWith(new DefaultSubscriber<>(valueConsumer, errorConsumer, completeConsumer, subscriptionConsumer));
    }

    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        subscribe0(FlowableUtils.toCoreSubscriber(subscriber));
    }

    public abstract void subscribe0(CoreSubscriber<? super T> subscriber);
}
