package com.github.pkovacs.util.data;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a cell (or position) in a table or matrix as an immutable pair of {@code int} values:
 * {@code (row, col)}. This class provides various useful methods and also supports lexicographical ordering
 * (first by row index, then by column index).
 * <p>
 * {@link Point} is a similar class with different order and names of the components: {@code (x, y)} instead of
 * {@code (row, col)}.
 *
 * @see Point
 * @see Table
 */
public record Cell(int row, int col) implements Position, Comparable<Cell> {

    /** The origin cell: (0, 0). */
    public static final Cell ORIGIN = new Cell(0, 0);

    @Override
    public int x() {
        return col;
    }

    @Override
    public int y() {
        return row;
    }

    /**
     * Returns true if the indices of this cell are between zero (inclusive) and the given row/column count
     * (exclusive).
     */
    public boolean isValid(int rowCount, int colCount) {
        return row >= 0 && row < rowCount && col >= 0 && col < colCount;
    }

    /**
     * Returns the neighbor of this cell in the given direction. (0, 0) represents the top left cell among the ones
     * with non-negative indices. The directions are interpreted accordingly, so "north" or "up" means decreasing
     * row index, while "south" or "down" means increasing row index.
     */
    public Cell neighbor(Direction dir) {
        return switch (dir) {
            case NORTH -> new Cell(row - 1, col);
            case EAST -> new Cell(row, col + 1);
            case SOUTH -> new Cell(row + 1, col);
            case WEST -> new Cell(row, col - 1);
        };
    }

    /**
     * Returns the neighbor of this cell in the given direction. (0, 0) represents the top left cell among the ones
     * with non-negative indices. The directions are interpreted accordingly, so "north" or "up" means decreasing
     * row index, while "south" or "down" means increasing row index.
     *
     * @param dir the direction character. One of 'N' (north), 'E' (east), 'S' (south), 'W' (west),
     *         'U' (up), 'R' (right), 'D' (down), 'L' (left), and their lowercase variants.
     */
    public Cell neighbor(char dir) {
        return neighbor(Direction.fromChar(dir));
    }

    /**
     * Returns the four neighbors of this cell.
     *
     * @return the four neighbor cells in clockwise order (N, E, S, W)
     */
    public Stream<Cell> neighbors() {
        return Stream.of(
                new Cell(row - 1, col),
                new Cell(row, col + 1),
                new Cell(row + 1, col),
                new Cell(row, col - 1));
    }

    /**
     * Returns the {@link #isValid(int, int) valid} neighbors of this cell with respect to the given row count and
     * column count.
     *
     * @return the valid neighbor cells in clockwise order (at most four cells in N, E, S, W order)
     */
    public Stream<Cell> validNeighbors(int rowCount, int colCount) {
        return neighbors().filter(cell -> cell.isValid(rowCount, colCount));
    }

    /**
     * Returns the eight "extended" neighbors of this cell, also including the diagonal ones.
     *
     * @return the eight "extended" neighbor cells in clockwise order (N, NE, E, SE, S, SW, W, NW)
     */
    public Stream<Cell> extendedNeighbors() {
        return Stream.of(
                new Cell(row - 1, col),
                new Cell(row - 1, col + 1),
                new Cell(row, col + 1),
                new Cell(row + 1, col + 1),
                new Cell(row + 1, col),
                new Cell(row + 1, col - 1),
                new Cell(row, col - 1),
                new Cell(row - 1, col - 1));
    }

    /**
     * Creates a new cell by adding the given delta values to the indices of this cell.
     */
    public Cell add(int deltaRow, int deltaCol) {
        return new Cell(row + deltaRow, col + deltaCol);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    @Override
    public int compareTo(Cell other) {
        return row != other.row ? Integer.compare(row, other.row) : Integer.compare(col, other.col);
    }

    /**
     * Returns an ordered stream of {@link #isValid(int, int) valid} cells within the given bounds.
     * If both arguments are positive, then the first element of the stream is (0, 0), the last element is
     * {@code (rowCount - 1, colCount - 1)}, and the stream is lexicographically sorted.
     * Otherwise, an empty stream is returned.
     */
    public static Stream<Cell> box(int rowCount, int colCount) {
        return box(ORIGIN, new Cell(rowCount - 1, colCount - 1));
    }

    /**
     * Returns an ordered stream of cells within the closed box {@code [min..max]}.
     * If {@code min.row <= max.row} and {@code min.col <= max.col}, then the first element of the stream is
     * {@code min}, the last element is {@code max}, and the stream is lexicographically sorted.
     * Otherwise, an empty stream is returned.
     */
    public static Stream<Cell> box(Cell min, Cell max) {
        int rowCount = max.row - min.row + 1;
        int colCount = max.col - min.col + 1;
        if (rowCount <= 0 || colCount <= 0) {
            return Stream.empty();
        }

        return IntStream.range(0, rowCount * colCount)
                .mapToObj(i -> new Cell(min.row + i / colCount, min.col + i % colCount));
    }

}
