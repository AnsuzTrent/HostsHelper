package org.akvo.foundation.util.tuple;

import java.io.Serial;
import java.util.Objects;

public sealed class Tuple6<T1, T2, T3, T4, T5, T6> extends Tuple5<T1, T2, T3, T4, T5> permits Tuple7 {
    @Serial
    private static final long serialVersionUID = -1L;
    protected final T6 t6;

    protected Tuple6(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        super(t1, t2, t3, t4, t5);
        this.t6 = Objects.requireNonNull(t6, "t6");
    }

    public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        return new Tuple6<>(t1, t2, t3, t4, t5, t6);
    }

    public T6 getT6() {
        return t6;
    }

    @Override
    public Object get(int index) {
        return switch (index) {
            case 0, 1, 2, 3, 4 -> super.get(index);
            case 5 -> t6;
            default -> null;
        };
    }

    @Override
    public Object[] toArray() {
        return new Object[]{t1, t2, t3, t4, t5, t6};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple6<?, ?, ?, ?, ?, ?> tuple6)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(t6, tuple6.t6);
    }

    @Override
    public int hashCode() {
        return toHashCode();
    }

    @Override
    public int size() {
        return 6;
    }
}
