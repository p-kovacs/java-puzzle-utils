package com.github.pkovacs.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongBiFunction;
import java.util.stream.Stream;

/**
 * Functional interface that represents the edges of a <i>weighted</i> directed or undirected graph. A {@code long}
 * value (the weight) is assigned to every edge. For each node, this interface provides the outgoing edges of that
 * node along with their weights. In the case of an undirected graph, this relation must be symmetric.
 * <p>
 * This is a simplistic approach that makes it as easy as possible to define weighted graphs when solving coding
 * puzzles (e.g., as a lambda expression). On the other hand, the collection of all nodes in the graph is not available
 * via this interface, so the algorithms require one or more nodes to be specified explicitly.
 * <p>
 * The {@link #of WeightedGraph.of} static factory methods can be used to create weighted graphs.
 * {@link #filterNodes(Predicate)} and {@link #filterEdges(BiPredicate)} can be used to obtain subgraphs of a graph.
 *
 * @param <T> the type of nodes in the graph
 * @see Graph
 */
@FunctionalInterface
public interface WeightedGraph<T> {

    /**
     * Represents an outgoing weighted edge of a node.
     *
     * @param <T> the type of nodes in the graph
     */
    record Edge<T>(T end, long weight) {}

    /**
     * Provides the outgoing weighted edges of the given node.
     * <p>
     * In the case of an undirected graph, the adjacency relation must be symmetric. That is, if node {code v} is
     * among the adjacent nodes of {@code u} with a certain weight, then {@code u} must be among the adjacent nodes
     * of {@code v} with the same weight.
     */
    Stream<? extends Edge<? extends T>> edges(T node);

    /**
     * Wraps the given function as a weighted graph. Actually, the function itself can directly be used when
     * a weighted graph is required by an algorithm, but this method is useful if you would like to use some
     * methods of this interface, e.g., to filter nodes or edges.
     */
    static <T> WeightedGraph<T> of(Function<? super T, ? extends Stream<? extends Edge<? extends T>>> edgeProvider) {
        return edgeProvider::apply;
    }

    /**
     * Wraps the given map as a weighted graph.
     */
    static <T> WeightedGraph<T> of(Map<? super T, ? extends Collection<? extends Edge<? extends T>>> map) {
        return u -> map.get(u).stream();
    }

    /**
     * Wraps the given map and the given weight function as a weighted graph.
     */
    static <T> WeightedGraph<T> of(Map<? super T, ? extends Collection<? extends T>> map,
            ToLongBiFunction<? super T, ? super T> weight) {
        return u -> map.get(u).stream().map(v -> new Edge<>(v, weight.applyAsLong(u, v)));
    }

    /**
     * Wraps the given {@link Graph} and the given weight function as a weighted graph.
     */
    static <T> WeightedGraph<T> of(Graph<T> graph, ToLongBiFunction<? super T, ? super T> weight) {
        return u -> graph.neighbors(u).map(v -> new Edge<>(v, weight.applyAsLong(u, v)));
    }

    /**
     * Restricts this weighted graph to only contain the nodes that satisfy the given predicate.
     */
    default WeightedGraph<T> filterNodes(Predicate<? super T> nodeFilter) {
        return u -> edges(u).filter(edge -> nodeFilter.test(edge.end()));
    }

    /**
     * Restricts this weighted graph to only contain the edges (pairs of nodes) that satisfy the given predicate.
     */
    default WeightedGraph<T> filterEdges(BiPredicate<? super T, ? super T> edgeFilter) {
        return u -> edges(u).filter(edge -> edgeFilter.test(u, edge.end()));
    }

}