package com.github.bibenga.alns.accept;

import java.util.random.RandomGenerator;

import com.github.bibenga.alns.State;

public interface AcceptanceCriterion {
    boolean test(RandomGenerator rng, State best, State current, State candidate);
}
