package pkovacs.util.data;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VectorTest {

    @Test
    void test2d() {
        var a = Vector.ORIGIN;
        var b = new Vector(42, 12);

        assertEquals(b, a.add(b));

        a = a.add(b).sub(new Vector(2, 2));
        assertEquals(new Vector(40, 10), a);
        assertEquals(50, a.dist());
        assertEquals(new Vector(-40, -10), a.negate());
        assertEquals(50, a.negate().dist());

        assertEquals(new Vector(10, -40), a.rotateRight());
        assertEquals(new Vector(-40, -10), a.rotateRight().rotateRight());
        assertEquals(new Vector(-10, 40), a.rotateRight().rotateRight().rotateRight());
        assertEquals(new Vector(40, 10), a.rotateRight().rotateRight().rotateRight().rotateRight());

        assertEquals(new Vector(-10, 40), a.rotateLeft());
        assertEquals(new Vector(-40, -10), a.rotateLeft().rotateLeft());
        assertEquals(new Vector(10, -40), a.rotateLeft().rotateLeft().rotateLeft());
        assertEquals(new Vector(40, 10), a.rotateLeft().rotateLeft().rotateLeft().rotateLeft());

        var c = new Vector(42, 12);
        var d = new Vector(42, 12);
        assertEquals(0, Vector.dist(c, d));
        d = d.rotateRight();
        assertEquals(42 + 12 + 30, Vector.dist(c, d));
        c = c.rotateLeft();
        assertEquals(c.dist() + d.dist(), c.dist(d));
        c = c.negate();
        assertEquals(0, c.dist(d));

        var e = new Vector(42, 12);
        assertEquals(Vector.ORIGIN, e.multiply(0));
        assertEquals(e, e.multiply(1));
        assertEquals(e.add(e), e.multiply(2));
        assertEquals(e.add(e).add(e).add(e).add(e), e.multiply(5));

        assertEquals(Vector.ORIGIN, Vector.origin(2));
        assertEquals(Vector.ORIGIN, Vector.ORIGIN.negate());
        assertEquals(Vector.ORIGIN, Vector.ORIGIN.rotateRight());
        assertEquals(Vector.NORTH, Vector.ORIGIN.add(Vector.NORTH));
        assertEquals(Vector.SOUTH, Vector.fromDirection(Direction.SOUTH).add(Vector.ORIGIN));
        assertEquals(Vector.fromChar('R'), Vector.fromChar('n').rotateRight());
        assertEquals(Vector.fromChar('L'), Vector.fromChar('s').negate().rotateLeft());

        assertEquals(new Vector(-42, 12), e.mirrorHorizontally());
        assertEquals(new Vector(42, -12), e.mirrorVertically());
        assertEquals(e, e.mirrorHorizontally().mirrorHorizontally());
        assertEquals(e, e.mirrorVertically().mirrorVertically());
        assertEquals(e.negate(), e.mirrorHorizontally().mirrorVertically());
        assertEquals(e.negate(), e.mirrorVertically().mirrorHorizontally());

        assertEquals("(42, 12)", new Vector(42, 12).toString());
        assertEquals("(0, 1)", Vector.NORTH.toString());
        assertEquals("(0, -1)", Vector.SOUTH.toString());
    }

    @Test
    void test3d() {
        var a = Vector.origin(3);
        var b = new Vector(42, 12, 314);

        assertEquals(3, a.dim());
        assertEquals(3, b.dim());
        assertEquals(b, a.add(b));

        a = a.add(b).sub(new Vector(2, 2, 14));
        assertEquals(new Vector(40, 10, 300), a);
        assertEquals(new Vector(-40, -10, -300), a.negate());

        var c = new Vector(42, 12, -3);
        assertEquals(Vector.origin(c.dim()), c.multiply(0));
        assertEquals(c, c.multiply(1));
        assertEquals(c.add(c), c.multiply(2));
        assertEquals(c.add(c).add(c).add(c).add(c), c.multiply(5));
        assertEquals(c.add(c.multiply(7)).sub(c.multiply(4)), c.multiply(4));

        assertEquals(42 + 12 + 3, c.dist());
        assertEquals(c.dist(), c.negate().dist());
        assertEquals(c.dist() * 3, c.negate().add(c.multiply(4)).dist());

        assertEquals("(42, 12, -3)", c.toString());

        assertThrows(UnsupportedOperationException.class, c::rotateLeft);
        assertThrows(UnsupportedOperationException.class, c::rotateRight);
    }

    @Test
    void testGeneral() {
        var a = Vector.origin(12);
        var b = new Vector(new long[] { 0, -1, 2, -3, 4, -5, 6, -7, 8, -9, 10, -11 });

        assertEquals(12, a.dim());
        assertEquals(12, b.dim());

        assertEquals(0, a.dist());
        assertEquals(66, b.dist());

        assertEquals(b, a.add(b));

        var c = new Vector(42, 12, -3);
        assertEquals(Vector.origin(c.dim()), c.multiply(0));
        assertEquals(c, c.multiply(1));
        assertEquals(c.add(c), c.multiply(2));
        assertEquals(c.add(c).add(c).add(c).add(c), c.multiply(5));
        assertEquals(c.add(c.multiply(7)).sub(c.multiply(4)), c.multiply(4));

        assertEquals(42 + 12 + 3, c.dist());
        assertEquals(c.dist(), c.negate().dist());
        assertEquals(c.dist() * 3, c.negate().add(c.multiply(4)).dist());

        assertEquals("(0, -1, 2, -3, 4, -5, 6, -7, 8, -9, 10, -11)", b.toString());

        assertThrows(UnsupportedOperationException.class, c::rotateLeft);
        assertThrows(UnsupportedOperationException.class, c::rotateRight);
    }

    @Test
    void testOrdering() {
        var list = List.of(
                new Vector(42, 12),
                new Vector(41, 12),
                new Vector(42, 11),
                new Vector(42, 13),
                new Vector(43, 12),
                new Vector(42, 12, 3),
                new Vector(42, 12, 2),
                new Vector(42, 12, 1),
                new Vector(5, 8, 1, 0),
                new Vector(5, 8, 1, -1),
                new Vector(5, 8, 1, 1),
                new Vector(5, 8, 0, 0));
        var sortedList = List.of(
                new Vector(41, 12),
                new Vector(42, 11),
                new Vector(42, 12),
                new Vector(42, 13),
                new Vector(43, 12),
                new Vector(42, 12, 1),
                new Vector(42, 12, 2),
                new Vector(42, 12, 3),
                new Vector(5, 8, 0, 0),
                new Vector(5, 8, 1, -1),
                new Vector(5, 8, 1, 0),
                new Vector(5, 8, 1, 1));

        assertEquals(sortedList, list.stream().sorted().toList());
    }

}
