package com.github.pkovacs.util.alg;

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
    void testMultipleSources() {
        var result = Dijkstra.findPathFromAny(IntStream.range(82, 100).boxed().toList(),
                i -> List.of(new Edge<>(i - 3, 1), new Edge<>(i - 7, 2)),
                i -> i == 42);

        assertTrue(result.isPresent());
        assertEquals(12, result.get().dist());
        assertEquals(List.of(84, 77, 70, 63, 56, 49, 42), result.get().nodes());
    }

}
