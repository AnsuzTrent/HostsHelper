package org.akvo.foundation.util.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface StreamUtil {
    static <T> Stream<T> streamNullable(Collection<T> collection) {
        return collection == null ? Stream.empty() : collection.stream();
    }

    static <T> Stream<T> streamNullable(T[] array) {
        return array == null ? Stream.empty() : Arrays.stream(array);
    }

    static <T> Stream<T> streamNullable(T entity) {
        return entity == null ? Stream.empty() : Stream.of(entity);
    }

    @SafeVarargs
    static <T> Stream<T> merge(Stream<? extends T>... streams) {
        return streamNullable(streams)
            .flatMap(Function.identity());
    }

    static <T> Stream<T> streamIterator(Iterator<? extends T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    static <T> Stream<T> streamIterable(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
