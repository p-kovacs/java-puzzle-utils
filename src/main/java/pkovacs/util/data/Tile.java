package pkovacs.util.data;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a tile (or cell) in a table or matrix as an immutable pair of {@code int} values: row index and
 * column index. Provides methods to get the neighbors of a tile and the Manhattan distance between two tiles.
 * Lexicographical ordering is also supported (first by row index, then by column index).
 * <p>
 * This record is similar to {@link Point} but with different index order and names.
 *
 * @see Point
 */
public record Tile(int row, int col) implements Comparable<Tile> {

    /**
     * Returns true the indices of this tile are between zero (inclusive) and the given row/column count (exclusive).
     */
    public boolean isValid(int rowCount, int colCount) {
        return row >= 0 && row < rowCount && col >= 0 && col < colCount;
    }

    /**
     * Returns the neighbor in the given direction. The tile (0, 0) represents the upper-left corner among the
     * tiles with non-negative indices. The directions are interpreted accordingly, so "north" or "up" means lower
     * row index, while "south" or "down" means higher row index.
     */
    public Tile neighbor(Direction dir) {
        return switch (dir) {
            case NORTH -> new Tile(row - 1, col);
            case EAST -> new Tile(row, col + 1);
            case SOUTH -> new Tile(row + 1, col);
            case WEST -> new Tile(row, col - 1);
        };
    }

    /**
     * Returns the neighbor in the given direction. The tile (0, 0) represents the upper-left corner among the
     * tiles with non-negative indices. The directions are interpreted accordingly, so "north" or "up" means lower
     * row index, while "south" or "down" means higher row index.
     *
     * @param dir the direction. One of 'N' (north), 'E' (east), 'S' (south), 'W' (west),
     *         'U' (up), 'R' (right), 'D' (down), 'L' (left), and their lowercase variants.
     */
    public Tile neighbor(char dir) {
        return neighbor(Direction.fromChar(dir));
    }

    /**
     * Returns the four neighbors of this tile.
     *
     * @return the four neighbor tiles in clockwise order (N, E, S, W)
     */
    public List<Tile> neighbors() {
        return List.of(
                new Tile(row - 1, col),
                new Tile(row, col + 1),
                new Tile(row + 1, col),
                new Tile(row, col - 1));
    }

    /**
     * Returns the neighbors of this tile that are accepted by the given predicate.
     *
     * @return the accepted neighbors (at most four tiles) in clockwise order (N, E, S, W)
     */
    public List<Tile> neighbors(Predicate<Tile> predicate) {
        return neighbors().stream().filter(predicate).toList();
    }

    /**
     * Returns the {@link #isValid(int, int) valid} neighbors of this tile with respect to the given row count and
     * column count.
     *
     * @return the valid neighbors (at most four tiles) in clockwise order (N, E, S, W)
     */
    public List<Tile> validNeighbors(int rowCount, int colCount) {
        return neighbors(t -> t.isValid(rowCount, colCount));
    }

    /**
     * Returns the eight "extended" neighbors of this tile, including the diagonal ones.
     *
     * @return the eight "extended" neighbor tiles in clockwise order (N, NE, E, SE, S, SW, W, NW)
     */
    public List<Tile> extendedNeighbors() {
        return List.of(
                new Tile(row - 1, col),
                new Tile(row - 1, col + 1),
                new Tile(row, col + 1),
                new Tile(row + 1, col + 1),
                new Tile(row + 1, col),
                new Tile(row + 1, col - 1),
                new Tile(row, col - 1),
                new Tile(row - 1, col - 1));
    }

    /**
     * Returns the "extended" neighbors of this tile that are accepted by the given predicate, including diagonal ones.
     *
     * @return the accepted "extended" neighbors (at most eight tiles) in clockwise order (N, NE, E, SE, S, SW, W, NW)
     */
    public List<Tile> extendedNeighbors(Predicate<Tile> predicate) {
        return extendedNeighbors().stream().filter(predicate).toList();
    }

    /**
     * Returns the Manhattan distance between this tile and the given tile.
     */
    public int dist(Tile t) {
        return dist(this, t);
    }

    /**
     * Returns the Manhattan distance between the given two tiles.
     */
    public static int dist(Tile t1, Tile t2) {
        return Math.abs(t1.row - t2.row) + Math.abs(t1.col - t2.col);
    }

    @Override
    public int compareTo(Tile other) {
        return row != other.row ? Integer.compare(row, other.row) : Integer.compare(col, other.col);
    }

    /**
     * Returns an ordered stream of valid tiles within the given bounds.
     * If both arguments are positive, then the first element of the stream will be (0, 0), and the last element
     * will be (rowCount - 1, colCount - 1). Otherwise, an empty stream is returned.
     */
    public static Stream<Tile> stream(int rowCount, int colCount) {
        return stream(0, 0, rowCount, colCount);
    }

    /**
     * Returns an ordered stream of tiles within the given bounds.
     * If {@code startRow < endRow} and {@code startCol < endCol}, then the first element of the stream will be
     * (startRow, startCol), and the last element will be (endRow - 1, endCol - 1). Otherwise, an empty stream
     * is returned.
     */
    public static Stream<Tile> stream(int startRow, int startCol, int endRow, int endCol) {
        int rowCount = endRow - startRow;
        int colCount = endCol - startCol;

        if (rowCount <= 0 || colCount <= 0) {
            return Stream.empty();
        } else {
            return IntStream.range(0, rowCount * colCount)
                    .mapToObj(i -> new Tile(startRow + i / colCount, startCol + i % colCount));
        }
    }

}
