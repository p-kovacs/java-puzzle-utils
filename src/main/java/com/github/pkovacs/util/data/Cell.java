package com.github.pkovacs.util.data;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a cell (or position) of a table or matrix. It is an immutable pair of {@code int} values: row index and
 * column index. It provides methods to get the neighbors of a cell and the Manhattan distance between two cells.
 * Lexicographical ordering is also supported (first by row index, then by column index).
 * <p>
 * This record is similar to {@link Point} but with different order and names of components.
 *
 * @see Point
 * @see Table
 */
public record Cell(int row, int col) implements Comparable<Cell> {

    /**
     * Returns true if the indices of this cell are between zero (inclusive) and the given row/column count
     * (exclusive).
     */
    public boolean isValid(int rowCount, int colCount) {
        return row >= 0 && row < rowCount && col >= 0 && col < colCount;
    }

    /**
     * Returns the neighbor of this cell in the given direction. (0, 0) represents the upper-left cell among
     * the ones with non-negative indices. The directions are interpreted accordingly, so "north" or "up" means
     * lower row index, while "south" or "down" means higher row index.
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
     * Returns the neighbor of this cell in the given direction. (0, 0) represents the upper-left cell among
     * the ones with non-negative indices. The directions are interpreted accordingly, so "north" or "up" means
     * lower row index, while "south" or "down" means higher row index.
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
     * @return the valid neighbors in clockwise order (N, E, S, W)
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
     * Returns the Manhattan distance between this cell and the given cell.
     */
    public int dist(Cell other) {
        return dist(this, other);
    }

    /**
     * Returns the Manhattan distance between the given two cells.
     */
    public static int dist(Cell cell1, Cell cell2) {
        return Math.abs(cell1.row - cell2.row) + Math.abs(cell1.col - cell2.col);
    }

    @Override
    public int compareTo(Cell other) {
        return row != other.row ? Integer.compare(row, other.row) : Integer.compare(col, other.col);
    }

    /**
     * Returns an ordered stream of cells within the given bounds.
     * If both arguments are positive, then the first element of the returned stream is {@code (0, 0)}, and the
     * last element is {@code (rowCount - 1, colCount - 1)}. Otherwise, an empty stream is returned.
     */
    public static Stream<Cell> stream(int rowCount, int colCount) {
        return stream(0, 0, rowCount, colCount);
    }

    /**
     * Returns an ordered stream of cells within the given bounds.
     * If {@code startRow < endRow} and {@code startCol < endCol}, then the first element of the returned stream is
     * {@code (startRow, startCol)}, and the last element is {@code (endRow - 1, endCol - 1)}.
     * Otherwise, an empty stream is returned.
     */
    public static Stream<Cell> stream(int startRow, int startCol, int endRow, int endCol) {
        int rowCount = endRow - startRow;
        int colCount = endCol - startCol;
        if (rowCount <= 0 || colCount <= 0) {
            return Stream.empty();
        }

        return IntStream.range(0, rowCount * colCount)
                .mapToObj(i -> new Cell(startRow + i / colCount, startCol + i % colCount));
    }

}
