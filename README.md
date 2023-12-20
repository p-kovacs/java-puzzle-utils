# Java Puzzle Utils

Various utilities, data structures, and algorithms written in Java for solving coding puzzles like
[Advent of Code](https://adventofcode.com/).

Compatibility: Java 21+

However, Java is not the best language for coding puzzles. Consider using Kotlin instead. :)

## Cheat sheet

This section lists a few notable utilities of the JDK and the [Guava](https://github.com/google/guava) library,
which can be practical for solving coding puzzles (besides the tools provided in this library).

### JDK

* Language features
    * lambdas
    * `var` (Java 11+)
    * records (Java 14+)
    * switch expressions (Java 14+)
* Collections
    * sequenced collections: first/last element and reversed view (Java 21+)
    * `List.of()`, `Set.of()`, `Map.of()`
    * `LinkedHashSet`, `LinkedHashMap`
    * `NavigableSet` (e.g. `TreeSet`)
    * `Collections.rotate(List, int)`
* Streams
    * stream API
    * `Stream::toList` (Java 16+)
* Other
    * `Math.clamp`, `Math.floorDiv`, `Math.floorMod`
    * `Arrays.mismatch()`

### Guava

* `Ints`, `Longs`, etc. - utils for (arrays of) primitives
* `Lists`
    * `partition(List, int)`
    * `cartesianProduct(List...)`
* `Sets`
    * `intersection(Set, Set)`, `union(Set, Set)`
    * `difference(Set, Set)`, `symmetricDifference(Set, Set)`
    * `combinations(Set, int)`, `powerSet(Set)`
    * `cartesianProduct(Set...)`
* Multisets, multimaps
    * `Multiset`, `HashMultiset`, `LinkedHashMultiset`, `TreeMultiset`
    * `Multimap`, `MultimapBuilder`, e.g.:
        * `MultimapBuilder.hashKeys().arrayListValues().build()`
        * `MultimapBuilder.linkedHashKeys().hashSetValues().build()`
    * `Multimaps`, `Multisets` - related utils (collectors, filtering, etc.)
* Other
    * `BiMap`, `HashBiMap`
    * `Range`
    * `RangeSet`
    * `Table`, `HashedBasedTable`, `TreeBasedTable`
* `LongMath` (or `IntMath`)
    * `gcd(long, long)` - greatest common divisor
    * `binomial(int, int)`
    * `factorial(int)`
    * `isPrime(long)`
    * `isPowerOfTwo(long)`
