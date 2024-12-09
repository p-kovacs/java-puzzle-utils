package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CharTableTest extends AbstractTableTest<Character> {

    @Test
    void testConstructors() {
        var t1 = new CharTable(2, 3, 'a');
        var t2 = new CharTable(t1);

        assertEquals(t1, t2);
        assertNotEquals(t1.asMatrix(), t2.asMatrix());
        assertTrue(Arrays.deepEquals(t1.asMatrix(), t2.asMatrix()));

        t2.set(1, 2, '@');

        assertEquals('a', t1.get(1, 1));
        assertEquals('a', t1.get(1, 2));
        assertEquals('a', t2.get(1, 1));
        assertEquals('@', t2.get(1, 2));

        t2.asMatrix()[2][1] = '$';
        assertEquals('$', t2.get(1, 2));

        var t3 = new CharTable(t2.asMatrix());
        t3.set(0, 0, 'x');
        t2.set(0, 1, 'y');

        assertEquals('a', t1.get(0, 0));
        assertEquals('a', t2.get(0, 0));
        assertEquals('x', t3.get(0, 0));
        assertEquals('a', t1.get(0, 1));
        assertEquals('y', t2.get(0, 1));
        assertEquals('a', t3.get(0, 1));
    }

    @Test
    void testWrapMethods() {
        var cells = List.of(p(12, 12), p(13, 13), p(11, 11), p(11, 14));
        assertEquals(new CharTable(List.of("#  ", " # ", "  #", "#  ")), CharTable.wrap(cells, '#', ' '));

        var table1 = new CharTable(List.of("123", "abc", "def", "xyz"));
        var table2 = new CharTable(List.of(".2.", "a.c", ".e.", "x.z"));
        var map = table1.cells().filter(p -> (p.x + p.y) % 2 == 1)
                .collect(Collectors.toMap(p -> p, table1::get));
        assertEquals(table2, CharTable.wrap(map, '.'));
    }

    @Test
    void testGettersAndSetters() {
        var table = new CharTable(4, 3, ' ');

        assertEquals(4, table.width());
        assertEquals(3, table.height());
        assertEquals(4, table.colCount());
        assertEquals(3, table.rowCount());

        assertContentEquals(List.of("    ", "    ", "    "), table);

        var table2 = createTestTable(4, 3);
        table.cells().forEach(p -> table.set(p, table2.get(p))); // deliberately copied this way to test get-set

        assertContentEquals(List.of("0123", "abcd", "ABCD"), table);

        table.cells().forEach(p -> table.update(p, c -> (char) (c + 1)));
        table.set(0, 0, '#');
        table.set(p(2, 2), '@');

        assertContentEquals(List.of("#234", "bcde", "BC@E"), table);
        assertEquals(1, table.count('@'));
        assertEquals(0, table.count('x'));

        table.fill('x');

        assertContentEquals(List.of("xxxx", "xxxx", "xxxx"), table);
        assertEquals(0, table.count('@'));
        assertEquals(12, table.count('x'));

        assertContentEquals(List.of("123", "abc", "def", "xyz"), new CharTable(List.of("123", "abc", "def", "xyz")));
    }

    @Test
    void testWrappedMatrix() {
        var matrix = new char[][] { { '0', '1', '2', '3' }, { 'a', 'b', 'c', 'd' }, { 'A', 'B', 'C', 'D' } };
        var table = new CharTable(matrix);

        assertContentEquals(List.of("0123", "abcd", "ABCD"), table);

        matrix[0][0] = '#';
        table.set(2, 2, '@');

        assertContentEquals(List.of("0123", "abcd", "AB@D"), table);

        assertThrows(IllegalArgumentException.class,
                () -> new CharTable(new char[][] { { '0', '1' }, { 'a', 'b' }, { 'A', 'B', 'C' } }));
    }

    @Test
    void testStreamMethods() {
        var table = createTestTable(4, 3);

        assertEquals(table.values().toList(), table.cells().map(table::get).toList());
        assertEquals(table.rowValues(1).toList(), table.row(1).map(table::get).toList());
        assertEquals(table.colValues(2).toList(), table.col(2).map(table::get).toList());
    }

    @Test
    void testCharTableRays() {
        var pos = p(5, 3);

        var rookTable = new CharTable(8, 8, '.');
        rookTable.set(pos, 'R');
        List<Stream<Pos>> rays = rookTable.neighbors(pos)
                .map(other -> rookTable.ray(pos, other))
                .toList();
        for (int i = 0; i < rays.size(); i++) {
            char ch = (char) ('1' + i);
            rays.get(i).forEach(p -> rookTable.set(p, ch));
        }

        assertContentEquals(List.of(".....2..", ".....2..", ".....2..", "11111R44",
                ".....3..", ".....3..", ".....3..", ".....3.."), rookTable);

        var queenTable = new CharTable(8, 8, '.');
        queenTable.set(pos, 'Q');
        rays = queenTable.neighbors8(pos)
                .map(other -> queenTable.ray(pos, other))
                .toList();
        for (int i = 0; i < rays.size(); i++) {
            char ch = (char) ('1' + i);
            rays.get(i).forEach(p -> queenTable.set(p, ch));
        }

        assertContentEquals(List.of("..1..4..", "...1.4.6", "....146.", "22222Q77",
                "....358.", "...3.5.8", "..3..5..", ".3...5.."), queenTable);
    }

    @Test
    void testFindMethods() {
        var table = new CharTable(List.of("123", "abc", "123", "xyz"));

        assertEquals(p(2, 1), table.find('c'));
        assertEquals(p(1, 0), table.find('2'));
        assertThrows(NoSuchElementException.class, () -> table.find('X'));

        assertEquals(List.of(p(2, 1)), table.findAll('c').toList());
        assertEquals(List.of(p(1, 0), p(1, 2)), table.findAll('2').toList());
        assertEquals(List.of(), table.findAll('X').toList());
    }

    @Test
    void testToString() {
        var table = createTestTable(4, 3);
        assertEquals("0123\nabcd\nABCD\n", table.toString());
    }

    private static void assertContentEquals(List<String> expected, CharTable table) {
        var array = new char[expected.size()][];
        for (int i = 0; i < array.length; i++) {
            array[i] = expected.get(i).toCharArray();
        }
        assertTrue(Arrays.deepEquals(array, table.asMatrix()));
    }

    @Override
    CharTable createTestTable(int width, int height) {
        var table = new CharTable(width, height, p -> switch (p.y % 3) {
            case 0 -> (char) ('0' + p.x);
            case 1 -> (char) ('a' + p.x);
            default -> (char) ('A' + p.x);
        });
        return table;
    }

}
