package com.github.pkovacs.util.data;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Abstract base class of table data structures. A table has a fixed number of rows and columns. A cell of a table
 * is identified by a {@link Tile} object or two integer indices.
 *
 * @param <T> the type of the elements stored in this table
 */
public abstract class AbstractTable<T> {

    /**
     * Returns the number of rows in this table.
     */
    public abstract int rowCount();

    /**
     * Returns the number of columns in this table.
     */
    public abstract int colCount();

    /**
     * Returns the number of cells in this table.
     */
    public int size() {
        return rowCount() * colCount();
    }

    abstract T get0(int row, int col);

    abstract void set0(int row, int col, T value);

    abstract AbstractTable<T> newInstance(int rowCount, int colCount, BiFunction<Integer, Integer, T> function);

    /**
     * Returns true if this table contains the given cell.
     */
    public boolean containsCell(Tile cell) {
        return cell.isValid(rowCount(), colCount());
    }

    /**
     * Returns an ordered stream of the cells in the specified row of this table.
     */
    public Stream<Tile> row(int i) {
        return IntStream.range(0, colCount()).mapToObj(j -> new Tile(i, j));
    }

    /**
     * Returns an ordered stream of the cells in the specified column of this table.
     */
    public Stream<Tile> col(int j) {
        return IntStream.range(0, rowCount()).mapToObj(i -> new Tile(i, j));
    }

    /**
     * Returns an ordered stream of all cells in this table (row by row).
     */
    public Stream<Tile> cells() {
        return cells(0, 0, rowCount(), colCount());
    }

    /**
     * Returns an ordered stream of the cells in the given part of this table (row by row).
     * The given lower bounds for row and column indices are inclusive, but the upper bounds are exclusive.
     */
    public Stream<Tile> cells(int startRow, int startCol, int endRow, int endCol) {
        if (startRow < 0 || startCol < 0 || endRow > rowCount() || endCol > colCount()) {
            throw new IndexOutOfBoundsException("Cell range out of bounds.");
        }
        return Tile.stream(startRow, startCol, endRow, endCol);
    }

    /**
     * Returns an ordered stream of the neighbors of the given cell in clockwise order (at most four cells).
     */
    public Stream<Tile> neighborCells(Tile cell) {
        return cell.neighbors(c -> c.isValid(rowCount(), colCount())).stream();
    }

    /**
     * Returns an ordered stream of the "extended" neighbors of the given cell in clockwise order
     * (at most eight cells, including the diagonal ones).
     */
    public Stream<Tile> extendedNeighborCells(Tile cell) {
        return cell.extendedNeighbors(c -> c.isValid(rowCount(), colCount())).stream();
    }

    /**
     * Updates the value associated with the specified cell by applying the given function to the current value
     * and returns the new value.
     */
    public T update(int row, int col, Function<? super T, ? extends T> function) {
        T value = function.apply(get0(row, col));
        set0(row, col, value);
        return value;
    }

    /**
     * Updates the value associated with the specified cell by applying the given function to the current value
     * and returns the new value.
     */
    public T update(Tile cell, Function<? super T, ? extends T> function) {
        return update(cell.row(), cell.col(), function);
    }

    /**
     * Updates all values stored in this table by applying the given function to each of them. The function
     * should not depend on the current state of this table besides its own argument, the update operations
     * should be independent.
     */
    public void updateAll(Function<? super T, ? extends T> function) {
        for (int i = 0, rowCount = rowCount(); i < rowCount; i++) {
            for (int j = 0, colCount = colCount(); j < colCount; j++) {
                set0(i, j, function.apply(get0(i, j)));
            }
        }
    }

    /**
     * Mirrors this table horizontally: the row indices remain the same, while column indices are flipped.
     * A new table is generated and returned, this table is not changed.
     */
    public AbstractTable<T> mirrorHorizontally() {
        int colCount = colCount();
        return newInstance(rowCount(), colCount, (i, j) -> get0(i, colCount - 1 - j));
    }

    /**
     * Mirrors this table vertically: the column indices remain the same, while the row indices are flipped.
     * A new table is generated and returned, this table is not changed.
     */
    public AbstractTable<T> mirrorVertically() {
        int rowCount = rowCount();
        return newInstance(rowCount, colCount(), (i, j) -> get0(rowCount - 1 - i, j));
    }

    /**
     * Rotates this table to the right (clockwise).
     * A new table is generated and returned, this table is not changed.
     */
    public AbstractTable<T> rotateRight() {
        int rowCount = rowCount();
        return newInstance(colCount(), rowCount(), (i, j) -> get0(rowCount - 1 - j, i));
    }

    /**
     * Rotates this table to the left (counter-clockwise).
     * A new table is generated and returned, this table is not changed.
     */
    public AbstractTable<T> rotateLeft() {
        int colCount = colCount();
        return newInstance(colCount(), rowCount(), (i, j) -> get0(j, colCount - 1 - i));
    }

    /**
     * Transposes this table: turns rows into columns and vice versa.
     * A new table is generated and returned, this table is not changed.
     */
    public AbstractTable<T> transpose() {
        return newInstance(colCount(), rowCount(), (i, j) -> get0(j, i));
    }

}
