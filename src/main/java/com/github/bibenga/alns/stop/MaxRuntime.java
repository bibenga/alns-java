package com.github.bibenga.alns.stop;

import java.time.Duration;
import java.util.random.RandomGenerator;

import com.github.bibenga.alns.State;

public class MaxRuntime implements StoppingCriterion {
    private final long maxRuntime;
    private long startRuntime = -1;

    public MaxRuntime(Duration maxRuntime) {
        if (maxRuntime.isNegative()) {
            throw new IllegalArgumentException("max_runtime < 0 not understood.");
        }
        this.maxRuntime = maxRuntime.toNanos();
    }

    public Duration getMaxRuntime() {
        return Duration.ofNanos(maxRuntime);
    }

    public boolean isDone(RandomGenerator rng, State best, State current) {
        if (startRuntime < 0) {
            startRuntime = System.nanoTime();
        }
        long elapsedNanos = System.nanoTime() - startRuntime;
        return elapsedNanos > maxRuntime;
    }
}