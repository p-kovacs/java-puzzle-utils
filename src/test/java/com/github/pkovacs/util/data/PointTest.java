package com.github.pkovacs.util.data;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointTest {

    @Test
    void testBasicMethods() {
        var a = new Point(42, 12);
        var b = new Point(12, 42);
        var c = new Point(12, 42);

        assertEquals(42, a.x());
        assertEquals(12, a.y());
        assertNotEquals(a, b);
        assertEquals(b, c);

        assertTrue(a.isValid(43, 13));
        assertFalse(a.isValid(43, 12));
        assertFalse(a.isValid(42, 13));

        assertEquals(54, a.dist());
        assertEquals(60, a.dist(b));
        assertEquals(0, b.dist(c));
    }

    @Test
    void testNeighborMethods() {
        var a = new Point(42, 12);

        assertEquals(List.of(
                        new Point(42, 11),
                        new Point(43, 12),
                        new Point(42, 13),
                        new Point(41, 12)),
                a.neighbors().toList());
        assertEquals(List.of(new Point(42, 11), new Point(41, 12)),
                a.neighbors().filter(p -> p.x() <= a.x() && p.y() <= a.y()).toList());

        assertEquals(new Point(42, 11), a.neighbor(Direction.NORTH));
        assertEquals(new Point(43, 12), a.neighbor(Direction.EAST));
        assertEquals(new Point(42, 13), a.neighbor(Direction.SOUTH));
        assertEquals(new Point(41, 12), a.neighbor(Direction.WEST));

        assertEquals(new Point(42, 11), a.neighbor('n'));
        assertEquals(new Point(43, 12), a.neighbor('E'));
        assertEquals(new Point(42, 13), a.neighbor('s'));
        assertEquals(new Point(41, 12), a.neighbor('W'));

        assertEquals(new Point(42, 11), a.neighbor('u'));
        assertEquals(new Point(43, 12), a.neighbor('R'));
        assertEquals(new Point(42, 13), a.neighbor('d'));
        assertEquals(new Point(41, 12), a.neighbor('L'));

        assertEquals(new Point(42, 13), a.neighborWithUpwardY(Direction.NORTH));
        assertEquals(new Point(43, 12), a.neighborWithUpwardY(Direction.EAST));
        assertEquals(new Point(42, 11), a.neighborWithUpwardY(Direction.SOUTH));
        assertEquals(new Point(41, 12), a.neighborWithUpwardY(Direction.WEST));

        assertEquals(new Point(42, 13), a.neighborWithUpwardY('n'));
        assertEquals(new Point(43, 12), a.neighborWithUpwardY('E'));
        assertEquals(new Point(42, 11), a.neighborWithUpwardY('s'));
        assertEquals(new Point(41, 12), a.neighborWithUpwardY('W'));

        assertEquals(new Point(42, 13), a.neighborWithUpwardY('u'));
        assertEquals(new Point(43, 12), a.neighborWithUpwardY('R'));
        assertEquals(new Point(42, 11), a.neighborWithUpwardY('d'));
        assertEquals(new Point(41, 12), a.neighborWithUpwardY('L'));

        assertEquals(List.of(
                        new Point(42, 11),
                        new Point(43, 11),
                        new Point(43, 12),
                        new Point(43, 13),
                        new Point(42, 13),
                        new Point(41, 13),
                        new Point(41, 12),
                        new Point(41, 11)),
                a.extendedNeighbors().toList());

        assertTrue(a.neighbors().allMatch(a::isNeighbor));
        assertTrue(a.neighbors().allMatch(a::isExtendedNeighbor));
        assertEquals(4, a.extendedNeighbors().filter(a::isNeighbor).count());
        assertEquals(8, a.extendedNeighbors().filter(a::isExtendedNeighbor).count());

        assertTrue(a.neighbors().allMatch(p -> p.dist(a) == 1));
        assertTrue(a.neighbors().mapToInt(a::dist).allMatch(d -> d == 1));

        assertEquals(4, a.validNeighbors(44, 14).count());
        assertEquals(2, a.validNeighbors(43, 13).count());
        assertEquals(1, a.validNeighbors(43, 12).count());
        assertEquals(1, a.validNeighbors(42, 13).count());
        assertEquals(0, a.validNeighbors(42, 12).count());

        assertTrue(a.extendedNeighbors().allMatch(n -> a.dist(n) <= 2));
        assertEquals(12, a.extendedNeighbors().mapToInt(a::dist).sum());
    }

    @Test
    void testToString() {
        assertEquals("(12, 42)", new Point(12, 42).toString());
        assertEquals("(-3, -5)", new Point(-3, -5).toString());
    }

    @Test
    void testOrdering() {
        var a = new Point(42, 12);
        var sortedNeighbors = List.of(
                new Point(41, 12),
                new Point(42, 11),
                new Point(42, 13),
                new Point(43, 12));

        assertNotEquals(sortedNeighbors, a.neighbors().toList());
        assertEquals(sortedNeighbors, a.neighbors().sorted().toList());
    }

    @Test
    void testStreamMethods() {
        assertEquals(List.of(
                        new Point(0, 0), new Point(0, 1), new Point(0, 2),
                        new Point(1, 0), new Point(1, 1), new Point(1, 2)),
                Point.stream(2, 3).toList());
        assertEquals(List.of(
                        new Point(0, 0), new Point(0, 1),
                        new Point(1, 0), new Point(1, 1),
                        new Point(2, 0), new Point(2, 1)),
                Point.stream(3, 2).toList());
        assertEquals(List.of(
                        new Point(42, 10), new Point(42, 11),
                        new Point(43, 10), new Point(43, 11),
                        new Point(44, 10), new Point(44, 11)),
                Point.stream(42, 10, 45, 12).toList());
    }

}
