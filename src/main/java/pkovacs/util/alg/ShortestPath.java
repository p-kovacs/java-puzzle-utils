package pkovacs.util.alg;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Implements a general shortest path algorithm, which is a more efficient version of the classic Bellman-Ford
 * algorithm. Namely, it is the
 * <a href="https://en.wikipedia.org/wiki/Shortest_Path_Faster_Algorithm">SPFA algorithm</a>.
 * <p>
 * The input is a directed graph with long integer edge weights (implicitly defined by an edge provider function)
 * and one or more source nodes. The edge provider function has to provide for each node {@code u} a collection of
 * {@code (node, weight)} pairs describing the outgoing directed edges of {@code u}.
 * <p>
 * The algorithm also supports negative edge weights, but the graph must not contain a directed cycle with negative
 * total weight. The current implementation might not terminate for such input!
 * <p>
 * A target predicate can also be specified in order to find path to a single node instead of all nodes.
 * However, for this algorithm, it does not make the search process faster.
 *
 * @see Bfs
 */
public final class ShortestPath {

    /**
     * Represents an outgoing directed edge of a node being evaluated (expanded) by this algorithm. It is a record
     * containing the endpoint (target node) and the weight of the edge.
     */
    public static record Edge<T>(T node, long weight) {

        public static <T> Edge<T> of(T node, long weight) {
            return new Edge<T>(node, weight);
        }

    }

    private ShortestPath() {
    }

    /**
     * Finds a shortest path from the given source node to a target node determined by the given predicate.
     *
     * @param source the source node.
     * @param edgeProvider the edge provider function. For each node {@code u}, it has to provide the outgoing
     *         directed edges of {@code u} as a collection of {@link Edge} records.
     * @param targetPredicate a predicate that returns true for the target node(s). It can accept multiple
     *         nodes, in which case a {@link PathResult} for one of them will be provided.
     *         However, in the case of a single target node {@code t}, you can simply use {@code t::equals}.
     * @return a {@link PathResult} specifying a shortest path to a target node or an empty optional if no target
     *         nodes are reachable from the source node.
     */
    public static <T> Optional<PathResult<T>> findPath(T source, Function<T, Iterable<Edge<T>>> edgeProvider,
            Predicate<T> targetPredicate) {
        var map = run(Collections.singleton(source), edgeProvider, targetPredicate);
        return map.values().stream().filter(PathResult::isTarget).findFirst();
    }

    /**
     * Runs the algorithm to find shortest paths to all nodes reachable from the given source node.
     *
     * @param source the source node.
     * @param edgeProvider the edge provider function. For each node {@code u}, it has to provide the outgoing
     *         directed edges of {@code u} as a collection of {@link Edge} records.
     * @return a map that associates a {@link PathResult} with each node reachable from the source node.
     */
    public static <T> Map<T, PathResult<T>> run(T source, Function<T, Iterable<Edge<T>>> edgeProvider) {
        return run(Collections.singleton(source), edgeProvider, n -> false);
    }

    /**
     * Runs the algorithm to find shortest paths to all target nodes (or all nodes) reachable from the given
     * source nodes. This is the most general way to execute the algorithm, consider to use another method
     * if it is also appropriate.
     *
     * @param sources the source nodes.
     * @param edgeProvider the edge provider function. For each node {@code u}, it has to provide the outgoing
     *         directed edges of {@code u} as a collection of {@link Edge} records.
     * @param targetPredicate a predicate that returns true for the target node(s). It can accept multiple
     *         nodes or no nodes at all (e.g. {@code t -> false}).
     * @return a map that associates a {@link PathResult} with each node that was reached from the source nodes
     *         during the search process.
     */
    public static <T> Map<T, PathResult<T>> run(Iterable<T> sources, Function<T, Iterable<Edge<T>>> edgeProvider,
            Predicate<T> targetPredicate) {
        var results = new HashMap<T, PathResult<T>>();

        var queue = new ArrayDeque<T>();
        for (T source : sources) {
            results.put(source, new PathResult<>(source, 0, targetPredicate.test(source), null));
            queue.add(source);
        }

        while (!queue.isEmpty()) {
            T node = queue.poll();
            var result = results.get(node);
            for (var edge : edgeProvider.apply(node)) {
                var neighbor = edge.node();
                var dist = result.dist() + edge.weight();
                var current = results.get(neighbor);
                if (current == null || dist < current.dist()) {
                    results.put(neighbor, new PathResult<>(neighbor, dist, targetPredicate.test(neighbor), result));
                    queue.add(neighbor);
                }
            }
        }

        return results;
    }

}
