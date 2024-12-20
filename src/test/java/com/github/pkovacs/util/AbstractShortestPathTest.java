package com.github.pkovacs.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.pkovacs.util.Dijkstra.Edge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class AbstractShortestPathTest {

    static final String MAZE = """
            .#........#.
            .#.######...
            .#.#....#.#.
            .#.#.##.##..
            .#.#.#.....#
            .#.#.######.
            ...#........
            .#.#.#.####.
            .#.#.#....#.
            ...#.#.##...
            """;

    abstract <T> Optional<Path<T>> findPath(T source,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider,
            Predicate<? super T> targetPredicate);

    @Test
    void testWithSimpleGraph() {
        var graph = new HashMap<String, List<Edge<String>>>();
        graph.put("A", List.of(new Edge<>("B", 10), new Edge<>("D", 5)));
        graph.put("B", List.of(new Edge<>("C", 1)));
        graph.put("C", List.of(new Edge<>("E", 1)));
        graph.put("D", List.of(new Edge<>("B", 3), new Edge<>("C", 9), new Edge<>("E", 11)));
        graph.put("E", List.of());

        var result = findPath("A", graph::get, "E"::equals);
        assertTrue(result.isPresent());
        assertEquals(10, result.get().dist());
        assertEquals(List.of("A", "D", "B", "C", "E"), result.get().nodes());
    }

    @Test
    void testWithMaze() {
        // We have to find the shortest path in a maze from the top left tile to the bottom right tile.
        // Walls should be bypassed or "blown up", but it takes detonationTime seconds to blow up a single wall
        // tile next to the current tile and step into its location, while a single step to an adjacent empty
        // tile takes only 1 second.
        // See MAZE, '#' represents a wall tile, '.' represents an empty tile.

        var input = MAZE.lines().toList();
        var maze = new CharTable(input);
        var start = maze.topLeft();
        var end = maze.bottomRight();

        // Find path with large detonationTime --> same as BFS
        long detonationTime = 32;
        var result = findPathInMaze(maze, start, end, detonationTime);

        assertEquals(50, result.dist());
        assertEquals(51, result.nodes().size());
        assertEquals(start, result.nodes().get(0));
        assertEquals(end, result.nodes().get(result.nodes().size() - 1));

        // Find path with smaller detonationTime --> better than BFS
        detonationTime = 30;
        result = findPathInMaze(maze, start, end, detonationTime);

        assertEquals(49, result.dist());

        // Find path with detonationTime == 1 --> Manhattan distance
        detonationTime = 1;
        result = findPathInMaze(maze, start, end, detonationTime);

        assertEquals(20, result.dist());
        assertEquals(start.dist1(end), result.dist());
    }

    private Path<Pos> findPathInMaze(CharTable maze, Pos start, Pos end, long detonationTime) {
        var result = findPath(start,
                p -> maze.neighbors(p)
                        .map(n -> new Edge<>(n, maze.get(n) == '.' ? 1 : detonationTime))
                        .toList(),
                end::equals);

        assertTrue(result.isPresent());
        return result.get();
    }

    @Test
    void testWithDirections() {
        var result1 = findPath(Dir8.N,
                dir -> List.of(new Edge<>(dir.next(), 3), new Edge<>(dir.prev(), 2)), Dir8.SE::equals);
        var result2 = findPath(Dir8.N,
                dir -> List.of(new Edge<>(dir.next(), 7), new Edge<>(dir.prev(), 4)), Dir8.SE::equals);

        assertTrue(result1.isPresent());
        assertEquals(9, result1.get().dist());
        assertEquals(List.of(Dir8.N, Dir8.NE, Dir8.E, Dir8.SE), result1.get().nodes());

        assertTrue(result2.isPresent());
        assertEquals(20, result2.get().dist());
        assertEquals(List.of(Dir8.N, Dir8.NW, Dir8.W, Dir8.SW, Dir8.S, Dir8.SE), result2.get().nodes());
    }

    @Test
    void testMultipleTargets() {
        var nodes = new ArrayList<>(IntStream.range(0, 100).boxed().toList());
        Collections.shuffle(nodes, new Random(123456789));

        var result = findPath(nodes.get(0),
                i -> IntStream.rangeClosed(nodes.indexOf(i), nodes.indexOf(i) + 7)
                        .filter(j -> j < 100)
                        .mapToObj(j -> new Edge<>(nodes.get(j), 1))
                        .toList(),
                i -> nodes.indexOf(i) >= 42);

        assertTrue(result.isPresent());
        assertEquals(6, result.get().dist());
    }

    @Test
    void testGenericParameters() {
        Function<Collection<Integer>, Collection<Edge<List<Integer>>>> edgeProvider = c ->
                IntStream.rangeClosed(0, 3)
                        .mapToObj(i -> new Edge<>(concat(c, i).toList(), Math.max(i * 10, 1)))
                        .filter(e -> e.endNode().size() <= 6)
                        .toList();

        var start = List.of(1, 0);
        var target = List.of(1, 0, 1, 0, 2, 1);
        Predicate<Collection<Integer>> predicate = target::equals;

        var path = findPath(start, edgeProvider, predicate);

        assertTrue(path.isPresent());
        assertEquals(41, path.get().dist());
        assertEquals(target, path.get().end());
    }

    private static Stream<Integer> concat(Collection<Integer> collection, int i) {
        return Stream.concat(collection.stream(), Stream.of(i));
    }

}
