package com.github.bibenga.alns.stop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MaxIterationsTest {

    @Test
    void testMaxIterations() {
        MaxIterations stop = new MaxIterations(10);
        int i = 0;
        while (true) {
            if (stop.isDone(null, null, null))
                break;
            i++;
        }
        assertEquals(10, i, "10 iterations expected, actual %d iterations".formatted(i));
        assertEquals(11, stop.getCurrentIteration(),
                "number 11 expected, actual number %d".formatted(stop.getCurrentIteration()));
    }
}