package com.github.pkovacs.util;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoxTest {

    @Test
    void testBasicMethods() {
        var a = new Box(new Range(5, 10), new Range(12, 42));
        var b = new Box(p(8, 24), p(20, 30));

        assertEquals(a.x().min, a.min().x);
        assertEquals(a.x().max, a.max().x);
        assertEquals(a.y().min, a.min().y);
        assertEquals(a.y().max, a.max().y);
        assertEquals(new Range(5, 10), a.x());
        assertEquals(new Range(12, 42), a.y());
        assertEquals(new Pos(5, 12), a.min());
        assertEquals(new Pos(10, 42), a.max());

        assertFalse(a.isEmpty());
        assertTrue(b.isNonEmpty());
        assertEquals(6 * 31, a.size());
        assertEquals(13 * 7, b.size());

        assertFalse(a.contains(p(10, 50)));
        assertFalse(a.contains(p(20, 40)));
        assertTrue(a.contains(p(10, 40)));
        assertTrue(a.contains(a.min()));
        assertTrue(a.contains(a.max()));
        assertTrue(a.contains(b.min()));
        assertFalse(a.contains(b.max()));
        assertFalse(a.containsAll(b));
        assertTrue(a.containsAll(a));

        assertEquals("([5..10],[12..42])", a.toString());
    }

    @Test
    void testIteration() {
        var a = new Box(new Range(10, 14), new Range(20, 25));
        var list = a.toList();
        assertEquals(5 * 6, a.size());
        assertEquals(5 * 6, list.size());
        assertEquals(list, a.stream().distinct().toList());
        assertTrue(list.stream().allMatch(a::contains));
        assertTrue(a.containsAll(list));
        assertTrue(IntStream.range(0, list.size() - 1).allMatch(i -> list.get(i).compareTo(list.get(i + 1)) <= 0));

        assertEquals(List.of(),
                new Box(new Range(10, 10), new Range(20, 10)).toList());
        assertEquals(List.of(p(10, 20)),
                new Box(new Range(10, 10), new Range(20, 20)).toList());
        assertEquals(List.of(p(10, 20), p(11, 20)),
                new Box(new Range(10, 11), new Range(20, 20)).toList());
        assertEquals(List.of(p(10, 20), p(10, 21), p(10, 22)),
                new Box(new Range(10, 10), new Range(20, 22)).toList());
        assertEquals(List.of(
                        p(10, 20), p(10, 21), p(10, 22),
                        p(11, 20), p(11, 21), p(11, 22),
                        p(12, 20), p(12, 21), p(12, 22),
                        p(13, 20), p(13, 21), p(13, 22)
                ),
                new Box(new Range(10, 13), new Range(20, 22)).toList());

        assertEquals(List.of(), new Box(0, 3).toList());
        assertEquals(List.of(), new Box(1, -1).toList());
        assertEquals(List.of(Pos.ORIGIN), new Box(1, 1).toList());
        assertEquals(List.of(p(0, 0), p(0, 1), p(0, 2), p(1, 0), p(1, 1), p(1, 2)),
                new Box(2, 3).toList());
    }

    @Test
    void testBoundingBox() {
        var list1 = List.of(p(40, 20), p(41, 20), p(42, 20));
        var list2 = List.of(p(42, 10), p(42, 11), p(43, 10), p(43, 11), p(44, 10), p(44, 11));

        assertEquals(list1, Box.bound(List.of(list1.getFirst(), list1.getLast())).toList());
        assertEquals(list1, Box.bound(list1).toList());
        assertEquals(list2, Box.bound(List.of(list2.getLast(), list2.getFirst())).toList());
        assertEquals(list2, Box.bound(list2).toList());

        assertEquals(List.of(), new Box(p(42, 20), p(40, 20)).toList());
        assertEquals(list1, Box.bound(Set.of(p(42, 20), p(40, 20))).toList());
        assertEquals(list2, Box.bound(List.of(p(44, 10), p(42, 11))).toList());
        assertEquals(list2, Box.bound(Set.of(p(44, 10), p(43, 11), p(42, 10))).toList());

        assertThrows(NoSuchElementException.class, () -> Box.bound(List.of()));
    }

    @Test
    void testOperations() {
        var a = new Box(new Range(5, 12), new Range(8, 42));
        var b = new Box(p(8, 24), p(20, 30));
        var c = new Box(new Range(8, 12), new Range(24, 30));
        var d = new Box(p(5, 8), p(20, 42));

        assertTrue(a.overlaps(b));
        assertTrue(a.overlaps(a));
        assertFalse(new Box(p(1, 1), p(5, 8)).overlaps(new Box(p(2, 4), p(-5, 0))));

        assertEquals(c, a.intersection(b));
        assertEquals(c, b.intersection(a));
        assertTrue(a.contains(c.min()));
        assertTrue(a.contains(c.max()));
        assertTrue(b.contains(c.min()));
        assertTrue(b.contains(c.max()));
        assertTrue(a.containsAll(c));
        assertTrue(b.containsAll(c));
        assertFalse(c.containsAll(a));
        assertTrue(a.containsAll(c.toList()));
        assertTrue(b.containsAll(c.toList()));
        assertFalse(c.containsAll(a.toList()));

        assertEquals(d, a.span(b));
        assertEquals(d, b.span(a));
        assertTrue(d.containsAll(a));
        assertTrue(d.containsAll(b));
        assertTrue(d.containsAll(c));
        assertTrue(d.containsAll(c.shift(8, 8)));
        assertFalse(d.containsAll(c.shift(10, 10)));
        assertFalse(d.containsAll(c.shift(8, 16)));

        assertEquals(new Box(new Range(105, 112), new Range(3008, 3042)), a.shift(new Pos(100, 3000)));
        assertEquals(new Box(new Range(-95, -88), new Range(3008, 3042)), a.shift(-100, 3000));
        assertEquals(new Box(new Range(2, 15), new Range(5, 45)), a.extend(3));
        assertEquals(new Box(new Range(7, 10), new Range(3, 47)), a.extend(-2, 5));
    }

    private static Pos p(long x, long y) {
        return new Pos(x, y);
    }

}
