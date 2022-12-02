package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base class of test classes of table data structures.
 */
abstract class AbstractTableTest<T> {

    abstract AbstractTable<T> createTestTable(int rowCount, int colCount);

    @Test
    void testBasicMethods() {
        var table = createTestTable(3, 4);

        assertEquals(3, table.rowCount());
        assertEquals(4, table.colCount());
        assertEquals(12, table.size());
        assertEquals(12, table.cells().count());

        assertTrue(table.containsCell(new Tile(2, 3)));
        assertFalse(table.containsCell(new Tile(3, 3)));
        assertFalse(table.containsCell(new Tile(2, 4)));

        var table2 = createTestTable(4, 3);
        table2.cells().forEach(t -> table2.set0(t.row(), t.col(), table.get0(t.col(), t.row())));

        assertEquals(table.cells().map(t -> table.get0(t.row(), t.col())).collect(Collectors.toSet()),
                table2.cells().map(t -> table2.get0(t.row(), t.col())).collect(Collectors.toSet()));
    }

    @Test
    void testCellStreamMethods() {
        var table = createTestTable(3, 4);

        assertEquals(Stream.concat(table.row(0), Stream.concat(table.row(1), table.row(2))).toList(),
                table.cells().toList());
        assertEquals(Stream.concat(table.row(1), table.row(2)).toList(),
                table.cells(1, 0, 3, 4).toList());
        assertEquals(table.row(1).toList(),
                table.cells(1, 0, 2, 4).toList());
        assertEquals(Stream.concat(table.row(1).limit(3), table.row(2).limit(3)).toList(),
                table.cells(1, 0, 3, 3).toList());
        assertEquals(Stream.concat(table.row(1).skip(1).limit(2), table.row(2).skip(1).limit(2)).toList(),
                table.cells(1, 1, 3, 3).toList());
    }

    @Test
    void testCellNeighbors() {
        var table = createTestTable(3, 4);

        assertEquals(List.of(
                        new Tile(0, 2),
                        new Tile(1, 3),
                        new Tile(2, 2),
                        new Tile(1, 1)),
                table.neighborCells(new Tile(1, 2)).toList());
        assertEquals(List.of(
                        new Tile(0, 1),
                        new Tile(1, 0)),
                table.neighborCells(new Tile(0, 0)).toList());
        assertEquals(List.of(
                        new Tile(1, 1),
                        new Tile(2, 2),
                        new Tile(2, 0)),
                table.neighborCells(new Tile(2, 1)).toList());

        assertEquals(List.of(
                        new Tile(0, 2),
                        new Tile(0, 3),
                        new Tile(1, 3),
                        new Tile(2, 3),
                        new Tile(2, 2),
                        new Tile(2, 1),
                        new Tile(1, 1),
                        new Tile(0, 1)),
                table.extendedNeighborCells(new Tile(1, 2)).toList());
        assertEquals(List.of(
                        new Tile(0, 1),
                        new Tile(1, 1),
                        new Tile(1, 0)),
                table.extendedNeighborCells(new Tile(0, 0)).toList());
        assertEquals(List.of(
                        new Tile(1, 1),
                        new Tile(1, 2),
                        new Tile(2, 2),
                        new Tile(2, 0),
                        new Tile(1, 0)),
                table.extendedNeighborCells(new Tile(2, 1)).toList());
    }

    @Test
    void testTransformations() {
        var table = createTestTable(3, 4);
        var transposed = table.transpose();
        var right = table.rotateRight();
        var left = table.rotateLeft();

        assertEquals(table.rowCount(), transposed.colCount());
        assertEquals(table.colCount(), transposed.rowCount());
        assertEquals(table.rowCount(), right.colCount());
        assertEquals(table.colCount(), right.rowCount());
        assertEquals(table.rowCount(), left.colCount());
        assertEquals(table.colCount(), left.rowCount());

        assertNotEquals(transposed, right);
        assertNotEquals(transposed, left);
        assertNotEquals(right, left);

        assertEquals(table, transposed.transpose());
        assertEquals(table, right.rotateLeft());
        assertEquals(table, left.rotateRight());
        assertEquals(table, right.rotateRight().rotateRight().rotateRight());
        assertEquals(table, left.rotateLeft().rotateLeft().rotateLeft());
        assertEquals(table, right.rotateRight().mirrorVertically().mirrorHorizontally());
        assertEquals(table, left.rotateLeft().mirrorVertically().mirrorHorizontally());

        assertEquals(right.rotateRight(), left.rotateLeft());

        assertEquals(transposed, right.mirrorHorizontally());
        assertEquals(transposed, left.mirrorVertically());
        assertEquals(transposed, table.mirrorVertically().rotateRight());
        assertEquals(transposed, table.mirrorHorizontally().rotateLeft());
    }

    @Test
    void testEqualsAndHashCode() {
        var table1 = createTestTable(3, 4);
        var table2 = createTestTable(3, 4);
        var table3 = createTestTable(3, 4);

        assertEquals(table1, table2);
        assertEquals(table2, table3);
        assertEquals(table1, table3);
        assertEquals(table1.hashCode(), table2.hashCode());
        assertEquals(table2.hashCode(), table3.hashCode());
        assertEquals(table1.hashCode(), table3.hashCode());

        table2.set0(1, 1, table2.get0(0, 0));

        assertNotEquals(table1, table2);
        assertNotEquals(table2, table3);
        assertEquals(table1, table3);
        assertNotEquals(table1.hashCode(), table2.hashCode());
        assertNotEquals(table2.hashCode(), table3.hashCode());
        assertEquals(table1.hashCode(), table3.hashCode());
    }

}
