package org.akvo.foundation.util.tuple;

import java.io.Serial;
import java.util.Objects;
import java.util.function.Function;

public sealed class Tuple1<T1> extends Tuple permits Tuple2 {
    @Serial
    private static final long serialVersionUID = -1L;
    protected final T1 t1;

    protected Tuple1(T1 t1) {
        this.t1 = Objects.requireNonNull(t1, "t1");
    }

    public static <T1> Tuple1<T1> of(T1 t1) {
        return new Tuple1<>(t1);
    }

    public T1 getT1() {
        return t1;
    }

    public <T> Tuple1<T> mapT1(Function<T1, T> function) {
        return new Tuple1<>(function.apply(t1));
    }

    @Override
    public Object get(int index) {
        return index == 0 ? t1 : null;
    }

    @Override
    public <T extends Tuples> T reverse() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[]{t1};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple1<?> tuple1 = (Tuple1<?>) o;
        return Objects.equals(t1, tuple1.t1);
    }

    @Override
    public int hashCode() {
        return toHashCode();
    }

    @Override
    public int size() {
        return 1;
    }
}
