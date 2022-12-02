package com.github.pkovacs.util.alg;

import java.util.List;

import com.google.common.math.LongMath;

/**
 * Implements basic numerical algorithms.
 */
public final class NumericalAlgorithms {

    /**
     * Represents a congruence as a pair of divisor and remainder.
     * That is, (d, r) represents {@code x -> x % d == r}.
     */
    public static record Congruence(long divisor, long remainder) {

        public Congruence(long divisor, long remainder) {
            if (divisor < 1) {
                throw new IllegalArgumentException("Illegal divisor: " + divisor + ".");
            }
            this.divisor = divisor;
            this.remainder = LongMath.mod(remainder, divisor);
        }

        @Override
        public String toString() {
            return String.format("x %% %d == %d", divisor, remainder);
        }

    }

    private NumericalAlgorithms() {
    }

    /**
     * Checks if the given non-negative integer is prime by calling {@link LongMath#isPrime(long)}.
     */
    @SuppressWarnings("UnstableApiUsage")
    public static boolean isPrime(long a) {
        return LongMath.isPrime(a);
    }

    /**
     * Calculates the greatest common divisor (GCD) of the given two non-negative integers by calling
     * {@link LongMath#gcd(long, long)}.
     */
    public static long gcd(long a, long b) {
        return LongMath.gcd(a, b);
    }

    /**
     * Calculates the least common multiple (LCM) of the given two non-negative integers using
     * {@link LongMath#gcd(long, long)}.
     */
    public static long lcm(long a, long b) {
        return a / LongMath.gcd(a, b) * b; // operations are applied in this order to avoid overflow
    }

    /**
     * Finds a non-negative integer satisfying a list of congruences based on the Chinese remainder theorem.
     * The congruences are defined by the given divisors and remainders, where the divisors must be pairwise coprime.
     * <p>
     * This method implements a simple
     * <a href="https://en.wikipedia.org/wiki/Chinese_remainder_theorem#Search_by_sieving">sieving algorithm</a>.
     */
    public static long solveCrt(List<Congruence> congruences) {
        // Check that the divisors are pairwise coprime
        for (var c1 : congruences) {
            for (var c2 : congruences) {
                if (c1 != c2) {
                    checkCoprimeDivisors(c1, c2);
                }
            }
        }

        // Find the smallest appropriate number
        long n = 0;
        long step = 1;
        for (var c : congruences) {
            while (n % c.divisor != c.remainder) {
                n += step;
            }
            step *= c.divisor;
        }

        return n;
    }

    private static void checkCoprimeDivisors(Congruence c1, Congruence c2) {
        if (gcd(c1.divisor, c2.divisor) > 1) {
            throw new IllegalArgumentException(
                    "Divisors are not coprime: " + c1.divisor + " and " + c2.divisor + ".");
        }
    }

}
