package com.github.pkovacs.util;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputUtilsTest extends InputUtils {

    @Test
    void testCollectLineBlocks() {
        String input = "a\nb c d\ne\n\n\n\nf g\nh\n\ni j k";
        var blocks = collectLineBlocks(input);

        assertEquals(3, blocks.size());
        assertEquals(List.of("a", "b c d", "e"), blocks.get(0));
        assertEquals(List.of("f g", "h"), blocks.get(1));
        assertEquals(List.of("i j k"), blocks.get(2));

        assertEquals(3, collectLineBlocks(input + "\n").size());
        assertEquals(3, collectLineBlocks(input + "\n\n\n\n").size());

        String inputWin = "a\r\nb c d\r\ne\r\n\r\nf g\r\nh\r\n\r\ni j k";
        var blocks2 = collectLineBlocks(inputWin);
        assertEquals(blocks, blocks2);
    }

    @Test
    void testParseIntegers() {
        String input1 = "5 apples and 12 bananas. -42 is the opposite of 42.";
        String input2 = "-1-2+3, 5-10, 6+-12. A23, B-34. [-100,+200]";
        assertArrayEquals(new int[] { 5, 12, -42, 42 }, parseInts(input1));
        assertArrayEquals(new long[] { 5, 12, -42, 42 }, parseLongs(input1));
        assertArrayEquals(new int[] { -1, 2, 3, 5, 10, 6, -12, 23, 34, -100, 200 }, parseInts(input2));
        assertArrayEquals(new long[] { -1, 2, 3, 5, 10, 6, -12, 23, 34, -100, 200 }, parseLongs(input2));
    }

    @Test
    void testParseIntFromChar() {
        assertEquals(0, parseInt('0'));
        assertEquals(5, parseInt('5'));
        assertEquals(10, parseInt('a'));
        assertEquals(10, parseInt('A'));
        assertEquals(15, parseInt('f'));
        assertEquals(15, parseInt('F'));
        assertEquals(35, parseInt('z'));
        assertEquals(35, parseInt('Z'));
    }

    @Test
    void testParse() {
        var values = parse("Product PID_4242X is ordered.", ".*PID_%d%c is %s[.]");

        assertEquals("[4242, X, ordered]", values.toString());

        assertEquals(3, values.size());
        assertTrue(values.get(0).isLong());
        assertEquals(4242, values.get(0).toInt());
        assertEquals(4242L, values.get(0).toLong());
        assertTrue(values.get(1).isChar());
        assertEquals('X', values.get(1).toChar());
        assertTrue(values.get(2).isString());
        assertEquals("ordered", values.get(2).get());
        assertEquals("ordered", values.get(2).toString());
    }

}
