package com.github.pkovacs.util.data;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoxTest {

    @Test
    void testTwoDim() {
        var x = new Box(new Vector(5, 12), new Vector(12, 42));
        var y = new Box(new Vector(8, 24), new Vector(20, 30));

        assertFalse(x.isEmpty());
        assertEquals(8 * 31, x.count());

        assertFalse(x.contains(new Vector(10, 50)));
        assertFalse(x.contains(new Vector(20, 40)));
        assertTrue(x.contains(new Vector(10, 40)));
        assertTrue(x.contains(x.min()));
        assertTrue(x.contains(x.max()));
        assertTrue(x.contains(y.min()));
        assertFalse(x.contains(y.max()));

        assertFalse(x.containsAll(y));
        assertTrue(x.containsAll(x));
        assertTrue(x.overlaps(y));
        assertTrue(x.overlaps(x));

        var z = new Box(new Vector(8, 24), new Vector(12, 30));
        assertEquals(z, x.intersection(y));
        assertEquals(z, y.intersection(x));
        assertTrue(x.contains(z.min()));
        assertTrue(x.contains(z.max()));
        assertTrue(y.contains(z.min()));
        assertTrue(y.contains(z.max()));
        assertTrue(x.containsAll(z));
        assertTrue(y.containsAll(z));

        assertEquals("[(5, 12) .. (12, 42)]", x.toString());

        var list = new Box(new Vector(10, 20), new Vector(14, 25)).stream().toList();
        assertEquals(5 * 6, list.size());
        assertTrue(IntStream.range(0, list.size() - 1).allMatch(i -> list.get(i).compareTo(list.get(i + 1)) <= 0));
    }

    @Test
    void testThreeDim() {
        var x = new Box(new Vector(5, 12, 1), new Vector(12, 42, 100));
        var y = new Box(new Vector(8, 24, -48), new Vector(20, 40, 80));

        assertFalse(x.isEmpty());
        assertEquals(8 * 31 * 100, x.count());

        assertFalse(x.contains(new Vector(10, 50, 42)));
        assertFalse(x.contains(new Vector(20, 40, 42)));
        assertFalse(x.contains(new Vector(10, 40, 0)));
        assertFalse(x.contains(new Vector(10, 40, 101)));
        assertTrue(x.contains(new Vector(10, 40, 1)));
        assertTrue(x.contains(new Vector(10, 40, 50)));
        assertTrue(x.contains(new Vector(10, 40, 100)));
        assertTrue(x.contains(x.min()));
        assertTrue(x.contains(x.max()));
        assertFalse(x.contains(y.min()));
        assertFalse(x.contains(y.max()));

        assertFalse(x.containsAll(y));
        assertTrue(x.containsAll(x));
        assertTrue(x.overlaps(y));
        assertTrue(x.overlaps(x));

        var z = new Box(new Vector(8, 24, 1), new Vector(12, 40, 80));
        assertEquals(z, x.intersection(y));
        assertEquals(z, y.intersection(x));
        assertTrue(x.contains(z.min()));
        assertTrue(x.contains(z.max()));
        assertTrue(y.contains(z.min()));
        assertTrue(y.contains(z.max()));
        assertTrue(x.containsAll(z));
        assertTrue(y.containsAll(z));

        assertEquals("[(5, 12, 1) .. (12, 42, 100)]", x.toString());
        assertEquals("[(8, 24, -48) .. (20, 40, 80)]", y.toString());

        var list = new Box(new Vector(10, 20, 30), new Vector(14, 21, 32)).stream().toList();
        assertEquals(5 * 2 * 3, list.size());
        assertTrue(IntStream.range(0, list.size() - 1).allMatch(i -> list.get(i).compareTo(list.get(i + 1)) <= 0));
    }

    @Test
    void testGeneral() {
        var x = new Box(new Vector(5, 40, 1, 0, 1000), new Vector(6, 42, 4, 0, 1005));
        var y = new Box(new Vector(5, 40, 2, 0, 1001), new Vector(6, 41, 4, 0, 1002));

        assertFalse(x.isEmpty());
        assertEquals(2 * 3 * 4 * 6, x.count());
        assertEquals(2 * 2 * 3 * 2, y.count());

        assertTrue(x.contains(x.min()));
        assertTrue(x.contains(x.max()));
        assertTrue(x.contains(y.min()));
        assertTrue(x.contains(y.max()));

        assertTrue(x.containsAll(y));
        assertEquals(y, x.intersection(y));
        assertEquals(y, y.intersection(x));

        var list = x.stream().toList();
        assertEquals(x.count(), list.size());
        assertTrue(list.stream().allMatch(v -> v.compareTo(x.min()) >= 0));
        assertTrue(list.stream().allMatch(v -> v.compareTo(x.max()) <= 0));
        assertTrue(IntStream.range(0, list.size() - 1).allMatch(i -> list.get(i).compareTo(list.get(i + 1)) <= 0));
    }

    @Test
    void testExceptions() {
        assertThrows(IllegalArgumentException.class, () -> new Box(new Vector(5, 12), new Vector(12, 42, 100)));

        var x = new Box(new Vector(5, 12), new Vector(12, 42));
        var y = new Box(new Vector(5, 12, 1), new Vector(12, 42, 100));
        assertThrows(IllegalArgumentException.class, () -> x.contains(y.min()));
        assertThrows(IllegalArgumentException.class, () -> y.contains(x.max()));
        assertThrows(IllegalArgumentException.class, () -> x.containsAll(y));
    }

}
