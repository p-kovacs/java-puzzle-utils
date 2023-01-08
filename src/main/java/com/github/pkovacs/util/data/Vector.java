package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

/**
 * Represents a position vector in D-dimensional coordinate space with integer precision. It is an immutable array of
 * {@code long} coordinates, which provides various useful methods and also supports lexicographical ordering.
 */
public class Vector implements Comparable<Vector> {

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
     */
    public Vector(long... coords) {
        if (coords.length <= 1) {
            throw new IllegalArgumentException("At least two coordinates are required.");
        }
        this.coords = coords.clone();
    }

    /**
     * Returns the origin vector with the given dimension.
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
     * @throws IndexOutOfBoundsException if the dimension of this vector is less than three
     */
    public long z() {
        return coords[2];
    }

    /**
     * Returns the k-th coordinate of this vector.
     *
     * @throws IndexOutOfBoundsException if the dimension of this vector is less than or equal to {@code k}
     */
    public long get(int k) {
        return coords[k];
    }

    /**
     * Creates a new vector by adding the given vector to this one.
     */
    public Vector add(Vector v) {
        return newInstance(checkDimensions(this, v), i -> coords[i] + v.coords[i]);
    }

    /**
     * Creates a new vector by subtracting the given vector from this one.
     */
    public Vector subtract(Vector v) {
        return newInstance(checkDimensions(this, v), i -> coords[i] - v.coords[i]);
    }

    /**
     * Creates a new vector that is the opposite of this vector.
     */
    public Vector opposite() {
        return newInstance(dim(), i -> -coords[i]);
    }

    /**
     * Creates a new vector by multiplying this vector by the given scalar factor.
     */
    public Vector multiply(long factor) {
        return newInstance(dim(), i -> factor * coords[i]);
    }

    private static int checkDimensions(Vector a, Vector b) {
        if (a.coords.length != b.coords.length) {
            throw new IllegalArgumentException("Vector dimensions mismatch.");
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
     * Returns the <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">Manhattan distance</a>
     * (aka. L1 distance or "taxicab" distance) between this vector and the {@link #origin(int) origin}.
     */
    public long dist1() {
        return Arrays.stream(coords).map(Math::abs).sum();
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">Manhattan distance</a>
     * (aka. L1 distance or "taxicab" distance) between this vector and the given vector.
     *
     * @throws IllegalArgumentException if the vectors have different dimensions
     */
    public long dist1(Vector v) {
        return subtract(v).dist1();
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
     * (aka. L∞ distance or Chebyshev distance) between this vector and the given vector.
     *
     * @throws IllegalArgumentException if the vectors have different dimensions
     */
    public long distMax(Vector v) {
        return subtract(v).distMax();
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Euclidean_distance#Squared_Euclidean_distance">squared
     * Eucledian distance</a> between this vector and the {@link #origin(int) origin}.
     * <p>
     * Warning: this distance does not satisfy the triangle inequality.
     */
    public long distSq() {
        return Arrays.stream(coords).map(i -> i * i).sum();
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Euclidean_distance#Squared_Euclidean_distance">squared
     * Eucledian distance</a> between this vector and the given vector.
     * <p>
     * Warning: this distance does not satisfy the triangle inequality.
     *
     * @throws IllegalArgumentException if the vectors have different dimensions
     */
    public long distSq(Vector v) {
        return subtract(v).distSq();
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
     * (aka. L2 distance) between this vector and the given vector.
     *
     * @throws IllegalArgumentException if the vectors have different dimensions
     */
    public double dist2(Vector v) {
        return subtract(v).dist2();
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
    public String toString() {
        return "(" + Arrays.stream(coords).mapToObj(String::valueOf).collect(joining(", ")) + ")";
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

}
