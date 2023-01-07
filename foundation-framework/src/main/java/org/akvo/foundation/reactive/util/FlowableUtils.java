package org.akvo.foundation.reactive.util;

import org.akvo.foundation.reactive.base.CoreSubscriber;
import org.akvo.foundation.reactive.subscriber.ConvertSubscriber;
import org.akvo.foundation.reactive.subscription.EmptySubscription;

import java.util.concurrent.Flow;

public interface FlowableUtils {
    int DEFAULT_BATCH_SIZE = Math.max(16, 256);

    static void complete(Flow.Subscriber<?> subscriber) {
        complete(subscriber, EmptySubscription.instance());
    }

    static void complete(Flow.Subscriber<?> subscriber, Flow.Subscription subscription) {
        subscriber.onSubscribe(subscription);
        subscriber.onComplete();
    }

    static void error(Flow.Subscriber<?> subscriber, Throwable e) {
        error(subscriber, e, EmptySubscription.instance());
    }

    static void error(Flow.Subscriber<?> subscriber, Throwable e, Flow.Subscription subscription) {
        subscriber.onSubscribe(subscription);
        subscriber.onError(e);
    }

    static <T> CoreSubscriber<? super T> toCoreSubscriber(Flow.Subscriber<? super T> subscriber) {
        if (subscriber instanceof CoreSubscriber<? super T> s) {
            return s;
        }
        return new ConvertSubscriber<>(subscriber);
    }

}
