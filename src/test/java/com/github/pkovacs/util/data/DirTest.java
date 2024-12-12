package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DirTest {

    @Test
    void test() {
        Map.of("NORTH", Dir.N, "EAST", Dir.E, "SOUTH", Dir.S, "WEST", Dir.W,
                        "north", Dir.N, "east", Dir.E, "south", Dir.S, "west", Dir.W)
                .forEach((str, dir) -> assertEquals(dir, Dir.fromString(str)));
        Map.of("N", Dir.N, "E", Dir.E, "S", Dir.S, "W", Dir.W)
                .forEach((str, dir) -> assertEquals(dir, Dir.fromString(str)));
        Map.of("U", Dir.N, "R", Dir.E, "D", Dir.S, "L", Dir.W,
                        "^", Dir.N, ">", Dir.E, "v", Dir.S, "<", Dir.W)
                .forEach((str, dir) -> assertEquals(dir, Dir.fromString(str)));

        for (char ch : new char[] { 'N', 'E', 'S', 'W' }) {
            assertEquals("" + ch, Dir.fromChar(ch).toString());
        }

        assertEquals(Dir.N, Dir.S.opposite());
        assertEquals(Dir.W, Dir.E.opposite());

        assertEquals(Dir.S, Dir.S.mirrorHorizontally());
        assertEquals(Dir.W, Dir.E.mirrorHorizontally());
        assertEquals(Dir.N, Dir.S.mirrorVertically());
        assertEquals(Dir.E, Dir.E.mirrorVertically());

        var dirs = Dir.values();
        for (int i = 0; i < 4; i++) {
            var dir1 = dirs[i];
            var dir2 = dirs[(i + 1) % 4];
            assertEquals(dir2, dir1.rotateRight());
            assertEquals(dir1, dir2.rotateLeft());
            assertEquals(dir1.isHorizontal(), dir2.isVertical());
        }

        Arrays.stream(dirs).forEach(dir -> {
            assertEquals(dir, dir.opposite().opposite());
            assertEquals(dir, dir.rotateRight().rotateRight().opposite());
            assertEquals(dir, dir.rotateLeft().opposite().rotateLeft());
            assertEquals(dir, dir.opposite().rotateLeft().rotateLeft());
            assertEquals(dir.rotateLeft(), dir.rotateRight().rotateRight().rotateRight());
            assertEquals(dir.rotateRight(), dir.rotateLeft().rotateLeft().rotateLeft());
            assertEquals(dir, dir.mirrorVertically().mirrorVertically());
            assertEquals(dir, dir.mirrorHorizontally().mirrorHorizontally());
            assertEquals(dir.opposite(), dir.mirrorHorizontally().mirrorVertically());
            assertEquals(dir.opposite(), dir.mirrorVertically().mirrorHorizontally());
            assertEquals(dir.isHorizontal(), !dir.isVertical());
            assertEquals(dir.isHorizontal(), dir.rotateLeft().isVertical());
            assertEquals(dir.isHorizontal(), dir.rotateRight().isVertical());
        });
    }

}
