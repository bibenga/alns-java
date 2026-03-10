package com.github.bibenga.alns;

public class FakeState implements State {

    private final double objective;

    public FakeState(double objective) {
        this.objective = objective;
    }

    public FakeState clone() {
        return new FakeState(0.0);
    }

    public double objective() {
        return objective;
    }
}