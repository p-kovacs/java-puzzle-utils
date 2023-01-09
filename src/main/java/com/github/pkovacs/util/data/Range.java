package com.github.pkovacs.util.data;

import java.util.stream.LongStream;

/**
 * An immutable closed range of {@code long} integers {@code [min..max]}.
 * <p>
 * If you need a more general tool, consider using Guava's {@code Range} or {@code RangeSet}.
 *
 * @see Box
 */
public record Range(long min, long max) {

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
