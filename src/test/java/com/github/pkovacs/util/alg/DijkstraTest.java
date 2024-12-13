package com.github.pkovacs.util.alg;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import com.github.pkovacs.util.alg.Dijkstra.Edge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DijkstraTest extends AbstractShortestPathTest {

    @Override
    <T> Optional<Path<T>> findPath(T source,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider,
            Predicate<? super T> targetPredicate) {
        return Dijkstra.findPath(source, edgeProvider, targetPredicate);
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

        assertEquals(0, Dijkstra.dist("A", graph::get, "A"::equals));
        assertEquals(1, Dijkstra.dist("A", graph::get, "B"::equals));
        assertEquals(3, Dijkstra.dist("A", graph::get, "E"::equals));
        assertEquals(8, Dijkstra.dist("A", graph::get, "F"::equals));
        assertEquals(5, Dijkstra.dist("A", graph::get, "G"::equals));

        var result = Dijkstra.run("A", graph::get);

        assertEquals(0, result.get("A").dist());
        assertEquals(1, result.get("B").dist());
        assertEquals(3, result.get("E").dist());
        assertEquals(8, result.get("F").dist());
        assertEquals(5, result.get("G").dist());
    }

    @Test
    void testWithMultipleSources() {
        var result = Dijkstra.findPathFromAny(IntStream.range(82, 100).boxed().toList(),
                i -> List.of(new Edge<>(i - 3, 1), new Edge<>(i - 7, 2)),
                i -> i == 42);

        assertTrue(result.isPresent());
        assertEquals(12, result.get().dist());
        assertEquals(List.of(84, 77, 70, 63, 56, 49, 42), result.get().nodes());
    }

}
