package com.github.bibenga.alns.select;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import com.github.bibenga.alns.Outcome;
import com.github.bibenga.alns.State;

public class RouletteWheel extends OperatorSelectionScheme {

    private final double[] scores;
    private final double[] dWeights;
    private final double[] rWeights;
    private final double decay;

    public RouletteWheel(
            double[] scores,
            double decay,
            int numDestroy,
            int numRepair,
            boolean[][] opCoupling) {
        super(numDestroy, numRepair, opCoupling);

        for (double s : scores) {
            if (s < 0)
                throw new IllegalArgumentException("Negative scores are not understood.");
        }
        if (scores.length < 4) {
            throw new IllegalArgumentException(
                    "Expected four scores, found %d".formatted(scores.length));
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

    public RouletteWheel(double[] scores, double decay, int numDestroy, int numRepair) {
        this(scores, decay, numDestroy, numRepair, null);
    }

    public double[] getScores() {
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

    /**
     * Selects a destroy and repair operator pair based on normalised operator
     * weights. Operators with higher weights are selected with higher probability.
     *
     * @return int array {d_idx, r_idx} — indices into destroy and repair operator lists.
     */
    @Override
    public int[] select(RandomGenerator rng, State best, State curr) {
        int dIdx = weightedChoice(rng, dWeights);

        // Collect coupled repair operator indices for the chosen destroy op
        boolean[][] coupling = getOpCoupling();
        int[] coupledR = coupledRepairIndices(coupling[dIdx]);
        double[] coupledRWeights = new double[coupledR.length];
        for (int i = 0; i < coupledR.length; i++) {
            coupledRWeights[i] = rWeights[coupledR[i]];
        }
        int rIdx = coupledR[weightedChoice(rng, coupledRWeights)];

        return new int[] { dIdx, rIdx };
    }

    @Override
    public void update(State candidate, int dIdx, int rIdx, Outcome outcome) {
        double score = scores[outcome.getValue()];
        dWeights[dIdx] = decay * dWeights[dIdx] + (1 - decay) * score;
        rWeights[rIdx] = decay * rWeights[rIdx] + (1 - decay) * score;
    }

    /** Samples an index from weights[] proportionally (roulette wheel selection). */
    private static int weightedChoice(RandomGenerator rng, double[] weights) {
        double total = Arrays.stream(weights).sum();
        double r = rng.nextDouble() * total;
        double cumulative = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (r < cumulative)
                return i;
        }
        return weights.length - 1; // fallback for floating-point edge cases
    }

    /** Returns indices where the coupling row is true. */
    private static int[] coupledRepairIndices(boolean[] couplingRow) {
        int count = 0;
        for (boolean b : couplingRow)
            if (b)
                count++;
        int[] indices = new int[count];
        int k = 0;
        for (int i = 0; i < couplingRow.length; i++) {
            if (couplingRow[i])
                indices[k++] = i;
        }
        return indices;
    }
}