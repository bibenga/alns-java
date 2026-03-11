package com.github.bibenga.alns.stop;

import java.util.random.RandomGenerator;

import com.github.bibenga.alns.State;

public interface StoppingCriterion {
    boolean isDone(RandomGenerator rng, State best, State current);
}
