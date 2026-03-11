package com.github.bibenga.alns.select;

import java.util.random.RandomGenerator;

import com.github.bibenga.alns.Outcome;
import com.github.bibenga.alns.State;

public interface OperatorSelectionScheme {
    SelectedOperator select(RandomGenerator rng, State best, State current);

    void update(State candidate, SelectedOperator op, Outcome outcome);
}