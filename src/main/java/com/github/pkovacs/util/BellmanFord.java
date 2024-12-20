package com.github.pkovacs.util;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.github.pkovacs.util.Dijkstra.Edge;

/**
 * Implements an efficient version of the Bellman-Ford algorithm, which is known as the
 * <a href="https://en.wikipedia.org/wiki/Shortest_Path_Faster_Algorithm">SPFA algorithm</a>.
 * This algorithm is significantly slower than {@link Dijkstra}, but it also supports negative edge weights.
 * <p>
 * The input is a directed or undirected graph with {@code long} edge weights (implicitly defined by an edge provider
 * function) and one or more source nodes. The edge provider function has to provide for each node {@code u} a
 * collection of {@code (node, weight)} pairs (as {@link Dijkstra.Edge} objects) describing the outgoing edges of
 * {@code u} together with their weights (costs). The algorithm might call this function multiple times for a single
 * node as necessary.
 * <p>
 * In contrast with {@link Dijkstra}, this algorithm also supports negative edge weights, but the graph must not
 * contain a directed cycle with negative total weight. The current implementation does not terminate if such a cycle
 * is reachable from the source node(s). Furthermore, the underlying graph must be finite (or, at least, the set of
 * nodes reachable from the source node(s) must be finite).
 *
 * @see Dijkstra
 * @see Bfs
 */
public final class BellmanFord {

    private BellmanFord() {
    }

    /**
     * Runs the algorithm to find shortest paths to all nodes reachable from the given source node.
     *
     * @param source the source node.
     * @param edgeProvider the edge provider function. For each node {@code u}, it has to provide the outgoing
     *         weighted edges of {@code u} as {@link Edge} objects. This function might be called multiple times
     *         per node as necessary.
     * @return a map that associates a {@link Path} with each node reachable from the source node.
     */
    public static <T> Map<T, Path<T>> run(T source,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider) {
        return runFromAll(List.of(source), edgeProvider);
    }

    /**
     * Runs the algorithm to find shortest paths to all nodes reachable from the given source nodes.
     *
     * @param sources the source nodes.
     * @param edgeProvider the edge provider function. For each node {@code u}, it has to provide the outgoing
     *         weighted edges of {@code u} as {@link Edge} objects. This function might be called multiple times
     *         per node as necessary.
     * @return a map that associates a {@link Path} with each node reachable from the source nodes.
     */
    public static <T> Map<T, Path<T>> runFromAll(Iterable<T> sources,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider) {

        var results = new HashMap<T, Path<T>>();

        var queue = new ArrayDeque<Path<T>>();
        for (var source : sources) {
            var path = new Path<>(source, 0, null);
            results.put(source, path);
            queue.add(path);
        }

        while (!queue.isEmpty()) {
            var path = queue.poll();
            for (var edge : edgeProvider.apply(path.endNode())) {
                var node = edge.endNode();
                var dist = path.dist() + edge.weight();
                var current = results.get(node);
                if (current == null || dist < current.dist()) {
                    var p = new Path<>(node, dist, path);
                    results.put(node, p);
                    queue.add(p);
                }
            }
        }

        return results;
    }

}
