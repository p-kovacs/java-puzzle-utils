package com.github.pkovacs.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongBiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.pkovacs.util.WeightedGraph.Edge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WeightedGraphTest {

    @Test
    void testFiltering() {
        WeightedGraph<Integer> graph = WeightedGraph.of(i ->
                IntStream.rangeClosed(i - 5, i + 5).mapToObj(j -> new Edge<>(j, (long) i * j)));

        assertEquals(11, graph.edges(0).count());
        assertEquals(11, graph.edges(4).count());
        assertEquals(11, graph.edges(23).count());
        assertEquals(new Edge<>(3, 0), graph.edges(0).skip(8).findFirst().orElseThrow());
        assertEquals(new Edge<>(7, 28), graph.edges(4).skip(8).findFirst().orElseThrow());
        assertEquals(new Edge<>(26, 23 * 26), graph.edges(23).skip(8).findFirst().orElseThrow());

        var range = new Range(0, 20);
        var subgraph1 = graph.filterNodes(range::contains);
        assertEquals(List.of(0, 1, 2, 3, 4, 5), subgraph1.edges(0).map(Edge::end).toList());
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), subgraph1.edges(4).map(Edge::end).toList());
        assertEquals(List.of(18, 19, 20),
                subgraph1.edges(23).map(Edge::end).toList()); // the input node is not filtered!

        var subgraph2 = graph.filterNodes(i -> i % 2 == 0);
        assertEquals(List.of(-4, -2, 0, 2, 4), subgraph2.edges(0).map(Edge::end).toList());
        assertEquals(List.of(0, 2, 4, 6, 8), subgraph2.edges(4).map(Edge::end).toList());
        assertEquals(6, subgraph2.edges(23).map(Edge::end).count()); // the input node is not filtered!

        var subgraph3 = graph.filterEdges((i, j) -> (i % 2 == 0) ^ (j % 2 == 0));
        assertEquals(List.of(-5, -3, -1, 1, 3, 5), subgraph3.edges(0).map(Edge::end).toList());
        assertEquals(List.of(-1, 1, 3, 5, 7, 9), subgraph3.edges(4).map(Edge::end).toList());
        assertEquals(List.of(18, 20, 22, 24, 26, 28), subgraph3.edges(23).map(Edge::end).toList());

        Predicate<Object> testPredicate1 = o -> true;
        BiPredicate<Object, Number> testPredicate2 = (a, b) -> true;
        var subgraph4 = graph.filterNodes(testPredicate1).filterEdges(testPredicate2);
        assertEquals(11, subgraph4.edges(0).count());
        assertEquals(11, subgraph4.edges(4).count());
        assertEquals(11, subgraph4.edges(23).count());
    }

    @Test
    void testGenericParameters() {
        Function<Collection<Integer>, Stream<List<Integer>>> append = collection ->
                IntStream.range(0, 2).mapToObj(i -> Stream.concat(collection.stream(), Stream.of(i)).toList());
        ToLongBiFunction<Object, Collection<Integer>> weight = (a, b) -> (long) b.size();

        var nodes = List.of(List.of(1), List.of(1, 2), List.of(1, 2, 3), List.of(1, 2, 3, 4));

        var map1a = new HashMap<Collection<Integer>, Set<Collection<Integer>>>();
        var map1b = new HashMap<Collection<Integer>, Set<Edge<Collection<Integer>>>>();
        var map2a = new HashMap<Collection<Integer>, Set<List<Integer>>>();
        var map2b = new HashMap<Collection<Integer>, Set<Edge<List<Integer>>>>();
        for (var node : nodes) {
            map1a.put(node, append.apply(node).collect(Collectors.toSet()));
            map1b.put(node, append.apply(node).map(v -> new Edge<Collection<Integer>>(v, weight.applyAsLong(node, v)))
                    .collect(Collectors.toSet()));
            map2a.put(node, append.apply(node).collect(Collectors.toSet()));
            map2b.put(node, append.apply(node).map(v -> new Edge<>(v, weight.applyAsLong(node, v)))
                    .collect(Collectors.toSet()));
        }

        WeightedGraph<Collection<Integer>> graph1a =
                WeightedGraph.of(u -> append.apply(u).map(v -> new Edge<>(v, weight.applyAsLong(u, v))));
        WeightedGraph<Collection<Integer>> graph1b =
                WeightedGraph.of(u -> append.apply(u).map(c -> c), weight);
        WeightedGraph<Collection<Integer>> graph1c =
                WeightedGraph.of(Graph.of(u -> append.apply(u).map(c -> c)), weight);
        WeightedGraph<Collection<Integer>> graph1d =
                Graph.<Collection<Integer>>of(u -> append.apply(u).map(c -> c)).weighted(weight);
        WeightedGraph<Collection<Integer>> graph1e = WeightedGraph.of(map1a, weight);
        WeightedGraph<Collection<Integer>> graph1f = WeightedGraph.of(Graph.of(map1a), weight);
        WeightedGraph<Collection<Integer>> graph1g = Graph.of(map1a).weighted(weight);
        WeightedGraph<Collection<Integer>> graph1h = WeightedGraph.of(map1b);

        WeightedGraph<List<Integer>> graph2a =
                WeightedGraph.of(u -> append.apply(u).map(v -> new Edge<>(v, weight.applyAsLong(u, v))));
        WeightedGraph<List<Integer>> graph2b = WeightedGraph.of(append::apply, weight);
        WeightedGraph<List<Integer>> graph2c = WeightedGraph.of(Graph.of(append), weight);
        WeightedGraph<List<Integer>> graph2d = Graph.of(append).weighted(weight);
        WeightedGraph<List<Integer>> graph2e = WeightedGraph.of(map2a, weight);
        WeightedGraph<List<Integer>> graph2f = WeightedGraph.of(Graph.of(map2a), weight);
        WeightedGraph<List<Integer>> graph2g = Graph.<List<Integer>>of(map2a).weighted(weight);
        WeightedGraph<List<Integer>> graph2h = WeightedGraph.of(map2b);

        assertAll(Stream.of(graph1a, graph1b, graph1c, graph1d, graph1e, graph1f, graph1g, graph1h)
                .map(g -> () -> assertEquals(8, nodes.stream().flatMap(g::edges).count())));
        assertAll(Stream.of(graph2a, graph2b, graph2c, graph2d, graph2e, graph2f, graph2g, graph2h)
                .map(g -> () -> assertEquals(8, nodes.stream().flatMap(g::edges).count())));

        Predicate<Collection<Integer>> nodeFilter1 = c -> c.size() <= 4;
        Predicate<List<Integer>> nodeFilter2 = c -> c.size() <= 3;

        assertAll(Stream.of(graph1a, graph1b, graph1c, graph1d, graph1e, graph1f, graph1g, graph1h)
                .map(g -> g.filterNodes(nodeFilter1))
                .map(sg -> () -> assertEquals(6, nodes.stream().flatMap(sg::edges).count())));
        assertAll(Stream.of(graph2a, graph2b, graph2c, graph2d, graph2e, graph2f, graph2g, graph2h)
                .map(g -> g.filterNodes(nodeFilter1).filterNodes(nodeFilter2))
                .map(sg -> () -> assertEquals(4, nodes.stream().flatMap(sg::edges).count())));

        BiPredicate<Collection<Integer>, Collection<Integer>> edgeFilter1 = (a, b) -> b.size() <= 4;
        BiPredicate<Collection<Integer>, List<Integer>> edgeFilter2 = (a, b) -> b.getLast() == 1;

        assertAll(Stream.of(graph1a, graph1b, graph1c, graph1d, graph1e, graph1f, graph1g, graph1h)
                .map(g -> g.filterEdges(edgeFilter1))
                .map(sg -> () -> assertEquals(6, nodes.stream().flatMap(sg::edges).count())));
        assertAll(Stream.of(graph2a, graph2b, graph2c, graph2d, graph2e, graph2f, graph2g, graph2h)
                .map(g -> g.filterEdges(edgeFilter1).filterEdges(edgeFilter2))
                .map(sg -> () -> assertEquals(3, nodes.stream().flatMap(sg::edges).count())));
    }

}
