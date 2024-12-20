package com.github.pkovacs.util;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BacktrackingTest {

    @Test
    void testPermutations() {
        var permutations = Backtracking.findAllPermutations(6);
        assertEquals(720, permutations.size());
        assertTrue(permutations.stream().allMatch(BacktrackingTest::isPermutation));
    }

    private static boolean isPermutation(int[] x) {
        int[] a = x.clone();
        Arrays.sort(a);
        return a[0] == 0 && IntStream.range(1, a.length).allMatch(i -> a[i] == a[i - 1] + 1);
    }

    @Test
    void testSubsets() {
        var subsets = Backtracking.findAll(10, 2, (x, k) -> true);
        assertEquals(1024, subsets.size());
        assertTrue(subsets.stream().allMatch(x -> Arrays.stream(x).allMatch(v -> v == 0 || v == 1)));
    }

    @Test
    void testEightQueens() {
        assertTrue(Backtracking.findFirstDistinct(1, 1, BacktrackingTest::isSafeQueen).isPresent());
        assertTrue(Backtracking.findFirstDistinct(2, 2, BacktrackingTest::isSafeQueen).isEmpty());
        assertTrue(Backtracking.findFirstDistinct(3, 3, BacktrackingTest::isSafeQueen).isEmpty());
        assertTrue(Backtracking.findFirstDistinct(4, 4, BacktrackingTest::isSafeQueen).isPresent());

        assertArrayEquals(new int[] { 0, 4, 7, 5, 2, 6, 1, 3 },
                Backtracking.findFirstDistinct(8, 8, BacktrackingTest::isSafeQueen).orElseThrow());
        assertEquals(92, Backtracking.findAllDistinct(8, 8, BacktrackingTest::isSafeQueen).size());
    }

    private static boolean isSafeQueen(int[] array, int k) {
        return IntStream.range(0, k).noneMatch(i -> Math.abs(array[i] - array[k]) == k - i);
    }

}
