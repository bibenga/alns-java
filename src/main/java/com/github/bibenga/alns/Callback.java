package com.github.bibenga.alns;

import java.util.random.RandomGenerator;

@FunctionalInterface
public interface Callback {
    void call(Outcome outcome, State state, RandomGenerator rng);
}