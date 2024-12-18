package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * An immutable box (hyperrectangle) of D-dimensional {@link VectorD} objects (rectangle in 2D, right prism in 3D).
 * Its bounds are represented by two vectors {@code min} and {@code max}, and a vector {@code v} is contained in
 * the box if and only if for each dimension {@code k}, {@code min.get(k) <= v.get(k) <= max.get(k)}.
 * <p>
 * This class is the D-dimensional generalization of {@link Range}.
 *
 * @see VectorD
 * @see Range
 */
public record VectorBox(VectorD min, VectorD max) {

    /**
     * Constructs a new vector box identified by the given two vectors {@code min} and {@code max}. It contains each
     * vector {@code v} for which {@code min.get(k) <= v.get(k) <= max.get(k)} holds for every coordinate {@code k}.
     *
     * @throws IllegalArgumentException if the given vectors have different dimensions
     */
    public VectorBox {
        if (min.dim() != max.dim()) {
            throw new IllegalArgumentException("The vectors have different dimensions.");
        }
    }

    /**
     * Constructs the axis-aligned <a href="https://en.wikipedia.org/wiki/Minimum_bounding_box">minimum bounding box</a>
     * of the given vectors.
     *
     * @throws IllegalArgumentException if the given vectors have different dimensions
     * @throws NoSuchElementException if the collection is empty
     */
    public static VectorBox bound(Collection<VectorD> vectors) {
        return new VectorBox(bound(vectors, Math::min), bound(vectors, Math::max));
    }

    /**
     * Returns the dimension of this vector box.
     */
    public int dim() {
        return min.dim();
    }

    /**
     * Returns true if this vector box is empty.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns true if this vector box is non-empty.
     */
    public boolean isNonEmpty() {
        return size() > 0;
    }

    /**
     * Returns the number of vectors within this vector box.
     */
    public long size() {
        return IntStream.range(0, dim())
                .mapToLong(k -> Math.max(max.get(k) - min.get(k) + 1, 0))
                .reduce(1, (x, y) -> x * y);
    }

    /**
     * Returns true if this vector box contains the given vector.
     */
    public boolean contains(VectorD v) {
        if (v.dim() != dim()) {
            throw new IllegalArgumentException("The box and the vector have different dimensions.");
        }
        return IntStream.range(0, dim()).allMatch(k -> v.get(k) >= min.get(k) && v.get(k) <= max.get(k));
    }

    /**
     * Returns true if this vector box contains the given other box.
     */
    public boolean containsAll(VectorBox other) {
        return intersection(other).equals(other);
    }

    /**
     * Returns true if this vector box contains all elements of the given collection of vectors.
     */
    public boolean containsAll(Collection<VectorD> vectors) {
        return vectors.stream().allMatch(this::contains);
    }

    /**
     * Returns true if this vector box overlaps with the given other box.
     */
    public boolean overlaps(VectorBox other) {
        return !intersection(other).isEmpty();
    }

    /**
     * Returns the intersection of this vector box and the given other box.
     */
    public VectorBox intersection(VectorBox other) {
        if (other.dim() != dim()) {
            throw new IllegalArgumentException("The boxes have different dimensions.");
        }

        long[] lower = IntStream.range(0, dim()).mapToLong(k -> Math.max(min.get(k), other.min.get(k))).toArray();
        long[] upper = IntStream.range(0, dim()).mapToLong(k -> Math.min(max.get(k), other.max.get(k))).toArray();
        return new VectorBox(new VectorD(lower), new VectorD(upper));
    }

    /**
     * Returns an ordered stream of the vectors within this vector box. If the box is not empty, then the first
     * element is {@code min}, the last element is {@code max}, and the stream is lexicographically sorted.
     * <p>
     * Warning: this method eagerly constructs all elements of the stream, so be careful with large boxes.
     */
    public Stream<VectorD> stream() {
        if (IntStream.range(0, min.dim()).anyMatch(k -> min.get(k) > max.get(k))) {
            return Stream.empty();
        }

        var list = List.of(min);
        for (int i = 0; i < min.dim(); i++) {
            int k = i;
            list = list.stream()
                    .flatMap(v -> LongStream.rangeClosed(min.get(k), max.get(k)).mapToObj(c -> v.with(k, c)))
                    .toList();
        }
        return list.stream();
    }

    @Override
    public String toString() {
        return "[" + min + " .. " + max + "]";
    }

    private static VectorD bound(Collection<VectorD> vectors, BinaryOperator<Long> op) {
        var first = vectors.iterator().next();
        if (vectors.stream().anyMatch(v -> v.dim() != first.dim())) {
            throw new IllegalArgumentException("The vectors have different dimensions.");
        }

        long[] coords = IntStream.range(0, first.dim()).mapToLong(first::get).toArray();
        for (var v : vectors) {
            for (int k = 0; k < first.dim(); k++) {
                coords[k] = op.apply(coords[k], v.get(k));
            }
        }
        return new VectorD(coords);
    }

}
