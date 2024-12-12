package com.github.pkovacs.util.data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.pkovacs.util.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base class of test classes of table data structures.
 */
abstract class AbstractTableTest<T> {

    abstract AbstractTable<T> createTestTable(int width, int height);

    @Test
    void testBasicMethods() {
        var table = createTestTable(4, 3);

        assertEquals(4, table.width());
        assertEquals(3, table.height());
        assertEquals(12, table.size());
        assertEquals(12, table.cells().count());

        assertTrue(table.containsCell(p(3, 2)));
        assertFalse(table.containsCell(p(3, 3)));
        assertFalse(table.containsCell(p(4, 2)));

        var table2 = createTestTable(3, 4);
        table2.cells().forEach(p -> table2.set0(p.x, p.y, table.get0(p.y, p.x)));

        assertEquals(table.cells().map(p -> table.get0(p.x, p.y)).collect(Collectors.toSet()),
                table2.cells().map(p -> table2.get0(p.x, p.y)).collect(Collectors.toSet()));
    }

    @Test
    void testCellAccessMethods() {
        var table = createTestTable(4, 3);

        assertEquals(Stream.concat(table.row(0), Stream.concat(table.row(1), table.row(2))).toList(),
                table.cells().toList()); // cells are listed row by row
        assertEquals(Utils.intersectionOf(table.row(0), table.col(0)),
                Set.of(table.topLeft()));
        assertEquals(Utils.intersectionOf(table.row(2), table.col(0)),
                Set.of(table.bottomLeft()));
        assertEquals(Utils.intersectionOf(table.row(0), table.col(3)),
                Set.of(table.topRight()));
        assertEquals(Utils.intersectionOf(table.row(2), table.col(3)),
                Set.of(table.bottomRight()));
    }

    @Test
    void testNeighbors() {
        var table = createTestTable(3, 4);

        assertEquals(List.of(p(0, 2), p(1, 1), p(1, 3), p(2, 2)),
                table.neighbors(p(1, 2)).toList());
        assertEquals(List.of(p(0, 1), p(1, 0)),
                table.neighbors(p(0, 0)).toList());
        assertEquals(List.of(p(1, 1), p(2, 0), p(2, 2)),
                table.neighbors(p(2, 1)).toList());
        assertEquals(List.of(p(0, 0), p(0, 1), p(1, 0)),
                table.neighborsAndSelf(p(0, 0)).toList());

        assertEquals(List.of(p(0, 1), p(0, 2), p(0, 3), p(1, 1),
                        p(1, 3), p(2, 1), p(2, 2), p(2, 3)),
                table.neighbors8(p(1, 2)).toList());
        assertEquals(List.of(p(0, 1), p(1, 0), p(1, 1)),
                table.neighbors8(p(0, 0)).toList());
        assertEquals(List.of(p(1, 0), p(1, 1), p(1, 2), p(2, 0), p(2, 2)),
                table.neighbors8(p(2, 1)).toList());
        Pos result;
        int x = 0;
        result = p(1, x);
        assertEquals(List.of(p(0, 0), p(0, 1), result, p(1, 1)),
                table.neighbors8AndSelf(p(0, 0)).toList());
    }

    @Test
    void testRays() {
        var table = createTestTable(4, 5);
        var p = p(3, 2);

        assertEquals(List.of(p(3, 1), p(3, 0)), table.ray(p, Dir.N).toList());
        assertEquals(List.of(p(3, 3), p(3, 4)), table.ray(p, Dir.S).toList());
        assertEquals(List.of(), table.ray(p, Dir.E).toList());
        assertEquals(List.of(p(2, 2), p(1, 2), p(0, 2)), table.ray(p, Dir.W).toList());

        assertEquals(List.of(p(2, 1), p(1, 0)), table.ray(p, p(2, 1)).toList());
        assertEquals(List.of(), table.ray(p, p(4, 3)).toList());
        assertEquals(List.of(p(2, 3), p(1, 4)), table.ray(p, p(2, 3)).toList());
    }

    @Test
    void testTransformations() {
        var table = createTestTable(4, 3);
        var transposed = table.transpose();
        var right = table.rotateRight();
        var left = table.rotateLeft();

        assertEquals(table.rowCount(), transposed.colCount());
        assertEquals(table.colCount(), transposed.rowCount());
        assertEquals(table.rowCount(), right.colCount());
        assertEquals(table.colCount(), right.rowCount());
        assertEquals(table.rowCount(), left.colCount());
        assertEquals(table.colCount(), left.rowCount());

        assertNotEquals(transposed, right);
        assertNotEquals(transposed, left);
        assertNotEquals(right, left);

        assertEquals(table, transposed.transpose());
        assertEquals(table, right.rotateLeft());
        assertEquals(table, left.rotateRight());
        assertEquals(table, right.rotateRight().rotateRight().rotateRight());
        assertEquals(table, left.rotateLeft().rotateLeft().rotateLeft());
        assertEquals(table, right.rotateRight().mirrorVertically().mirrorHorizontally());
        assertEquals(table, left.rotateLeft().mirrorVertically().mirrorHorizontally());

        assertEquals(right.rotateRight(), left.rotateLeft());

        assertEquals(transposed, right.mirrorHorizontally());
        assertEquals(transposed, left.mirrorVertically());
        assertEquals(transposed, table.mirrorVertically().rotateRight());
        assertEquals(transposed, table.mirrorHorizontally().rotateLeft());
    }

    @Test
    void testEqualsAndHashCode() {
        var table1 = createTestTable(4, 3);
        var table2 = createTestTable(4, 3);
        var table3 = createTestTable(4, 3);

        assertEquals(table1, table2);
        assertEquals(table2, table3);
        assertEquals(table1, table3);
        assertEquals(table1.hashCode(), table2.hashCode());
        assertEquals(table2.hashCode(), table3.hashCode());
        assertEquals(table1.hashCode(), table3.hashCode());

        table2.set0(1, 1, table2.get0(0, 0));

        assertNotEquals(table1, table2);
        assertNotEquals(table2, table3);
        assertEquals(table1, table3);
        assertNotEquals(table1.hashCode(), table2.hashCode());
        assertNotEquals(table2.hashCode(), table3.hashCode());
        assertEquals(table1.hashCode(), table3.hashCode());
    }

    static Pos p(int x, int y) {
        return new Pos(x, y);
    }

}
