package org.akvo.foundation.util.tuple;

import java.io.Serial;
import java.util.Objects;

public sealed class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> permits Tuple5 {
    @Serial
    private static final long serialVersionUID = -1L;
    protected final T4 t4;

    protected Tuple4(T1 t1, T2 t2, T3 t3, T4 t4) {
        super(t1, t2, t3);
        this.t4 = Objects.requireNonNull(t4, "t4");
    }

    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(T1 t1, T2 t2, T3 t3, T4 t4) {
        return new Tuple4<>(t1, t2, t3, t4);
    }

    public T4 getT4() {
        return t4;
    }

    @Override
    public Object get(int index) {
        return switch (index) {
            case 0, 1, 2 -> super.get(index);
            case 3 -> t4;
            default -> null;
        };
    }

    @Override
    public Object[] toArray() {
        return new Object[]{t1, t2, t3, t4};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple4<?, ?, ?, ?> tuple4)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(t4, tuple4.t4);
    }

    @Override
    public int hashCode() {
        return toHashCode();
    }

    @Override
    public int size() {
        return 4;
    }
}
