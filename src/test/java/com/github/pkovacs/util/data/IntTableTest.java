package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntTableTest extends AbstractTableTest<Integer> {

    @Test
    void testConstructors() {
        var t1 = new IntTable(2, 3, 42);
        var t2 = new IntTable(t1);

        assertEquals(t1, t2);
        assertNotEquals(t1.asMatrix(), t2.asMatrix());
        assertTrue(Arrays.deepEquals(t1.asMatrix(), t2.asMatrix()));

        t2.set(1, 2, 23);

        assertEquals(42, t1.get(1, 1));
        assertEquals(42, t1.get(1, 2));
        assertEquals(42, t2.get(1, 1));
        assertEquals(23, t2.get(1, 2));

        t2.asMatrix()[2][1] = 123;
        assertEquals(123, t2.get(1, 2));

        var t3 = new IntTable(t2.asMatrix());
        t3.set(0, 0, -1);
        t2.set(0, 1, -2);

        assertEquals(42, t1.get(0, 0));
        assertEquals(42, t2.get(0, 0));
        assertEquals(-1, t3.get(0, 0));
        assertEquals(42, t1.get(0, 1));
        assertEquals(-2, t2.get(0, 1));
        assertEquals(42, t3.get(0, 1));
    }

    @Test
    void testWrapMethods() {
        var cells = List.of(p(12, 12), p(13, 13), p(11, 11), p(11, 14));
        assertContentEquals(new int[][] { { 10, 0, 0 }, { 0, 10, 0 }, { 0, 0, 10 }, { 10, 0, 0 } },
                IntTable.wrap(cells, 10, 0));

        var table1 = new IntTable(new int[][] { { 0, 1, 2, 3 }, { 100, 101, 102, 103 }, { 200, 201, 202, 203 } });
        var table2 = new IntTable(new int[][] { { -1, 1, -1, 3 }, { 100, -1, 102, -1 }, { -1, 201, -1, 203 } });
        var map = table1.cells().filter(p -> (p.x + p.y) % 2 == 1)
                .collect(Collectors.toMap(p -> p, table1::get));
        assertEquals(table2, IntTable.wrap(map, -1));
    }

    @Test
    void testGettersAndSetters() {
        var table = new IntTable(4, 3);

        assertEquals(4, table.width());
        assertEquals(3, table.height());
        assertEquals(4, table.colCount());
        assertEquals(3, table.rowCount());

        assertContentEquals(new int[][] { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } }, table);

        table.cells().forEach(p -> table.set(p, p.y * 100 + p.x));

        assertContentEquals(new int[][] { { 0, 1, 2, 3 }, { 100, 101, 102, 103 }, { 200, 201, 202, 203 } }, table);

        table.cells().forEach(p -> table.update(p, x -> 2 * x));
        table.set(0, 0, 42);
        table.set(p(2, 2), -1);

        assertContentEquals(new int[][] { { 42, 2, 4, 6 }, { 200, 202, 204, 206 }, { 400, 402, -1, 406 } }, table);

        table.update(0, 0, v -> v + 6);
        assertEquals(49, table.inc(0, 0));
        assertEquals(50, table.inc(p(0, 0)));
        assertEquals(-10, table.update(p(2, 2), v -> v * 10));

        assertContentEquals(new int[][] { { 50, 2, 4, 6 }, { 200, 202, 204, 206 }, { 400, 402, -10, 406 } }, table);

        table.cells().forEach(p -> table.update(p, v -> v / 2));

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

        assertContentEquals(new int[][] { { 0, 1, 2, 3 }, { 100, 101, 102, 103 }, { 200, 201, -1, 203 } }, table);

        assertThrows(IllegalArgumentException.class,
                () -> new IntTable(new int[][] { { 0, 1 }, { 10, 20 }, { 100, 200, 300 } }));
    }

    @Test
    void testStreamMethods() {
        var table = new IntTable(4, 3, p -> ((p.y + 2) % 3) * 4 + p.x);

        assertContentEquals(new int[][] { { 8, 9, 10, 11 }, { 0, 1, 2, 3 }, { 4, 5, 6, 7 } }, table);

        assertEquals(List.of(8, 9, 10, 11, 0, 1, 2, 3, 4, 5, 6, 7),
                table.cells().map(table::get).toList());
        assertEquals(List.of(8, 9, 10, 11, 0, 1, 2, 3, 4, 5, 6, 7),
                table.values().boxed().toList());

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
    }

    @Test
    void testWithEmptyTable() {
        var table1 = new IntTable(0, 42);
        var table2 = new IntTable(23, 0);

        assertTrue(table1.isEmpty());
        assertTrue(table2.isEmpty());
        assertEquals(0, table1.size());
        assertEquals(0, table2.size());
        assertEquals(List.of(), table1.cells().toList());
        assertEquals(List.of(), table2.cells().toList());
        assertEquals(List.of(), table1.values().boxed().toList());
        assertEquals(List.of(), table2.values().boxed().toList());
        assertThrows(IndexOutOfBoundsException.class, () -> table1.get(0, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> table2.get(0, 0));
    }

    @Test
    void testFindMethods() {
        var table = new IntTable(new int[][] { { 3, 2, 1 }, { 10, 20, 30 }, { 1, 2, 3 }, { -1, -2, -3 } });

        assertEquals(p(2, 1), table.find(30));
        assertEquals(p(1, 0), table.find(2));
        assertThrows(NoSuchElementException.class, () -> table.find(42));

        assertEquals(List.of(p(2, 1)), table.findAll(30).toList());
        assertEquals(List.of(p(2, 0), p(0, 2)), table.findAll(1).toList());
        assertEquals(List.of(), table.findAll(42).toList());
    }

    @Test
    void testToString() {
        var table1 = createTestTable(4, 3);
        var table2 = new IntTable(4, 3, p -> ((p.x + p.y) % 2 == 0) ? 1 : -1);
        var table3 = new IntTable(4, 3, p -> table1.get(p) * table2.get(p));

        assertEquals("  0   1   2   3\n100 101 102 103\n200 201 202 203\n", table1.toString());
        assertEquals(" 1 -1  1 -1\n-1  1 -1  1\n 1 -1  1 -1\n", table2.toString());
        assertEquals("   0   -1    2   -3\n-100  101 -102  103\n 200 -201  202 -203\n", table3.toString());

        table3.set(1, 1, 424242);
        assertEquals("     0     -1      2     -3\n  -100 424242   -102    103\n   200   -201    202   -203\n",
                table3.toString());

        assertEquals("  3   2   1   0\n103 102 101 100\n203 202 201 200\n",
                table1.mirrorHorizontally().toString());
        assertEquals("200 201 202 203\n100 101 102 103\n  0   1   2   3\n",
                table1.mirrorVertically().toString());
        assertEquals("200 100   0\n201 101   1\n202 102   2\n203 103   3\n", table1.rotateRight().toString());
        assertEquals("  3 103 203\n  2 102 202\n  1 101 201\n  0 100 200\n", table1.rotateLeft().toString());
        assertEquals("  0 100 200\n  1 101 201\n  2 102 202\n  3 103 203\n", table1.transpose().toString());
    }

    private static void assertContentEquals(int[][] expected, IntTable table) {
        assertTrue(Arrays.deepEquals(expected, table.asMatrix()));
    }

    @Override
    IntTable createTestTable(int width, int height) {
        return new IntTable(width, height, p -> p.y * 100 + p.x);
    }

}
