package com.github.pkovacs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest extends Utils {

    // ****************************** MATH UTILS ******************************

    @Test
    void testInts() {
        int[] x = { 3, 2, 1, 5, 4 };

        assertEquals(List.of(), listOf(new int[0]));
        assertEquals(Set.of(), setOf(new int[0]));
        assertEquals(List.of(3, 2, 1, 5, 4), listOf(x));
        assertEquals(Set.of(3, 2, 1, 5, 4), setOf(x));
        assertEquals(3, streamOf(x).filter(i -> i % 2 == 1).count());

        assertEquals(1, min(3, 1, 5));
        assertEquals(1, min(x));
        assertEquals(1, min(listOf(x)));
        assertEquals(5, max(3, 1, 5));
        assertEquals(5, max(x));
        assertEquals(5, max(listOf(x)));

        assertThrows(NoSuchElementException.class, () -> min(new int[0]));
        assertThrows(NoSuchElementException.class, () -> max(new int[0]));
        assertThrows(NoSuchElementException.class, () -> min(new ArrayList<Integer>()));
        assertThrows(NoSuchElementException.class, () -> max(new ArrayList<Integer>()));
    }

    @Test
    void testLongs() {
        long[] x = { 3, 2, 1, 5, 4 };

        assertEquals(List.of(), listOf(new int[0]));
        assertEquals(Set.of(), setOf(new int[0]));
        assertEquals(List.of(3L, 2L, 1L, 5L, 4L), listOf(x));
        assertEquals(Set.of(3L, 2L, 1L, 5L, 4L), setOf(x));
        assertEquals(3, streamOf(x).filter(i -> i % 2 == 1).count());

        assertEquals(1L, min(3L, 1L, 5L));
        assertEquals(1L, min(x));
        assertEquals(1L, min(listOf(x)));
        assertEquals(5L, max(3L, 1L, 5L));
        assertEquals(5L, max(x));
        assertEquals(5L, max(listOf(x)));

        assertThrows(NoSuchElementException.class, () -> min(new long[0]));
        assertThrows(NoSuchElementException.class, () -> max(new long[0]));
        assertThrows(NoSuchElementException.class, () -> min(new ArrayList<Long>()));
        assertThrows(NoSuchElementException.class, () -> max(new ArrayList<Long>()));
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
        assertEquals('e', min(listOf(x)));
        assertEquals('f', max('c', 'a', 'f', 'b'));
        assertEquals('o', max(x));
        assertEquals('o', max(listOf(x)));

        assertThrows(NoSuchElementException.class, () -> min(new char[0]));
        assertThrows(NoSuchElementException.class, () -> max(new char[0]));
    }

    @Test
    void testStrings() {
        assertEquals("a", min(List.of("c", "a", "f", "b")));
        assertEquals("f", max(List.of("c", "a", "f", "b")));
        assertEquals("aba", min(List.of("abc", "acd", "adb", "aba", "ada")));
        assertEquals("adb", max(List.of("abc", "acd", "adb", "aba", "ada")));
    }

    @Test
    void testAbs() {
        assertEquals(42, abs(42));
        assertEquals(42, abs(-42));
        assertEquals(Integer.MAX_VALUE, abs(Integer.MIN_VALUE + 1));
        assertThrows(ArithmeticException.class, () -> abs(Integer.MIN_VALUE));

        assertEquals(123456789123456789L, abs(123456789123456789L));
        assertEquals(123456789123456789L, abs(-123456789123456789L));
        assertEquals(Long.MAX_VALUE, abs(Long.MIN_VALUE + 1));
        assertEquals(Integer.MAX_VALUE + 1L, abs((long) Integer.MIN_VALUE));
        assertThrows(ArithmeticException.class, () -> abs(Long.MIN_VALUE));
    }

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

        assertTrue(isInRange('k', 'a', 'z'));
        assertTrue(isInRange(5, 3, 8));
        assertTrue(isInRange(3.0, Math.E, Math.PI));
    }

    @Test
    void testGcd() {
        assertEquals(1, gcd(3, 5));
        assertEquals(6, gcd(210, 36));
        assertEquals(6, gcd(36, 210));
        assertEquals(11, gcd(11, 0));
        assertEquals(11, gcd(0, 11));
        assertEquals(11, gcd(0, 11));
        assertEquals(1, gcd(1234L, 1111111111111111111L));
        assertEquals(3739,
                gcd(3739L * 3779L * 3889L * 4093L, 3739L * 3767L * 3821L * 4057L));
        assertEquals(3739 * 4057,
                gcd(3739L * 3779L * 3889L * 4057L, 3739L * 3767L * 3821L * 4057L));

        assertEquals(3, gcd(210, 36, 15));
        assertEquals(3, gcd(IntStream.of(15, 36, 210)));
        assertEquals(3, gcd(LongStream.of(15, 36, 210)));
        assertEquals(3, Utils.gcd(List.of(15, 36, 210)));
        assertEquals(3, Utils.gcd(List.of(15L, 36L, 210L)));
        assertEquals(35,
                gcd(3 * 3 * 5 * 7 * 11, 5 * 7 * 7 * 11 * 17, 2 * 2 * 5 * 5 * 7 * 19 * 29));
        assertEquals(4057,
                gcd(3739L * 3889L * 4057L, 3739L * 3767L * 4057L, 3767L * 3889L * 4057L));

        assertEquals(1234, gcd(1234));
        assertEquals(1234, gcd(List.of(1234)));
        assertEquals(0, gcd());
        assertEquals(0, gcd(List.of()));
    }

    @Test
    void testLcm() {
        assertEquals(15, lcm(3, 5));
        assertEquals(30, lcm(15, 6));
        assertEquals(3739L * 3821L * 3889L * 4057L,
                lcm(3739L * 3889L * 4057L, 3739L * 3821L * 4057L));
        assertEquals(3739L * 3821L * 3889L * 4057L * 4093L,
                lcm(3739L * 3889L * 4057L * 4093L, 3739L * 3821L * 4057L));
        assertEquals(3739L * 3739L * 3821L * 3889L * 4057L,
                lcm(3739L * 3889L * 4057L, 3739L * 3739L * 3821L * 4057L));

        assertEquals(1260, lcm(210, 36, 15));
        assertEquals(1260, lcm(IntStream.of(15, 36, 210)));
        assertEquals(1260, lcm(LongStream.of(15, 36, 210)));
        assertEquals(1260, Utils.lcm(List.of(15, 36, 210)));
        assertEquals(1260, Utils.lcm(List.of(15L, 36L, 210L)));
        assertEquals(2L * 2 * 3 * 3 * 5 * 5 * 7 * 7 * 11 * 17 * 19 * 29,
                lcm(3 * 3 * 5 * 7 * 11, 5 * 7 * 7 * 11 * 17, 2 * 2 * 5 * 5 * 7 * 19 * 29));
        assertEquals(3739L * 3767L * 3889L * 4057L,
                lcm(3739L * 3889L * 4057L, 3739L * 3767L * 4057L, 3767L * 3889L * 4057L));

        assertEquals(1234, lcm(1234));
        assertEquals(1234, lcm(List.of(1234)));
        assertEquals(1, lcm());
        assertEquals(1, lcm(List.of()));
    }

    // ****************************** COLLECTION AND STREAM UTILS ******************************

    @Test
    void testCountMethods() {
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

        assertEquals(setOf('h', 'e', 'l', 'o', 'c'), unionOf(c1, c2));
        assertEquals(setOf('h', 'e', 'l', 'o', 'v'), unionOf(c1, c3));
        assertEquals(setOf('h', 'e', 'l', 'o', 'd'), unionOf(c1, c4));
        assertEquals(setOf('e', 'c', 'h', 'o', 'l', 'v'), unionOf(c2.stream(), c3.stream()));
        assertEquals(setOf('e', 'c', 'h', 'o', 'l', 'd'), unionOf(c2.stream(), c4.stream()));
        assertEquals(setOf('l', 'o', 'v', 'e', 'd'), unionOf(c3.stream(), c4.stream()));
        assertEquals(setOf('h', 'e', 'l', 'o', 'c', 'v'), unionOf(List.of(c1, c2, c3)));
        assertEquals(setOf('h', 'e', 'l', 'o', 'v', 'd'), unionOf(List.of(c1, c3, c4)));
        assertEquals(setOf('h', 'e', 'l', 'o', 'c', 'v', 'd'), unionOf(List.of(c1, c2, c3, c4)));

        assertEquals(setOf('h', 'e', 'o'), intersectionOf(c1, c2));
        assertEquals(setOf('e', 'l', 'o'), intersectionOf(c1, c3));
        assertEquals(setOf('l', 'o'), intersectionOf(c1, c4));
        assertEquals(setOf('e', 'o'), intersectionOf(c2.stream(), c3.stream()));
        assertEquals(setOf('o'), intersectionOf(c2.stream(), c4.stream()));
        assertEquals(setOf('l', 'o'), intersectionOf(c3.stream(), c4.stream()));
        assertEquals(setOf('e', 'o'), intersectionOf(List.of(c1, c2, c3)));
        assertEquals(setOf('l', 'o'), intersectionOf(List.of(c1, c3, c4)));
        assertEquals(setOf('o'), intersectionOf(List.of(c1, c2, c3, c4)));
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
    void testListSlicing() {
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
    void testInverseMap() {
        var map = Map.of('a', 1, 'b', 2, 'c', 3);

        assertEquals(Map.of(1, 'a', 2, 'b', 3, 'c'), inverse(map));
        assertEquals(map, inverse(inverse(map)));
        assertEquals(Map.of(), inverse(Map.of()));
    }

    // ****************************** ARRAY UTILS ******************************

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
