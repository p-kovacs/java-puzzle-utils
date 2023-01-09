# Java Puzzle Utils

Various utilities, data structures, and algorithms written in Java for solving coding puzzles like
[Advent of Code](https://adventofcode.com/).

Compatibility: Java 17+

However, Java is not the best tool for such tasks. Consider using Kotlin instead. :)

## Hints

This section lists a few notable utilities of the JDK and the [Guava](https://github.com/google/guava) library.

### JDK

* Collections
    * `Collections.reverse(List)`
    * `Collections.rotate(List, int)`
* Streams
    * Stream API - obviously :)
    * `Stream::toList` (Java 16+)
* Records (Java 14+)
* Other
    * `Math.floorDiv`, `Math.floorMod`

### Guava

* `Ints`, `Longs`, etc. - utils for (arrays of) primitives
* `Lists`
    * `partition(List, int)` - convert to list of chunks
    * `cartesianProduct(List...)`
* `Sets`
    * `intersection(Set, Set)`, `union(Set, Set)`
    * `difference(Set, Set)`, `symmetricDifference(Set, Set)`
    * `combinations(Set, int)`, `powerSet(Set)` - enumerate subsets
    * `cartesianProduct(Set...)`
* Multisets, multimaps
    * `Multiset`, `HashMultiset`, `LinkedHashMultiset`, `TreeMultiset`
    * `Multimap`, `MultimapBuilder`, e.g.:
        * `MultimapBuilder.hashKeys().arrayListValues().build()`
        * `MultimapBuilder.linkedHashKeys().hashSetValues().build()`
    * `Multimaps`, `Multisets` - related utils (collectors, filtering, etc.)
* Other
    * `BiMap`, `HashBiMap`
    * `Table`, `HashedBasedTable`, `TreeBasedTable`
    * `Range`
    * `RangeSet`
* `LongMath` (or `IntMath`)
    * `binomial(int, int)`
    * `factorial(int)`
    * `gcd(long, long)` - greatest common divisor
    * `isPowerOfTwo(long)`
    * `isPrime(long)` - currently `@Beta` (31.1)
