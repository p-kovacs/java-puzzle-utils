package com.github.pkovacs.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BfsTest {

    @Test
    void testWithSimpleGraph() {
        var graph = new HashMap<String, List<String>>();
        graph.put("A", List.of("B", "C", "D"));
        graph.put("B", List.of("E"));
        graph.put("C", List.of("E"));
        graph.put("D", List.of("G"));
        graph.put("E", List.of("D", "F", "G"));
        graph.put("F", List.of("B", "G"));
        graph.put("G", List.of());

        assertEquals(0, Bfs.dist(u -> graph.get(u).stream(), "A", "A"::equals));
        assertEquals(1, Bfs.dist(u -> graph.get(u).stream(), "A", "B"::equals));
        assertEquals(3, Bfs.dist(u -> graph.get(u).stream(), "A", "F"::equals));
        assertEquals(2, Bfs.dist(u -> graph.get(u).stream(), "A", "G"::equals));

        var paths = Bfs.findPaths(Graph.of(graph), "A");

        assertEquals(7, paths.size());
        assertEquals(0, paths.get("A").dist());
        assertEquals(1, paths.get("B").dist());
        assertEquals(1, paths.get("C").dist());
        assertEquals(2, paths.get("G").dist());

        var result1 = Bfs.findPath(u -> graph.get(u).stream(), "A", "G"::equals);

        assertTrue(result1.isPresent());
        assertEquals("G", result1.get().end());
        assertEquals(2, result1.get().dist());
        assertEquals(List.of("A", "D", "G"), result1.get().nodes());

        graph.put("A", List.of("B", "C", "D", "G"));
        var result2 = Bfs.findPath(Graph.of(graph), "A", "G"::equals);

        assertTrue(result2.isPresent());
        assertEquals("G", result2.get().end());
        assertEquals(1, result2.get().dist());
        assertEquals(List.of("A", "G"), result2.get().nodes());

        var result3 = Bfs.findPath(u -> graph.get(u).stream(), "A", "A"::equals);

        assertTrue(result3.isPresent());
        assertEquals(0, result3.get().dist());
        assertEquals(List.of("A"), result3.get().nodes());

        var result4 = Bfs.findPath(Graph.of(graph), "B", "C"::equals);

        assertTrue(result4.isEmpty());
    }

    @Test
    void testWithMaze() {
        // We have to find the shortest path in a maze from the top left tile to the bottom right tile.
        var input = AbstractShortestPathTest.MAZE.lines().toList();
        var maze = new CharTable(input);
        var start = maze.topLeft();
        var end = maze.bottomRight();

        var result = Bfs.findPath(maze.graph(c -> c == '.'), start, end::equals);

        assertTrue(result.isPresent());
        assertEquals(end, result.get().end());
        assertEquals(50, result.get().dist());

        var path = result.get().nodes();
        assertEquals(51, path.size());
        assertEquals(start, path.get(0));
        assertEquals(end, path.get(path.size() - 1));
    }

    @Test
    void testTableGraphs() {
        var table = new CharTable(List.of(".......", "..#....", "..#...E", "S.#...."));
        var start = table.find('S');
        var end = table.find('E');

        assertEquals(7, Bfs.dist(table.graph(), start, end::equals));
        assertEquals(6, Bfs.dist(table.graph8(), start, end::equals));

        assertEquals(11, Bfs.dist(table.graph(c -> c != '#'), start, end::equals));
        assertEquals(7, Bfs.dist(table.graph8(c -> c != '#'), start, end::equals));

        assertEquals(table.size() - table.count('#'), Bfs.findPaths(table.graph(c -> c != '#'), start).size());
        assertEquals(table.size() - table.count('#'), Bfs.findPaths(table.graph8(c -> c != '#'), end).size());
    }

    @Test
    void testWithJugs() {
        // A simple puzzle also featured in the movie "Die Hard 3". :)
        // We have a 3-liter jug, a 5-liter jug, and a fountain. Let's measure 4 liters of water.
        // BFS algorithm can be used for finding the optimal path in an "implicit graph": the nodes represent
        // valid states, and the edges represent state transformations (steps). The graph is not generated
        // explicitly, but the next states are generated on-the-fly during the traversal.

        record State(int a, int b) {}

        var result = Bfs.findPath(state -> {
            var list = new ArrayList<State>();
            list.add(new State(3, state.b())); // 3-liter jug <-- fountain
            list.add(new State(state.a(), 5)); // 5-liter jug <-- fountain
            list.add(new State(0, state.b())); // 3-liter jug --> fountain
            list.add(new State(state.a(), 0)); // 5-liter jug --> fountain
            int d1 = Math.min(3 - state.a(), state.b());
            list.add(new State(state.a() + d1, state.b() - d1)); // 3-liter jug <-- 5-liter jug
            int d2 = Math.min(5 - state.b(), state.a());
            list.add(new State(state.a() - d2, state.b() + d2)); // 3-liter jug --> 5-liter jug
            return list.stream();
        }, new State(0, 0), pair -> pair.b() == 4);

        assertTrue(result.isPresent());
        assertEquals(6, result.get().dist());
        assertEquals(List.of(
                        new State(0, 0),
                        new State(0, 5),
                        new State(3, 2),
                        new State(0, 2),
                        new State(2, 0),
                        new State(2, 5),
                        new State(3, 4)),
                result.get().nodes());
    }

    @Test
    void testWithTheoreticallyInfiniteGraph() {
        var result1 = Bfs.findPath(i -> Stream.of(i + 1, 2 * i), 0, i -> i == 128);
        var result2 = Bfs.findPath(i -> Stream.of(i + 1, 2 * i), 0, i -> i == 127);
        var result3 = Bfs.findPath(i -> Stream.of(i + 1, 2 * i), 0, i -> i == 42);
        var result4 = Bfs.findPath(i -> Stream.of(i + 1, 2 * i), 0, i -> i == 137);

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertTrue(result3.isPresent());
        assertTrue(result4.isPresent());
        assertEquals(List.of(0, 1, 2, 4, 8, 16, 32, 64, 128), result1.get().nodes());
        assertEquals(List.of(0, 1, 2, 3, 6, 7, 14, 15, 30, 31, 62, 63, 126, 127), result2.get().nodes());
        assertEquals(List.of(0, 1, 2, 4, 5, 10, 20, 21, 42), result3.get().nodes());
        assertEquals(List.of(0, 1, 2, 4, 8, 16, 17, 34, 68, 136, 137), result4.get().nodes());
    }

    @Test
    void testMultipleTargets() {
        var nodes = new ArrayList<>(IntStream.range(0, 100).boxed().toList());
        Collections.shuffle(nodes, new Random(123456789));

        var result = Bfs.findPath(
                i -> IntStream.rangeClosed(nodes.indexOf(i), nodes.indexOf(i) + 7).mapToObj(nodes::get),
                nodes.get(0), i -> nodes.indexOf(i) >= 42);

        assertTrue(result.isPresent());
        assertEquals(6, result.get().dist());
    }

    @Test
    void testMultipleSources() {
        var result = Bfs.findPathFromAny(i -> Stream.of(i - 3, i - 7),
                IntStream.range(82, 100).boxed(), i -> i == 42);

        assertTrue(result.isPresent());
        assertEquals(6, result.get().dist());
        assertEquals(List.of(84, 77, 70, 63, 56, 49, 42), result.get().nodes());
    }

    @Test
    void testWithDirections() {
        var result = Bfs.findPathFromAny(dir -> Stream.of(dir.prev(), dir.next()),
                Stream.of(Dir8.NW, Dir8.N), Dir8.SE::equals);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().dist());
        assertEquals(List.of(Dir8.N, Dir8.NE, Dir8.E, Dir8.SE), result.get().nodes());
    }

    @Test
    void testGenericParameters1() {
        Function<Collection<Integer>, Stream<List<Integer>>> neighborProvider =
                c -> IntStream.rangeClosed(0, 3).mapToObj(i -> new ArrayList<>(concat(c, i).toList()));

        var start = List.of(1, 0);
        var target = List.of(1, 0, 1, 0, 0, 1, 2);
        Predicate<List<Integer>> predicate = target::equals;

        // Check different ways of defining the graph
        var path = Bfs.findPath(neighborProvider::apply, start, predicate);
        var path2 = Bfs.findPath(
                c -> IntStream.rangeClosed(0, 3).mapToObj(i -> new ArrayList<>(concat(c, i).toList())),
                start, predicate);
        var path3 = Bfs.findPath(Graph.of(neighborProvider), start, predicate);

        assertTrue(path.isPresent());
        assertEquals(5, path.get().dist());
        assertEquals(target, path.get().end());
        assertEquals(path.get().nodes(), path2.orElseThrow().nodes());
        assertEquals(path.get().nodes(), path3.orElseThrow().nodes());
    }

    @Test
    void testGenericParameters2() {
        assertEquals(1,
                Bfs.findPaths(c -> Stream.of(List.of(42)), List.of(42)).size());
        assertEquals(1,
                Bfs.findPaths(c -> Stream.of(new ArrayList<>(List.of(42))), List.of(42)).size());
        assertEquals(1,
                Bfs.<List<Integer>>findPaths(c -> Stream.of(List.of(42)), new ArrayList<>(List.of(42))).size());
    }

    private static Stream<Integer> concat(Collection<Integer> collection, int i) {
        return Stream.concat(collection.stream(), Stream.of(i));
    }

}
