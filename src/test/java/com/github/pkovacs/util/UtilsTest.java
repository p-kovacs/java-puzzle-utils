package com.github.pkovacs.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @Test
    void testChars() {
        assertEquals(List.of('h', 'e', 'l', 'l', 'o'), Utils.charsOf("hello").toList());
        assertEquals(2, Utils.charsOf("hello").filter(c -> c == 'l').count());

        assertEquals(List.of('h', 'e', 'l', 'l', 'o'), Utils.listOf("hello".toCharArray()));
        assertEquals(Set.of('h', 'e', 'l', 'o'), Utils.setOf("hello".toCharArray()));
        assertEquals(3, Utils.streamOf("hello".toCharArray()).filter(c -> c != 'l').count());
    }

    @Test
    void testSetOperations() {
        var c1 = Utils.setOf("hello".toCharArray());
        var c2 = Utils.setOf("echo".toCharArray());
        var c3 = Utils.listOf("love".toCharArray());
        var c4 = Utils.listOf("old".toCharArray());

        assertEquals(Set.of('h', 'e', 'l', 'o', 'c'), Utils.unionOf(c1, c2));
        assertEquals(Set.of('h', 'e', 'l', 'o', 'v'), Utils.unionOf(c1, c3));
        assertEquals(Set.of('h', 'e', 'l', 'o', 'd'), Utils.unionOf(c1, c4));
        assertEquals(Set.of('e', 'c', 'h', 'o', 'l', 'v'), Utils.unionOf(c2.stream(), c3.stream()));
        assertEquals(Set.of('e', 'c', 'h', 'o', 'l', 'd'), Utils.unionOf(c2.stream(), c4.stream()));
        assertEquals(Set.of('l', 'o', 'v', 'e', 'd'), Utils.unionOf(c3.stream(), c4.stream()));
        assertEquals(Set.of('h', 'e', 'l', 'o', 'c', 'v'), Utils.unionOf(List.of(c1, c2, c3)));
        assertEquals(Set.of('h', 'e', 'l', 'o', 'v', 'd'), Utils.unionOf(List.of(c1, c3, c4)));
        assertEquals(Set.of('h', 'e', 'l', 'o', 'c', 'v', 'd'), Utils.unionOf(List.of(c1, c2, c3, c4)));

        assertEquals(Set.of('h', 'e', 'o'), Utils.intersectionOf(c1, c2));
        assertEquals(Set.of('e', 'l', 'o'), Utils.intersectionOf(c1, c3));
        assertEquals(Set.of('l', 'o'), Utils.intersectionOf(c1, c4));
        assertEquals(Set.of('e', 'o'), Utils.intersectionOf(c2.stream(), c3.stream()));
        assertEquals(Set.of('o'), Utils.intersectionOf(c2.stream(), c4.stream()));
        assertEquals(Set.of('l', 'o'), Utils.intersectionOf(c3.stream(), c4.stream()));
        assertEquals(Set.of('e', 'o'), Utils.intersectionOf(List.of(c1, c2, c3)));
        assertEquals(Set.of('l', 'o'), Utils.intersectionOf(List.of(c1, c3, c4)));
        assertEquals(Set.of('o'), Utils.intersectionOf(List.of(c1, c2, c3, c4)));
    }

    @Test
    void testGenericParametersOfSetOperations() {
        var a = List.of(List.of(1, 2), List.of(1, 2, 3));
        var b = Set.of(new ArrayList<>(List.of(1)), new ArrayList<>(List.of(1, 2)));
        var c = Set.of(new LinkedList<>(List.of(1)), new LinkedList<>(List.of(1, 2, 3)));

        var union = Set.of(List.of(1), List.of(1, 2), List.of(1, 2, 3));
        assertEquals(union, Utils.unionOf(a, b));
        assertEquals(union, Utils.unionOf(b.stream(), a.stream()));
        assertEquals(union, Utils.unionOf(a, c));
        assertEquals(union, Utils.unionOf(c.stream(), a.stream()));
        assertEquals(union, Utils.unionOf(b, c));
        assertEquals(union, Utils.unionOf(c.stream(), b.stream()));
        assertEquals(union, Utils.unionOf(List.of(a, b, c)));
        assertEquals(union, Utils.unionOf(List.of(c, b, a)));

        assertEquals(Set.of(List.of(1, 2)), Utils.intersectionOf(a, b));
        assertEquals(Set.of(List.of(1, 2)), Utils.intersectionOf(b.stream(), a.stream()));
        assertEquals(Set.of(List.of(1, 2, 3)), Utils.intersectionOf(a, c));
        assertEquals(Set.of(List.of(1, 2, 3)), Utils.intersectionOf(c.stream(), a.stream()));
        assertEquals(Set.of(List.of(1)), Utils.intersectionOf(b, c));
        assertEquals(Set.of(List.of(1)), Utils.intersectionOf(c.stream(), b.stream()));
        assertEquals(Set.of(), Utils.intersectionOf(List.of(a, b, c)));
        assertEquals(Set.of(), Utils.intersectionOf(List.of(c, b, a)));
    }

}
