package org.akvo.foundation.reactive.subscriber;

import org.akvo.foundation.reactive.base.CoreSubscriber;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public class CompleteFutureSubscriber<T> extends CompletableFuture<T> implements CoreSubscriber<T> {
    Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T item) {
        if (subscription != null) {
            complete(item);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (subscription != null) {
            completeExceptionally(throwable);
        }
    }

    @Override
    public void onComplete() {
        if (subscription != null) {
            complete(null);
        }
    }

    @Override
    public void cancel() {
        if (subscription != null) {
            subscription.cancel();
        }
    }
}
