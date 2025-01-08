package com.github.pkovacs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.pkovacs.util.WeightedGraph.Edge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DijkstraTest extends AbstractShortestPathTest {

    @Override
    <T> Optional<Path<T>> findPath(WeightedGraph<T> graph, T source, Predicate<? super T> targetPredicate) {
        return Dijkstra.findPath(graph, source, targetPredicate);
    }

    @Test
    void testDijkstraWithSimpleGraph() {
        var graph = new HashMap<String, List<Edge<String>>>();
        graph.put("A", List.of(new Edge<>("B", 1), new Edge<>("C", 1), new Edge<>("D", 1)));
        graph.put("B", List.of(new Edge<>("E", 2)));
        graph.put("C", List.of(new Edge<>("E", 3)));
        graph.put("D", List.of(new Edge<>("G", 4)));
        graph.put("E", List.of(new Edge<>("D", 5), new Edge<>("F", 5), new Edge<>("G", 5)));
        graph.put("F", List.of(new Edge<>("B", 6), new Edge<>("G", 6)));
        graph.put("G", List.of());

        assertEquals(0, Dijkstra.dist(u -> graph.get(u).stream(), "A", "A"::equals));
        assertEquals(1, Dijkstra.dist(u -> graph.get(u).stream(), "A", "B"::equals));
        assertEquals(3, Dijkstra.dist(u -> graph.get(u).stream(), "A", "E"::equals));
        assertEquals(8, Dijkstra.dist(u -> graph.get(u).stream(), "A", "F"::equals));
        assertEquals(5, Dijkstra.dist(u -> graph.get(u).stream(), "A", "G"::equals));

        var paths = Dijkstra.findPaths(WeightedGraph.of(graph), "A");

        assertEquals(0, paths.get("A").dist());
        assertEquals(1, paths.get("B").dist());
        assertEquals(3, paths.get("E").dist());
        assertEquals(8, paths.get("F").dist());
        assertEquals(5, paths.get("G").dist());
    }

    @Test
    void testWithMultipleSources() {
        var result = Dijkstra.findPathFromAny(
                i -> Stream.of(new Edge<>(i - 3, 1), new Edge<>(i - 7, 2)),
                IntStream.range(82, 100).boxed(), i -> i == 42);

        assertTrue(result.isPresent());
        assertEquals(12, result.get().dist());
        assertEquals(List.of(84, 77, 70, 63, 56, 49, 42), result.get().nodes());
    }

    @Test
    void testGenericParameters2() {
        assertEquals(1, Dijkstra.findPaths(c -> Stream.of(new Edge<>(List.of(42), 0)),
                List.of(42)).size());
        assertEquals(1, Dijkstra.findPaths(c -> Stream.of(new Edge<>(new ArrayList<>(List.of(42)), 0)),
                List.of(42)).size());
        assertEquals(1, Dijkstra.<List<Integer>>findPaths(c -> Stream.of(new Edge<>(List.of(42), 0)),
                new ArrayList<>(List.of(42))).size());
    }

}
