package com.github.pkovacs.util.alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result object for path search algorithms.
 *
 * @see Bfs
 * @see Dijkstra
 * @see ShortestPath
 */
public final class PathResult<T> {

    private final T node;
    private final long dist;
    private final PathResult<T> prev;

    private List<T> path;

    PathResult(T node, long dist, PathResult<T> prev) {
        this.node = node;
        this.dist = dist;
        this.prev = prev;
    }

    public T node() {
        return node;
    }

    public long dist() {
        return dist;
    }

    public List<T> path() {
        if (path == null) {
            // Lazy load: construct path
            var list = new ArrayList<T>();
            for (var e = this; e != null; e = e.prev) {
                list.add(e.node);
            }
            Collections.reverse(list);
            path = Collections.unmodifiableList(list);
        }
        return path;
    }

}
