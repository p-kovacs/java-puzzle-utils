package com.github.pkovacs.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Provides various useful utility methods, also including the ones defined in {@link InputUtils}.
 * <p>
 * You can extend this class in order to provide easier access to its methods.
 */
public class Utils extends InputUtils {

    protected Utils() {
    }

    // ****************************** MATH UTILS ******************************

    /**
     * Returns the minimum of the given {@code int} values.
     *
     * @throws java.util.NoSuchElementException if no numbers are given
     */
    public static int min(int... ints) {
        return streamOf(ints).min().orElseThrow();
    }

    /**
     * Returns the minimum of the given {@code long} values.
     *
     * @throws java.util.NoSuchElementException if no numbers are given
     */
    public static long min(long... longs) {
        return streamOf(longs).min().orElseThrow();
    }

    /**
     * Returns the minimum of the given {@code char} values.
     *
     * @throws java.util.NoSuchElementException if no characters are given
     */
    public static char min(char... chars) {
        return streamOf(chars).min(Comparator.naturalOrder()).orElseThrow();
    }

    /**
     * Returns the minimum of the the given comparable values.
     *
     * @throws java.util.NoSuchElementException if no values are given
     */
    public static <T extends Comparable<T>> T min(Collection<T> values) {
        return values.stream().min(Comparator.naturalOrder()).orElseThrow();
    }

    /**
     * Returns the maximum of the given {@code int} values.
     *
     * @throws java.util.NoSuchElementException if no numbers are given
     */
    public static int max(int... ints) {
        return streamOf(ints).max().orElseThrow();
    }

    /**
     * Returns the maximum of the given {@code long} values.
     *
     * @throws java.util.NoSuchElementException if no numbers are given
     */
    public static long max(long... longs) {
        return streamOf(longs).max().orElseThrow();
    }

    /**
     * Returns the maximum of the given {@code char} values.
     *
     * @throws java.util.NoSuchElementException if no characters are given
     */
    public static char max(char... chars) {
        return streamOf(chars).max(Comparator.naturalOrder()).orElseThrow();
    }

    /**
     * Returns the maximum of the the given comparable values.
     *
     * @throws java.util.NoSuchElementException if no values are given
     */
    public static <T extends Comparable<T>> T max(Collection<T> values) {
        return values.stream().max(Comparator.naturalOrder()).orElseThrow();
    }

    /**
     * Returns the absolute value of an {@code int} value.
     * This is just a shorthand for {@link Math#absExact(int)}.
     *
     * @throws ArithmeticException if the argument is {@link Integer#MIN_VALUE}
     */
    public static int abs(int value) {
        return Math.absExact(value);
    }

    /**
     * Returns the absolute value of a {@code long} value.
     * This is just a shorthand for {@link Math#absExact(long)}.
     *
     * @throws ArithmeticException if the argument is {@link Long#MIN_VALUE}
     */
    public static long abs(long value) {
        return Math.absExact(value);
    }

    /**
     * Returns the <i>greatest common divisor</i> (GCD) of the given two non-negative {@code long} values.
     *
     * @throws IllegalArgumentException if {@code a < 0} or {@code b < 0}
     */
    public static long gcd(long a, long b) {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Negative numbers are not supported.");
        }

