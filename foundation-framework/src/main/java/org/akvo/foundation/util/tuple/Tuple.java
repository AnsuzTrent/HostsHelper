package org.akvo.foundation.util.tuple;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract sealed class Tuple implements Tuples permits Tuple1 {

    @Override
    public String toString() {
        return Stream.of(toArray())
            .map(String::valueOf)
            .collect(Collectors.joining(",", "[", "]"));
    }
}
