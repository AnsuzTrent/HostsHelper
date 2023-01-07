package org.akvo.foundation.util.tuple;

import java.io.Serial;
import java.util.Objects;

public sealed class Tuple5<T1, T2, T3, T4, T5> extends Tuple4<T1, T2, T3, T4> permits Tuple6 {
    @Serial
    private static final long serialVersionUID = -1L;
    protected final T5 t5;

    protected Tuple5(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        super(t1, t2, t3, t4);
        this.t5 = Objects.requireNonNull(t5, "t5");
    }

    public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return new Tuple5<>(t1, t2, t3, t4, t5);
    }

    public T5 getT5() {
        return t5;
    }

    @Override
    public Object get(int index) {
        return switch (index) {
            case 0, 1, 2, 3 -> super.get(index);
            case 4 -> t5;
            default -> null;
        };
    }

    @Override
    public Object[] toArray() {
        return new Object[]{t1, t2, t3, t4, t5};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple5<?, ?, ?, ?, ?> tuple5)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(t5, tuple5.t5);
    }

    @Override
    public int hashCode() {
        return toHashCode();
    }

    @Override
    public int size() {
        return 5;
    }
}
