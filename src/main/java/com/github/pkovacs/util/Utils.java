package com.github.pkovacs.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Provides various useful utility methods, also including the ones defined in {@link InputUtils}.
 */
public class Utils extends InputUtils {

    protected Utils() {
    }

    /**
     * Returns the union of the given collections.
     */
    public static <E> Set<E> unionOf(Collection<? extends E> a, Collection<? extends E> b) {
        return unionOf(List.of(a, b));
    }

    /**
     * Returns the union of the given streams.
     */
    public static <E> Set<E> unionOf(Stream<? extends E> a, Stream<? extends E> b) {
        return unionOf(List.of(a.toList(), b.toList()));
    }

    /**
     * Returns the union of the given collections.
     */
    public static <E> Set<E> unionOf(Collection<? extends Collection<? extends E>> collections) {
        var result = new HashSet<E>(collections.iterator().next());
        collections.stream().skip(1).forEach(result::addAll);
        return result;
    }

    /**
     * Returns the intersection of the given collections.
     */
    public static <E> Set<E> intersectionOf(Collection<? extends E> a, Collection<? extends E> b) {
        return intersectionOf(List.of(a, b));
    }

    /**
     * Returns the intersection of the given streams.
     */
    public static <E> Set<E> intersectionOf(Stream<? extends E> a, Stream<? extends E> b) {
        return intersectionOf(List.of(a.toList(), b.toList()));
    }

    /**
     * Returns the intersection of the given collections.
     */
    public static <E> Set<E> intersectionOf(Collection<? extends Collection<? extends E>> collections) {
        var result = new HashSet<E>(collections.iterator().next());
        collections.stream()
                .skip(1)
                .map(c -> c instanceof Set ? c : new HashSet<>(c))
                .forEach(result::retainAll);
        return result;
    }

}
