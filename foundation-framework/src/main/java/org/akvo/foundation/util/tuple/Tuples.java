package org.akvo.foundation.util.tuple;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public sealed interface Tuples extends Iterable<Object>, Serializable permits Tuple {
    static Tuples fromArray(Object... list) {
        if (list == null) {
            throw new IllegalArgumentException("null");
        }
        return switch (list.length) {
            case 1 -> of(list[0]);
            case 2 -> of(list[0], list[1]);
            case 3 -> of(list[0], list[1], list[2]);
            case 4 -> of(list[0], list[1], list[2], list[3]);
            case 5 -> of(list[0], list[1], list[2], list[3], list[4]);
            case 6 -> of(list[0], list[1], list[2], list[3], list[4], list[5]);
            case 7 -> of(list[0], list[1], list[2], list[3], list[4], list[5], list[6]);
            default -> of(new IllegalArgumentException("inappropriate argument number (" +
                list.length + "), need between 2 and 7 values"));
        };
    }

    static <T1> Tuple1<T1> of(T1 t1) {
        return Tuple1.of(t1);
    }

    static <T1, T2> Tuple2<T1, T2> fromEntry(Map.Entry<? extends T1, ? extends T2> entry) {
        Objects.requireNonNull(entry, "entry is null");
        return Tuple2.fromEntry(entry);
    }

    static <T1, T2> Tuple2<T1, T2> of(T1 t1, T2 t2) {
        return Tuple2.of(t1, t2);
    }

    static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
        return Tuple3.of(t1, t2, t3);
    }

    static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(T1 t1, T2 t2, T3 t3, T4 t4) {
        return Tuple4.of(t1, t2, t3, t4);
    }

    static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return Tuple5.of(t1, t2, t3, t4, t5);
    }

    static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        return Tuple6.of(t1, t2, t3, t4, t5, t6);
    }

    static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
        return Tuple7.of(t1, t2, t3, t4, t5, t6, t7);
    }

    static <E extends RuntimeException> Tuples of(E throwable) {
        throw throwable;
    }

    Object[] toArray();

    int size();

    Object get(int index);

    <T extends Tuples> T reverse();

    /**
     * 以List 的形式返回元组内所有元素
     *
     * @return 包含所有元素的有序不可变List
     */
    default List<Object> toList() {
        return List.of(toArray());
    }

    default int toHashCode() {
        return Stream.of(toArray())
            .mapToInt(Object::hashCode)
            .reduce(size(), (o1, o2) -> o1 * 31 + o2);
    }

    default boolean isEqual(Object o) {
        if (!(o instanceof Tuples tuples)
            || size() != tuples.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!Objects.equals(get(i), tuples.get(i))) {
                return false;
            }
        }
        return true;
    }


    default void forEachWithIndex(BiConsumer<Integer, ? super Object> action) {
        Objects.requireNonNull(action);
        List<Object> objectList = toList();
        for (int i = 0, l = objectList.size(); i < l; i++) {
            action.accept(i, objectList.get(i));
        }
    }

    @Override
    default Iterator<Object> iterator() {
        return toList().iterator();
    }

    @Override
    default Spliterator<Object> spliterator() {
        return toList().spliterator();
    }
}
