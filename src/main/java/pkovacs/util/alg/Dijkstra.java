package pkovacs.util.alg;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Implements <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm">Dijkstra's algorithm</a> for
 * finding shortest paths.
 * <p>
 * The input is a directed graph with long integer edge weights (implicitly defined by an edge provider function)
 * and one or more source nodes. The edge provider function has to provide for each node {@code u} a collection of
 * (node, weight) pairs ({@link Edge} objects) describing the outgoing directed edges of {@code u}. This function
 * is applied at most once for each node, when the algorithm advances from that node.
 * <p>
 * This algorithm only supports non-negative edge weights. If you need negative weights as well, use
 * {@link ShortestPath} instead.
 * <p>
 * A target predicate can also be specified in order to find path to a single node instead of all nodes.
 * The algorithm terminates when a shortest path is found for at least one target node (more precisely, for each
 * target node having minimum distance). This way, paths can be searched even in an infinite graph provided that
 * the edges are generated on-the-fly when requested by the algorithm. For example, nodes and edges might represent
 * feasible states and steps of a combinatorial problem, and we might not know or do not want to enumerate all
 * possible states in advance.
 *
 * @see Bfs
 * @see ShortestPath
 */
public final class Dijkstra {

    /**
     * Represents an outgoing directed edge of a node being evaluated (expanded) by this algorithm. It is a record
     * containing the endpoint (target node) and the weight of the edge.
     */
    public static record Edge<T>(T endNode, long weight) {}

    private Dijkstra() {
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
    public static <T> Optional<PathResult<T>> findPath(T source,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider,
            Predicate<? super T> targetPredicate) {
        var map = run(Collections.singleton(source), edgeProvider, targetPredicate);
        return map.values().stream().filter(PathResult::isTarget).min(Comparator.comparing(PathResult::dist));
    }

    /**
     * Runs the algorithm to find shortest paths to all nodes reachable from the given source node.
     *
     * @param source the source node.
     * @param edgeProvider the edge provider function. For each node {@code u}, it has to provide the outgoing
     *         directed edges of {@code u} as a collection of {@link Edge} records.
     * @return a map that associates a {@link PathResult} with each node reachable from the source node.
     */
    public static <T> Map<T, PathResult<T>> run(T source,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider) {
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
    public static <T> Map<T, PathResult<T>> run(Iterable<? extends T> sources,
            Function<? super T, ? extends Iterable<Edge<T>>> edgeProvider,
            Predicate<? super T> targetPredicate) {
        var results = new HashMap<T, PathResult<T>>();

        var queue = new PriorityQueue<Pair<T>>();
        for (T source : sources) {
            results.put(source, new PathResult<>(source, 0, targetPredicate.test(source), null));
            queue.add(new Pair<>(source, 0));
        }

        var processed = new HashSet<T>();
        while (!queue.isEmpty()) {
            var pair = queue.poll();
            T node = pair.node;
            if (!processed.add(node)) {
                continue;
            }

            var result = results.get(node);
            if (result.isTarget()) {
                break;
            }
            for (var edge : edgeProvider.apply(node)) {
                var neighbor = edge.endNode();
                var dist = pair.dist + edge.weight();
                var current = results.get(neighbor);
                if (current == null || dist < current.dist()) {
                    results.put(neighbor, new PathResult<>(neighbor, dist, targetPredicate.test(neighbor), result));
                    queue.add(new Pair<>(neighbor, dist));
                }
            }
        }

        return results;
    }

    private static record Pair<T>(T node, long dist) implements Comparable<Pair<T>> {
        @Override
        public int compareTo(Pair<T> other) {
            return Long.compare(dist, other.dist);
        }
    }

}
