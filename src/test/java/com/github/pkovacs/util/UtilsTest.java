package com.github.pkovacs.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @Test
    void testSetOperations() {
        var c1 = Utils.setOf("hello");
        var c2 = Utils.setOf("echo");
        var c3 = Utils.listOf("love");
        var c4 = Utils.listOf("old");

        assertEquals(Utils.setOf("heloch"), Utils.unionOf(c1, c2));
        assertEquals(Utils.setOf("helov"), Utils.unionOf(c1, c3));
        assertEquals(Utils.setOf("helod"), Utils.unionOf(c1, c4));
        assertEquals(Utils.setOf("echolv"), Utils.unionOf(c2.stream(), c3.stream()));
        assertEquals(Utils.setOf("echold"), Utils.unionOf(c2.stream(), c4.stream()));
        assertEquals(Utils.setOf("loved"), Utils.unionOf(c3.stream(), c4.stream()));
        assertEquals(Utils.setOf("helochv"), Utils.unionOf(List.of(c1, c2, c3)));
        assertEquals(Utils.setOf("helovd"), Utils.unionOf(List.of(c1, c3, c4)));
        assertEquals(Utils.setOf("helochvd"), Utils.unionOf(List.of(c1, c2, c3, c4)));

        assertEquals(Utils.setOf("heo"), Utils.intersectionOf(c1, c2));
        assertEquals(Utils.setOf("elo"), Utils.intersectionOf(c1, c3));
        assertEquals(Utils.setOf("lo"), Utils.intersectionOf(c1, c4));
        assertEquals(Utils.setOf("eo"), Utils.intersectionOf(c2.stream(), c3.stream()));
        assertEquals(Utils.setOf("o"), Utils.intersectionOf(c2.stream(), c4.stream()));
        assertEquals(Utils.setOf("lo"), Utils.intersectionOf(c3.stream(), c4.stream()));
        assertEquals(Utils.setOf("eo"), Utils.intersectionOf(List.of(c1, c2, c3)));
        assertEquals(Utils.setOf("lo"), Utils.intersectionOf(List.of(c1, c3, c4)));
        assertEquals(Utils.setOf("o"), Utils.intersectionOf(List.of(c1, c2, c3, c4)));
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
