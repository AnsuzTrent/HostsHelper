package org.akvo.foundation.util.throwing;

public class WrappedException extends RuntimeException {
    public WrappedException(Throwable cause) {
        super(cause.getMessage(), cause, cause != null, cause != null);
    }
}
