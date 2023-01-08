package com.github.pkovacs.util.data;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a cell (or position) of a table or matrix. It is an immutable pair of {@code int} values: row index and
 * column index. It provides methods to get the neighbors of a cell and the Manhattan distance between two cells.
 * Lexicographical ordering is also supported (first by row index, then by column index).
 * <p>
 * {@link Point} is a similar class with different order and names of the components: {@code (x, y)} instead of
 * {@code (row, col)}.
 *
 * @see Point
 * @see Table
 */
public record Cell(int row, int col) implements Comparable<Cell> {

    /** The origin cell: (0, 0). */
    public static final Cell ORIGIN = new Cell(0, 0);

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
     * Returns true if the given cell is a neighbor of this cell.
     */
    public boolean isNeighbor(Cell other) {
        return (row == other.row && Math.abs(col - other.col) == 1)
                || (col == other.col && Math.abs(row - other.row) == 1);
    }

    /**
     * Returns true if the given cell is an "extended" neighbor of this cell, also including the diagonal ones.
     */
    public boolean isExtendedNeighbor(Cell other) {
        return !equals(other) && Math.abs(row - other.row) <= 1 && Math.abs(col - other.col) <= 1;
    }

    /**
     * Returns the Manhattan distance (aka. "taxicab" distance) between this cell and (0, 0).
     */
    public int dist() {
        return Math.abs(row) + Math.abs(col);
    }

    /**
     * Returns the Manhattan distance (aka. "taxicab" distance) between this cell and the given cell.
     */
    public int dist(Cell other) {
        return Math.abs(row - other.row) + Math.abs(col - other.col);
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
