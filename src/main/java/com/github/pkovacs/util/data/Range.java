package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.github.pkovacs.util.Utils;

/**
 * An immutable closed range of {@code long} integers {@code [min..max]}.
 * <p>
 * If you need a more general tool, consider using Guava's {@code Range} or {@code RangeSet}.
 */
public record Range(long min, long max) {

    /**
     * Constructs a new closed range {@code [min..max]}.
     */
    public Range {
    }

    /**
     * Constructs the bounding range of the given {@code int} values.
     *
     * @throws java.util.NoSuchElementException if the array is empty
     */
    public static Range bound(int... ints) {
        return new Range(Utils.min(ints), Utils.max(ints));
    }

    /**
     * Constructs the bounding range of the given {@code int} values.
     *
     * @throws java.util.NoSuchElementException if the stream is empty
     */
    public static Range bound(IntStream ints) {
        return bound(ints.toArray());
    }

    /**
     * Constructs the bounding range of the given {@code long} values.
     *
     * @throws java.util.NoSuchElementException if the array is empty
     */
    public static Range bound(long... longs) {
        return new Range(Utils.min(longs), Utils.max(longs));
    }

    /**
     * Constructs the bounding range of the given {@code long} values.
     *
     * @throws java.util.NoSuchElementException if the stream is empty
     */
    public static Range bound(LongStream longs) {
        return bound(longs.toArray());
    }

    /**
     * Constructs the bounding range of the given integers.
     *
     * @throws IllegalArgumentException if any of the given numbers is not of type {@code Integer} or
     *         {@code Long}
     * @throws java.util.NoSuchElementException if the collection is empty
     */
    public static Range bound(Collection<? extends Number> numbers) {
        if (numbers.stream().map(Object::getClass).anyMatch(c -> c != Integer.class && c != Long.class)) {
            throw new IllegalArgumentException("Only Integer and Long values are supported");
        }

        return Range.bound(numbers.stream().mapToLong(Number::longValue));
    }

    /**
     * Returns true if this range is empty, that is, {@code min > max}.
     */
    public boolean isEmpty() {
        return min > max;
    }

    /**
     * Returns true if this range is non-empty, that is, {@code min <= max}.
     */
    public boolean isNonEmpty() {
        return min <= max;
    }

    /**
     * Returns the number of integers within this range.
     */
    public long size() {
        return Math.max(max - min + 1, 0);
    }

    /**
     * Returns true if this range contains the given {@code int} value.
     */
    public boolean contains(int i) {
        return i >= min && i <= max;
    }

    /**
     * Returns true if this range contains the given {@code long} value.
     */
    public boolean contains(long i) {
        return i >= min && i <= max;
    }

    /**
     * Returns true if this range contains all elements of the given other range.
     */
    public boolean containsAll(Range other) {
        return other.min >= min && other.max <= max;
    }

    /**
     * Returns true if this range contains all given {@code int} values.
     */
    public boolean containsAll(int... ints) {
        return Arrays.stream(ints).allMatch(this::contains);
    }

    /**
     * Returns true if this range contains all given {@code long} values.
     */
    public boolean containsAll(long... longs) {
        return Arrays.stream(longs).allMatch(this::contains);
    }

    /**
     * Returns true if this range contains all elements of the given collection.
     *
     * @throws IllegalArgumentException if any of the given numbers is not of type {@code Integer} or
     *         {@code Long}
     * @throws java.util.NoSuchElementException if the collection is empty
     */
    public boolean containsAll(Collection<? extends Number> numbers) {
        if (numbers.stream().map(Object::getClass).anyMatch(c -> c != Integer.class && c != Long.class)) {
            throw new IllegalArgumentException("Only Integer and Long values are supported");
        }

        return numbers.stream().map(Number::longValue).allMatch(this::contains);
    }

    /**
     * Returns true if this range overlaps with the given other range (they have a non-empty intersection).
     */
    public boolean overlaps(Range other) {
        return intersection(other).isNonEmpty();
    }

    /**
     * Returns the intersection of this range and the given other range.
     * <p>
     * For example, the intersection of {@code [1..6]} and {@code [4..9]} is {@code [4..6]}.
     */
    public Range intersection(Range other) {
        return new Range(Math.max(min, other.min), Math.min(max, other.max));
    }

    /**
     * Returns the minimal range that contains all elements of both this range and the given other range.
     * <p>
     * For example, the span of {@code [1..3]} and {@code [6..9]} is {@code [1..9]}.
     */
    public Range span(Range other) {
        return new Range(Math.min(min, other.min), Math.max(max, other.max));
    }

    /**
     * Returns the maximal range lying between this range and the given non-overlapping other range. The resulting
     * range may be empty if the two ranges are adjacent but non-overlapping.
     * <p>
     * For example, the gap between {@code [1..3]} and {@code [7..10]} is {@code [4..6]}; the gap between
     * {@code [1..3]} and {@code [4..8]} is the empty range {@code [4..3]}; and an exception is thrown when
     * the two ranges are overlapping like {@code [1..5]} and {@code [5..8]}.
     *
     * @throws IllegalArgumentException if this range overlaps with the given other range
     */
    public Range gap(Range other) {
        if (overlaps(other)) {
            throw new IllegalArgumentException("Overlapping ranges: " + this + " and " + other + ".");
        }

        return new Range(Math.min(max, other.max) + 1, Math.max(min, other.min) - 1);
    }

    /**
     * Returns a new range by shifting this range with the given signed delta value.
     * <p>
     * For example, shifting {@code [5..9]} with 2 results in {@code [7..11]}, while shifting with -3 results in
     * {@code [2..6]}.
     */
    public Range shift(long delta) {
        return new Range(min + delta, max + delta);
    }

    /**
     * Returns a new range by extending this range with the given delta value in both directions. Negative parameter
     * means shrinking.
     * <p>
     * For example, extending {@code [5..9]} with 2 results in {@code [3..11]}, while extending with -1 results in
     * {@code [6..8]}.
     */
    public Range extend(long delta) {
        return new Range(min - delta, max + delta);
    }

    /**
     * Returns a sorted stream of the {@code long} values within this range.
     */
    public LongStream stream() {
        return LongStream.rangeClosed(min, max);
    }

    /**
     * Returns a sorted list of the {@code long} values within this range.
     */
    public List<Long> toList() {
        return stream().boxed().toList();
    }

    /**
     * Returns a sorted array of the {@code long} values within this range.
     */
    public long[] toArray() {
        return stream().toArray();
    }

    @Override
    public String toString() {
        return "[" + min + ".." + max + "]";
    }

}
