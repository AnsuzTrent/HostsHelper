package org.akvo.foundation.util.current.policy;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * 当任务队列过长时处于阻塞状态，直到添加到队列中
 * 如果阻塞过程中被中断，就会抛出{@link InterruptedException}异常<br>
 * 有时候在线程池内访问第三方接口，只希望固定并发数去访问，并且不希望丢弃任务时使用此策略，队列满的时候会处于阻塞状态(例如刷库的场景)
 */
public class BlockPolicy implements RejectedExecutionHandler {
    private final Consumer<Runnable> doOnShutDown;

    public BlockPolicy() {
        this(r -> {
        });
    }

    public BlockPolicy(Consumer<Runnable> doOnShutDown) {
        this.doOnShutDown = doOnShutDown;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        // 线程池未关闭时，阻塞等待
        if (!executor.isShutdown()) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RejectedExecutionException("Task " + r + " rejected from " + executor);
            }
        }
        // 当设置了关闭时候的处理
        doOnShutDown.accept(r);
    }
}
