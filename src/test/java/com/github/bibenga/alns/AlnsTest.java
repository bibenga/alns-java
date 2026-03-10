package com.github.bibenga.alns;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import com.github.bibenga.alns.accept.HillClimbing;
import com.github.bibenga.alns.select.RouletteWheel;
import com.github.bibenga.alns.select.Scores;
import com.github.bibenga.alns.stop.MaxIterations;

class AlnsTest {

    @Test
    void testAlns() {
        final int total = 10000;

        RouletteWheel opSelect = new RouletteWheel(Scores.of(3, 2, 1, 0.5), 0.8, 1, 1, null);
        HillClimbing accept = new HillClimbing();
        MaxIterations stop = new MaxIterations(total);

        Random rnd = new Random();
        double[] lastBest = { rnd.nextDouble() };
        FakeState initSol = new FakeState(lastBest[0]);

        int[] bestCount = { 0 };
        int[] destroyCalled = { 0 };
        int[] repairCalled = { 0 };

        Operator destroyOp = (state, r) -> {
            destroyCalled[0]++;
            FakeState current = (FakeState) state;
            return current.clone();
        };

        Operator repairOp = (state, r) -> {
            repairCalled[0]++;
            FakeState current = (FakeState) state;
            current.setObjective(rnd.nextDouble());
            if (current.objective() < lastBest[0]) {
                lastBest[0] = current.objective();
                bestCount[0]++;
            }
            return current;
        };

        ALNS a = new ALNS(rnd);
        a.addDestroyOperator("destroyOp", destroyOp);
        a.addRepairOperator("repairOp", repairOp);

        Result res = assertDoesNotThrow(() -> a.iterate(initSol, opSelect, accept, stop));

        assertEquals(total, destroyCalled[0],
                "%d destroy calls expected, actual %d calls".formatted(total, destroyCalled[0]));
        assertEquals(total, repairCalled[0],
                "%d repair calls expected, actual %d calls".formatted(total, repairCalled[0]));
        assertEquals(total + 1, stop.getCurrentIteration(),
                "%d iterations expected, actual %d".formatted(total + 1, stop.getCurrentIteration()));
        assertEquals(total + 1, res.statistics().getObjectives().size(),
                "%d objectives expected, actual %d".formatted(total + 1, res.statistics().getObjectives().size()));

        EnumMap<Outcome, Integer> repairOperatorCounts = new EnumMap<>(Map.of(
                Outcome.BEST, bestCount[0],
                Outcome.BETTER, 0,
                Outcome.ACCEPT, 0,
                Outcome.REJECT, total - bestCount[0]));
        assertEquals(repairOperatorCounts, res.statistics().getRepairOperatorCounts().get(0));

        EnumMap<Outcome, Integer> destroyOperatorCounts = new EnumMap<>(Map.of(
                Outcome.BEST, bestCount[0],
                Outcome.BETTER, 0,
                Outcome.ACCEPT, 0,
                Outcome.REJECT, total - bestCount[0]));
        assertEquals(destroyOperatorCounts, res.statistics().getDestroyOperatorCounts().get(0));
    }
}