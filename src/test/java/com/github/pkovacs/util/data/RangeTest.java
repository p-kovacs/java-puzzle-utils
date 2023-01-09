package com.github.pkovacs.util.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangeTest {

    @Test
    void test() {
        var x = new Range(12, 42);
        var y = new Range(30, 50);
        var z = new Range(5, 12);

        assertFalse(x.isEmpty());
        assertEquals(31, x.count());

        assertFalse(x.contains(10));
        assertTrue(x.contains(20));
        assertTrue(x.contains(x.min()));
        assertTrue(x.contains(x.max()));

        assertFalse(x.containsAll(y));
        assertFalse(x.containsAll(z));
        assertTrue(x.containsAll(new Range(12, 40)));

        assertTrue(x.overlaps(y));
        assertTrue(y.overlaps(x));
        assertTrue(x.overlaps(z));
        assertTrue(z.overlaps(x));
        assertFalse(y.overlaps(z));
        assertFalse(z.overlaps(y));

        assertEquals(new Range(30, 42), y.intersection(x));
        assertEquals(new Range(12, 12), x.intersection(z));
        assertTrue(y.intersection(z).isEmpty());
        assertTrue(z.intersection(y).isEmpty());

        assertArrayEquals(new long[] { 5, 6, 7, 8, 9, 10, 11, 12 }, z.stream().toArray());
        assertEquals(x.count(), x.stream().count());

        assertEquals("[12..42]", x.toString());
    }

}
