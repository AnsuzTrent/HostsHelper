package org.akvo.foundation.reactive.subscriber;


import org.akvo.foundation.reactive.base.CoreSubscriber;

import java.util.concurrent.Flow;

public class ConvertSubscriber<T> implements CoreSubscriber<T> {
    private final Flow.Subscriber<? super T> subscriber;
    private Flow.Subscription subscription;

    public ConvertSubscriber(Flow.Subscriber<? super T> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T item) {
        subscriber.onNext(item);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

    @Override
    public void cancel() {
        subscription.cancel();
    }
}
