package org.akvo.foundation.reactive.base;

import org.akvo.foundation.reactive.Flowable;
import org.akvo.foundation.reactive.Hooks;

import java.util.concurrent.Flow;

public class FlowableAssembly<T> extends Flowable<T> {
    private final Flowable<? super T> source;

    public FlowableAssembly(Flowable<? super T> source) {
        this.source = source;
    }

    @Override
    public void subscribe0(CoreSubscriber<? super T> subscriber) {
        if (!Hooks.isLogTrace()) {
            return;
        }
        source.subscribe0(new AssemblySubscriber<>());
    }

    static class AssemblySubscriber<T> implements CoreSubscriber<T> {

        @Override
        public void onSubscribe(Flow.Subscription subscription) {

        }

        @Override
        public void onNext(T item) {

        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }

        @Override
        public void cancel() {

        }
    }
}
