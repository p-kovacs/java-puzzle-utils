package com.github.pkovacs.util.data;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectionTest {

    @Test
    void test() {
        for (char ch : new char[] { 'N', 'E', 'S', 'W' }) {
            assertEquals(ch, Direction.fromChar(ch).toChar());
        }
        for (char ch : new char[] { 'n', 'e', 's', 'w' }) {
            assertEquals(ch, Direction.fromChar(ch).toLowerCaseChar());
        }

        Map.of("U", 'N', "R", 'E', "D", 'S', "L", 'W').forEach((str, ch) -> {
            assertEquals(ch, Direction.fromChar(str.charAt(0)).toChar());
            assertEquals(ch, Direction.fromChar(str.toLowerCase(Locale.ROOT).charAt(0)).toChar());
        });

        assertEquals(Direction.NORTH, Direction.SOUTH.opposite());
        assertEquals(Direction.WEST, Direction.EAST.opposite());

        assertEquals(Direction.SOUTH, Direction.SOUTH.mirrorHorizontally());
        assertEquals(Direction.WEST, Direction.EAST.mirrorHorizontally());
        assertEquals(Direction.NORTH, Direction.SOUTH.mirrorVertically());
        assertEquals(Direction.EAST, Direction.EAST.mirrorVertically());

        var dirs = Direction.values();
        for (int i = 0; i < 4; i++) {
            var dir1 = dirs[i];
            var dir2 = dirs[(i + 1) % 4];
            assertEquals(dir2, dir1.rotateRight());
            assertEquals(dir1, dir2.rotateLeft());
            assertEquals(dir1.isHorizontal(), dir2.isVertical());
        }

        Arrays.stream(dirs).forEach(dir -> {
            assertEquals(dir, dir.opposite().opposite());
            assertEquals(dir.opposite(), dir.rotateRight().rotateRight());
            assertEquals(dir.rotateLeft(), dir.rotateRight().rotateRight().rotateRight());
            assertEquals(dir, dir.rotateLeft().opposite().rotateLeft());
            assertEquals(dir.rotateRight(), dir.opposite().rotateLeft());
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
