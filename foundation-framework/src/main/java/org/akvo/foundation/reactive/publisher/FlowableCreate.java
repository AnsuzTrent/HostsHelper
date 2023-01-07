package org.akvo.foundation.reactive.publisher;

import org.akvo.foundation.reactive.emitter.Sink;

import java.util.function.Consumer;

public class FlowableCreate<T> {
    private Consumer<Sink<T>> sinkConsumer;
}
