package com.github.pkovacs.util.data;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VectorTest {

    @Test
    void testBasicMethods() {
        var a = Vector.ORIGIN;
        var b = v(42, 12, 314);

        assertEquals(42, b.x);
        assertEquals(12, b.y);
        assertEquals(314, b.z);
        assertEquals(42, b.x());
        assertEquals(12, b.y());
        assertEquals(314, b.z());

        assertEquals(b, new Vector(b.x, b.y, b.z));
        assertNotEquals(b, new Vector(42, 12, 23));

        assertEquals(v(100, 12, 314), b.withX(100));
        assertEquals(v(42, 2000, 314), b.withY(2000));
        assertEquals(v(42, 12, 30000), b.withZ(30000));

        assertEquals("(0,0,0)", Vector.ORIGIN.toString());
        assertEquals("(42,12,314)", b.toString());
        assertEquals("(42,12,-3)", v(42, 12, -3).toString());
    }

    @Test
    void testNeighborMethods() {
        var a = v(42, 12, 5);

        assertEquals(List.of(
                        v(41, 12, 5),
                        v(42, 11, 5),
                        v(42, 12, 4),
                        v(42, 12, 5),
                        v(42, 12, 6),
                        v(42, 13, 5),
                        v(43, 12, 5)),
                a.neighborsAndSelf().toList());
        assertEquals(6, a.neighbors().count());
        assertEquals(6, a.neighbors().distinct().count());
        assertEquals(7, a.neighborsAndSelf().count());
        assertEquals(7, a.neighborsAndSelf().distinct().count());
        assertTrue(a.neighbors().allMatch(v -> v.dist1(a) == 1));
        assertTrue(a.neighborsAndSelf().allMatch(v -> v.dist1(a) <= 1));
        assertTrue(a.neighborsAndSelf().allMatch(v -> v.dist1(a) == 1 || v == a));
        assertTrue(a.neighbors().allMatch(a::isNeighbor));
        assertTrue(a.neighbors().allMatch(a::isExtendedNeighbor));
        assertEquals(6, a.neighborsAndSelf().filter(a::isNeighbor).count());
        assertEquals(6, a.neighborsAndSelf().filter(a::isExtendedNeighbor).count());
        assertTrue(a.neighborsAndSelf().allMatch(v -> v == a || v.isExtendedNeighbor(a)));
        assertEquals(a.neighbors().sorted().toList(), a.neighbors().toList());
        assertEquals(a.neighborsAndSelf().sorted().toList(), a.neighborsAndSelf().toList());

        assertEquals(26, a.extendedNeighbors().count());
        assertEquals(26, a.extendedNeighbors().distinct().count());
        assertEquals(27, a.extendedNeighborsAndSelf().count());
        assertEquals(27, a.extendedNeighborsAndSelf().distinct().count());
        assertTrue(a.extendedNeighbors().allMatch(v -> v.distMax(a) == 1));
        assertTrue(a.extendedNeighborsAndSelf().allMatch(v -> v.distMax(a) <= 1));
        assertTrue(a.extendedNeighborsAndSelf().allMatch(v -> v.distMax(a) == 1 || v == a));
        assertEquals(6, a.extendedNeighbors().filter(a::isNeighbor).count());
        assertEquals(26, a.extendedNeighbors().filter(a::isExtendedNeighbor).count());
        assertTrue(a.extendedNeighbors().allMatch(a::isExtendedNeighbor));
        assertEquals(6, a.extendedNeighborsAndSelf().filter(a::isNeighbor).count());
        assertEquals(26, a.extendedNeighborsAndSelf().filter(a::isExtendedNeighbor).count());
        assertTrue(a.extendedNeighborsAndSelf().allMatch(v -> v == a || v.isExtendedNeighbor(a)));
        assertEquals(a.extendedNeighbors().sorted().toList(), a.extendedNeighbors().toList());
        assertEquals(a.extendedNeighborsAndSelf().sorted().toList(), a.extendedNeighborsAndSelf().toList());
    }

    @Test
    void testOperations() {
        var a = Vector.ORIGIN;
        var b = v(42, 12, 314);

        assertEquals(b, a.plus(b));
        assertEquals(v(44, 15, 214), b.plus(v(2, 3, -100)));

        a = a.plus(b).minus(v(2, 2, 14));
        assertEquals(v(40, 10, 300), a);
        assertEquals(v(-40, -10, -300), a.opposite());

        var c = v(42, 12, -3);
        assertEquals(Vector.ORIGIN, c.multiply(0));
        assertEquals(c, c.multiply(1));
        assertEquals(c.plus(c), c.multiply(2));
        assertEquals(c.plus(c).plus(c).plus(c).plus(c), c.multiply(5));
        assertEquals(c.plus(c.multiply(7)).minus(c.multiply(4)), c.multiply(4));
    }

    @Test
    void testDistanceMethods() {
        var a = v(42, 12, -3);

        assertEquals(42 + 12 + 3, a.dist1());
        assertEquals(42, a.distMax());
        assertEquals(42 * 42 + 12 * 12 + 3 * 3, a.distSq());
        assertEquals(Math.sqrt(42 * 42 + 12 * 12 + 3 * 3), a.dist2(), 1e-10);

        assertEquals(a.dist1(), a.opposite().dist1());
        assertEquals(a.distMax(), a.opposite().distMax());
        assertEquals(a.distSq(), a.opposite().distSq());
        assertEquals(a.dist2(), a.opposite().dist2(), 1e-10);

        assertEquals(a.dist1() * 5, a.opposite().dist1(a.multiply(4)));
        assertEquals(a.distMax() * 5, a.opposite().distMax(a.multiply(4)));
        assertNotEquals(a.distSq() * 5, a.opposite().distSq(a.multiply(4))); // does not satisfy the triangle inequality
        assertEquals(a.dist2() * 5, a.opposite().dist2(a.multiply(4)), 1e-10);
    }

    @Test
    void testOrdering() {
        var list = List.of(
                v(42, 12, 3),
                v(42, 12, 2),
                v(42, 12, 1),
                v(30, 15, 100),
                v(30, 12, 200),
                v(30, 10, 300)
        );
        var sortedList = List.of(
                v(30, 10, 300),
                v(30, 12, 200),
                v(30, 15, 100),
                v(42, 12, 1),
                v(42, 12, 2),
                v(42, 12, 3)
        );

        assertEquals(sortedList, list.stream().sorted().toList());
    }

    private static Vector v(long x, long y, long z) {
        return new Vector(x, y, z);
    }

}
