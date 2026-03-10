package com.github.bibenga.alns.accept;

import java.util.random.RandomGenerator;

import com.github.bibenga.alns.State;

public class HillClimbing implements AcceptanceCriterion {

    @Override
    public boolean test(RandomGenerator rng, State best, State current, State candidate) {
        return candidate.objective() <= current.objective();
    }
}