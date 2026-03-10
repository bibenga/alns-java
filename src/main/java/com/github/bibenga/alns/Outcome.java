package com.github.bibenga.alns;

public enum Outcome {
    /** Candidate solution is a new global best. */
    BEST(0),
    /** Candidate solution is better than the current incumbent. */
    BETTER(1),
    /** Candidate solution is accepted. */
    ACCEPT(2),
    /** Candidate solution is rejected. */
    REJECT(3);

    private final int value;

    Outcome(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}