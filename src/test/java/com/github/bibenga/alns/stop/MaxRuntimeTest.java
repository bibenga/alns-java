package com.github.bibenga.alns.stop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;

class MaxRuntimeTest {

    @Test
    void testMaxRuntime() throws InterruptedException {
        MaxRuntime stop = new MaxRuntime(Duration.ofMillis(100));
        Instant started = Instant.now();
        while (true) {
            if (stop.isDone(null, null, null))
                break;
            Thread.sleep(1);
        }
        long elapsed = Duration.between(started, Instant.now()).toMillis();
        assertTrue(100 <= elapsed && elapsed <= 105,
                "expected duration 100ms, actual %d".formatted(elapsed));
    }
}