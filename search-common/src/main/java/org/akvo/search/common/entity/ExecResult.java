package org.akvo.search.common.entity;

public record ExecResult(
    Level level,
    String msg
) {
    public static ExecResult success(String msg) {
        return new ExecResult(Level.SUCCESS, msg);
    }

    public static ExecResult fail(String msg) {
        return new ExecResult(Level.FAIL, msg);
    }

    public static ExecResult exception(Throwable t) {
        return new ExecResult(Level.EXCEPTION, "%s:%s".formatted(t.getClass().getName(), t.getMessage()));
    }

    public static ExecResult terminal() {
        return new ExecResult(Level.TERMINAL, null);
    }

    public enum Level {
        SUCCESS,
        FAIL,
        EXCEPTION,
        TERMINAL,
        RESULT,
    }
}
