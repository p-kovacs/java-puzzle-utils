package com.github.pkovacs.util.data;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a point (or position) in 2D coordinate space with integer precision. It is an immutable pair of
 * {@code int} values: x and y coordinates. It provides methods to get the neighbors of a point and the Manhattan
 * distance between two points. Lexicographical ordering is also supported (first by x coordinate, then by
 * y coordinate).
 * <p>
 * {@link Cell} is a similar class with different order and names of the components: {@code (row, col)} instead of
 * {@code (x, y)}. Another related class is {@link Vector}, which is the D-dimensional generalization of {@link Point}.
 *
 * @see Cell
 * @see Vector
 */
public record Point(int x, int y) implements Comparable<Point> {

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
     * Returns the four neighbors of this point.
     */
    public Stream<Point> neighbors() {
        return Stream.of(
                new Point(x, y - 1),
                new Point(x + 1, y),
                new Point(x, y + 1),
                new Point(x - 1, y));
    }

    /**
     * Returns the {@link #isValid(int, int) valid} neighbors of this point with respect to the given width and
     * height (at most four points).
     */
    public Stream<Point> validNeighbors(int width, int height) {
        return neighbors().filter(p -> p.isValid(width, height));
    }

    /**
     * Returns the eight "extended" neighbors of this point, also including the diagonal ones.
     */
    public Stream<Point> extendedNeighbors() {
        return Stream.of(
                new Point(x, y - 1),
                new Point(x + 1, y - 1),
                new Point(x + 1, y),
                new Point(x + 1, y + 1),
                new Point(x, y + 1),
                new Point(x - 1, y + 1),
                new Point(x - 1, y),
                new Point(x - 1, y - 1));
    }

    /**
     * Returns true if the given point is a neighbor of this point.
     */
    public boolean isNeighbor(Point other) {
        return (x == other.x && Math.abs(y - other.y) == 1) || (y == other.y && Math.abs(x - other.x) == 1);
    }

    /**
     * Returns true if the given point is an "extended" neighbor of this point, also including the diagonal ones.
     */
    public boolean isExtendedNeighbor(Point other) {
        return !equals(other) && Math.abs(x - other.x) <= 1 && Math.abs(y - other.y) <= 1;
    }

    /**
     * Creates a new point by adding the coordinates of given point to the coordinates of this point.
     */
    public Point add(Point other) {
        return new Point(x + other.x, y + other.y);
    }

    /**
     * Creates a new point by subtracting the coordinates of given point to the coordinates of this point.
     */
    public Point subtract(Point other) {
        return new Point(x - other.x, y - other.y);
    }

    /**
     * Returns the Manhattan distance (aka. "taxicab" distance) between this point and (0, 0).
     */
    public int dist() {
        return Math.abs(x) + Math.abs(y);
    }

    /**
     * Returns the Manhattan distance (aka. "taxicab" distance) between this point and the given point.
     */
    public int dist(Point other) {
        return subtract(other).dist();
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
     * Returns an ordered stream of points within the given bounds.
     * If both arguments are positive, then the first element of the returned stream is {@code (0, 0)}, and the
     * last element is {@code (width - 1, height - 1)}. Otherwise, an empty stream is returned.
     */
    public static Stream<Point> stream(int width, int height) {
        return stream(0, 0, width, height);
    }

    /**
     * Returns an ordered stream of points within the given bounds (the upper bounds are exclusive).
     * If {@code startX < endX} and {@code startY < endY}, then the first element of the returned stream is
     * {@code (startX, startY)}, and the last element is {@code (endX - 1, endY - 1)}.
     * Otherwise, an empty stream is returned.
     */
    public static Stream<Point> stream(int startX, int startY, int endX, int endY) {
        int width = endX - startX;
        int height = endY - startY;
        if (endX <= startX || endY <= startY) {
            return Stream.empty();
        }

        return IntStream.range(0, width * height)
                .mapToObj(i -> new Point(startX + i / height, startY + i % height));
    }

}
