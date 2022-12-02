package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableTest extends AbstractTableTest<String> {

    @Test
    void testGettersAndSetters() {
        var table = new Table<String>(3, 4);

        assertEquals(3, table.rowCount());
        assertEquals(4, table.colCount());

        assertContentEquals(List.of(
                Arrays.asList(new String[4]),
                Arrays.asList(new String[4]),
                Arrays.asList(new String[4])), table);

        table.cells().forEach(t -> table.set(t, String.valueOf((char) ('A' + t.row())) + (t.col() + 1)));

        assertContentEquals(List.of(
                List.of("A1", "A2", "A3", "A4"),
                List.of("B1", "B2", "B3", "B4"),
                List.of("C1", "C2", "C3", "C4")), table);

        table.cells().forEach(t -> table.update(t, x -> x + "!"));
        table.set(0, 0, "xyz");
        table.set(new Tile(2, 2), "abc");

        assertContentEquals(List.of(
                List.of("xyz", "A2!", "A3!", "A4!"),
                List.of("B1!", "B2!", "B3!", "B4!"),
                List.of("C1!", "C2!", "abc", "C4!")), table);

        table.fill("A");

        assertContentEquals(List.of(
                List.of("A", "A", "A", "A"),
                List.of("A", "A", "A", "A"),
                List.of("A", "A", "A", "A")), table);
    }

    @Test
    void testStreamMethods() {
        var table = createTestTable(3, 4);

        assertEquals(table.values().toList(), table.cells().map(table::get).toList());
        assertEquals(table.rowValues(1).toList(), table.row(1).map(table::get).toList());
        assertEquals(table.colValues(2).toList(), table.col(2).map(table::get).toList());
    }

    @Test
    void testToString() {
        var table = createTestTable(3, 4);

        assertEquals("A1 A2 A3 A4\nB1 B2 B3 B4\nC1 C2 C3 C4\n", table.toString());
    }

    private static void assertContentEquals(List<List<String>> expected, Table<String> table) {
        var actual = IntStream.range(0, table.rowCount())
                .mapToObj(table::rowValues)
                .map(Stream::toList)
                .toList();
        assertEquals(expected, actual);
    }

    @Override
    Table<String> createTestTable(int rowCount, int colCount) {
        return new Table<>(rowCount, colCount, (r, c) -> String.valueOf((char) ('A' + r)) + (c + 1));
    }

}
