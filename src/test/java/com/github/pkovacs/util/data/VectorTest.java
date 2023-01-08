package com.github.pkovacs.util.data;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VectorTest {

    @Test
    void test2d() {
        var a = Vector.origin(2);
        var b = new Vector(42, 12);

        assertEquals(b.x(), 42);
        assertEquals(b.y(), 12);
        assertEquals(b, a.add(b));

        a = a.add(b).subtract(new Vector(2, 2));
        assertEquals(new Vector(40, 10), a);
        assertEquals(50, a.dist1());
        assertEquals(new Vector(-40, -10), a.opposite());
        assertEquals(50, a.opposite().dist1());

        var c = new Vector(42, 12);
        var d = new Vector(12, -42);
        assertEquals(54 + 30, c.dist1(d));
        assertEquals(54, c.distMax(d));
        assertEquals(54 * 54 + 30 * 30, c.distSq(d));
        assertEquals(Math.sqrt(54 * 54 + 30 * 30), c.dist2(d), 1e-10);
        c = new Vector(-12, 42);
        assertEquals(c.dist1() + d.dist1(), c.dist1(d));
        assertEquals(c.distMax() + d.distMax(), c.distMax(d));
        assertNotEquals(c.distSq() + d.distSq(), c.distSq(d)); // does not satisfy the triangle inequality
        assertEquals(c.dist2() + d.dist2(), c.dist2(d), 1e-10);
        c = c.opposite();
        assertEquals(0, c.dist1(d));
        assertEquals(0, c.distMax(d));
        assertEquals(0, c.distSq(d));
        assertEquals(0, c.dist2(d), 1e-10);

        var e = new Vector(42, 12);
        assertEquals(new Vector(0, 0), e.multiply(0));
        assertEquals(e, e.multiply(1));
        assertEquals(e.add(e), e.multiply(2));
        assertEquals(e.add(e).add(e).add(e).add(e), e.multiply(5));

        assertEquals("(42, 12)", new Vector(42, 12).toString());
        assertEquals("(42, -12)", new Vector(42, -12).toString());
    }

    @Test
    void test3d() {
        var a = Vector.origin(3);
        var b = new Vector(42, 12, 314);

        assertEquals(3, a.dim());
        assertEquals(3, b.dim());

        assertEquals(b.x(), 42);
        assertEquals(b.y(), 12);
        assertEquals(b.z(), 314);
        assertEquals(b, a.add(b));

        a = a.add(b).subtract(new Vector(2, 2, 14));
        assertEquals(new Vector(40, 10, 300), a);
        assertEquals(new Vector(-40, -10, -300), a.opposite());

        var c = new Vector(42, 12, -3);
        assertEquals(Vector.origin(c.dim()), c.multiply(0));
        assertEquals(c, c.multiply(1));
        assertEquals(c.add(c), c.multiply(2));
        assertEquals(c.add(c).add(c).add(c).add(c), c.multiply(5));
        assertEquals(c.add(c.multiply(7)).subtract(c.multiply(4)), c.multiply(4));

        assertEquals(42 + 12 + 3, c.dist1());
        assertEquals(42, c.distMax());
        assertEquals(42 * 42 + 12 * 12 + 3 * 3, c.distSq());
        assertEquals(Math.sqrt(42 * 42 + 12 * 12 + 3 * 3), c.dist2(), 1e-10);

        assertEquals(c.dist1(), c.opposite().dist1());
        assertEquals(c.distMax(), c.opposite().distMax());
        assertEquals(c.distSq(), c.opposite().distSq());
        assertEquals(c.dist2(), c.opposite().dist2(), 1e-10);

        assertEquals(c.dist1() * 5, c.opposite().dist1(c.multiply(4)));
        assertEquals(c.distMax() * 5, c.opposite().distMax(c.multiply(4)));
        assertNotEquals(c.distSq() * 5, c.opposite().distSq(c.multiply(4))); // does not satisfy the triangle inequality
        assertEquals(c.dist2() * 5, c.opposite().dist2(c.multiply(4)), 1e-10);

        assertEquals("(42, 12, -3)", c.toString());
    }

    @Test
    void testGeneral() {
        var a = Vector.origin(10);
        var b = new Vector(1, -2, 3, -4, 5, -6, 7, -8, 9, -10);
        var c = new Vector(1, 2, 3, 4, 5, 6, 7, 8);

        assertEquals(10, a.dim());
        assertEquals(10, b.dim());

        assertEquals(0, a.dist1());
        assertEquals(0, a.distMax());
        assertEquals(0, a.distSq());
        assertEquals(0, a.dist2(), 1e-10);

        assertEquals(55, b.dist1());
        assertEquals(10, b.distMax());
        assertEquals(1 + 4 + 9 + 16 + 25 + 36 + 49 + 64 + 81 + 100, b.distSq());
        assertEquals(Math.sqrt(1 + 4 + 9 + 16 + 25 + 36 + 49 + 64 + 81 + 100), b.dist2(), 1e-10);

        assertThrows(IllegalArgumentException.class, () -> b.add(c));
        assertThrows(IllegalArgumentException.class, () -> c.subtract(b));
        assertThrows(IllegalArgumentException.class, () -> b.dist1(c));
        assertThrows(IllegalArgumentException.class, () -> b.distMax(c));
        assertThrows(IllegalArgumentException.class, () -> c.distSq(b));
        assertThrows(IllegalArgumentException.class, () -> c.dist2(b));

        assertEquals(b, a.add(b));
        assertEquals(a, b.subtract(b));

        assertEquals("(1, -2, 3, -4, 5, -6, 7, -8, 9, -10)", b.toString());
    }

    @Test
    void testOrdering() {
        var list = List.of(
                new Vector(42, 12),
                new Vector(41, 12),
                new Vector(42, 11),
                new Vector(42, 13),
                new Vector(43, 12),
                new Vector(42, 12, 3),
                new Vector(42, 12, 2),
                new Vector(42, 12, 1),
                new Vector(5, 8, 1, 0),
                new Vector(5, 8, 1, -1),
                new Vector(5, 8, 1, 1),
                new Vector(5, 8, 0, 0));
        var sortedList = List.of(
                new Vector(41, 12),
                new Vector(42, 11),
                new Vector(42, 12),
                new Vector(42, 13),
                new Vector(43, 12),
                new Vector(42, 12, 1),
                new Vector(42, 12, 2),
                new Vector(42, 12, 3),
                new Vector(5, 8, 0, 0),
                new Vector(5, 8, 1, -1),
                new Vector(5, 8, 1, 0),
                new Vector(5, 8, 1, 1));

        assertEquals(sortedList, list.stream().sorted().toList());
    }

}