        return b == 0 ? a : gcd(b, a % b);
    }

    /**
     * Returns the <i>greatest common divisor</i> (GCD) of the given non-negative {@code int} values.
     *
     * @throws IllegalArgumentException if any of the given values is negative
     */
    public static long gcd(int... values) {
        return gcd(IntStream.of(values));
    }

    /**
     * Returns the <i>greatest common divisor</i> (GCD) of the given non-negative {@code int} values.
     *
     * @throws IllegalArgumentException if any of the given values is negative
     */
    public static long gcd(IntStream values) {
        return gcd(values.mapToLong(i -> i));
    }

    /**
     * Returns the <i>greatest common divisor</i> (GCD) of the given non-negative {@code long} values.
     *
     * @throws IllegalArgumentException if any of the given values is negative
     */
    public static long gcd(long... values) {
        return gcd(LongStream.of(values));
    }

    /**
     * Returns the <i>greatest common divisor</i> (GCD) of the given non-negative {@code long} values.
     *
     * @throws IllegalArgumentException if any of the given values is negative
     */
    public static long gcd(LongStream values) {
        return values.reduce(0L, (a, b) -> gcd(a, b));
    }

    /**
     * Returns the <i>greatest common divisor</i> (GCD) of the given non-negative integers.
     *
     * @throws IllegalArgumentException if any of the given values is negative or not of type {@code Integer}
     *         or {@code Long}
     */
    public static long gcd(Collection<? extends Number> values) {
        if (values.stream().map(Object::getClass).anyMatch(c -> c != Integer.class && c != Long.class)) {
            throw new IllegalArgumentException("Only Integer and Long values are supported");
        }

        return gcd(values.stream().mapToLong(Number::longValue));
    }

    /**
     * Returns the <i>least common multiple</i> (LCM) of the given two non-negative {@code long} values.
     */
    public static long lcm(long a, long b) {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Negative numbers are not supported.");
        }

        return a / gcd(a, b) * b; // note: unusual order of operations to avoid overflow
    }

    /**
     * Returns the <i>least common multiple</i> (LCM) of the given non-negative {@code int} values.
     *
     * @throws IllegalArgumentException if any of the given values is negative
     */
    public static long lcm(int... values) {
        return lcm(IntStream.of(values));
    }

    /**
     * Returns the <i>least common multiple</i> (LCM) of the given non-negative {@code int} values.
     *
     * @throws IllegalArgumentException if any of the given values is negative
     */
    public static long lcm(IntStream values) {
        return lcm(values.mapToLong(i -> i));
    }

    /**
     * Returns the <i>least common multiple</i> (LCM) of the given non-negative {@code long} values.
     *
     * @throws IllegalArgumentException if any of the given values is negative
     */
    public static long lcm(long... values) {
        return lcm(LongStream.of(values));
    }

    /**
     * Returns the <i>least common multiple</i> (LCM) of the given non-negative {@code long} values.
     *
     * @throws IllegalArgumentException if any of the given values is negative
     */
    public static long lcm(LongStream values) {
        return values.reduce(1L, (a, b) -> lcm(a, b));
    }

    /**
     * Returns the <i>least common multiple</i> (LCM) of the given non-negative integers.
     *
     * @throws IllegalArgumentException if any of the given values is negative or not of type {@code Integer}
     *         or {@code Long}
     */
    public static long lcm(Collection<? extends Number> values) {
        if (values.stream().map(Object::getClass).anyMatch(c -> c != Integer.class && c != Long.class)) {
            throw new IllegalArgumentException("Only Integer and Long values are supported");
        }

        return lcm(values.stream().mapToLong(Number::longValue));
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
    public static int constrainToRange(long value, int min, int max) {
        return Math.clamp(value, min, max);
    }

    /**
     * Constrains the given long {@code value} to the closed range {@code [min..max]}.
     */
    public static long constrainToRange(long value, long min, long max) {
        return Math.clamp(value, min, max);
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

    // ****************************** COLLECTION AND STREAM UTILS ******************************

    /**
     * Returns the occurrence count of the given value in the given collection.
     */
    public static <T> int count(Collection<T> collection, T value) {
        return (int) collection.stream().filter(v -> Objects.equals(v, value)).count();
    }

    /**
     * Returns the occurrence count of the given character in the given string.
     */
    public static int count(CharSequence s, char ch) {
        return (int) charsOf(s).filter(c -> c == ch).count();
    }

    /**
     * Returns the given {@code int} values as an unmodifiable list.
     */
    public static List<Integer> listOf(int... ints) {
        return IntStream.of(ints).boxed().toList();
    }

    /**
     * Returns the given {@code int} values as an unmodifiable set.
     */
    public static Set<Integer> setOf(int... ints) {
        return IntStream.of(ints).boxed().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the given {@code int} values as an {@link IntStream}.
     */
    public static IntStream streamOf(int... ints) {
        return IntStream.of(ints);
    }

    /**
     * Returns the given {@code long} values as an unmodifiable list.
     */
    public static List<Long> listOf(long... longs) {
        return LongStream.of(longs).boxed().toList();
    }

    /**
     * Returns the given {@code long} values as an unmodifiable set.
     */
    public static Set<Long> setOf(long... longs) {
        return LongStream.of(longs).boxed().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the given {@code long} values as a {@link LongStream}.
     */
    public static LongStream streamOf(long... longs) {
        return LongStream.of(longs);
    }

    /**
     * Returns the given {@code char} values as an unmodifiable list.
     */
    public static List<Character> listOf(char... chars) {
        return streamOf(chars).toList();
    }

    /**
     * Returns the given {@code char} values as an unmodifiable set.
     */
    public static Set<Character> setOf(char... chars) {
        return streamOf(chars).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the given {@code char} values as a stream.
     */
    public static Stream<Character> streamOf(char... chars) {
        return IntStream.range(0, chars.length).mapToObj(i -> chars[i]);
    }

    /**
     * Returns the characters of the given {@code CharSequence} as a stream.
     */
    public static Stream<Character> charsOf(CharSequence s) {
        return s.toString().chars().mapToObj(i -> (char) i);
    }

    /**
     * Returns the union of the given two collections as a set.
     */
    public static <E> Set<E> unionOf(Collection<? extends E> a, Collection<? extends E> b) {
        return unionOf(List.of(a, b));
    }

    /**
     * Returns the union of the given streams as a set.
     */
    public static <E> Set<E> unionOf(Stream<? extends E> a, Stream<? extends E> b) {
        return unionOf(List.of(a.toList(), b.toList()));
    }

    /**
     * Returns the union of the given collections as a set.
     */
    public static <E> Set<E> unionOf(Collection<? extends Collection<? extends E>> collections) {
        var result = new HashSet<E>(collections.iterator().next());
        collections.stream().skip(1).forEach(result::addAll);
        return result;
    }

    /**
     * Returns the intersection of the given two collections as a set.
     */
    public static <E> Set<E> intersectionOf(Collection<? extends E> a, Collection<? extends E> b) {
        return intersectionOf(List.of(a, b));
    }

    /**
     * Returns the intersection of the given streams as a set.
     */
    public static <E> Set<E> intersectionOf(Stream<? extends E> a, Stream<? extends E> b) {
        return intersectionOf(List.of(a.toList(), b.toList()));
    }

    /**
     * Returns the intersection of the given collections as a set.
     */
    public static <E> Set<E> intersectionOf(Collection<? extends Collection<? extends E>> collections) {
        var result = new HashSet<E>(collections.iterator().next());
        collections.stream()
                .skip(1)
                .map(c -> c instanceof Set ? c : new HashSet<>(c))
                .forEach(result::retainAll);
        return result;
    }

    /**
     * Returns an ordered stream of the consecutive {@linkplain List#subList(int, int) sublists} (chunks) of the
     * given size constructed from the given list (the last sublist might be smaller).
     * <p>
     * Example: {@code chunked(List.of(1, 2, 3, 4, 5), 3)} is {@code [[1, 2, 3], [4, 5]]}.
     *
     * @throws IllegalArgumentException if the chunk size is smaller than 1
     */
    public static <E> Stream<List<E>> chunked(List<E> list, int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Chunk size must be at least 1.");
        }

        return IntStream.range(0, (list.size() + size - 1) / size)
                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())));
    }

    /**
     * Returns an ordered stream of all {@linkplain List#subList(int, int) sublists} of the given size constructed
     * from the given list. As if the list was looking at through a sliding window of a certain size.
     * <p>
     * Example: {@code windowed(List.of(1, 2, 3, 4, 5), 3)} is {@code [[1, 2, 3], [2, 3, 4], [3, 4, 5]]}.
     *
     * @throws IllegalArgumentException if the window size is smaller than 1
     */
    public static <E> Stream<List<E>> windowed(List<E> list, int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Window size must be at least 1.");
        }

        return IntStream.rangeClosed(0, list.size() - size).mapToObj(i -> list.subList(i, i + size));
    }

    /**
     * Returns an unmodifiable map that is the inverse of the given map.
     * <p>
     * Note: this method simply constructs a new map each time it is called. If you need a dynamic view of the inverse
     * map, consider using Guava's {@code BiMap}.
     *
     * @throws IllegalArgumentException if the values of the given map are not unique
     */
    public static <K, V> Map<V, K> inverse(Map<K, V> map) {
        var inverse = new HashMap<V, K>();
        for (var e : map.entrySet()) {
            if (inverse.put(e.getValue(), e.getKey()) != null) {
                throw new IllegalArgumentException("The values of the map are not unique.");
            }
        }
        return Collections.unmodifiableMap(inverse);
    }

    // ****************************** ARRAY AND MATRIX UTILS ******************************

    /**
     * Returns a deep copy of the given {@code int} matrix.
     * The "rows" might have different sizes, but they must not be null.
     */
    public static int[][] deepCopy(int[][] matrix) {
        var result = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = matrix[i].clone();
        }
        return result;
    }

    /**
     * Returns a deep copy of the given {@code long} matrix.
     * The "rows" might have different sizes, but they must not be null.
     */
    public static long[][] deepCopy(long[][] matrix) {
        var result = new long[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = matrix[i].clone();
        }
        return result;
    }

    /**
     * Returns a deep copy of the given {@code byte} matrix.
     * The "rows" might have different sizes, but they must not be null.
     */
    public static byte[][] deepCopy(byte[][] matrix) {
        var result = new byte[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = matrix[i].clone();
        }
        return result;
    }

    /**
     * Returns a deep copy of the given {@code char} matrix.
     * The "rows" might have different sizes, but they must not be null.
     */
    public static char[][] deepCopy(char[][] matrix) {
        var result = new char[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = matrix[i].clone();
        }
        return result;
    }

}
