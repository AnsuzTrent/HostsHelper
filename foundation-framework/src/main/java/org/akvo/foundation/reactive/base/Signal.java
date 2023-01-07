package org.akvo.foundation.reactive.base;

public enum Signal {
    /**
     * 触发订阅
     */
    SUBSCRIBE,
    /**
     * 通过订阅发出请求
     */
    REQUEST,
    /**
     * 取消订阅
     */
    CANCEL,
    /**
     * 操作收到订阅
     */
    ON_SUBSCRIBE,
    /**
     * 操作收到值
     */
    ON_NEXT,
    /**
     * 操作收到错误
     */
    ON_ERROR,
    /**
     * 操作完成
     */
    ON_COMPLETE,

}
