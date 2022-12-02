package com.github.pkovacs.util.data;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TileTest {

    @Test
    void testBasicMethods() {
        var a = new Tile(12, 42);
        var b = new Tile(42, 12);
        var c = new Tile(42, 12);

        assertEquals(12, a.row());
        assertEquals(42, a.col());
        assertNotEquals(a, b);
        assertEquals(b, c);

        assertTrue(a.isValid(13, 43));
        assertFalse(a.isValid(12, 43));
        assertFalse(a.isValid(13, 42));

        assertEquals(60, a.dist(b));
        assertEquals(0, b.dist(c));
    }

    @Test
    void testNeighborMethods() {
        var a = new Tile(12, 42);

        assertEquals(List.of(
                        new Tile(11, 42),
                        new Tile(12, 43),
                        new Tile(13, 42),
                        new Tile(12, 41)),
                a.neighbors().toList());
        assertEquals(List.of(new Tile(11, 42), new Tile(12, 41)),
                a.neighbors().filter(n1 -> n1.row() <= a.row() && n1.col() <= a.col()).toList());

        assertEquals(new Tile(11, 42), a.neighbor(Direction.NORTH));
        assertEquals(new Tile(12, 43), a.neighbor(Direction.EAST));
        assertEquals(new Tile(13, 42), a.neighbor(Direction.SOUTH));
        assertEquals(new Tile(12, 41), a.neighbor(Direction.WEST));

        assertEquals(new Tile(11, 42), a.neighbor('n'));
        assertEquals(new Tile(12, 43), a.neighbor('E'));
        assertEquals(new Tile(13, 42), a.neighbor('s'));
        assertEquals(new Tile(12, 41), a.neighbor('W'));

        assertEquals(new Tile(11, 42), a.neighbor('u'));
        assertEquals(new Tile(12, 43), a.neighbor('R'));
        assertEquals(new Tile(13, 42), a.neighbor('d'));
        assertEquals(new Tile(12, 41), a.neighbor('L'));

        assertEquals(List.of(
                        new Tile(11, 42),
                        new Tile(11, 43),
                        new Tile(12, 43),
                        new Tile(13, 43),
                        new Tile(13, 42),
                        new Tile(13, 41),
                        new Tile(12, 41),
                        new Tile(11, 41)),
                a.extendedNeighbors().toList());

        assertTrue(a.neighbors().allMatch(n -> Tile.dist(a, n) == 1));
        assertTrue(a.neighbors().mapToInt(a::dist).allMatch(d -> d == 1));
        assertEquals(4, a.validNeighbors(14, 44).count());
        assertEquals(2, a.validNeighbors(13, 43).count());
        assertEquals(1, a.validNeighbors(12, 43).count());
        assertEquals(1, a.validNeighbors(13, 42).count());
        assertEquals(0, a.validNeighbors(12, 42).count());

        assertTrue(a.extendedNeighbors().allMatch(n -> a.dist(n) <= 2));
        assertEquals(12, a.extendedNeighbors().mapToInt(a::dist).sum());
    }

    @Test
    void testStreamMethods() {
        assertEquals(List.of(
                        new Tile(0, 0), new Tile(0, 1), new Tile(0, 2),
                        new Tile(1, 0), new Tile(1, 1), new Tile(1, 2)),
                Tile.stream(2, 3).toList());
        assertEquals(List.of(
                        new Tile(0, 0), new Tile(0, 1),
                        new Tile(1, 0), new Tile(1, 1),
                        new Tile(2, 0), new Tile(2, 1)),
                Tile.stream(3, 2).toList());
        assertEquals(List.of(
                        new Tile(42, 10), new Tile(42, 11),
                        new Tile(43, 10), new Tile(43, 11),
                        new Tile(44, 10), new Tile(44, 11)),
                Tile.stream(42, 10, 45, 12).toList());
    }

    @Test
    void testOrdering() {
        var a = new Tile(12, 42);
        var sortedNeighbors = List.of(
                new Tile(11, 42),
                new Tile(12, 41),
                new Tile(12, 43),
                new Tile(13, 42));

        assertNotEquals(sortedNeighbors, a.neighbors().toList());
        assertEquals(sortedNeighbors, a.neighbors().sorted().toList());
    }

}
