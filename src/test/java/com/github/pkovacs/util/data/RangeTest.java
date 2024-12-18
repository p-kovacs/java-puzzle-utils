package com.github.pkovacs.util.data;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.github.pkovacs.util.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangeTest {

    @Test
    void testBasicMethods() {
        var x = new Range(12, 42);
        var y = new Range(30, 50);
        var z = new Range(5, 12);

        assertEquals(12, x.min);
        assertEquals(12, x.min());
        assertEquals(42, x.max);
        assertEquals(42, x.max());

        assertFalse(x.isEmpty());
        assertTrue(x.isNonEmpty());
        assertEquals(31, x.size());

        assertFalse(x.contains(10));
        assertTrue(x.contains(20));
        assertTrue(x.contains(x.min()));
        assertTrue(x.contains(x.max()));

        long[] array = new long[] { 5, 6, 7, 8, 9, 10, 11, 12 };
        assertArrayEquals(array, z.stream().toArray());
        assertArrayEquals(array, z.toArray());
        assertEquals(Utils.listOf(array), z.stream().boxed().toList());
        assertEquals(Utils.listOf(array), z.toList());
        assertEquals(x.size(), x.stream().count());

        assertEquals(x, Range.closed(12, 42));
        assertEquals(x, Range.closedOpen(12, 43));

        assertEquals("[12..42]", x.toString());

        var a = new Range(x.min, x.max - 1);
        var b = new Range(x.min, x.max + 1);
        assertEquals(List.of(z, x, y), Stream.of(x, y, z).sorted().toList());
        assertEquals(List.of(z, a, x, b, y), Stream.of(a, b, x, y, z).sorted().toList());
    }

    @Test
    void testContains() {
        var x = new Range(12, 42);
        var y = new Range(30, 50);
        var z = new Range(5, 12);

        assertFalse(x.containsAll(y));
        assertFalse(x.containsAll(z));
        assertTrue(x.containsAll(new Range(12, 40)));

        assertFalse(x.containsAll(y.stream().toArray()));
        assertFalse(x.containsAll(y.stream().boxed().toList()));
        assertTrue(x.containsAll(new Range(12, 40).stream().toArray()));
        assertTrue(x.containsAll(new Range(12, 40).stream().boxed().toList()));
        assertTrue(x.containsAll());
        assertTrue(x.containsAll(List.of()));

        assertFalse(x.containsAll(y.stream().mapToInt(i -> (int) i).toArray()));
        assertFalse(x.containsAll(y.stream().mapToInt(i -> (int) i).boxed().toList()));
        assertTrue(x.containsAll(new Range(12, 40).stream().mapToInt(i -> (int) i).toArray()));
        assertTrue(x.containsAll(new Range(12, 40).stream().mapToInt(i -> (int) i).boxed().toList()));
    }

    @Test
    void testOperations() {
        var x = new Range(12, 42);
        var y = new Range(30, 50);
        var z = new Range(5, 12);

        assertEquals(new Range(7, 11), new Range(5, 9).shift(2));
        assertEquals(new Range(2, 6), new Range(5, 9).shift(-3));

        assertEquals(new Range(3, 11), new Range(5, 9).extend(2));
        assertEquals(new Range(6, 8), new Range(5, 9).extend(-1));

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

        assertEquals(new Range(1, 9), new Range(1, 3).span(new Range(6, 9)));
        assertEquals(new Range(1, 9), new Range(6, 9).span(new Range(1, 3)));
        assertEquals(new Range(12, 50), x.span(y));
        assertEquals(new Range(12, 50), y.span(x));
        assertEquals(new Range(5, 42), x.span(z));
        assertEquals(new Range(5, 42), z.span(x));
        assertEquals(x, x.span(x));

        assertEquals(new Range(4, 6), new Range(1, 3).gap(new Range(7, 10)));
        assertEquals(new Range(4, 6), new Range(7, 8).gap(new Range(0, 3)));
        assertEquals(new Range(4, 3), new Range(1, 3).gap(new Range(4, 8)));
        assertEquals(new Range(4, 3), new Range(4, 8).gap(new Range(1, 3)));
        assertTrue(new Range(1, 3).gap(new Range(4, 8)).isEmpty());
        assertTrue(new Range(4, 8).gap(new Range(1, 3)).isEmpty());
        assertEquals(new Range(13, 29), y.gap(z));
        assertEquals(new Range(13, 29), z.gap(y));
        assertThrows(IllegalArgumentException.class, () -> new Range(1, 5).gap(new Range(5, 8)));
        assertThrows(IllegalArgumentException.class, () -> x.gap(z));
        assertThrows(IllegalArgumentException.class, () -> z.gap(x));
        assertThrows(IllegalArgumentException.class, () -> x.gap(x));
    }

    @Test
    void testBoundingRange() {
        List<Number> list = List.of(234, 100, 200L);

        assertEquals(new Range(100, 234), Range.bound(list));
        assertEquals(new Range(100, 234), Range.bound(234, 100, 200));
        assertEquals(new Range(100, 100), Range.bound(100));
        assertEquals(new Range(12, 33), Range.bound(IntStream.range(12, 34).toArray()));
        assertEquals(new Range(100, 234), Range.bound(new long[] { 234, 100, 200 }));
        assertEquals(new Range(1234, 5678), Range.bound(LongStream.rangeClosed(1234, 5678).toArray()));

        assertTrue(new Range(100, 99).isEmpty());
        assertFalse(Range.bound(100, 99).isEmpty());

        assertThrows(NoSuchElementException.class, () -> Range.bound(List.of()));
        assertThrows(NoSuchElementException.class, () -> Range.bound(new int[0]));
        assertThrows(NoSuchElementException.class, () -> Range.bound(new long[0]));
    }

}
