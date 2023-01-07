package org.akvo.foundation.reactive.base;

import org.akvo.foundation.reactive.Controllable;

import java.util.concurrent.Flow;

public interface CoreSubscriber<T> extends Controllable, Flow.Subscriber<T> {

}
