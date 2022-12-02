package com.github.pkovacs.util.alg;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A general implementation of the BFS (breadth-first search) algorithm.
 * <p>
 * The input is a directed graph (implicitly defined by a neighbor provider function) and one or more source nodes.
 * The nodes of the graph often represent different states of a puzzle, and the directed edges represent the feasible
 * steps. The neighbor provider function has to provide for each node {@code u} the collection of neighbor nodes
 * reachable form {@code u} via directed edges. This function is applied at most once for each node, when the
 * algorithm advances from that node.
 * <p>
 * A target predicate can also be specified in order to find path to a single node instead of all nodes.
 * The algorithm terminates when a shortest path is found for at least one target node (more precisely, for each
 * target node having minimum distance). This way, paths can be searched even in an infinite graph provided that
 * the edges are generated on-the-fly when requested by the algorithm. For example, nodes and edges might represent
 * feasible states and steps of a combinatorial problem, and we might not know or do not want to enumerate all
 * possible states in advance.
 *
 * @see Dijkstra
 * @see ShortestPath
 */
public final class Bfs {

    private Bfs() {
    }

    /**
     * Finds a shortest path (in terms of the number of edges) from the given source node to a target node determined
     * by the given predicate.
     *
     * @param source the source node.
     * @param neighborProvider the neighbor provider function. For each node {@code u}, it has to provide the
     *         neighbor nodes reachable form {@code u} via directed edges. The graph implicitly defined this way
     *         can also be infinite.
     * @param targetPredicate a predicate that returns true for the target node(s). It can accept multiple
     *         nodes, in which case a {@link PathResult} for one of them will be provided.
     *         However, in the case of a single target node {@code t}, you can simply use {@code t::equals}.
     * @return a {@link PathResult} specifying a shortest path to a target node or an empty optional if no target
     *         nodes are reachable from the source node.
     */
    public static <T> Optional<PathResult<T>> findPath(T source,
            Function<? super T, ? extends Iterable<T>> neighborProvider,
            Predicate<? super T> targetPredicate) {
        var map = run(List.of(source), neighborProvider, targetPredicate);
        return map.values().stream().filter(PathResult::isTarget).min(Comparator.comparing(PathResult::dist));
    }

    /**
     * Runs the algorithm to find shortest paths (in terms of the number of edges) to all nodes reachable from the
     * given source node.
     *
     * @param source the source node.
     * @param neighborProvider the neighbor provider function. For each node {@code u}, it has to provide the
     *         neighbor nodes reachable form {@code u} via directed edges.
     * @return a map that associates a {@link PathResult} with each node reachable from the source node.
     */
    public static <T> Map<T, PathResult<T>> run(T source,
            Function<? super T, ? extends Iterable<T>> neighborProvider) {
        return run(List.of(source), neighborProvider, t -> false);
    }

    /**
     * Runs the algorithm to find shortest paths (in terms of the number of edges) to all target nodes (or all nodes)
     * reachable from the given source nodes. This is the most general way to execute the algorithm, consider to use
     * another method if it is also appropriate.
     *
     * @param sources the source nodes.
     * @param neighborProvider the neighbor provider function. For each node {@code u}, it has to provide the
     *         neighbor nodes reachable form {@code u} via directed edges.
     * @param targetPredicate a predicate that returns true for the target node(s). It can accept multiple
     *         nodes or no nodes at all (e.g. {@code t -> false}).
     * @return a map that associates a {@link PathResult} with each node that was reached from the source nodes
     *         during the search process.
     */
    public static <T> Map<T, PathResult<T>> run(Iterable<? extends T> sources,
            Function<? super T, ? extends Iterable<T>> neighborProvider,
            Predicate<? super T> targetPredicate) {
        var results = new HashMap<T, PathResult<T>>();

        var queue = new ArrayDeque<T>();
        for (T source : sources) {
            results.put(source, new PathResult<>(source, 0, targetPredicate.test(source), null));
            queue.add(source);
        }

        while (!queue.isEmpty()) {
            T node = queue.poll();
            var result = results.get(node);
            if (result.isTarget()) {
                break;
            }
            for (T neighbor : neighborProvider.apply(node)) {
                if (!results.containsKey(neighbor)) {
                    results.put(neighbor, new PathResult<>(neighbor, result.dist() + 1,
                            targetPredicate.test(neighbor), result));
                    queue.add(neighbor);
                }
            }
        }

        return results;
    }

}
