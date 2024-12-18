package com.github.pkovacs.util.data;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PosTest {

    @Test
    void testBasicMethods() {
        var a = p(42, 12);
        var b = p(12, 42);
        var c = p(12, 42);

        assertEquals(42, a.x);
        assertEquals(42, a.x());
        assertEquals(12, a.y);
        assertEquals(12, a.y());
        assertNotEquals(a, b);
        assertEquals(b, c);

        assertEquals("(12,42)", p(12, 42).toString());
        assertEquals("(-3,-5)", p(-3, -5).toString());

        assertEquals(p(23, 12), a.withX(23));
        assertEquals(p(42, 23), a.withY(23));
    }

    @Test
    void testNeighborMethods() {
        var a = p(42, 12);

        assertEquals(p(42, 11), a.neighbor(Dir.N));
        assertEquals(p(43, 12), a.neighbor(Dir.E));
        assertEquals(p(42, 13), a.neighbor(Dir.S));
        assertEquals(p(41, 12), a.neighbor(Dir.W));

        assertEquals(p(42, 11), a.neighbor('n'));
        assertEquals(p(43, 12), a.neighbor('E'));
        assertEquals(p(42, 13), a.neighbor('s'));
        assertEquals(p(41, 12), a.neighbor('W'));

        assertEquals(p(42, 11), a.neighbor('u'));
        assertEquals(p(43, 12), a.neighbor('R'));
        assertEquals(p(42, 13), a.neighbor('d'));
        assertEquals(p(41, 12), a.neighbor('L'));

        assertEquals(p(42, 11), a.neighbor8(Dir8.N));
        assertEquals(p(43, 11), a.neighbor8(Dir8.NE));
        assertEquals(p(43, 12), a.neighbor8(Dir8.E));
        assertEquals(p(43, 13), a.neighbor8(Dir8.SE));
        assertEquals(p(42, 13), a.neighbor8(Dir8.S));
        assertEquals(p(41, 13), a.neighbor8(Dir8.SW));
        assertEquals(p(41, 12), a.neighbor8(Dir8.W));
        assertEquals(p(41, 11), a.neighbor8(Dir8.NW));

        assertEquals(List.of(
                        p(41, 12),
                        p(42, 11),
                        p(42, 13),
                        p(43, 12)),
                a.neighbors().toList());
        assertEquals(List.of(
                        p(41, 12),
                        p(42, 11),
                        p(42, 12),
                        p(42, 13),
                        p(43, 12)),
                a.neighborsAndSelf().toList());
        assertEquals(a.neighbors().sorted().toList(), a.neighbors().toList());
        assertEquals(a.neighborsAndSelf().sorted().toList(), a.neighborsAndSelf().toList());

        assertEquals(List.of(
                        p(41, 11),
                        p(41, 12),
                        p(41, 13),
                        p(42, 11),
                        p(42, 13),
                        p(43, 11),
                        p(43, 12),
                        p(43, 13)),
                a.neighbors8().toList());
        assertEquals(List.of(
                        p(41, 11),
                        p(41, 12),
                        p(41, 13),
                        p(42, 11),
                        p(42, 12),
                        p(42, 13),
                        p(43, 11),
                        p(43, 12),
                        p(43, 13)),
                a.neighbors8AndSelf().toList());
        assertEquals(a.neighbors8().sorted().toList(), a.neighbors8().toList());
        assertEquals(a.neighbors8AndSelf().sorted().toList(), a.neighbors8AndSelf().toList());

        assertTrue(a.neighbors().allMatch(a::isNeighbor));
        assertTrue(a.neighbors().allMatch(a::isNeighbor8));
        assertEquals(4, a.neighbors8().filter(a::isNeighbor).count());

        assertTrue(a.neighbors().mapToLong(a::dist1).allMatch(d -> d == 1));
        assertTrue(a.neighbors().mapToLong(a::distMax).allMatch(d -> d == 1));

        assertTrue(a.neighbors8().mapToLong(a::dist1).allMatch(d -> d <= 2));
        assertTrue(a.neighbors8().mapToLong(a::distMax).allMatch(d -> d == 1));
        assertEquals(12, a.neighbors8().mapToLong(a::dist1).sum());
        assertEquals(8, a.neighbors8().mapToLong(a::distMax).sum());
    }

    @Test
    void testDirectionMethods() {
        var a = p(42, 12);

        assertEquals(Dir.N, a.dirTo(p(42, 10)));
        assertEquals(Dir.E, a.dirTo(p(50, 12)));
        assertEquals(Dir.S, a.dirTo(p(42, 20)));
        assertEquals(Dir.W, a.dirTo(p(0, 12)));
        assertThrows(IllegalArgumentException.class, () -> a.dirTo(a));
        assertThrows(IllegalArgumentException.class, () -> a.dirTo(Pos.ORIGIN));
        assertThrows(IllegalArgumentException.class, () -> a.dirTo(p(20, 0)));
        assertThrows(IllegalArgumentException.class, () -> a.dirTo(p(30, 0)));

        assertEquals(Dir8.N, a.dir8To(p(42, 10)));
        assertEquals(Dir8.NE, a.dir8To(p(44, 10)));
        assertEquals(Dir8.E, a.dir8To(p(50, 12)));
        assertEquals(Dir8.SE, a.dir8To(p(50, 20)));
        assertEquals(Dir8.S, a.dir8To(p(42, 20)));
        assertEquals(Dir8.SW, a.dir8To(p(30, 24)));
        assertEquals(Dir8.W, a.dir8To(p(0, 12)));
        assertEquals(Dir8.NW, a.dir8To(p(30, 0)));
        assertThrows(IllegalArgumentException.class, () -> a.dir8To(a));
        assertThrows(IllegalArgumentException.class, () -> a.dir8To(Pos.ORIGIN));
        assertThrows(IllegalArgumentException.class, () -> a.dirTo(p(20, 0)));

        for (var dir : Dir.values()) {
            assertEquals(dir, a.dirTo(a.neighbor(dir)));
            assertEquals(a.neighbor(dir), a.neighbor8(dir.toDir8()));
        }
        for (var dir : Dir8.values()) {
            assertEquals(dir, a.dir8To(a.neighbor8(dir)));
        }
    }

    @Test
    void testVectorMethods() {
        var a = p(42, 12);
        var b = p(10, 20);

        assertEquals(p(-42, -12), a.opposite());
        assertEquals(p(52, 32), a.plus(b));
        assertEquals(p(52, 32), a.plus((int) b.x, (int) b.y));
        assertEquals(p(52, 32), b.plus(a));
        assertEquals(p(52, 32), b.plus((int) a.x, (int) a.y));
        assertEquals(p(32, -8), a.minus(b));
        assertEquals(p(-32, 8), b.minus(a));
        assertEquals(b.plus(a.opposite()), b.minus(a));

        assertEquals(p(420, 120), a.multiply(10));
        assertEquals(p(-84, -24), a.multiply(-2));
        assertEquals(p(42_000_000_000_000L, 12_000_000_000_000L), a.multiply(1_000_000_000_000L));
    }

    @Test
    void testTransformations() {
        var a = p(40, 10);

        assertEquals(p(-40, -10), a.opposite());

        assertEquals(p(-10, 40), a.rotateLeft());
        assertEquals(p(-40, -10), a.rotateLeft().rotateLeft());
        assertEquals(p(10, -40), a.rotateLeft().rotateLeft().rotateLeft());
        assertEquals(p(40, 10), a.rotateLeft().rotateLeft().rotateLeft().rotateLeft());

        assertEquals(p(10, -40), a.rotateRight());
        assertEquals(p(-40, -10), a.rotateRight().rotateRight());
        assertEquals(p(-10, 40), a.rotateRight().rotateRight().rotateRight());
        assertEquals(p(40, 10), a.rotateRight().rotateRight().rotateRight().rotateRight());

        assertEquals(p(-40, 10), a.mirrorHorizontally());
        assertEquals(p(40, -10), a.mirrorVertically());
        assertEquals(a, a.mirrorHorizontally().mirrorHorizontally());
        assertEquals(a, a.mirrorVertically().mirrorVertically());
        assertEquals(a.opposite(), a.mirrorHorizontally().mirrorVertically());
        assertEquals(a.opposite(), a.mirrorVertically().mirrorHorizontally());

        assertEquals(a.opposite(), a.mirrorAcross(Pos.ORIGIN));
        assertEquals(p(20, 2), a.mirrorAcross(p(30, 6)));
        assertEquals(p(6, 36), a.mirrorAcross(p(23, 23)));
    }

    @Test
    void testLines() {
        assertEquals(List.of(p(10, 42), p(11, 42), p(12, 42)),
                p(10, 42).lineTo(p(12, 42)).toList());
        assertEquals(List.of(p(12, 40), p(12, 41), p(12, 42)),
                p(12, 40).lineTo(p(12, 42)).toList());
        assertEquals(List.of(p(12, 42), p(11, 42), p(10, 42), p(9, 42)),
                p(12, 42).lineTo(p(9, 42)).toList());
        assertEquals(List.of(p(12, 42), p(13, 43), p(14, 44)),
                p(12, 42).lineTo(p(14, 44)).toList());
        assertEquals(List.of(p(12, 42), p(11, 43), p(10, 44)),
                p(12, 42).lineTo(p(10, 44)).toList());
        assertEquals(List.of(p(12, 42)),
                p(12, 42).lineTo(p(12, 42)).toList());

        assertThrows(IllegalArgumentException.class, () -> p(12, 42).lineTo(p(10, 45)));
    }

    @Test
    void testRays() {
        var a = p(12, 42);

        assertEquals(List.of(p(12, 41), p(12, 40), p(12, 39)),
                a.ray(Dir.N).limit(3).toList());
        assertEquals(List.of(p(13, 42), p(14, 42), p(15, 42)),
                a.ray(Dir.E).limit(3).toList());
        assertEquals(List.of(p(12, 43), p(12, 44), p(12, 45)),
                a.ray(Dir.S).limit(3).toList());
        assertEquals(List.of(p(11, 42), p(10, 42), p(9, 42)),
                a.ray(Dir.W).limit(3).toList());

        assertEquals(List.of(p(11, 41), p(10, 40), p(9, 39)),
                a.ray(Dir8.NW).limit(3).toList());
        assertEquals(List.of(p(16, 46), p(17, 47), p(18, 48)),
                a.ray(Dir8.SE).skip(3).limit(3).toList());

        assertEquals(List.of(p(10, 52), p(8, 62), p(6, 72)),
                a.ray(p(10, 52)).limit(3).toList());
    }

    @Test
    void testDistanceMethods() {
        var a = p(42, 12);
        var b = p(30, 30);

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

        var c = p(Integer.MIN_VALUE, Integer.MIN_VALUE);

        assertEquals(2 * (Integer.MAX_VALUE + 1L), c.dist1());
        assertEquals(Integer.MAX_VALUE + 1L, c.distMax());
    }

    @Test
    void testRangeMethods() {
        var list = List.of(p(20, 30), p(10, 50), p(15, 25));
        assertEquals(new Range(10, 20), Pos.xRange(list));
        assertEquals(new Range(25, 50), Pos.yRange(list));
        assertThrows(NoSuchElementException.class, () -> Pos.xRange(List.of()));
        assertThrows(NoSuchElementException.class, () -> Pos.yRange(List.of()));
    }

    @Test
    void testBoxMethods() {
        assertEquals(List.of(), Pos.box(0, 3).toList());
        assertEquals(List.of(), Pos.box(1, -1).toList());
        assertEquals(List.of(Pos.ORIGIN), Pos.box(1, 1).toList());
        assertEquals(List.of(
                        p(0, 0), p(0, 1), p(0, 2),
                        p(1, 0), p(1, 1), p(1, 2)),
                Pos.box(2, 3).toList());

        var list1 = List.of(p(40, 20), p(41, 20), p(42, 20));
        var list2 = List.of(
                p(42, 10), p(42, 11),
                p(43, 10), p(43, 11),
                p(44, 10), p(44, 11));

        assertEquals(List.of(),
                Pos.box(new Range(40, 40), new Range(20, 19)).toList());
        assertEquals(list1, Pos.box(new Range(40, 42), new Range(20, 20)).toList());
        assertEquals(list2, Pos.box(new Range(42, 44), new Range(10, 11)).toList());

        assertEquals(List.of(), Pos.box(p(40, 20), p(40, 19)).toList());
        assertEquals(list1, Pos.box(p(40, 20), p(42, 20)).toList());
        assertEquals(list2, Pos.box(p(42, 10), p(44, 11)).toList());

        assertEquals(List.of(), Pos.box(p(42, 20), p(40, 20)).toList());
        assertEquals(list1, Pos.boundingBox(p(42, 20), p(40, 20)).toList());
        assertEquals(list2, Pos.boundingBox(List.of(p(44, 10), p(42, 11))).toList());
        assertEquals(list2, Pos.boundingBox(p(44, 10), p(43, 11), p(42, 10)).toList());
    }

    private static Pos p(long x, long y) {
        return new Pos(x, y);
    }

}
