package com.github.pkovacs.util.data;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CellTest {

    @Test
    void testBasicMethods() {
        var a = new Cell(12, 42);
        var b = new Cell(42, 12);
        var c = new Cell(42, 12);

        assertEquals(12, a.row());
        assertEquals(42, a.col());
        assertNotEquals(a, b);
        assertEquals(b, c);

        assertTrue(a.isValid(13, 43));
        assertFalse(a.isValid(12, 43));
        assertFalse(a.isValid(13, 42));

        assertEquals(new Cell(20, 30), a.add(8, -12));
        assertEquals(new Cell(84, 24), b.add(c.row(), c.col()));
    }

    @Test
    void testDistances() {
        var a = new Cell(12, 42);
        var b = new Cell(30, 30);

        assertEquals(54, a.dist1());
        assertEquals(30, a.dist1(b));
        assertEquals(0, a.dist1(a));

        assertEquals(42, a.distMax());
        assertEquals(18, a.distMax(b));
        assertEquals(0, a.distMax(a));

        assertEquals(12 * 12 + 42 * 42, a.distSq());
        assertEquals(12 * 12 + 18 * 18, a.distSq(b));
        assertEquals(0, a.distSq(a));

        assertEquals(Math.sqrt(12 * 12 + 42 * 42), a.dist2(), 1e-10);
        assertEquals(Math.sqrt(12 * 12 + 18 * 18), a.dist2(b), 1e-10);
        assertEquals(0, a.dist2(a), 1e-10);
    }

    @Test
    void testNeighborMethods() {
        var a = new Cell(12, 42);

        assertEquals(List.of(
                        new Cell(11, 42),
                        new Cell(12, 43),
                        new Cell(13, 42),
                        new Cell(12, 41)),
                a.neighbors().toList());
        assertEquals(List.of(new Cell(11, 42), new Cell(12, 41)),
                a.neighbors().filter(n1 -> n1.row() <= a.row() && n1.col() <= a.col()).toList());

        assertEquals(new Cell(11, 42), a.neighbor(Direction.NORTH));
        assertEquals(new Cell(12, 43), a.neighbor(Direction.EAST));
        assertEquals(new Cell(13, 42), a.neighbor(Direction.SOUTH));
        assertEquals(new Cell(12, 41), a.neighbor(Direction.WEST));

        assertEquals(new Cell(11, 42), a.neighbor('n'));
        assertEquals(new Cell(12, 43), a.neighbor('E'));
        assertEquals(new Cell(13, 42), a.neighbor('s'));
        assertEquals(new Cell(12, 41), a.neighbor('W'));

        assertEquals(new Cell(11, 42), a.neighbor('u'));
        assertEquals(new Cell(12, 43), a.neighbor('R'));
        assertEquals(new Cell(13, 42), a.neighbor('d'));
        assertEquals(new Cell(12, 41), a.neighbor('L'));

        assertEquals(List.of(
                        new Cell(11, 42),
                        new Cell(11, 43),
                        new Cell(12, 43),
                        new Cell(13, 43),
                        new Cell(13, 42),
                        new Cell(13, 41),
                        new Cell(12, 41),
                        new Cell(11, 41)),
                a.extendedNeighbors().toList());

        assertTrue(a.neighbors().allMatch(a::isNeighbor));
        assertTrue(a.neighbors().allMatch(a::isExtendedNeighbor));
        assertEquals(4, a.extendedNeighbors().filter(a::isNeighbor).count());
        assertEquals(8, a.extendedNeighbors().filter(a::isExtendedNeighbor).count());

        assertTrue(a.neighbors().mapToInt(a::dist1).allMatch(d -> d == 1));
        assertTrue(a.neighbors().mapToInt(a::distMax).allMatch(d -> d == 1));
        assertTrue(a.neighbors().mapToInt(a::distSq).allMatch(d -> d == 1));

        assertEquals(4, a.validNeighbors(14, 44).count());
        assertEquals(2, a.validNeighbors(13, 43).count());
        assertEquals(1, a.validNeighbors(12, 43).count());
        assertEquals(1, a.validNeighbors(13, 42).count());
        assertEquals(0, a.validNeighbors(12, 42).count());

        assertTrue(a.extendedNeighbors().mapToInt(a::dist1).allMatch(d -> d <= 2));
        assertTrue(a.extendedNeighbors().mapToInt(a::distMax).allMatch(d -> d == 1));
        assertTrue(a.extendedNeighbors().mapToInt(a::distSq).allMatch(d -> d <= 2));
        assertEquals(12, a.extendedNeighbors().mapToInt(a::dist1).sum());
        assertEquals(12, a.extendedNeighbors().mapToInt(a::distSq).sum());
    }

    @Test
    void testToString() {
        assertEquals("(12, 42)", new Cell(12, 42).toString());
        assertEquals("(-3, -5)", new Cell(-3, -5).toString());
    }

    @Test
    void testOrdering() {
        var a = new Cell(12, 42);
        var sortedNeighbors = List.of(
                new Cell(11, 42),
                new Cell(12, 41),
                new Cell(12, 43),
                new Cell(13, 42));

        assertNotEquals(sortedNeighbors, a.neighbors().toList());
        assertEquals(sortedNeighbors, a.neighbors().sorted().toList());
    }

    @Test
    void testBoxMethods() {
        assertEquals(List.of(), Cell.box(0, 3).toList());
        assertEquals(List.of(), Cell.box(1, -1).toList());
        assertEquals(List.of(Cell.ORIGIN), Cell.box(1, 1).toList());
        assertEquals(List.of(
                        new Cell(0, 0), new Cell(0, 1), new Cell(0, 2),
                        new Cell(1, 0), new Cell(1, 1), new Cell(1, 2)),
                Cell.box(2, 3).toList());

        assertEquals(List.of(),
                Cell.box(new Cell(40, 20), new Cell(40, 19)).toList());
        assertEquals(List.of(new Cell(40, 20), new Cell(41, 20), new Cell(42, 20)),
                Cell.box(new Cell(40, 20), new Cell(42, 20)).toList());
        assertEquals(List.of(
                        new Cell(42, 10), new Cell(42, 11),
                        new Cell(43, 10), new Cell(43, 11),
                        new Cell(44, 10), new Cell(44, 11)),
                Cell.box(new Cell(42, 10), new Cell(44, 11)).toList());
    }

}
