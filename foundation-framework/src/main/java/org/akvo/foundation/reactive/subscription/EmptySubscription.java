package org.akvo.foundation.reactive.subscription;

import java.util.concurrent.Flow;

public final class EmptySubscription implements Flow.Subscription {
    private static final EmptySubscription INSTANCE = new EmptySubscription();

    private EmptySubscription() {
    }

    public static EmptySubscription instance() {
        return INSTANCE;
    }

    @Override
    public void request(long n) {
    }

    @Override
    public void cancel() {
    }
}
