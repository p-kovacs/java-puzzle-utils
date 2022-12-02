package com.github.pkovacs.util.alg;

import java.util.List;

import org.junit.jupiter.api.Test;
import com.github.pkovacs.util.alg.NumericalAlgorithms.Congruence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumericalAlgorithmsTest {

    @Test
    void testIsPrime() {
        assertTrue(NumericalAlgorithms.isPrime(1129));
        assertTrue(NumericalAlgorithms.isPrime(6637));
        assertTrue(NumericalAlgorithms.isPrime(27644437));
        assertTrue(NumericalAlgorithms.isPrime(87178291199L));
        assertFalse(NumericalAlgorithms.isPrime(27644437 * 87178291199L));
    }

    @Test
    void testGcd() {
        assertEquals(15, NumericalAlgorithms.gcd(120, 105));
        assertEquals(27644437L * 7,
                NumericalAlgorithms.gcd(27644437L * 777, 27644437L * 7 * 13 * 1129 * 6637));
    }

    @Test
    void testLcm() {
        assertEquals(840, NumericalAlgorithms.lcm(120, 105));
        assertEquals(2092367088727798701L,
                NumericalAlgorithms.lcm(27644437L * 777, 27644437L * 7 * 13 * 1129 * 6637));
    }

    @Test
    void testCrt() {
        var congruences = List.of(
                new Congruence(19, 0),
                new Congruence(41, 32),
                new Congruence(37, 24),
                new Congruence(367, 348),
                new Congruence(13, 7),
                new Congruence(17, 15),
                new Congruence(29, 10),
                new Congruence(373, 323),
                new Congruence(23, 19)
        );

        var result = NumericalAlgorithms.solveCrt(congruences);

        assertEquals(560214575859998L, result);
        assertTrue(congruences.stream().allMatch(c -> result % c.divisor() == c.remainder()));
    }

}
