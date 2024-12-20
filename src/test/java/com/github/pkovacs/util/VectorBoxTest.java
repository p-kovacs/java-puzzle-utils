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

class VectorBoxTest {

    @Test
    void testBasicMethods() {
        var a = new VectorBox(new Range(5, 10), new Range(12, 42), new Range(1, 100));
        var b = new VectorBox(v(8, 24, -48), v(20, 40, 80));

        assertEquals(a.x().min, a.min().x);
        assertEquals(a.x().max, a.max().x);
        assertEquals(a.y().min, a.min().y);
        assertEquals(a.y().max, a.max().y);
        assertEquals(a.z().min, a.min().z);
        assertEquals(a.z().max, a.max().z);
        assertEquals(new Range(5, 10), a.x());
        assertEquals(new Range(12, 42), a.y());
        assertEquals(new Range(1, 100), a.z());
        assertEquals(new Vector(5, 12, 1), a.min());
        assertEquals(new Vector(10, 42, 100), a.max());

        assertFalse(a.isEmpty());
        assertTrue(b.isNonEmpty());
        assertEquals(6 * 31 * 100, a.size());
        assertEquals(13 * 17 * 129, b.size());

        assertFalse(a.contains(v(10, 50, 42)));
        assertFalse(a.contains(v(20, 40, 42)));
        assertFalse(a.contains(v(10, 40, 0)));
        assertFalse(a.contains(v(10, 40, 101)));
        assertTrue(a.contains(v(10, 40, 1)));
        assertTrue(a.contains(v(10, 40, 50)));
        assertTrue(a.contains(v(10, 40, 100)));
        assertTrue(a.contains(a.min()));
        assertTrue(a.contains(a.max()));
        assertFalse(a.contains(b.min()));
        assertFalse(a.contains(b.max()));
        assertFalse(a.containsAll(b));
        assertTrue(a.containsAll(a));

        assertEquals("([5..10],[12..42],[1..100])", a.toString());
        assertEquals("([8..20],[24..40],[-48..80])", b.toString());
    }

    @Test
    void testIteration() {
        var a = new VectorBox(v(10, 20, 30), v(14, 21, 32));
        var list = a.toList();
        assertEquals(5 * 2 * 3, a.size());
        assertEquals(5 * 2 * 3, list.size());
        assertEquals(list, a.stream().distinct().toList());
        assertTrue(list.stream().allMatch(a::contains));
        assertTrue(a.containsAll(list));
        assertTrue(IntStream.range(0, list.size() - 1).allMatch(i -> list.get(i).compareTo(list.get(i + 1)) <= 0));

        assertEquals(List.of(),
                new VectorBox(new Range(10, 10), new Range(20, 10), new Range(30, 40)).toList());
        assertEquals(List.of(v(10, 20, 30)),
                new VectorBox(new Range(10, 10), new Range(20, 20), new Range(30, 30)).toList());
        assertEquals(List.of(v(10, 20, 30), v(11, 20, 30)),
                new VectorBox(new Range(10, 11), new Range(20, 20), new Range(30, 30)).toList());
        assertEquals(List.of(v(10, 20, 30), v(10, 21, 30)),
                new VectorBox(new Range(10, 10), new Range(20, 21), new Range(30, 30)).toList());
        assertEquals(List.of(v(10, 20, 30), v(10, 20, 31), v(10, 20, 32), v(10, 20, 33)),
                new VectorBox(new Range(10, 10), new Range(20, 20), new Range(30, 33)).toList());
        assertEquals(List.of(
                        v(10, 20, 30), v(10, 20, 31), v(10, 20, 32),
                        v(10, 21, 30), v(10, 21, 31), v(10, 21, 32),
                        v(11, 20, 30), v(11, 20, 31), v(11, 20, 32),
                        v(11, 21, 30), v(11, 21, 31), v(11, 21, 32),
                        v(12, 20, 30), v(12, 20, 31), v(12, 20, 32),
                        v(12, 21, 30), v(12, 21, 31), v(12, 21, 32),
                        v(13, 20, 30), v(13, 20, 31), v(13, 20, 32),
                        v(13, 21, 30), v(13, 21, 31), v(13, 21, 32)
                ),
                new VectorBox(new Range(10, 13), new Range(20, 21), new Range(30, 32)).toList());
    }

    @Test
    void testBoundingBox() {
        var list = List.of(v(5, -12, 1), v(12, 42, 80), v(8, -24, -48), v(20, 40, 100));

        assertEquals(new VectorBox(v(5, -24, -48), v(20, 42, 100)), VectorBox.bound(list));
        assertEquals(new VectorBox(v(5, -24, -48), v(20, 42, 100)),
                VectorBox.bound(List.of(v(5, 42, -48), v(20, -24, 100))));
        assertEquals(new VectorBox(v(-5, -24, -48), v(20, 42, 100)),
                VectorBox.bound(List.of(v(20, 0, 0), v(-5, 0, 100), v(0, -24, 0), v(0, 42, -48))));

        assertEquals(0, new VectorBox(v(20, 40, 100), v(5, -12, 1)).toList().size());
        assertEquals(84800, VectorBox.bound(List.of(v(20, 40, 100), v(5, -12, 1))).toList().size());

        assertTrue(new VectorBox(v(5, 10, 0), v(10, 5, 0)).isEmpty());
        assertFalse(VectorBox.bound(Set.of(v(5, 10, 0), v(10, 5, 0))).isEmpty());

        assertThrows(NoSuchElementException.class, () -> VectorBox.bound(List.of()));
    }

    @Test
    void testOperations() {
        var a = new VectorBox(new Range(5, 10), new Range(12, 42), new Range(1, 100));
        var b = new VectorBox(v(8, 24, -48), v(20, 40, 80));
        var c = new VectorBox(v(8, 24, 1), v(10, 40, 80));
        var d = new VectorBox(new Range(5, 20), new Range(12, 42), new Range(-48, 100));

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
        assertTrue(d.containsAll(c.shift(10, 2, -40)));
        assertFalse(d.containsAll(c.shift(11, 0, 0)));
        assertFalse(d.containsAll(c.shift(0, 3, 0)));
        assertFalse(d.containsAll(c.shift(0, 0, -50)));

        assertEquals(new VectorBox(new Range(105, 110), new Range(3012, 3042), new Range(50001, 50100)),
                a.shift(new Vector(100, 3000, 50000)));
        assertEquals(new VectorBox(new Range(-95, -90), new Range(3012, 3042), new Range(2, 101)),
                a.shift(-100, 3000, 1));
        assertEquals(new VectorBox(new Range(2, 13), new Range(9, 45), new Range(-2, 103)),
                a.extend(3));
        assertEquals(new VectorBox(new Range(7, 8), new Range(7, 47), new Range(-9, 110)),
                a.extend(-2, 5, 10));
    }

    private static Vector v(long x, long y, long z) {
        return new Vector(x, y, z);
    }

}
