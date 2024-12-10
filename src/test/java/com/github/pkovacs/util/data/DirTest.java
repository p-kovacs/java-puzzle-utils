package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DirTest {

    @Test
    void test() {
        for (char ch : new char[] { 'N', 'E', 'S', 'W' }) {
            assertEquals(ch, Dir.fromChar(ch).toChar());
        }
        for (char ch : new char[] { 'n', 'e', 's', 'w' }) {
            assertEquals(ch, Dir.fromChar(ch).toLowerCaseChar());
        }

        Map.of("U", 'N', "R", 'E', "D", 'S', "L", 'W').forEach((str, ch) -> {
            assertEquals(ch, Dir.fromChar(str.charAt(0)).toChar());
            assertEquals(ch, Dir.fromChar(str.toLowerCase(Locale.ROOT).charAt(0)).toChar());
        });

        assertEquals(Dir.NORTH, Dir.SOUTH.opposite());
        assertEquals(Dir.WEST, Dir.EAST.opposite());

        assertEquals(Dir.SOUTH, Dir.SOUTH.mirrorHorizontally());
        assertEquals(Dir.WEST, Dir.EAST.mirrorHorizontally());
        assertEquals(Dir.NORTH, Dir.SOUTH.mirrorVertically());
        assertEquals(Dir.EAST, Dir.EAST.mirrorVertically());

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
