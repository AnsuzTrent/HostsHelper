package org.akvo.foundation.util.current;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class Schedulers {
    private static final Executor SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

    private Schedulers() {
    }

    public static Worker single() {
        return fromExecutor(SINGLE_THREAD_EXECUTOR);
    }

    public static Worker fromExecutor(Executor executor) {
        return new Worker(executor);
    }

    public record Worker(Executor executor) {
    }
}
