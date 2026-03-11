package com.github.bibenga.alns.select;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import com.github.bibenga.alns.FakeState;
import com.github.bibenga.alns.Outcome;

class RouletteWheelTest {

    @Test
    void testValidation() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> new RouletteWheel(Scores.of(-1, 2, 1, 0.5), 0.8, 2, 3, null));
        assertEquals("Negative scores are not understood", ex.getMessage());
    }

    @Test
    void testSimple() {
        Random r = new Random(42);
        RouletteWheel selector = assertDoesNotThrow(() -> new RouletteWheel(Scores.of(3, 2, 1, 0.5), 0.8, 3, 2, null));

        FakeState best = new FakeState(0);
        FakeState current = new FakeState(0);
        FakeState candidate = new FakeState(0);

        int[] dCounter = new int[3];
        int[] rCounter = new int[2];
        int total = 10000;

        for (int n = 0; n < total; n++) {
            Outcome outcome = Outcome.values()[r.nextInt(4)];
            var selected = selector.select(r, best, current);
            selector.update(candidate, selected, outcome);
            dCounter[selected.dIdx()]++;
            rCounter[selected.rIdx()]++;
        }

        int[][] counters = { dCounter, rCounter };
        for (int counterNum = 0; counterNum < counters.length; counterNum++) {
            int[] counter = counters[counterNum];
            for (int i = 0; i < counter.length; i++) {
                double expected = (1.0 / counter.length) * total;
                double got = counter[i];
                assertTrue((got - expected) / expected <= 0.05,
                        "index (%d, %d): got %f, expected ~%f".formatted(counterNum, i, got, expected));
            }
        }
    }

    @Test
    void testCoupling() {
        Random r = new Random(42);
        RouletteWheel selector = assertDoesNotThrow(() -> new RouletteWheel(Scores.of(3, 2, 1, 0.5), 0.8, 2, 3,
                new boolean[][] { { true, true, false }, { false, true, true } }));

        FakeState best = new FakeState(0);
        FakeState current = new FakeState(0);
        FakeState candidate = new FakeState(0);

        int[] dCounter = new int[selector.getNumDestroy()];
        int[] rCounter = new int[selector.getNumRepair()];
        int total = 10000;

        for (int n = 0; n < total; n++) {
            Outcome outcome = Outcome.values()[r.nextInt(4)];
            var selected = selector.select(r, best, current);
            int dIdx = selected.dIdx();
            int rIdx = selected.rIdx();

            assertTrue(0 <= dIdx && dIdx < selector.getNumDestroy(),
                    "destroy index %d is invalid".formatted(dIdx));
            assertTrue(0 <= rIdx && rIdx < selector.getNumRepair(),
                    "repair index %d is invalid".formatted(rIdx));

            selector.update(candidate, selected, outcome);
            dCounter[dIdx]++;
            rCounter[rIdx]++;
        }

        double[] expectedDestroyPercent = { 0.5, 0.5 };
        for (int i = 0; i < dCounter.length; i++) {
            double expected = expectedDestroyPercent[i] * total;
            double got = dCounter[i];
            assertTrue((got - expected) / expected <= 0.05,
                    "destroy index %d: got %f, expected ~%f".formatted(i, got, expected));
        }

        double[] expectedRepairPercent = { 0.25, 0.5, 0.25 };
        for (int i = 0; i < rCounter.length; i++) {
            double expected = expectedRepairPercent[i] * total;
            double got = rCounter[i];
            assertTrue((got - expected) / expected <= 0.05,
                    "repair index %d: got %f, expected ~%f".formatted(i, got, expected));
        }
    }
}