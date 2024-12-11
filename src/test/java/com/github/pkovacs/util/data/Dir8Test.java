package com.github.pkovacs.util.data;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Dir8Test {

    @Test
    void test() {
        assertEquals(Dir8.N, Dir8.S.opposite());
        assertEquals(Dir8.W, Dir8.E.opposite());
        assertEquals(Dir8.NW, Dir8.SE.opposite());
        assertEquals(Dir8.NE, Dir8.SW.opposite());

        assertEquals(Dir8.S, Dir8.S.mirrorHorizontally());
        assertEquals(Dir8.W, Dir8.E.mirrorHorizontally());
        assertEquals(Dir8.SW, Dir8.SE.mirrorHorizontally());
        assertEquals(Dir8.N, Dir8.S.mirrorVertically());
        assertEquals(Dir8.E, Dir8.E.mirrorVertically());
        assertEquals(Dir8.NE, Dir8.SE.mirrorVertically());

        var dirs = Dir8.values();
        for (int i = 0; i < 8; i++) {
            var dir1 = dirs[i];
            var dir2 = dirs[(i + 1) % 8];
            assertEquals(dir2, dir1.next());
            assertEquals(dir1, dir2.prev());
        }

        Arrays.stream(dirs).forEach(dir -> {
            assertEquals(dir, dir.opposite().opposite());
            assertEquals(dir, dir.next().next().next().next().opposite());
            assertEquals(dir, dir.prev().prev().opposite().prev().prev());
            assertEquals(dir, dir.next().next().opposite().next().next());
            assertEquals(dir, dir.opposite().prev().opposite().next());
            assertEquals(dir, dir.mirrorVertically().mirrorVertically());
            assertEquals(dir, dir.mirrorHorizontally().mirrorHorizontally());
            assertEquals(dir.opposite(), dir.mirrorHorizontally().mirrorVertically());
            assertEquals(dir.opposite(), dir.mirrorVertically().mirrorHorizontally());
        });
    }

}
