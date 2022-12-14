package com.github.pkovacs.util.data;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a point (or position) in 2D coordinate space with integer precision. It is an immutable pair of
 * {@code int} values: x and y coordinates. It provides methods to get the neighbors of a point and the Manhattan
 * distance between two points. Lexicographical ordering is also supported (first by x coordinate, then by
 * y coordinate).
 * <p>
 * This record is similar to {@link Cell} but with different order and names of components.
 * Another related class is {@link Vector}, which supports vector operations (addition, rotation, etc.).
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
     * Returns the four neighbors of this point.
     */
    public Stream<Point> neighbors() {
        return Stream.of(
                new Point(x - 1, y),
                new Point(x + 1, y),
                new Point(x, y - 1),
                new Point(x, y + 1));
    }

    /**
     * Returns the {@link #isValid(int, int) valid} neighbors of this point with respect to the given width and
     * height.
     */
    public Stream<Point> validNeighbors(int width, int height) {
        return neighbors().filter(p -> p.isValid(width, height));
    }

    /**
     * Returns the Manhattan distance between this point and the given point.
     */
    public int dist(Point p) {
        return dist(this, p);
    }

    /**
     * Returns the Manhattan distance between the given two points.
     */
    public static int dist(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
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
     * Returns an ordered stream of points within the given bounds.
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
