package org.akvo.foundation.util.tuple;

import java.io.Serial;
import java.util.Objects;

public sealed class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> permits Tuple4 {
    @Serial
    private static final long serialVersionUID = -1L;
    protected final T3 t3;

    protected Tuple3(T1 t1, T2 t2, T3 t3) {
        super(t1, t2);
        this.t3 = Objects.requireNonNull(t3, "t3");
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>(t1, t2, t3);
    }

    public T3 getT3() {
        return t3;
    }

    @Override
    public Object get(int index) {
        return switch (index) {
            case 0, 1 -> super.get(index);
            case 2 -> t3;
            default -> null;
        };
    }

    @Override
    public Object[] toArray() {
        return new Object[]{t1, t2, t3};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple3<?, ?, ?> tuple3)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(t3, tuple3.t3);
    }

    @Override
    public int hashCode() {
        return toHashCode();
    }

    @Override
    public int size() {
        return 3;
    }
}
