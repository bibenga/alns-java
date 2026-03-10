package com.github.bibenga.alns;

public enum Outcome {
    /** Candidate solution is a new global best. */
    BEST,
    /** Candidate solution is better than the current incumbent. */
    BETTER,
    /** Candidate solution is accepted. */
    ACCEPT,
    /** Candidate solution is rejected. */
    REJECT;
}