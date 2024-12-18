package com.github.pkovacs.util.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * Represents a position vector in D-dimensional coordinate space with integer precision. It is an immutable array of
 * {@code long} coordinates, which provides various useful methods and also supports lexicographical ordering.
 * <p>
 * This class is the D-dimensional generalization of {@link Pos}.
 *
 * @see Pos
 * @see Box
 */
public final class Vector implements Comparable<Vector> {

    private final long[] coords;

    /**
     * Creates a 2D vector with the given coordinates.
     */
    public Vector(long x, long y) {
        coords = new long[] { x, y };
    }

    /**
     * Creates a 3D vector with the given coordinates.
     */
    public Vector(long x, long y, long z) {
        coords = new long[] { x, y, z };
    }

    /**
     * Creates a D-dimensional vector with the given coordinates.
     *
     * @throws IllegalArgumentException if less than two coordinates are given
     */
    public Vector(long... coords) {
        if (coords.length < 2) {
            throw new IllegalArgumentException("At least two coordinates are required.");
        }
        this.coords = coords.clone();
    }

    /**
     * Returns the origin vector with the given dimension.
     *
     * @throws IllegalArgumentException if the dimension is less than two
     */
    public static Vector origin(int dim) {
        return new Vector(new long[dim]);
    }

    /**
     * Returns the dimension of this vector.
     */
    public int dim() {
        return coords.length;
    }

    /**
     * Returns the x coordinate of this vector. It is the same as {@link #get(int) get(0)}.
     */
    public long x() {
        return coords[0];
    }

    /**
     * Returns the y coordinate of this vector. It is the same as {@link #get(int) get(1)}.
     */
    public long y() {
        return coords[1];
    }

    /**
     * Returns the z coordinate of this vector. It is the same as {@link #get(int) get(2)}.
     *
     * @throws IndexOutOfBoundsException if this is a 2D vector
     */
    public long z() {
        return coords[2];
    }

    /**
     * Returns the k-th coordinate of this vector.
     *
     * @throws IndexOutOfBoundsException if {@code k >= dim()}
     */
    public long get(int k) {
        return coords[k];
    }

    /**
     * Creates a new vector by changing the k-th coordinate of this vector.
     *
     * @throws IndexOutOfBoundsException if {@code k >= dim()}
     */
    public Vector set(int k, long value) {
        long[] newCoords = coords.clone();
        newCoords[k] = value;
        return new Vector(newCoords);
    }

    /**
     * Returns a lexicographically sorted stream of the neighbors of this vector.
     * The stream contains {@code 2 * dim()} vectors, and for each vector {@code v}, {@code v.dist1(this) == 1}.
     */
    public Stream<Vector> neighbors() {
        return neighborsAndSelf().filter(p -> p != this);
    }

    /**
     * Returns a lexicographically sorted stream of this vector and its neighbors.
     * The stream contains {@code 2 * dim() + 1} vectors, and for each vector {@code v}, {@code v.dist1(this) <= 1}.
     */
    public Stream<Vector> neighborsAndSelf() {
        var list = new ArrayList<Vector>();
        for (int k = 0; k < dim(); k++) {
            list.add(set(k, coords[k] - 1));
        }
        list.add(this);
        for (int k = dim() - 1; k >= 0; k--) {
            list.add(set(k, coords[k] + 1));
        }
        return list.stream();
    }

    /**
     * Returns a lexicographically sorted stream of the "extended" neighbors of this vector.
     * The stream contains {@code 3^dim() - 1} vectors, and for each vector {@code v}, {@code v.distMax(this) == 1}.
     */
    public Stream<Vector> extendedNeighbors() {
        return extendedNeighborsAndSelf().filter(p -> p != this);
    }

    /**
     * Returns a lexicographically sorted stream of this vector and its "extended" neighbors.
     * The stream contains {@code 3^dim()} vectors, and for each vector {@code v}, {@code v.distMax(this) <= 1}.
     */
    public Stream<Vector> extendedNeighborsAndSelf() {
        var list = List.of(this);
        for (int i = 0; i < dim(); i++) {
            int k = i;
            list = list.stream()
                    .flatMap(v -> Stream.of(v.set(k, v.get(k) - 1), v, v.set(k, v.get(k) + 1)))
                    .toList();
        }
        return list.stream();
    }

    /**
     * Creates a new vector by adding the given delta values to the coordinates of this vector.
     *
     * @throws IllegalArgumentException if the number of delta values is not equal to the dimension of the
     *         vector
     */
    public Vector plus(long... delta) {
        return plus(new Vector(delta));
    }

    /**
     * Creates a new vector by adding the given other vector to this one.
     *
     * @throws IllegalArgumentException if the vectors have different dimensions
     */
    public Vector plus(Vector other) {
        return newInstance(checkDimensions(this, other), i -> coords[i] + other.coords[i]);
    }

    /**
     * Creates a new vector by subtracting the given other vector from this one.
     *
     * @throws IllegalArgumentException if the vectors have different dimensions
     */
    public Vector minus(Vector other) {
        return newInstance(checkDimensions(this, other), i -> coords[i] - other.coords[i]);
    }

    /**
     * Creates a new vector that is the opposite of this vector.
     */
    public Vector opposite() {
        return newInstance(dim(), i -> -coords[i]);
    }

    /**
     * Creates a new vector by multiplying each coordinate of this vector by the given scalar factor.
     */
    public Vector multiply(long factor) {
        return newInstance(dim(), i -> factor * coords[i]);
    }

