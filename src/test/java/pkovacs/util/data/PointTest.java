package pkovacs.util.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointTest {

    @Test
    void test() {
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

        assertEquals(60, a.dist(b));
        assertEquals(0, b.dist(c));
    }

    @Test
    void testNeighborMethods() {
        var a = new Point(42, 12);

        assertEquals(Set.of(
                        new Point(42, 11),
                        new Point(43, 12),
                        new Point(42, 13),
                        new Point(41, 12)),
                new HashSet<>(a.neighbors()));
        assertEquals(Set.of(new Point(42, 11), new Point(41, 12)),
                new HashSet<>(a.neighbors(n -> n.x() <= a.x() && n.y() <= a.y())));

        assertTrue(a.neighbors().stream().allMatch(n -> Point.dist(a, n) == 1));
        assertEquals(4, a.validNeighbors(44, 14).size());
        assertEquals(2, a.validNeighbors(43, 13).size());
        assertEquals(1, a.validNeighbors(43, 12).size());
        assertEquals(1, a.validNeighbors(42, 13).size());
        assertEquals(0, a.validNeighbors(42, 12).size());
    }

    @Test
    void testOrdering() {
        var a = new Point(42, 12);
        var sortedNeighbors = List.of(
                new Point(41, 12),
                new Point(42, 11),
                new Point(42, 13),
                new Point(43, 12));

        assertNotEquals(sortedNeighbors, a.neighbors());
        assertEquals(sortedNeighbors, a.neighbors().stream().sorted().toList());
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
