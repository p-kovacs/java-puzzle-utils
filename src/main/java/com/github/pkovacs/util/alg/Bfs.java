package com.github.pkovacs.util.alg;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A general implementation of the BFS (breadth-first search) algorithm.
 * <p>
 * The input is a directed or undirected graph (implicitly defined by a neighbor provider function) and one or more
 * source nodes. The neighbor provider function has to provide for each node {@code u} the neighbor nodes directly
 * reachable form {@code u} via its outgoing edges. This function is applied at most once for each node, when the
 * algorithm advances from that node.
 * <p>
 * A target predicate can also be used in order to find path to a single target node instead of all nodes. The
 * algorithm terminates when a shortest path is found for a target node having minimum distance from the (nearest)
 * source node. This way, paths can also be searched in huge or (theoretically) infinite graphs provided that the
 * edges are generated on-the-fly when requested by the algorithm. For example, nodes and edges might represent
 * feasible states and steps of a combinatorial problem, and we might not be able to or do not want to enumerate
 * all possible (and reachable) states in advance.
 *
 * @see Dijkstra
 * @see ShortestPath
 */
public final class Bfs {

    private Bfs() {
    }

    /**
     * Finds a shortest path (in terms of the number of edges) from the given source node to a target node specified
     * by the given predicate.
     *
     * @param source the source node.
     * @param neighborProvider the neighbor provider function. For each node {@code u}, it has to provide the
     *         end nodes of the outgoing edges of {@code u} as a collection.
     * @param targetPredicate a predicate that returns true for the target node(s). It can accept multiple
     *         nodes, in which case a shortest path to one of the nearest target nodes is to be found.
     *         However, for a single target node {@code t}, you can simply use {@code t::equals}.
     * @return a {@link PathResult} specifying a shortest path to the nearest target node or an empty optional if
     *         no target nodes are reachable from the source node.
     */
    public static <T> Optional<PathResult<T>> findPath(T source,
            Function<? super T, ? extends Iterable<T>> neighborProvider,
            Predicate<? super T> targetPredicate) {
        return findPathFromAny(List.of(source), neighborProvider, targetPredicate);
    }

    /**
     * Finds a shortest path (in terms of the number of edges) from one of the given source nodes to a target node
     * specified by the given predicate.
     *
     * @param sources the source nodes.
     * @param neighborProvider the neighbor provider function. For each node {@code u}, it has to provide the
     *         end nodes of the outgoing edges of {@code u} as a collection.
     * @param targetPredicate a predicate that returns true for the target node(s). It can accept multiple
     *         nodes, in which case a shortest path to one of the nearest target nodes is to be found.
     *         However, for a single target node {@code t}, you can simply use {@code t::equals}.
     * @return a {@link PathResult} specifying a shortest path to the nearest target node or an empty optional if
     *         no target nodes are reachable from the source nodes.
     */
    public static <T> Optional<PathResult<T>> findPathFromAny(Iterable<? extends T> sources,
            Function<? super T, ? extends Iterable<T>> neighborProvider,
            Predicate<? super T> targetPredicate) {
        var results = new HashMap<T, PathResult<T>>();
        return run(sources, neighborProvider, targetPredicate, results);
    }

    /**
     * Runs the algorithm to find shortest paths (in terms of the number of edges) to all nodes reachable from the
     * given source nodes.
     *
     * @param sources the source nodes.
     * @param neighborProvider the neighbor provider function. For each node {@code u}, it has to provide the
     *         end nodes of the outgoing edges of {@code u} as a collection.
     * @return a map that associates a {@link PathResult} with each node reachable from the source nodes.
     */
    public static <T> Map<T, PathResult<T>> run(Iterable<? extends T> sources,
            Function<? super T, ? extends Iterable<T>> neighborProvider) {
        var results = new HashMap<T, PathResult<T>>();
        run(sources, neighborProvider, n -> false, results);
        return results;
    }

    private static <T> Optional<PathResult<T>> run(Iterable<? extends T> sources,
            Function<? super T, ? extends Iterable<T>> neighborProvider,
            Predicate<? super T> targetPredicate,
            Map<T, PathResult<T>> results) {

        var queue = new ArrayDeque<PathResult<T>>();
        for (var source : sources) {
            var path = new PathResult<T>(source, 0, null);
            results.put(source, path);
            queue.add(path);
        }

        while (!queue.isEmpty()) {
            var path = queue.poll();
            if (targetPredicate.test(path.node())) {
                return Optional.of(path);
            }

            for (T neighbor : neighborProvider.apply(path.node())) {
                if (!results.containsKey(neighbor)) {
                    var p = new PathResult<>(neighbor, path.dist() + 1, path);
                    results.put(neighbor, p);
                    queue.add(p);
                }
            }
        }

        return Optional.empty();
    }

}
