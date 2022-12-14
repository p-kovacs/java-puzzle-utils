package com.github.pkovacs.util.alg;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Implements <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm">Dijkstra's algorithm</a> for
 * finding shortest paths.
 * <p>
 * The input is a directed or undirected graph with {@code long} edge weights (implicitly defined by an edge provider
 * function) and one or more source nodes. The edge provider function has to provide for each node {@code u} a
 * collection of {@code (node, weight)} pairs ({@link Edge} objects) describing the outgoing edges of {@code u}.
 * This function is applied at most once for each node, when the algorithm advances from that node.
 * <p>
 * This algorithm only supports non-negative edge weights. If you also need negative edge weights, use
 * {@link ShortestPath} instead.
 * <p>
 * A target predicate can also be used in order to find path to a single target node instead of all nodes. The
 * algorithm terminates when a shortest path is found for a target node having minimum distance from the (nearest)
 * source node. This way, paths can also be searched in huge or (theoretically) infinite graphs provided that the
 * edges are generated on-the-fly when requested by the algorithm. For example, nodes and edges might represent
 * feasible states and steps of a combinatorial problem, and we might not be able to or do not want to enumerate
 * all possible (and reachable) states in advance.
 *
 * @see Bfs
 * @see ShortestPath
 */
public final class Dijkstra {

    /**
     * Represents an outgoing directed edge of a node being evaluated (expanded) by this algorithm.
     */
    public record Edge<T>(T endNode, long weight) {}

    private Dijkstra() {
    }

    /**
     * Finds a shortest path from the given source node to a target node specified by the given predicate.
     *
     * @param source the source node.
     * @param edgeProvider the edge provider function. For each node {@code u}, it has to provide the outgoing
     *         edges of {@code u} as a collection of {@link Edge} objects.
     * @param targetPredicate a predicate that returns true for the target node(s). It can accept multiple
     *         nodes, in which case a shortest path to one of the nearest target nodes is to be found.
     *         However, for a single target node {@code t}, you can simply use {@code t::equals}.
     * @return a {@link PathResult} specifying a shortest path to the nearest target node or an empty optional if
     *         no target nodes are reachable from the source node.
     */
    public static <T> Optional<PathResult<T>> findPath(T source,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider,
            Predicate<? super T> targetPredicate) {
        return findPathFromAny(List.of(source), edgeProvider, targetPredicate);
    }

    /**
     * Finds a shortest path from one of the given source nodes to a target node specified by the given predicate.
     *
     * @param sources the source nodes.
     * @param edgeProvider the edge provider function. For each node {@code u}, it has to provide the outgoing
     *         edges of {@code u} as a collection of {@link Edge} objects.
     * @param targetPredicate a predicate that returns true for the target node(s). It can accept multiple
     *         nodes, in which case a shortest path to one of the nearest target nodes is to be found.
     *         However, for a single target node {@code t}, you can simply use {@code t::equals}.
     * @return a {@link PathResult} specifying a shortest path to the nearest target node or an empty optional if
     *         no target nodes are reachable from the source nodes.
     */
    public static <T> Optional<PathResult<T>> findPathFromAny(Iterable<? extends T> sources,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider,
            Predicate<? super T> targetPredicate) {
        var results = new HashMap<T, PathResult<T>>();
        return run(sources, edgeProvider, targetPredicate, results);
    }

    /**
     * Runs the algorithm to find shortest paths to all nodes reachable from the given source nodes.
     *
     * @param sources the source nodes.
     * @param edgeProvider the edge provider function. For each node {@code u}, it has to provide the outgoing
     *         edges of {@code u} as a collection of {@link Edge} objects.
     * @return a map that associates a {@link PathResult} with each node reachable from the source nodes.
     */
    public static <T> Map<T, PathResult<T>> run(Iterable<? extends T> sources,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider) {
        var results = new HashMap<T, PathResult<T>>();
        run(sources, edgeProvider, n -> false, results);
        return results;
    }

    private static <T> Optional<PathResult<T>> run(Iterable<? extends T> sources,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider,
            Predicate<? super T> targetPredicate,
            HashMap<T, PathResult<T>> results) {

        var queue = new PriorityQueue<PathResult<T>>(Comparator.comparing(PathResult::dist));
        for (var source : sources) {
            var path = new PathResult<T>(source, 0, null);
            results.put(source, path);
            queue.add(path);
        }

        var processed = new HashSet<T>();
        while (!queue.isEmpty()) {
            var path = queue.poll();
            if (targetPredicate.test(path.node())) {
                return Optional.of(path);
            }
            if (!processed.add(path.node())) {
                continue;
            }

            for (var edge : edgeProvider.apply(path.node())) {
                var neighbor = edge.endNode();
                var dist = path.dist() + edge.weight();
                var current = results.get(neighbor);
                if (current == null || dist < current.dist()) {
                    var p = new PathResult<>(neighbor, dist, path);
                    results.put(neighbor, p);
                    queue.add(p);
                }
            }
        }

        return Optional.empty();
    }

}
