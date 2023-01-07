package org.akvo.foundation.util.throwing.operator;

import org.akvo.foundation.util.throwing.WrappedException;

public interface ThrowingOperator<E> {
    /**
     * 抛出异常
     */
    @SuppressWarnings("unchecked")
    static <X extends Throwable, R> R sneakyThrow(Throwable t) throws X {
        throw (X) t;
    }

    @SuppressWarnings("unchecked")
    static <X extends Throwable> X propagate(Throwable t) {
        if (t instanceof VirtualMachineError ||
            t instanceof ThreadDeath ||
            t instanceof LinkageError) {
            sneakyThrow(t);
        }

        return (X) new WrappedException(t);
    }

    /**
     * 返回原本的异常
     */
    E sneaky();

    /**
     * 返回包装过的异常
     */
    E unchecked();

}
