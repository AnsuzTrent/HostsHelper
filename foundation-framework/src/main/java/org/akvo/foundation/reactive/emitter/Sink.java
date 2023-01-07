package org.akvo.foundation.reactive.emitter;

public interface Sink<T> {
    /**
     * 发出一个值
     *
     * @param item 值
     */
    void next(T item);

    /**
     * 标识完成
     */
    void complete();

    /**
     * 发出错误
     *
     * @param t 错误
     */
    void error(Throwable t);
}
