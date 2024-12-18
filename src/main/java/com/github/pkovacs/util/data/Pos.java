package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Represents a position (point or position vector) in 2D coordinate space as an immutable pair of {@code long}
 * values: {@code (x,y)}. Provides various useful methods and also supports lexicographical ordering (first by
 * {@code x}, then by {@code y}).
 *
 * @apiNote This class is not a record in order to provide easier access to {@link #x} and {@link #y} as public
 *         final fields.
 * @see Vector
 */
public final class Pos implements Comparable<Pos> {

    /** The origin: {@code (0,0)}. */
    public static final Pos ORIGIN = new Pos(0, 0);

    /**
     * The x coordinate (or column index).
     * This field is deliberately made public to similify the usage of this class.
     */
    public final long x;

    /**
     * The y coordinate (or row index).
     * This field is deliberately made public to similify the usage of this class.
     */
    public final long y;

    /**
     * Constructs a new position.
     */
    public Pos(long x, long y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate (or column index).
     *
     * @apiNote You can also use the public final field {@link #x} directly, but this method is practical when
     *         used as a method reference: {@code Pos::x}.
     */
    public long x() {
        return x;
    }

    /**
     * Returns the y coordinate (or row index).
     *
     * @apiNote You can also use the public final field {@link #y} directly, but this method is practical when
     *         used as a method reference: {@code Pos::y}.
     */
    public long y() {
        return y;
    }

    /**
     * Returns true if the given other position is one of the 4 "regular" neighbors of this position.
     */
    public boolean isNeighbor(Pos other) {
        return dist1(other) == 1;
    }

    /**
     * Returns true if the given other position is one of the 8 "extended" neighbors of this position, also including
     * the diagonal ones.
     */
    public boolean isNeighbor8(Pos other) {
        return distMax(other) == 1;
    }

    /**
     * Returns the neighbor of this position in the given direction, assuming that axis y is directed <i>downward</i>
     * (to the south). That is, {@code (0,0)} represents the <i>top</i> left position among the ones with
     * non-negative coordinates.
     * <p>
     * If axis y is directed <i>upward</i> (to the north), then you can use {@link Dir#mirrorVertically()}.
     */
    public Pos neighbor(Dir dir) {
        return switch (dir) {
            case N -> new Pos(x, y - 1);
            case E -> new Pos(x + 1, y);
            case S -> new Pos(x, y + 1);
            case W -> new Pos(x - 1, y);
        };
    }

    /**
     * Returns the "extended" neighbor of this position in the given direction (out of 8 directions), assuming
     * that axis y is directed <i>downward</i> (to the south). That is, {@code (0,0)} represents the <i>top</i>
     * left position among the ones with non-negative coordinates.
     * <p>
     * If axis y is directed <i>upward</i> (to the north), then you can use {@link Dir8#mirrorVertically()}.
     */
    public Pos neighbor8(Dir8 dir) {
        return switch (dir) {
            case N -> new Pos(x, y - 1);
            case NE -> new Pos(x + 1, y - 1);
            case E -> new Pos(x + 1, y);
            case SE -> new Pos(x + 1, y + 1);
            case S -> new Pos(x, y + 1);
            case SW -> new Pos(x - 1, y + 1);
            case W -> new Pos(x - 1, y);
            case NW -> new Pos(x - 1, y - 1);
        };
    }

    /**
     * Returns the neighbor of this position in the given direction, assuming that axis y is directed <i>downward</i>
     * (to the south). That is, {@code (0,0)} represents the <i>top</i> left position among the ones with
     * non-negative coordinates.
     *
     * @param dir the direction character. One of 'N' (north), 'E' (east), 'S' (south), 'W' (west),
     *         'U' (up), 'R' (right), 'D' (down), 'L' (left), and their lowercase variants.
     */
    public Pos neighbor(char dir) {
        return neighbor(Dir.fromChar(dir));
    }

    /**
     * Returns a lexicographically sorted stream of the 4 "regular" neighbors of this position.
     */
    public Stream<Pos> neighbors() {
        return Stream.of(
                new Pos(x - 1, y),
                new Pos(x, y - 1),
                new Pos(x, y + 1),
                new Pos(x + 1, y));
    }

    /**
     * Returns a lexicographically sorted stream of this position and its 4 "regular" neighbors.
     */
    public Stream<Pos> neighborsAndSelf() {
        return Stream.of(
                new Pos(x - 1, y),
                new Pos(x, y - 1),
                this,
                new Pos(x, y + 1),
                new Pos(x + 1, y));
    }

    /**
     * Returns a lexicographically sorted stream of the 8 "extended" neighbors of this position (also including
     * the diagonal ones).
     */
    public Stream<Pos> neighbors8() {
        return Stream.of(
                new Pos(x - 1, y - 1),
                new Pos(x - 1, y),
                new Pos(x - 1, y + 1),
                new Pos(x, y - 1),
                new Pos(x, y + 1),
                new Pos(x + 1, y - 1),
                new Pos(x + 1, y),
                new Pos(x + 1, y + 1));
    }

    /**
     * Returns a lexicographically sorted stream of this position and its 8 "extended" neighbors (also including
     * the diagonal ones).
     */
    public Stream<Pos> neighbors8AndSelf() {
        return Stream.of(
                new Pos(x - 1, y - 1),
                new Pos(x - 1, y),
                new Pos(x - 1, y + 1),
                new Pos(x, y - 1),
                this,
                new Pos(x, y + 1),
                new Pos(x + 1, y - 1),
                new Pos(x + 1, y),
                new Pos(x + 1, y + 1));
    }

    /**
     * Returns the direction to the given other position, assuming that axis y is directed <i>downward</i>
     * (to the south). That is, {@code (0,0)} represents the <i>top</i> left position among the ones with
     * non-negative coordinates.
     * <p>
     * If axis y is directed <i>upward</i> (to the north), then you can use {@link Dir#mirrorVertically()}.
     *
     * @throws IllegalArgumentException if this position and the given position are equal or do not lay on a
     *         common horizontal or vertical line.
     */
    public Dir dirTo(Pos other) {
        Pos delta = other.minus(this);
        if (delta.x == 0 && delta.y == 0) {
            throw new IllegalArgumentException("The positions are equal.");
        } else if (delta.x == 0) {
            return delta.y < 0 ? Dir.N : Dir.S;
        } else if (delta.y == 0) {
            return delta.x < 0 ? Dir.W : Dir.E;
        } else {
            throw new IllegalArgumentException(
                    "The positions do not lay on a common horizontal or vertical line.");
        }
    }

    /**
     * Returns the direction to the given other position, assuming that axis y is directed <i>downward</i>
     * (to the south). That is, {@code (0,0)} represents the <i>top</i> left position among the ones with
     * non-negative coordinates.
     * <p>
     * If axis y is directed <i>upward</i> (to the north), then you can use {@link Dir8#mirrorVertically()}.
     *
     * @throws IllegalArgumentException if this position and the given position are equal or do not lay on a
     *         common horizontal, vertical, or diagonal line.
     */
    public Dir8 dir8To(Pos other) {
        Pos delta = other.minus(this);
        if (delta.x == 0 && delta.y == 0) {
            throw new IllegalArgumentException("The positions are equal.");
        } else if (delta.x == 0) {
            return delta.y < 0 ? Dir8.N : Dir8.S;
        } else if (delta.y == 0) {
            return delta.x < 0 ? Dir8.W : Dir8.E;
        } else if (delta.x == delta.y) {
            return delta.x < 0 ? Dir8.NW : Dir8.SE;
        } else if (delta.x == -delta.y) {
            return delta.x < 0 ? Dir8.SW : Dir8.NE;
        } else {
            throw new IllegalArgumentException(
                    "The positions do not lay on a common horizontal, vertical, or diagonal line.");
        }
    }

    /**
     * Returns an ordered stream of positions that constitutes a straight line segment from this position to the
     * given other position (horizontally, vertically, or diagonally). The first element of the stream is this
     * position, and the last element is the given other position (provided that they lay on a common line).
     *
     * @throws IllegalArgumentException if this position and the given position do not lay on a common
     *         horizontal, vertical, or diagonal line.
     */
    public Stream<Pos> lineTo(Pos other) {
        Pos delta = other.minus(this);
        if (delta.x == 0 && delta.y == 0) {
            return Stream.of(this);
        } else if (delta.x == 0 || delta.y == 0 || Math.abs(delta.x) == Math.abs(delta.y)) {
            long dist = Math.max(Math.abs(delta.x), Math.abs(delta.y));
            long dx = delta.x / dist;
            long dy = delta.y / dist;
            return LongStream.rangeClosed(0, dist).mapToObj(i -> new Pos(x + i * dx, y + i * dy));
        } else {
            throw new IllegalArgumentException(
                    "The positions do not lay on a common horizontal, vertical, or diagonal line.");
        }
    }

    /**
     * Returns an <i>infinite</i> ordered stream of positions that constitutes a "ray" moving away from this position
     * in the given direction, assuming that axis y is directed <i>downward</i> (to the south). The first element of
     * the stream is the corresponding neighbor of this position, the next element is the subsequent position in
     * the same direction (applying the same changes to the x and y coordinates), and so on.
     * <p>
     * If axis y is directed <i>upward</i> (to the north), then you can use {@link Dir#mirrorVertically()}.
     */
    public Stream<Pos> ray(Dir dir) {
        return ray(neighbor(dir));
    }

    /**
     * Returns an <i>infinite</i> ordered stream of positions that constitutes a "ray" moving away from this position
     * in the given direction, assuming that axis y is directed <i>downward</i> (to the south). The first element of
     * the stream is the corresponding neighbor of this position, the next element is the subsequent position in
     * the same direction (applying the same changes to the x and y coordinates), and so on.
     * <p>
     * If axis y is directed <i>upward</i> (to the north), then you can use {@link Dir8#mirrorVertically()}.
     */
    public Stream<Pos> ray(Dir8 dir) {
        return ray(neighbor8(dir));
    }

    /**
     * Returns an <i>infinite</i> ordered stream of positions that constitutes a "ray" moving away from this position
     * in the direction specified by the given other position. The first element of the stream is the given position,
     * the next element is the subsequent position in the same direction (applying the same changes to the x and y
     * coordinates), and so on.
     * <p>
     * This method can be combined with {@link #neighbors()} or {@link #neighbors8()} to obtain 4 or 8 rays
     * moving away from this position, respectively (i.e., the movement of a <i>rook</i> or <i>queen</i> in chess,
     * respectively).
     */
    public Stream<Pos> ray(Pos other) {
        var delta = other.minus(this);
        return Stream.iterate(other, t -> t.plus(delta));
    }

    /**
     * Creates a new position by adding the given delta values to the coordinates of this position vector.
     */
    public Pos plus(int dx, int dy) {
        return new Pos(x + dx, y + dy);
    }

    /**
     * Creates a new position by adding the given other position vector to this one.
     */
    public Pos plus(Pos other) {
        return new Pos(x + other.x, y + other.y);
    }

    /**
     * Creates a new position by subtracting the given other position vector from this one.
     */
    public Pos minus(Pos other) {
        return new Pos(x - other.x, y - other.y);
    }

    /**
     * Creates a new position that is the opposite of this position vector.
     */
    public Pos opposite() {
        return new Pos(-x, -y);
    }

    /**
     * Creates a new position by rotating this position vector 90 degrees to the left around the {@link #ORIGIN}.
     */
    public Pos rotateLeft() {
        return new Pos(-y, x);
    }

    /**
     * Creates a new position by rotating this position vector 90 degrees to the right around the {@link #ORIGIN}.
     */
    public Pos rotateRight() {
        return new Pos(y, -x);
    }

    /**
     * Creates a new position by mirroring this position vector horizontally.
     */
    public Pos mirrorHorizontally() {
        return new Pos(-x, y);
    }

    /**
     * Creates a new position by mirroring this position vector vertically.
     */
    public Pos mirrorVertically() {
        return new Pos(x, -y);
    }

    /**
     * Creates a new position by mirroring this position vector with respect to the given refelection center.
     */
    public Pos mirrorAcross(Pos center) {
        return center.plus(center.minus(this));
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">"taxicab" distance</a>
     * (aka. L1 distance or Manhattan distance) between this position and the {@link #ORIGIN} {@code (0,0)}.
     */
    public long dist1() {
        return dist1(ORIGIN);
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Taxicab_geometry">"taxicab" distance</a>
     * (aka. L1 distance or Manhattan distance) between this position and the given other position.
     */
    public long dist1(Pos other) {
        return Math.abs(other.x - x) + Math.abs(other.y - y);
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Chebyshev_distance">"maximum" distance</a>
     * (aka. L∞ distance or Chebyshev distance) between this position and the {@link #ORIGIN} {@code (0,0)}.
     */
    public long distMax() {
        return distMax(ORIGIN);
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Chebyshev_distance">"maximum" distance</a>
     * (aka. L∞ distance or Chebyshev distance) between this position and the given other position.
     */
    public long distMax(Pos other) {
        return Math.max(Math.abs(other.x - x), Math.abs(other.y - y));
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Euclidean_distance#Squared_Euclidean_distance">squared
     * Eucledian distance</a> between this position and the {@link #ORIGIN} {@code (0,0)}.
     * <p>
     * Warning: this distance metric does not satisfy the triangle inequality.
     */
    public long distSq() {
        return distSq(ORIGIN);
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Euclidean_distance#Squared_Euclidean_distance">squared
     * Eucledian distance</a> between this position and the given other position.
     * <p>
     * Warning: this distance metric does not satisfy the triangle inequality.
     */
    public long distSq(Pos other) {
        long dx = other.x - x;
        long dy = other.y - y;
        return dx * dx + dy * dy;
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Euclidean_distance">Eucledian distance</a>
     * (aka. L2 distance) between this position and the {@link #ORIGIN} {@code (0,0)}.
     */
    public double dist2() {
        return dist2(ORIGIN);
    }

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Euclidean_distance">Eucledian distance</a>
     * (aka. L2 distance) between this position and the given other position.
     */
    public double dist2(Pos other) {
        return Math.sqrt(distSq(other));
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Pos p && p.x == x && p.y == y;
    }

    @Override
    public int hashCode() {
        // Optimized for small integer values (the largest prime below the square root of 2^32 is used)
        return (int) (x * 65_521 + y);
    }

    @Override
    public int compareTo(Pos other) {
        return x != other.x ? Long.compare(x, other.x) : Long.compare(y, other.y);
    }

    /**
     * Returns the bounding {@link Range} of the x coordinates of the given positions.
     *
     * @throws java.util.NoSuchElementException if the collection is empty
     */
    public static Range xRange(Collection<Pos> positions) {
        return Range.bound(positions.stream().mapToLong(Pos::x));
    }

    /**
     * Returns the bounding {@link Range} of the y coordinates of the given positions.
     *
     * @throws java.util.NoSuchElementException if the collection is empty
     */
    public static Range yRange(Collection<Pos> positions) {
        return Range.bound(positions.stream().mapToLong(Pos::y));
    }

    /**
     * Returns a lexicographically sorted stream of positions within a box of the given width and height.
     * If both arguments are positive, then the first element of the stream is {@code (0,0)}, and the last element is
     * {@code (width-1,height-1)}. Otherwise, an empty stream is returned.
     */
    public static Stream<Pos> box(int width, int height) {
        return box(ORIGIN, new Pos(width - 1, height - 1));
    }

    /**
     * Returns a lexicographically sorted stream of positions within the closed box defined by the given
     * ranges of x and y coordinates. If both ranges are non-empty, then the first element of the stream is
     * {@code (xRange.min,yRange.min)}, and the last element is {@code (xRange.max,yRange.max)}.
     * Otherwise, an empty stream is returned.
     */
    public static Stream<Pos> box(Range xRange, Range yRange) {
        if (xRange.isEmpty() || yRange.isEmpty()) {
            return Stream.empty();
        }
        if (xRange.min() < Integer.MIN_VALUE || xRange.max() > Integer.MAX_VALUE
                || yRange.min() < Integer.MIN_VALUE || yRange.max() > Integer.MAX_VALUE
                || xRange.size() > Integer.MAX_VALUE || yRange.size() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("The ranges are too large.");
        }

        int xMin = (int) xRange.min();
        int yMin = (int) yRange.min();
        int width = (int) xRange.size();
        int height = (int) yRange.size();

        return IntStream.range(0, width * height)
                .mapToObj(i -> new Pos(xMin + i / height, yMin + i % height));
    }

    /**
     * Returns a lexicographically sorted stream of positions within the closed box {@code [min..max]}.
     * If {@code min.x <= max.x} and {@code min.y <= max.y}, then the first element of the stream is {@code min},
     * and the last element is {@code max}. Otherwise, an empty stream is returned.
     */
    public static Stream<Pos> box(Pos min, Pos max) {
        long width = max.x - min.x + 1;
        long height = max.y - min.y + 1;
        if (width <= 0 || height <= 0) {
            return Stream.empty();
        }

        return LongStream.range(0, width * height)
                .mapToObj(i -> new Pos(min.x + i / height, min.y + i % height));
    }

    /**
     * Returns a lexicographically sorted stream of positions within the
     * <a href="https://en.wikipedia.org/wiki/Minimum_bounding_box">minimum bounding box</a> of the given positions.
     * If the given array is empty, then an empty stream is returned.
     */
    public static Stream<Pos> boundingBox(Pos... positions) {
        return boundingBox(Arrays.asList(positions));
    }

    /**
     * Returns a lexicographically sorted stream of positions within the
     * <a href="https://en.wikipedia.org/wiki/Minimum_bounding_box">minimum bounding box</a> of the given positions.
     * If the given collection is empty, then an empty stream is returned.
     */
    public static Stream<Pos> boundingBox(Collection<Pos> positions) {
        return positions.isEmpty()
                ? Stream.empty()
                : box(xRange(positions), yRange(positions));
    }

}
