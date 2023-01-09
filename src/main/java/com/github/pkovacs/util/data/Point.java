package com.github.pkovacs.util.data;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a point (or position vector) in 2D coordinate space as an immutable pair of {@code int} values:
 * {@code (x, y)}. This class provides various useful methods and also supports lexicographical ordering
 * (first by x coordinate, then by y coordinate).
 * <p>
 * {@link Cell} is a similar class with different order and names of the components: {@code (row, col)} instead of
 * {@code (x, y)}. Another related class is {@link Vector}, which is the D-dimensional generalization of {@link Point}.
 *
 * @see Cell
 * @see Vector
 */
public record Point(int x, int y) implements Position, Comparable<Point> {

    /** The origin point: (0, 0). */
    public static final Point ORIGIN = new Point(0, 0);

    /**
     * Returns true if the coordinates of this point are between zero (inclusive) and the given width/height
     * (exclusive).
     */
    public boolean isValid(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Returns the neighbor of this point in the given direction, assuming that axis y is directed <i>downward</i>
     * (to the south). That is, (0, 0) represents the <i>top</i> left point among the ones with non-negative
     * coordinates.
     */
    public Point neighbor(Direction dir) {
        return switch (dir) {
            case NORTH -> new Point(x, y - 1);
            case EAST -> new Point(x + 1, y);
            case SOUTH -> new Point(x, y + 1);
            case WEST -> new Point(x - 1, y);
        };
    }

    /**
     * Returns the neighbor of this point in the given direction, assuming that axis y is directed <i>upward</i>
     * (to the north). That is, (0, 0) represents the <i>bottom</i> left point among the ones with non-negative
     * coordinates.
     */
    public Point neighborWithUpwardY(Direction dir) {
        return switch (dir) {
            case NORTH -> new Point(x, y + 1);
            case EAST -> new Point(x + 1, y);
            case SOUTH -> new Point(x, y - 1);
            case WEST -> new Point(x - 1, y);
        };
    }

    /**
     * Returns the neighbor of this point in the given direction, assuming that axis y is directed <i>downward</i>
     * (to the south). That is, (0, 0) represents the <i>top</i> left point among the ones with non-negative
     * coordinates.
     *
     * @param dir the direction character. One of 'N' (north), 'E' (east), 'S' (south), 'W' (west),
     *         'U' (up), 'R' (right), 'D' (down), 'L' (left), and their lowercase variants.
     */
    public Point neighbor(char dir) {
        return neighbor(Direction.fromChar(dir));
    }

    /**
     * Returns the neighbor of this point in the given direction, assuming that axis y is directed <i>upward</i>
     * (to the north). That is, (0, 0) represents the <i>bottom</i> left point among the ones with non-negative
     * coordinates.
     *
     * @param dir the direction character. One of 'N' (north), 'E' (east), 'S' (south), 'W' (west),
     *         'U' (up), 'R' (right), 'D' (down), 'L' (left), and their lowercase variants.
     */
    public Point neighborWithUpwardY(char dir) {
        return neighborWithUpwardY(Direction.fromChar(dir));
    }

    /**
     * Returns a lexicographically sorted stream of the four neighbors of this point.
     */
    public Stream<Point> neighbors() {
        return neighborsAndSelf().filter(p -> p != this);
    }

    /**
     * Returns a lexicographically sorted stream of this point and its four neighbors.
     */
    public Stream<Point> neighborsAndSelf() {
        return Stream.of(
                new Point(x - 1, y),
                new Point(x, y - 1),
                this,
                new Point(x, y + 1),
                new Point(x + 1, y));
    }

    /**
     * Returns a lexicographically sorted stream of the eight "extended" neighbors of this point (also including the
     * diagonal ones).
     */
    public Stream<Point> extendedNeighbors() {
        return extendedNeighborsAndSelf().filter(p -> p != this);
    }

    /**
     * Returns a lexicographically sorted stream of this point and its eight "extended" neighbors (also including the
     * diagonal ones).
     */
    public Stream<Point> extendedNeighborsAndSelf() {
        return Stream.of(
                new Point(x - 1, y - 1),
                new Point(x - 1, y),
                new Point(x - 1, y + 1),
                new Point(x, y - 1),
                this,
                new Point(x, y + 1),
                new Point(x + 1, y - 1),
                new Point(x + 1, y),
                new Point(x + 1, y + 1));
    }

    /**
     * Creates a new point by adding the given delta values to the coordinates of this point.
     */
    public Point add(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }

    /**
     * Creates a new point by adding the given point to this one (as a position vector).
     */
    public Point add(Point other) {
        return new Point(x + other.x, y + other.y);
    }

    /**
     * Creates a new point by subtracting the given point from this one (as a position vector).
     */
    public Point subtract(Point other) {
        return new Point(x - other.x, y - other.y);
    }

    /**
     * Creates a new point that is the opposite of this point (as a position vector).
     */
    public Point opposite() {
        return new Point(-x, -y);
    }

    /**
     * Creates a new point by rotating this point 90 degrees to the left (as a position vector).
     */
    public Point rotateLeft() {
        return new Point(-y, x);
    }

    /**
     * Creates a new point by rotating this point 90 degrees to the right (as a position vector).
     */
    public Point rotateRight() {
        return new Point(y, -x);
    }

    /**
     * Creates a new point by mirroring this point horizontally (as a position vector).
     */
    public Point mirrorHorizontally() {
        return new Point(-x, y);
    }

    /**
     * Creates a new point by mirroring this point vertically (as a position vector).
     */
    public Point mirrorVertically() {
        return new Point(x, -y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public int compareTo(Point other) {
        return x != other.x ? Integer.compare(x, other.x) : Integer.compare(y, other.y);
    }

    /**
     * Returns an ordered stream of {@link #isValid(int, int) valid} points within the given bounds.
     * If both arguments are positive, then the first element of the stream is (0, 0), the last element is
     * {@code (width - 1, height - 1)}, and the stream is lexicographically sorted.
     * Otherwise, an empty stream is returned.
     */
    public static Stream<Point> box(int width, int height) {
        return box(ORIGIN, new Point(width - 1, height - 1));
    }

    /**
     * Returns an ordered stream of points within the closed box {@code [min..max]}.
     * If {@code min.x <= max.x} and {@code min.y <= max.y}, then the first element of the stream is
     * {@code min}, the last element is {@code max}, and the stream is lexicographically sorted.
     * Otherwise, an empty stream is returned.
     */
    public static Stream<Point> box(Point min, Point max) {
        int width = max.x - min.x + 1;
        int height = max.y - min.y + 1;
        if (width <= 0 || height <= 0) {
            return Stream.empty();
        }

        return IntStream.range(0, width * height)
                .mapToObj(i -> new Point(min.x + i / height, min.y + i % height));
    }

}
