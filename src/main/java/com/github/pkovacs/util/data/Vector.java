package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

/**
 * Represents a position vector in D-dimensional coordinate space with integer precision. It is an immutable array of
 * {@code long} coordinates. It provides methods for various vector operations and for obtaining the Manhattan
 * distance between two vectors. Lexicographical ordering is also supported.
 * <p>
 * Some features are specific to 2D vectors, e.g., the four directions and rotation. The coordinates are interpreted
 * as usual in Math: (0, 0) is the {@link #ORIGIN}, (0, 1) means {@link #NORTH}, (0, -1) means {@link #SOUTH},
 * (1, 0) means {@link #EAST}, (-1, 0) means {@link #WEST}.
 */
public class Vector implements Comparable<Vector> {

    /** The 2D origin vector: (0, 0). For other dimensions, use {@link #origin(int)}. */
    public static final Vector ORIGIN = new Vector(0, 0);

    /** The 2D unit vector with "north" direction: (0, 1). */
    public static final Vector NORTH = new Vector(0, 1);

    /** The 2D unit vector with "south" direction: (0, -1). */
    public static final Vector SOUTH = new Vector(0, -1);

    /** The 2D unit vector with "east" direction: (1, 0). */
    public static final Vector EAST = new Vector(1, 0);

    /** The 2D unit vector with "west" direction: (-1, 0). */
    public static final Vector WEST = new Vector(-1, 0);

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
     * Creates a vector with the given coordinates.
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
     * Returns the 2D unit vector corresponding to the given direction.
     */
    public static Vector fromDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> Vector.NORTH;
            case EAST -> Vector.EAST;
            case SOUTH -> Vector.SOUTH;
            case WEST -> Vector.WEST;
        };
    }

    /**
     * Returns the 2D unit vector corresponding to the given direction character.
     *
     * @param dir the direction character. One of 'N' (north), 'E' (east), 'S' (south), 'W' (west),
     *         'U' (up), 'R' (right), 'D' (down), 'L' (left), and their lowercase variants.
     */
    public static Vector fromChar(char dir) {
        return fromDirection(Direction.fromChar(dir));
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
     * Creates a new vector by mirroring this 2D vector horizontally.
     */
    public Vector mirrorHorizontally() {
        if (dim() != 2) {
            throw new UnsupportedOperationException("Supported only for 2D vectors.");
        }
        return new Vector(-x(), y());
    }

    /**
     * Creates a new vector by mirroring this 2D vector vertically.
     */
    public Vector mirrorVertically() {
        if (dim() != 2) {
            throw new UnsupportedOperationException("Supported only for 2D vectors.");
        }
        return new Vector(x(), -y());
    }

    /**
     * Creates a new vector by rotating this 2D vector 90 degrees to the right.
     *
     * @throws UnsupportedOperationException if the dimension of this vector is larger than two
     */
    public Vector rotateRight() {
        if (dim() != 2) {
            throw new UnsupportedOperationException("Supported only for 2D vectors.");
        }
        return new Vector(y(), -x());
    }

    /**
     * Creates a new vector by rotating this 2D vector 90 degrees to the left.
     *
     * @throws UnsupportedOperationException if the dimension of this vector is larger than two
     */
    public Vector rotateLeft() {
        if (dim() != 2) {
            throw new UnsupportedOperationException("Supported only for 2D vectors.");
        }
        return new Vector(-y(), x());
    }

    /**
     * Returns the Manhattan distance (aka. "taxicab" distance) between this vector and the
     * {@link #origin(int) origin}.
     */
    public long dist() {
        return Arrays.stream(coords).map(Math::abs).sum();
    }

    /**
     * Returns the Manhattan distance (aka. "taxicab" distance) between this vector and the given vector.
     */
    public long dist(Vector v) {
        return subtract(v).dist();
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
