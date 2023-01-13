package com.github.pkovacs.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Provides various useful utility methods, also including the ones defined in {@link InputUtils}.
 */
public class Utils extends InputUtils {

    protected Utils() {
    }

    /**
     * Constrains the given {@code index} to the closed range {@code [0..(size - 1)]}.
     */
    public static int constrainIndex(int index, int size) {
        return constrainToRange(index, 0, size - 1);
    }

    /**
     * Wraps the given {@code index} to the closed range {@code [0..(size - 1)]}.
     */
    public static int wrapIndex(int index, int size) {
        checkRange(0, size - 1);
        return Math.floorMod(index, size);
    }

    /**
     * Constrains the given int {@code value} to the closed range {@code [min..max]}.
     */
    public static int constrainToRange(int value, int min, int max) {
        checkRange(min, max);
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Constrains the given long {@code value} to the closed range {@code [min..max]}.
     */
    public static long constrainToRange(long value, long min, long max) {
        checkRange(min, max);
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Wraps the given int {@code value} to the closed range {@code [min..max]}.
     */
    public static int wrapToRange(int value, int min, int max) {
        checkRange(min, max);
        return min + Math.floorMod(value - min, max - min + 1);
    }

    /**
     * Wraps the given long {@code value} to the closed range {@code [min..max]}.
     */
    public static long wrapToRange(long value, long min, long max) {
        checkRange(min, max);
        return min + Math.floorMod(value - min, max - min + 1);
    }

    /**
     * Returns true if the given {@code value} is within the closed range {@code [min..max]}.
     */
    public static <T extends Comparable<T>> boolean isInRange(T value, T min, T max) {
        checkRange(min, max);
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    private static <T extends Comparable<T>> void checkRange(T min, T max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Minimum value " + min + " is greater than maximum value " + max + ".");
        }
    }

    /**
     * Returns the elements of the given {@code int} array as an unmodifiable list.
     */
    public static List<Integer> listOf(int[] ints) {
        return streamOf(ints).boxed().toList();
    }

    /**
     * Returns the elements of the given {@code int} array as an unmodifiable set.
     */
    public static Set<Integer> setOf(int[] ints) {
        return streamOf(ints).boxed().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the elements of the given {@code int} array as an {@link IntStream}.
     */
    public static IntStream streamOf(int[] ints) {
        return IntStream.of(ints);
    }

    /**
     * Returns the elements of the given {@code long} array as an unmodifiable list.
     */
    public static List<Long> listOf(long[] longs) {
        return streamOf(longs).boxed().toList();
    }

    /**
     * Returns the elements of the given {@code long} array as an unmodifiable set.
     */
    public static Set<Long> setOf(long[] longs) {
        return streamOf(longs).boxed().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the elements of the given {@code long} array as a {@link LongStream}.
     */
    public static LongStream streamOf(long[] longs) {
        return LongStream.of(longs);
    }

    /**
     * Returns the elements of the given {@code char} array as an unmodifiable list.
     */
    public static List<Character> listOf(char[] chars) {
        return streamOf(chars).toList();
    }

    /**
     * Returns the elements of the given {@code char} array as an unmodifiable set.
     */
    public static Set<Character> setOf(char[] chars) {
        return streamOf(chars).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the elements of the given {@code char} array as a stream.
     */
    public static Stream<Character> streamOf(char[] chars) {
        return IntStream.range(0, chars.length).mapToObj(i -> chars[i]);
    }

    /**
     * Returns the characters of the given {@code CharSequence} as a stream.
     */
    public static Stream<Character> charsOf(CharSequence s) {
        return s.toString().chars().mapToObj(i -> (char) i);
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
