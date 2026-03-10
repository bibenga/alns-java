package com.github.bibenga.alns.select;

import java.util.Arrays;
import java.util.Map;
import java.util.random.RandomGenerator;

import com.github.bibenga.alns.Outcome;
import com.github.bibenga.alns.State;

public class RouletteWheel extends AbstractOperatorSelectionScheme {

    private final Map<Outcome, Double> scores;
    private final double[] dWeights;
    private final double[] rWeights;
    private final double decay;

    public RouletteWheel(
            Map<Outcome, Double> scores,
            double decay,
            int numDestroy,
            int numRepair,
            boolean[][] opCoupling) {
        super(numDestroy, numRepair, opCoupling);

        for (double s : scores.values()) {
            if (s < 0)
                throw new IllegalArgumentException("Negative scores are not understood.");
        }
        if (scores.size() < 4) {
            throw new IllegalArgumentException(
                    "Expected four scores, found %d".formatted(scores.size()));
        }
        if (decay < 0 || decay > 1) {
            throw new IllegalArgumentException("decay outside [0, 1] not understood.");
        }

        this.scores = scores;
        this.decay = decay;
        this.dWeights = new double[numDestroy];
        this.rWeights = new double[numRepair];
        Arrays.fill(dWeights, 1.0);
        Arrays.fill(rWeights, 1.0);
    }

    public RouletteWheel(Map<Outcome, Double> scores, double decay, int numDestroy, int numRepair) {
        this(scores, decay, numDestroy, numRepair, null);
    }

    public Map<Outcome, Double> getScores() {
        return scores;
    }

    public double[] getDestroyWeights() {
        return dWeights;
    }

    public double[] getRepairWeights() {
        return rWeights;
    }

    public double getDecay() {
        return decay;
    }

    @Override
    public SelectedOperator select(RandomGenerator rng, State best, State curr) {
        if (hasOpCoupling()) {
            int dIdx = weightedChoice(rng, dWeights);

            int[] coupledR = coupledRepairIndices(opCoupling[dIdx]);
            double[] coupledRWeights = new double[coupledR.length];
            for (int i = 0; i < coupledR.length; i++) {
                coupledRWeights[i] = rWeights[coupledR[i]];
            }
            int rIdx = coupledR[weightedChoice(rng, coupledRWeights)];

            return new SelectedOperator(dIdx, rIdx);
        } else {
            int dIdx = weightedChoice(rng, dWeights);
            int rIdx = weightedChoice(rng, rWeights);
            return new SelectedOperator(dIdx, rIdx);
        }
    }

    @Override
    public void update(State candidate, SelectedOperator op, Outcome outcome) {
        double score = scores.get(outcome);
        var dIdx = op.dIdx();
        var rIdx = op.rIdx();
        dWeights[dIdx] = decay * dWeights[dIdx] + (1 - decay) * score;
        rWeights[rIdx] = decay * rWeights[rIdx] + (1 - decay) * score;
    }

    private static int weightedChoice(RandomGenerator rng, double[] weights) {
        double total = getTotal(weights);
        double r = rng.nextDouble() * total;
        double cumulative = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (r < cumulative)
                return i;
        }
        return weights.length - 1; // fallback for floating-point edge cases
    }

    private static double getTotal(double[] weights) {
        // return Arrays.stream(weights).sum();
        double total = 0.0;
        for (double w : weights) {
            total += w;
        }
        return total;
    }

    private static int[] coupledRepairIndices(boolean[] couplingRow) {
        int count = 0;
        for (boolean b : couplingRow) {
            if (b) {
                count++;
            }
        }
        int[] indices = new int[count];
        int k = 0;
        for (int i = 0; i < couplingRow.length; i++) {
            if (couplingRow[i])
                indices[k++] = i;
        }
        return indices;
    }
}