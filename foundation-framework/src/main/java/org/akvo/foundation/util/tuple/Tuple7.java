package org.akvo.foundation.util.tuple;

import java.io.Serial;
import java.util.Objects;

public final class Tuple7<T1, T2, T3, T4, T5, T6, T7> extends Tuple6<T1, T2, T3, T4, T5, T6> {
    @Serial
    private static final long serialVersionUID = -1L;
    private final T7 t7;

    private Tuple7(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
        super(t1, t2, t3, t4, t5, t6);
        this.t7 = Objects.requireNonNull(t7, "t7");
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
        return new Tuple7<>(t1, t2, t3, t4, t5, t6, t7);
    }

    public T7 getT7() {
        return t7;
    }

    @Override
    public Object get(int index) {
        return switch (index) {
            case 0, 1, 2, 3, 4, 5 -> super.get(index);
            case 6 -> t7;
            default -> null;
        };
    }

    @Override
    public Object[] toArray() {
        return new Object[]{t1, t2, t3, t4, t5, t6, t7};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple7<?, ?, ?, ?, ?, ?, ?> tuple7)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(t7, tuple7.t7);
    }

    @Override
    public int hashCode() {
        return toHashCode();
    }

    @Override
    public int size() {
        return 7;
    }
}
