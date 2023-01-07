package org.akvo.foundation.reactive.subscriber;

import org.akvo.foundation.reactive.base.CorePublisher;
import org.akvo.foundation.reactive.base.CoreSubscriber;
import org.akvo.foundation.util.throwing.operator.ThrowingOperator;

import java.util.*;
import java.util.concurrent.Flow;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BlockingIterableSubscriber<T> implements Iterable<T> {
    final int batchSize;
    final BlockingIterator<T> iterator;
    final CorePublisher<T> publisher;

    public BlockingIterableSubscriber(CorePublisher<T> publisher,
                                      int batchSize,
                                      Supplier<Queue<T>> queueSupplier) {
        this.batchSize = batchSize;
        this.publisher = publisher;
        this.iterator = new BlockingIterator<>(batchSize, queueSupplier.get());
    }

    @Override
    public Iterator<T> iterator() {
        publisher.subscribe(iterator);
        return iterator;
    }

    @Override
    public Spliterator<T> spliterator() {
        return stream().spliterator();
    }

    public Stream<T> stream() {
        publisher.subscribe(iterator);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false)
            .onClose(iterator);
    }


    static final class BlockingIterator<T> implements CoreSubscriber<T>, Iterator<T>, Runnable {
        final int batchSize;
        final Queue<T> queue;
        final int limit;
        final Lock lock;
        final Condition condition;
        volatile boolean done;
        Throwable error;
        long produced;

        volatile Flow.Subscription subscription;

        BlockingIterator(int batchSize,
                         Queue<T> queue) {
            this.batchSize = batchSize;
            this.limit = (batchSize == Integer.MAX_VALUE ? Integer.MAX_VALUE : (batchSize - (batchSize >> 2)));
            this.queue = queue;
            this.lock = new ReentrantLock();
            this.condition = lock.newCondition();
        }

        @Override
        public boolean hasNext() {
            while (true) {
                if (done) {
                    Throwable e = error;
                    if (e != null) {
                        throw ThrowingOperator.<RuntimeException>propagate(e);
                    }
                    if (queue.isEmpty()) {
                        return false;
                    }
                }
                if (!queue.isEmpty()) {
                    return true;
                }
                lock.lock();
                try {
                    while (!done && queue.isEmpty()) {
                        condition.await();
                    }
                } catch (InterruptedException ex) {
                    run();
                    Thread.currentThread().interrupt();
                    throw ThrowingOperator.<RuntimeException>propagate(ex);
                } finally {
                    lock.unlock();
                }
            }
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            T v = queue.poll();
            if (v == null) {
                run();
                throw new IllegalStateException("Queue is empty: Expected one element to be available from the Reactive Streams source.");
            }

            long p = produced + 1;
            if (p == limit) {
                produced = 0;
                subscription.request(p);
            } else {
                produced = p;
            }

            return v;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(batchSize == Integer.MAX_VALUE ? Long.MAX_VALUE : batchSize);
        }

        @Override
        public void onNext(T item) {
            if (queue.offer(item)) {
                signalConsumer();
            } else {
                subscription.cancel();
                RuntimeException throwable = new RuntimeException("Queue is full: Reactive Streams source doesn't respect backpressure");
                onError(throwable);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            error = throwable;
            done = true;
            signalConsumer();
        }

        @Override
        public void onComplete() {
            done = true;
            signalConsumer();
        }

        @Override
        public void cancel() {
            subscription.cancel();
            signalConsumer();
        }

        @Override
        public void run() {
            signalConsumer();
        }

        void signalConsumer() {
            lock.lock();
            try {
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

    }
}
