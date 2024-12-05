package com.github.pkovacs.util.data;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.github.pkovacs.util.Utils;

/**
 * An immutable closed range of {@code long} integers {@code [min..max]}.
 * <p>
 * If you need a more general tool, consider using Guava's {@code Range} or {@code RangeSet}.
 *
 * @see Box
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
     * Constructs the bounding range of the {@code long} values of the given numbers.
     *
     * @throws java.util.NoSuchElementException if the collection is empty
     */
    public static Range bound(Collection<? extends Number> numbers) {
        return Range.bound(numbers.stream().mapToLong(Number::longValue));
    }

    /**
     * Returns true if this range is empty, that is, {@code max < min}.
     */
    public boolean isEmpty() {
        return max < min;
    }

    /**
     * Returns the number of integers within this range.
     */
    public long count() {
        return Math.max(max - min + 1, 0);
    }

    /**
     * Returns true if this range contains the given integer.
     */
    public boolean contains(long i) {
        return i >= min && i <= max;
    }

    /**
     * Returns true if this range contains the given range.
     */
    public boolean containsAll(Range other) {
        return other.min >= min && other.max <= max;
    }

    /**
     * Returns true if this range overlaps with the given range.
     */
    public boolean overlaps(Range other) {
        return !intersection(other).isEmpty();
    }

    /**
     * Returns the intersection of this range and the given range.
     */
    public Range intersection(Range other) {
        return new Range(Math.max(min, other.min), Math.min(max, other.max));
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

    @Override
    public String toString() {
        return "[" + min + ".." + max + "]";
    }

}
