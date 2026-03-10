package com.github.bibenga.alns.stop;

import java.util.random.RandomGenerator;

import com.github.bibenga.alns.State;

public class MaxIterations implements StoppingCriterion {
    private final int maxIterations;
    private int currentIteration = 0;

    public MaxIterations(int maxIterations) {
        if (maxIterations < 0) {
            throw new IllegalArgumentException("maxIterations < 0 not understood.");
        }
        this.maxIterations = maxIterations;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    int getCurrentIteration() {
        return currentIteration;
    }

    public boolean test(RandomGenerator rng, State best, State current) {
        currentIteration++;
        return currentIteration > maxIterations;
    }

}
