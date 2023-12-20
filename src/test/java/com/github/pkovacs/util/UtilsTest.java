package com.github.pkovacs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest extends Utils {

    @Test
    void testRangeMethods() {
        assertEquals(0, constrainIndex(0, 5));
        assertEquals(2, constrainIndex(2, 5));
        assertEquals(4, constrainIndex(4, 5));
        assertEquals(4, constrainIndex(5, 5));
        assertEquals(4, constrainIndex(42, 5));
        assertEquals(0, constrainIndex(-1, 5));
        assertEquals(0, constrainIndex(-42, 5));

        assertEquals(0, wrapIndex(0, 5));
        assertEquals(2, wrapIndex(2, 5));
        assertEquals(4, wrapIndex(4, 5));
        assertEquals(0, wrapIndex(5, 5));
        assertEquals(2, wrapIndex(42, 5));
        assertEquals(4, wrapIndex(-1, 5));
        assertEquals(3, wrapIndex(-42, 5));

        assertEquals(3, constrainToRange(0, 3, 7));
        assertEquals(5, constrainToRange(5, 3, 7));
        assertEquals(7, constrainToRange(10, 3, 7));
        assertEquals(3, constrainToRange(-2, 3, 7));
        assertEquals(7, constrainToRange(42, 3, 7));

        assertEquals(3L, constrainToRange(0L, 3L, 7L));
        assertEquals(5L, constrainToRange(5L, 3L, 7L));
        assertEquals(7L, constrainToRange(10L, 3L, 7L));
        assertEquals(3L, constrainToRange(-2L, 3L, 7L));
        assertEquals(7L, constrainToRange(42L, 3L, 7L));

        assertEquals(5, wrapToRange(0, 3, 7));
        assertEquals(4, wrapToRange(4, 3, 7));
        assertEquals(3, wrapToRange(8, 3, 7));
        assertEquals(7, wrapToRange(12, 3, 7));
        assertEquals(4, wrapToRange(34, 3, 7));
        assertEquals(6, wrapToRange(56, 3, 7));
        assertEquals(7, wrapToRange(-3, 3, 7));

        assertEquals(5L, wrapToRange(0L, 3L, 7L));
        assertEquals(4L, wrapToRange(4L, 3L, 7L));
        assertEquals(3L, wrapToRange(8L, 3L, 7L));
        assertEquals(7L, wrapToRange(12L, 3L, 7L));
        assertEquals(4L, wrapToRange(34L, 3L, 7L));
        assertEquals(6L, wrapToRange(56L, 3L, 7L));
        assertEquals(7L, wrapToRange(-3L, 3L, 7L));

        assertTrue(isInRange('k', 'a', 'z'));
        assertTrue(isInRange(5, 3, 8));
        assertTrue(isInRange(3.0, Math.E, Math.PI));
    }

    @Test
    void testInts() {
        int[] x = { 3, 2, 1, 5, 4 };

        assertEquals(List.of(), listOf(new int[0]));
        assertEquals(Set.of(), setOf(new int[0]));
        assertEquals(List.of(3, 2, 1, 5, 4), listOf(x));
        assertEquals(Set.of(3, 2, 1, 5, 4), setOf(x));
        assertEquals(3, streamOf(x).filter(c -> c % 2 == 1).count());

        assertEquals(1, min(3, 1, 5));
        assertEquals(1, min(x));
        assertEquals(1, minInt(listOf(x)));
        assertEquals(5, max(3, 1, 5));
        assertEquals(5, max(x));
        assertEquals(5, maxInt(listOf(x)));

        assertThrows(NoSuchElementException.class, () -> min(new int[0]));
        assertThrows(NoSuchElementException.class, () -> max(new int[0]));
        assertThrows(NoSuchElementException.class, () -> minInt(List.of()));
        assertThrows(NoSuchElementException.class, () -> maxInt(List.of()));
    }

    @Test
    void testLongs() {
        long[] x = { 3, 2, 1, 5, 4 };

        assertEquals(List.of(), listOf(new int[0]));
        assertEquals(Set.of(), setOf(new int[0]));
        assertEquals(List.of(3L, 2L, 1L, 5L, 4L), listOf(x));
        assertEquals(Set.of(3L, 2L, 1L, 5L, 4L), setOf(x));
        assertEquals(3, streamOf(x).filter(c -> c % 2 == 1).count());

        assertEquals(1L, min(3L, 1L, 5L));
        assertEquals(1L, min(x));
        assertEquals(1L, minInt(listOf(x)));
        assertEquals(5L, max(3L, 1L, 5L));
        assertEquals(5L, max(x));
        assertEquals(5L, maxInt(listOf(x)));

        assertThrows(NoSuchElementException.class, () -> min(new long[0]));
        assertThrows(NoSuchElementException.class, () -> max(new long[0]));
        assertThrows(NoSuchElementException.class, () -> minLong(List.of()));
        assertThrows(NoSuchElementException.class, () -> maxLong(List.of()));
    }

    @Test
    void testChars() {
        char[] x = { 'h', 'e', 'l', 'l', 'o' };

        assertEquals(List.of('h', 'e', 'l', 'l', 'o'), charsOf("hello").toList());
        assertEquals(2, charsOf("hello").filter(c -> c == 'l').count());

        assertEquals(List.of('h', 'e', 'l', 'l', 'o'), charsOf("hello").toList());
        assertEquals(2, charsOf("hello").filter(c -> c == 'l').count());

        assertEquals(List.of('h', 'e', 'l', 'l', 'o'), listOf(x));
        assertEquals(Set.of('h', 'e', 'l', 'o'), setOf(x));
        assertEquals(3, charsOf("hello").filter(c -> c != 'l').count());
        assertEquals(3, streamOf("hello".toCharArray()).filter(c -> c != 'l').count());

        assertEquals('a', min('c', 'a', 'f', 'b'));
        assertEquals('e', min(x));
        assertEquals('f', max('c', 'a', 'f', 'b'));
        assertEquals('o', max(x));

        assertThrows(NoSuchElementException.class, () -> min(new char[0]));
        assertThrows(NoSuchElementException.class, () -> max(new char[0]));
    }

    @Test
    public void testCountMethods() {
        assertEquals(3, count(List.of(1, 2, 3, 2, 1, 2, 3), 2));
        assertEquals(2, count(List.of("a", "b", "c", "b"), "b"));
        assertEquals(1, count(List.of("a", "b", "c", "b"), "c"));
        assertEquals(0, count(List.of("a", "b", "c", "b"), "d"));
        assertEquals(2, count(List.of("a", "bc", "d", "bc"), "bc"));
        assertEquals(2, count(Arrays.asList("a", null, "d", null), null));

        assertEquals(3, count("abcdcbab", 'b'));
        assertEquals(2, count("abcdcbab", 'c'));
        assertEquals(1, count("abcdcbab", 'd'));
        assertEquals(0, count("abcdcbab", 'e'));
    }

    @Test
    void testSetOperations() {
        var c1 = setOf("hello".toCharArray());
        var c2 = setOf("echo".toCharArray());
        var c3 = listOf("love".toCharArray());
        var c4 = listOf("old".toCharArray());

        assertEquals(Set.of('h', 'e', 'l', 'o', 'c'), unionOf(c1, c2));
        assertEquals(Set.of('h', 'e', 'l', 'o', 'v'), unionOf(c1, c3));
        assertEquals(Set.of('h', 'e', 'l', 'o', 'd'), unionOf(c1, c4));
        assertEquals(Set.of('e', 'c', 'h', 'o', 'l', 'v'), unionOf(c2.stream(), c3.stream()));
        assertEquals(Set.of('e', 'c', 'h', 'o', 'l', 'd'), unionOf(c2.stream(), c4.stream()));
        assertEquals(Set.of('l', 'o', 'v', 'e', 'd'), unionOf(c3.stream(), c4.stream()));
        assertEquals(Set.of('h', 'e', 'l', 'o', 'c', 'v'), unionOf(List.of(c1, c2, c3)));
        assertEquals(Set.of('h', 'e', 'l', 'o', 'v', 'd'), unionOf(List.of(c1, c3, c4)));
        assertEquals(Set.of('h', 'e', 'l', 'o', 'c', 'v', 'd'), unionOf(List.of(c1, c2, c3, c4)));

        assertEquals(Set.of('h', 'e', 'o'), intersectionOf(c1, c2));
        assertEquals(Set.of('e', 'l', 'o'), intersectionOf(c1, c3));
        assertEquals(Set.of('l', 'o'), intersectionOf(c1, c4));
        assertEquals(Set.of('e', 'o'), intersectionOf(c2.stream(), c3.stream()));
        assertEquals(Set.of('o'), intersectionOf(c2.stream(), c4.stream()));
        assertEquals(Set.of('l', 'o'), intersectionOf(c3.stream(), c4.stream()));
        assertEquals(Set.of('e', 'o'), intersectionOf(List.of(c1, c2, c3)));
        assertEquals(Set.of('l', 'o'), intersectionOf(List.of(c1, c3, c4)));
        assertEquals(Set.of('o'), intersectionOf(List.of(c1, c2, c3, c4)));
    }

    @Test
    void testGenericParametersOfSetOperations() {
        var a = List.of(listOf(1, 2), listOf(1, 2, 3));
        var b = Set.of(new ArrayList<>(listOf(1)), new ArrayList<>(listOf(1, 2)));
        var c = Set.of(new LinkedList<>(listOf(1)), new LinkedList<>(listOf(1, 2, 3)));

        var union = Set.of(listOf(1), listOf(1, 2), listOf(1, 2, 3));
        assertEquals(union, unionOf(a, b));
        assertEquals(union, unionOf(b.stream(), a.stream()));
        assertEquals(union, unionOf(a, c));
        assertEquals(union, unionOf(c.stream(), a.stream()));
        assertEquals(union, unionOf(b, c));
        assertEquals(union, unionOf(c.stream(), b.stream()));
        assertEquals(union, unionOf(List.of(a, b, c)));
        assertEquals(union, unionOf(List.of(c, b, a)));

        assertEquals(Set.of(listOf(1, 2)), intersectionOf(a, b));
        assertEquals(Set.of(listOf(1, 2)), intersectionOf(b.stream(), a.stream()));
        assertEquals(Set.of(listOf(1, 2, 3)), intersectionOf(a, c));
        assertEquals(Set.of(listOf(1, 2, 3)), intersectionOf(c.stream(), a.stream()));
        assertEquals(Set.of(listOf(1)), intersectionOf(b, c));
        assertEquals(Set.of(listOf(1)), intersectionOf(c.stream(), b.stream()));
        assertEquals(Set.of(), intersectionOf(List.of(a, b, c)));
        assertEquals(Set.of(), intersectionOf(List.of(c, b, a)));
    }

    @Test
    public void testListSlicing() {
        assertEquals(List.of(listOf(1, 2, 3), listOf(4, 5, 6)),
                chunked(listOf(1, 2, 3, 4, 5, 6), 3).toList());
        assertEquals(List.of(listOf(1, 2, 3), listOf(4, 5)),
                chunked(listOf(1, 2, 3, 4, 5), 3).toList());
        assertEquals(List.of(listOf(1, 2), listOf(3, 4), listOf(5)),
                chunked(listOf(1, 2, 3, 4, 5), 2).toList());
        assertEquals(List.of(listOf(1), listOf(2), listOf(3), listOf(4)),
                chunked(listOf(1, 2, 3, 4), 1).toList());
        assertEquals(List.of(listOf(1, 2, 3), listOf(4)),
                chunked(listOf(1, 2, 3, 4), 3).toList());
        assertEquals(List.of(listOf(1, 2, 3, 4)),
                chunked(listOf(1, 2, 3, 4), 4).toList());
        assertEquals(List.of(listOf(1, 2, 3, 4)),
                chunked(listOf(1, 2, 3, 4), 5).toList());

        assertThrows(IllegalArgumentException.class, () -> chunked(listOf(1, 2, 3), 0));
        assertThrows(IllegalArgumentException.class, () -> chunked(listOf(1, 2, 3), -1));

        assertEquals(List.of(listOf(1, 2, 3), listOf(2, 3, 4), listOf(3, 4, 5)),
                windowed(listOf(1, 2, 3, 4, 5), 3).toList());
        assertEquals(List.of(listOf(1, 2), listOf(2, 3), listOf(3, 4), listOf(4, 5)),
                windowed(listOf(1, 2, 3, 4, 5), 2).toList());
        assertEquals(List.of(listOf(1), listOf(2), listOf(3), listOf(4)),
                windowed(listOf(1, 2, 3, 4), 1).toList());
        assertEquals(List.of(listOf(1, 2, 3), listOf(2, 3, 4)),
                windowed(listOf(1, 2, 3, 4), 3).toList());
        assertEquals(List.of(listOf(1, 2, 3, 4)),
                windowed(listOf(1, 2, 3, 4), 4).toList());
        assertEquals(List.of(),
                windowed(listOf(1, 2, 3, 4), 5).toList());

        assertThrows(IllegalArgumentException.class, () -> windowed(listOf(1, 2, 3), 0));
        assertThrows(IllegalArgumentException.class, () -> windowed(listOf(1, 2, 3), -1));
    }

    @Test
    public void testInverseMap() {
        var map = Map.of('a', 1, 'b', 2, 'c', 3);

        assertEquals(Map.of(1, 'a', 2, 'b', 3, 'c'), inverse(map));
        assertEquals(map, inverse(inverse(map)));
        assertEquals(Map.of(), inverse(Map.of()));
    }

    @Test
    void testDeepCopy() {
        int[][] a = { { 0, 1, 2, 3, 4 }, {}, { Integer.MIN_VALUE, Integer.MAX_VALUE } };
        int[][] b = deepCopy(a);

        assertTrue(Arrays.deepEquals(a, b));
        assertNotSame(a, b);
        assertNotSame(a[0], b[0]);

        long[][] c = { { 0, 1, 2, 3, 4 }, {}, { Long.MIN_VALUE, Long.MAX_VALUE } };
        long[][] d = deepCopy(c);

        assertTrue(Arrays.deepEquals(c, d));
        assertNotSame(c, d);
        assertNotSame(c[0], d[0]);

        char[][] x = { "hello".toCharArray(), "".toCharArray(), "okay".toCharArray() };
        char[][] y = deepCopy(x);

        assertTrue(Arrays.deepEquals(x, y));
        assertNotSame(x, y);
        assertNotSame(x[0], y[0]);
    }

}
