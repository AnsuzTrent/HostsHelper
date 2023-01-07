package org.akvo.foundation.reactive.subscriber;


import org.akvo.foundation.reactive.base.CoreSubscriber;

import java.util.concurrent.Flow;
import java.util.function.Consumer;

public class DefaultSubscriber<T> implements CoreSubscriber<T> {
    final Consumer<? super T> valueConsumer;
    final Consumer<? super Throwable> errorConsumer;
    final Runnable completeConsumer;
    final Consumer<? super Flow.Subscription> subscriptionConsumer;
    Flow.Subscription subscription;

    public DefaultSubscriber(Consumer<? super T> valueConsumer,
                             Consumer<? super Throwable> errorConsumer,
                             Runnable completeConsumer,
                             Consumer<? super Flow.Subscription> subscriptionConsumer) {
        this.valueConsumer = valueConsumer;
        this.errorConsumer = errorConsumer;
        this.completeConsumer = completeConsumer;
        this.subscriptionConsumer = subscriptionConsumer;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        if (subscriptionConsumer != null) {
            try {
                subscriptionConsumer.accept(subscription);
            } catch (Throwable t) {
                subscription.cancel();
                onError(t);
            }
        }
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T item) {
        try {
            if (valueConsumer != null) {
                valueConsumer.accept(item);
            }
        } catch (Throwable t) {
            subscription.cancel();
            onError(t);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (errorConsumer != null) {
            errorConsumer.accept(throwable);
        }
    }

    @Override
    public void onComplete() {
        if (completeConsumer != null) {
            try {
                completeConsumer.run();
            } catch (Throwable t) {
                subscription.cancel();
                onError(t);
            }
        }
    }

    @Override
    public void cancel() {
        subscription.cancel();
    }
}
