package com.github.pkovacs.util.data;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntTableTest extends AbstractTableTest<Integer> {

    @Test
    void testGettersAndSetters() {
        var table = new IntTable(3, 4);

        assertEquals(3, table.rowCount());
        assertEquals(4, table.colCount());

        assertContentEquals(new int[][] { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } }, table);

        table.cells().forEach(t -> table.set(t, t.row() * 100 + t.col()));

        assertContentEquals(new int[][] { { 0, 1, 2, 3 }, { 100, 101, 102, 103 }, { 200, 201, 202, 203 } }, table);

        table.cells().forEach(t -> table.update(t, x -> 2 * x));
        table.set(0, 0, 42);
        table.set(new Tile(2, 2), -1);

        assertContentEquals(new int[][] { { 42, 2, 4, 6 }, { 200, 202, 204, 206 }, { 400, 402, -1, 406 } }, table);

        table.update(0, 0, v -> v + 6);
        assertEquals(49, table.inc(0, 0));
        assertEquals(50, table.inc(new Tile(0, 0)));
        assertEquals(-10, table.update(new Tile(2, 2), v -> v * 10));

        assertContentEquals(new int[][] { { 50, 2, 4, 6 }, { 200, 202, 204, 206 }, { 400, 402, -10, 406 } }, table);

        table.cells().forEach(t -> table.update(t, v -> v / 2));

        assertContentEquals(new int[][] { { 25, 1, 2, 3 }, { 100, 101, 102, 103 }, { 200, 201, -5, 203 } }, table);
        assertEquals(1, table.count(101));
        assertEquals(0, table.count(42));

        table.fill(42);

        assertContentEquals(new int[][] { { 42, 42, 42, 42 }, { 42, 42, 42, 42 }, { 42, 42, 42, 42 } }, table);
        assertEquals(0, table.count(101));
        assertEquals(12, table.count(42));
    }

    @Test
    void testWrappedMatrix() {
        var matrix = new int[][] { { 0, 1, 2, 3 }, { 100, 101, 102, 103 }, { 200, 201, 202, 203 } };
        var table = new IntTable(matrix);

        assertContentEquals(matrix, table);

        matrix[0][0] = 42;
        table.set(2, 2, -1);

        assertContentEquals(new int[][] { { 42, 1, 2, 3 }, { 100, 101, 102, 103 }, { 200, 201, -1, 203 } }, table);

        assertThrows(IllegalArgumentException.class,
                () -> new IntTable(new int[][] { { 0, 1 }, { 10, 20 }, { 100, 200, 300 } }));
    }

    @Test
    void testStreamMethods() {
        var table = new IntTable(3, 4, (r, c) -> ((r + 2) % 3) * 4 + c);

        assertContentEquals(new int[][] { { 8, 9, 10, 11 }, { 0, 1, 2, 3 }, { 4, 5, 6, 7 } }, table);

        assertArrayEquals(table.values().toArray(), table.cells().mapToInt(table::get).toArray());
        assertArrayEquals(table.rowValues(1).toArray(), table.row(1).mapToInt(table::get).toArray());
        assertArrayEquals(table.colValues(2).toArray(), table.col(2).mapToInt(table::get).toArray());

        assertEquals(66, table.sum());
        assertEquals(0, table.min());
        assertEquals(11, table.max());

        assertEquals(66, table.values().sum());
        assertEquals(0, table.values().min().orElseThrow());
        assertEquals(11, table.values().max().orElseThrow());

        assertEquals(38, table.rowValues(0).sum());
        assertEquals(6, table.rowValues(1).sum());
        assertEquals(22, table.rowValues(2).sum());

        assertEquals(12, table.colValues(0).sum());
        assertEquals(15, table.colValues(1).sum());
        assertEquals(18, table.colValues(2).sum());
        assertEquals(21, table.colValues(3).sum());

        assertEquals(38, table.row(0).mapToInt(table::get).sum());
        assertEquals(6, table.row(1).mapToInt(table::get).sum());
        assertEquals(22, table.row(2).mapToInt(table::get).sum());

        assertEquals(12, table.col(0).mapToInt(table::get).sum());
        assertEquals(15, table.col(1).mapToInt(table::get).sum());
        assertEquals(18, table.col(2).mapToInt(table::get).sum());
        assertEquals(21, table.col(3).mapToInt(table::get).sum());

        assertEquals(66, table.values(0, 0, 3, 4).sum());
        assertEquals(0, table.values(0, 0, 3, 4).min().orElseThrow());
        assertEquals(11, table.values(0, 0, 3, 4).max().orElseThrow());
        assertEquals(24, table.values(1, 1, 3, 4).sum());
        assertEquals(1, table.values(1, 1, 3, 4).min().orElseThrow());
        assertEquals(7, table.values(1, 1, 3, 4).max().orElseThrow());
        assertEquals(14, table.values(1, 1, 3, 3).sum());
        assertEquals(1, table.values(1, 1, 3, 3).min().orElseThrow());
        assertEquals(6, table.values(1, 1, 3, 3).max().orElseThrow());
        assertEquals(11, table.values(2, 1, 3, 3).sum());
        assertEquals(5, table.values(2, 1, 3, 3).min().orElseThrow());
        assertEquals(6, table.values(2, 1, 3, 3).max().orElseThrow());
    }

    @Test
    void testToString() {
        var table1 = createTestTable(3, 4);
        var table2 = new IntTable(3, 4, (r, c) -> (r + c) % 2 == 0 ? 1 : -1);
        var table3 = new IntTable(3, 4, (r, c) -> table2.get(r, c) * table1.get(r, c));

        assertEquals("  0   1   2   3\n100 101 102 103\n200 201 202 203\n", table1.toString());
        assertEquals(" 1 -1  1 -1\n-1  1 -1  1\n 1 -1  1 -1\n", table2.toString());
        assertEquals("   0   -1    2   -3\n-100  101 -102  103\n 200 -201  202 -203\n", table3.toString());

        table3.set(1, 1, 424242);
        assertEquals("     0     -1      2     -3\n  -100 424242   -102    103\n   200   -201    202   -203\n",
                table3.toString());

        var table4 = createTestTable(3, 4).transpose();
        var table5 = createTestTable(3, 4).rotateRight();

        assertEquals("  0 100 200\n  1 101 201\n  2 102 202\n  3 103 203\n", table4.toString());
        assertEquals("200 100   0\n201 101   1\n202 102   2\n203 103   3\n", table5.toString());
    }

    private static void assertContentEquals(int[][] expected, IntTable table) {
        assertTrue(Arrays.deepEquals(expected, table.asArray()));
    }

    @Override
    IntTable createTestTable(int rowCount, int colCount) {
        return new IntTable(rowCount, colCount, (r, c) -> r * 100 + c);
    }

}
