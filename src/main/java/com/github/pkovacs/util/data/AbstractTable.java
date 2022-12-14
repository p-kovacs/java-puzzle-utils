package com.github.pkovacs.util.data;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Abstract base class of table data structures. A table has a fixed number of rows and columns. A cell of a table
 * is identified by a {@link Cell} object or two integer indices, and it has an associated value.
 *
 * @param <V> the type of the values associated with the cells of this table
 */
public abstract class AbstractTable<V> {

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

    abstract V get0(int row, int col);

    abstract void set0(int row, int col, V value);

    abstract AbstractTable<V> newInstance(int rowCount, int colCount, BiFunction<Integer, Integer, V> function);

    /**
     * Returns true if this table contains the given cell.
     */
    public boolean containsCell(Cell cell) {
        return cell.isValid(rowCount(), colCount());
    }

    /**
     * Returns an ordered stream of the cells in the specified row of this table.
     */
    public Stream<Cell> row(int i) {
        return IntStream.range(0, colCount()).mapToObj(j -> new Cell(i, j));
    }

    /**
     * Returns an ordered stream of the cells in the specified column of this table.
     */
    public Stream<Cell> col(int j) {
        return IntStream.range(0, rowCount()).mapToObj(i -> new Cell(i, j));
    }

    /**
     * Returns an ordered stream of all cells in this table (row by row).
     */
    public Stream<Cell> cells() {
        return cells(0, 0, rowCount(), colCount());
    }

    /**
     * Returns an ordered stream of the cells in the specified part of this table (row by row).
     * The given lower bounds for row and column indices are inclusive, but the upper bounds are exclusive.
     */
    public Stream<Cell> cells(int startRow, int startCol, int endRow, int endCol) {
        if (startRow < 0 || startCol < 0 || endRow > rowCount() || endCol > colCount()) {
            throw new IndexOutOfBoundsException("Cell range out of bounds.");
        }
        return Cell.stream(startRow, startCol, endRow, endCol);
    }

    /**
     * Returns an ordered stream of the neighbors of the given cell within this table in clockwise order
     * (at most four cells). Only those cells are included that are contained in this table.
     */
    public Stream<Cell> neighbors(Cell cell) {
        return cell.neighbors().filter(c -> c.isValid(rowCount(), colCount()));
    }

    /**
     * Returns an ordered stream of the "extended" neighbors of the given cell within this table in clockwise order
     * (at most eight cells, also including the diagonal ones). Only those cells are included that are contained in
     * this table.
     */
    public Stream<Cell> extendedNeighbors(Cell cell) {
        return cell.extendedNeighbors().filter(c -> c.isValid(rowCount(), colCount()));
    }

    /**
     * Updates the value associated with the specified cell by applying the given function to the current value
     * and returns the new value.
     */
    public V update(int row, int col, Function<? super V, ? extends V> function) {
        V value = function.apply(get0(row, col));
        set0(row, col, value);
        return value;
    }

    /**
     * Updates the value associated with the specified cell by applying the given function to the current value
     * and returns the new value.
     */
    public V update(Cell cell, Function<? super V, ? extends V> function) {
        return update(cell.row(), cell.col(), function);
    }

    /**
     * Creates a new table by mirroring this one horizontally: row indices remain the same, while column indices
     * are flipped.
     */
    public AbstractTable<V> mirrorHorizontally() {
        int colCount = colCount();
        return newInstance(rowCount(), colCount, (i, j) -> get0(i, colCount - 1 - j));
    }

    /**
     * Creates a new table by mirroring this one vertically: column indices remain the same, while row indices
     * are flipped.
     */
    public AbstractTable<V> mirrorVertically() {
        int rowCount = rowCount();
        return newInstance(rowCount, colCount(), (i, j) -> get0(rowCount - 1 - i, j));
    }

    /**
     * Creates a new table by rotating this one to the right (clockwise).
     */
    public AbstractTable<V> rotateRight() {
        int rowCount = rowCount();
        return newInstance(colCount(), rowCount(), (i, j) -> get0(rowCount - 1 - j, i));
    }

    /**
     * Creates a new table by rotating this one to the left (counter-clockwise).
     */
    public AbstractTable<V> rotateLeft() {
        int colCount = colCount();
        return newInstance(colCount(), rowCount(), (i, j) -> get0(j, colCount - 1 - i));
    }

    /**
     * Creates a new table by transposing this one: turns rows into columns and vice versa.
     */
    public AbstractTable<V> transpose() {
        return newInstance(colCount(), rowCount(), (i, j) -> get0(j, i));
    }

}