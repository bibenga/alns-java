package com.github.bibenga.alns;

import java.util.random.RandomGenerator;

@FunctionalInterface
public interface Operator {
    State apply(State state, RandomGenerator rng);
}
