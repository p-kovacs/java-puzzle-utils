package pkovacs.util.data;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PointTest {

    @Test
    public void test() {
        var a = new Point(42, 12);
        var b = new Point(12, 42);
        var c = new Point(12, 42);

        assertEquals(42, a.x());
        assertEquals(12, a.y());
        assertNotEquals(a, b);
        assertEquals(b, c);

        assertTrue(a.isValid(43, 13));
        assertFalse(a.isValid(43, 12));
        assertFalse(a.isValid(42, 13));

        assertEquals(Set.of(
                        new Point(42, 11),
                        new Point(43, 12),
                        new Point(42, 13),
                        new Point(41, 12)),
                new HashSet<>(a.neighbors()));
        assertEquals(Set.of(new Point(42, 11), new Point(41, 12)),
                new HashSet<>(a.neighbors(n -> n.x() <= a.x() && n.y() <= a.y())));

        assertTrue(a.neighbors().stream().allMatch(n -> Point.dist(a, n) == 1));
        assertEquals(4, a.validNeighbors(44, 14).size());
        assertEquals(2, a.validNeighbors(43, 13).size());
        assertEquals(1, a.validNeighbors(43, 12).size());
        assertEquals(1, a.validNeighbors(42, 13).size());
        assertEquals(0, a.validNeighbors(42, 12).size());
    }

}
