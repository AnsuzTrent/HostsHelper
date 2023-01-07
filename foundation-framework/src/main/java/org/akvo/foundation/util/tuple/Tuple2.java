package org.akvo.foundation.util.tuple;

import java.io.Serial;
import java.util.Map;
import java.util.Objects;

public sealed class Tuple2<T1, T2> extends Tuple1<T1> permits Tuple3 {
    @Serial
    private static final long serialVersionUID = -1L;
    protected final T2 t2;

    protected Tuple2(T1 t1, T2 t2) {
        super(t1);
        this.t2 = Objects.requireNonNull(t2, "t2");
    }

    public static <T1, T2> Tuple2<T1, T2> of(T1 t1, T2 t2) {
        return new Tuple2<>(t1, t2);
    }

    public static <T1, T2> Tuple2<T1, T2> fromEntry(Map.Entry<? extends T1, ? extends T2> entry) {
        return new Tuple2<>(entry.getKey(), entry.getValue());
    }

    public T2 getT2() {
        return t2;
    }

    @Override
    public Object get(int index) {
        return switch (index) {
            case 0 -> super.get(index);
            case 1 -> t2;
            default -> null;
        };
    }

    @Override
    public Object[] toArray() {
        return new Object[]{t1, t2};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple2<?, ?> tuple2)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(t2, tuple2.t2);
    }

    @Override
    public int hashCode() {
        return toHashCode();
    }

    @Override
    public int size() {
        return 2;
    }
}