    private static int checkDimensions(Vector a, Vector b) {
        if (a.coords.length != b.coords.length) {
            throw new IllegalArgumentException("Vectors have different dimensions.");
        }
        return a.coords.length;
    }

    private static Vector newInstance(int dim, Function<Integer, Long> function) {
        long[] newCoords = new long[dim];
        for (int i = 0; i < newCoords.length; i++) {
            newCoords[i] = function.apply(i);
        }
        return new Vector(newCoords);
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">"taxicab" distance</a>
     * (aka. L1 distance or Manhattan distance) between this vector and the {@link #origin(int) origin}.
     */
    public long dist1() {
        return Arrays.stream(coords).map(Math::abs).sum();
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">"taxicab" distance</a>
     * (aka. L1 distance or Manhattan distance) between this vector and the given other vector.
     *
     * @throws IllegalArgumentException if the vectors have different dimensions
     */
    public long dist1(Vector other) {
        return other.minus(this).dist1();
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Chebyshev_distance">"maximum" distance</a>
     * (aka. L∞ distance or Chebyshev distance) between this vector and the {@link #origin(int) origin}.
     */
    public long distMax() {
        return Arrays.stream(coords).map(Math::abs).max().orElseThrow();
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Chebyshev_distance">"maximum" distance</a>
     * (aka. L∞ distance or Chebyshev distance) between this vector and the given other vector.
     *
     * @throws IllegalArgumentException if the vectors have different dimensions
     */
    public long distMax(Vector other) {
        return other.minus(this).distMax();
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Euclidean_distance#Squared_Euclidean_distance">squared
     * Eucledian distance</a> between this vector and the {@link #origin(int) origin}.
     * <p>
     * Warning: this distance metric does not satisfy the triangle inequality.
     */
    public long distSq() {
        return Arrays.stream(coords).map(i -> i * i).sum();
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Euclidean_distance#Squared_Euclidean_distance">squared
     * Eucledian distance</a> between this vector and the given other vector.
     * <p>
     * Warning: this distance metric does not satisfy the triangle inequality.
     *
     * @throws IllegalArgumentException if the vectors have different dimensions
     */
    public long distSq(Vector other) {
        return other.minus(this).distSq();
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Euclidean_distance">Eucledian distance</a>
     * (aka. L2 distance) between this vector and the {@link #origin(int) origin}.
     */
    public double dist2() {
        return Math.sqrt(distSq());
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Euclidean_distance">Eucledian distance</a>
     * (aka. L2 distance) between this vector and the given other vector.
     *
     * @throws IllegalArgumentException if the vectors have different dimensions
     */
    public double dist2(Vector other) {
        return other.minus(this).dist2();
    }

    @Override
    public String toString() {
        return "(" + Arrays.stream(coords).mapToObj(String::valueOf).collect(joining(", ")) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return Arrays.equals(coords, ((Vector) obj).coords);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(coords);
    }

    @Override
    public int compareTo(Vector other) {
        if (coords.length != other.coords.length) {
            return Integer.compare(coords.length, other.coords.length);
        }
        for (int i = 0; i < coords.length; i++) {
            int c = Long.compare(coords[i], other.coords[i]);
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /**
     * Returns a lexicographically sorted stream of vectors within the closed box defined by the given ranges
     * of coordinates for each dimension. If any range is empty, then an empty stream is returned.
     * <p>
     * Warning: this method eagerly constructs all elements of the stream, so be careful with large boxes.
     */
    public static Stream<Vector> box(Range... ranges) {
        return box(Arrays.asList(ranges));
    }

    /**
     * Returns a lexicographically sorted stream of vectors within the closed box defined by the given ranges
     * of coordinates for each dimension. If any range is empty, then an empty stream is returned.
     * <p>
     * Warning: this method eagerly constructs all elements of the stream, so be careful with large boxes.
     */
    public static Stream<Vector> box(List<Range> ranges) {
        if (ranges.stream().anyMatch(Range::isEmpty)) {
            return Stream.empty();
        }

        int dim = ranges.size();
        var list = List.of(new Vector(ranges.stream().mapToLong(Range::min).toArray()));
        for (int i = 0; i < dim; i++) {
            int k = i;
            var range = ranges.get(k);
            list = list.stream().flatMap(v -> range.stream().mapToObj(c -> v.set(k, c))).toList();
        }
        return list.stream();
    }

    /**
     * Returns a lexicographically sorted stream of vectors within the closed box {@code [min..max]}.
     * If {@code min.get(k) <= max.get(k)} for each {@code 0 <= k < dim()}, then the first element of the stream is
     * {@code min}, and the last element is {@code max}. Otherwise, an empty stream is returned.
     * <p>
     * Warning: this method eagerly constructs all elements of the stream, so be careful with large boxes.
     *
     * @throws IllegalArgumentException if the given vectors have different dimensions
     */
    public static Stream<Vector> box(Vector min, Vector max) {
        int dim = checkDimensions(min, max);
        if (IntStream.range(0, dim).anyMatch(k -> min.get(k) > max.get(k))) {
            return Stream.empty();
        }

        var list = List.of(min);
        for (int i = 0; i < dim; i++) {
            int k = i;
            list = list.stream()
                    .flatMap(v -> LongStream.rangeClosed(min.get(k), max.get(k)).mapToObj(c -> v.set(k, c)))
                    .toList();
        }
        return list.stream();
    }

}
