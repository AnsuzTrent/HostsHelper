package org.akvo.foundation.reactive.publisher;

import org.akvo.foundation.reactive.Flowable;
import org.akvo.foundation.reactive.base.CoreSubscriber;

public class FlowableEmpty<T> extends Flowable<T> {
    @Override
    public void subscribe0(CoreSubscriber<? super T> subscriber) {

    }
}
