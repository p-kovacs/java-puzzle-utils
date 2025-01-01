package com.github.pkovacs.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphTest {

    @Test
    void testFiltering() {
        Graph<Integer> graph = Graph.of(i -> IntStream.rangeClosed(i - 5, i + 5).boxed());

        assertEquals(List.of(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5), graph.neighbors(0).toList());
        assertEquals(11, graph.neighbors(4).count());
        assertEquals(11, graph.neighbors(23).count());

        var range = new Range(0, 20);
        var subgraph1 = graph.filterNodes(range::contains);
        assertEquals(List.of(0, 1, 2, 3, 4, 5), subgraph1.neighbors(0).toList());
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), subgraph1.neighbors(4).toList());
        assertEquals(List.of(18, 19, 20), subgraph1.neighbors(23).toList()); // the input node is not filtered!

        var subgraph2 = graph.filterNodes(i -> i % 2 == 0);
        assertEquals(List.of(-4, -2, 0, 2, 4), subgraph2.neighbors(0).toList());
        assertEquals(List.of(0, 2, 4, 6, 8), subgraph2.neighbors(4).toList());
        assertEquals(6, subgraph2.neighbors(23).count()); // the input node is not filtered!

        var subgraph3 = graph.filterEdges((i, j) -> (i % 2 == 0) ^ (j % 2 == 0));
        assertEquals(List.of(-5, -3, -1, 1, 3, 5), subgraph3.neighbors(0).toList());
        assertEquals(List.of(-1, 1, 3, 5, 7, 9), subgraph3.neighbors(4).toList());
        assertEquals(List.of(18, 20, 22, 24, 26, 28), subgraph3.neighbors(23).toList());

        Predicate<Object> testPredicate1 = o -> true;
        BiPredicate<Object, Number> testPredicate2 = (a, b) -> true;
        var subgraph4 = graph.filterNodes(testPredicate1).filterEdges(testPredicate2);
        assertEquals(11, subgraph4.neighbors(0).count());
        assertEquals(11, subgraph4.neighbors(4).count());
        assertEquals(11, subgraph4.neighbors(23).count());
    }

    @Test
    void testGenericParameters() {
        Function<Collection<Integer>, Stream<ArrayList<Integer>>> append = collection -> IntStream.range(0, 2)
                .mapToObj(i -> new ArrayList<>(Stream.concat(collection.stream(), Stream.of(i)).toList()));

        var nodes = Stream.of(List.of(1), List.of(1, 2), List.of(1, 2, 3), List.of(1, 2, 3, 4))
                .map(ArrayList::new).toList();
        var map = new HashMap<Collection<Integer>, Set<LinkedList<Integer>>>();
        for (var node : nodes) {
            map.put(node, append.apply(node).map(LinkedList::new).collect(Collectors.toSet()));
        }

        Graph<Collection<Integer>> graph1a = Graph.of(append);
        Graph<Collection<Integer>> graph1b = Graph.of(map);
        Graph<List<Integer>> graph2a = Graph.of(append);
        Graph<List<Integer>> graph2b = Graph.of(map);
        Graph<ArrayList<Integer>> graph3 = Graph.of(append);
        Graph<LinkedList<Integer>> graph4 = Graph.of(map);

        assertEquals(8, nodes.stream().flatMap(graph1a::neighbors).count());
        assertEquals(8, nodes.stream().flatMap(graph1b::neighbors).count());
        assertEquals(8, nodes.stream().flatMap(graph2a::neighbors).count());
        assertEquals(8, nodes.stream().flatMap(graph2b::neighbors).count());
        assertEquals(8, nodes.stream().flatMap(graph3::neighbors).count());
        assertEquals(8, nodes.stream().map(LinkedList::new).flatMap(graph4::neighbors).count());

        Predicate<Collection<Integer>> nodeFilter1 = c -> c.size() <= 4;
        Predicate<List<Integer>> nodeFilter2 = c -> c.size() <= 3;
        Predicate<ArrayList<Integer>> nodeFilter3 = c -> c.size() <= 2;
        var fn1a = graph1a.filterNodes(nodeFilter1);
        var fn1b = graph1b.filterNodes(nodeFilter1);
        var fn2a = graph2a.filterNodes(nodeFilter1).filterNodes(nodeFilter2);
        var fn2b = graph2b.filterNodes(nodeFilter1).filterNodes(nodeFilter2);
        var fn3 = graph3.filterNodes(nodeFilter1).filterNodes(nodeFilter2).filterNodes(nodeFilter3);
        var fn4 = graph4.filterNodes(nodeFilter1).filterNodes(nodeFilter2);

        assertEquals(6, nodes.stream().flatMap(fn1a::neighbors).count());
        assertEquals(6, nodes.stream().flatMap(fn1b::neighbors).count());
        assertEquals(4, nodes.stream().flatMap(fn2a::neighbors).count());
        assertEquals(4, nodes.stream().flatMap(fn2b::neighbors).count());
        assertEquals(2, nodes.stream().flatMap(fn3::neighbors).count());
        assertEquals(4, nodes.stream().map(LinkedList::new).flatMap(fn4::neighbors).count());

        BiPredicate<Collection<Integer>, Collection<Integer>> edgeFilter1 = (a, b) -> b.size() <= 4;
        BiPredicate<Collection<Integer>, List<Integer>> edgeFilter2 = (a, b) -> a.size() == 1 || b.getLast() == 1;
        BiPredicate<ArrayList<Integer>, List<Integer>> edgeFilter3 = (a, b) -> b.getLast() == 1;
        var fe1a = graph1a.filterEdges(edgeFilter1);
        var fe1b = graph1b.filterEdges(edgeFilter1);
        var fe2a = graph2a.filterEdges(edgeFilter1).filterEdges(edgeFilter2);
        var fe2b = graph2b.filterEdges(edgeFilter1).filterEdges(edgeFilter2);
        var fe3 = graph3.filterEdges(edgeFilter1).filterEdges(edgeFilter2).filterEdges(edgeFilter3);
        var fe4 = graph4.filterEdges(edgeFilter1).filterEdges(edgeFilter2);

        assertEquals(6, nodes.stream().flatMap(fe1a::neighbors).count());
        assertEquals(6, nodes.stream().flatMap(fe1b::neighbors).count());
        assertEquals(4, nodes.stream().flatMap(fe2a::neighbors).count());
        assertEquals(4, nodes.stream().flatMap(fe2b::neighbors).count());
        assertEquals(3, nodes.stream().flatMap(fe3::neighbors).count());
        assertEquals(4, nodes.stream().map(LinkedList::new).flatMap(fe4::neighbors).count());
    }

}
