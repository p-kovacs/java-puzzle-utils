package com.github.pkovacs.util.alg;

import java.util.Comparator;
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

class BellmanFordTest extends AbstractShortestPathTest {

    @Override
    <T> Optional<Path<T>> findPath(T source,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider,
            Predicate<? super T> targetPredicate) {
        var resultMap = BellmanFord.run(source, edgeProvider);
        return resultMap.keySet().stream()
                .filter(targetPredicate)
                .map(resultMap::get)
                .min(Comparator.comparing(Path::dist));
    }

    @Test
    void testBellmanFordWithSimpleGraph() {
        var graph = new HashMap<String, List<Edge<String>>>();
        graph.put("A", List.of(new Edge<>("B", 1), new Edge<>("C", 1), new Edge<>("D", 1)));
        graph.put("B", List.of(new Edge<>("E", 2)));
        graph.put("C", List.of(new Edge<>("E", -3)));
        graph.put("D", List.of(new Edge<>("G", 4)));
        graph.put("E", List.of(new Edge<>("D", 5), new Edge<>("F", 5), new Edge<>("G", 5)));
        graph.put("F", List.of(new Edge<>("B", -6), new Edge<>("G", -6)));
        graph.put("G", List.of());

        var result = BellmanFord.run("A", graph::get);

        assertEquals(0, result.get("A").dist());
        assertEquals(-3, result.get("B").dist());
        assertEquals(1, result.get("C").dist());
        assertEquals(-2, result.get("E").dist());
        assertEquals(3, result.get("F").dist());
        assertEquals(-3, result.get("G").dist());
    }

    @Test
    void testWithMultipleSources() {
        var resultMap = BellmanFord.runFromAll(IntStream.range(82, 100).boxed().toList(),
                i -> i >= 0 ? List.of(new Edge<>(i - 3, 1), new Edge<>(i - 7, 2)) : List.of());

        assertTrue(resultMap.containsKey(42));
        assertEquals(12, resultMap.get(42).dist());
        assertEquals(List.of(84, 77, 70, 63, 56, 49, 42), resultMap.get(42).nodes());
    }

}
