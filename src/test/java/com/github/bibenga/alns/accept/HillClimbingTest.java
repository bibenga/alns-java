package com.github.bibenga.alns.accept;

import org.junit.jupiter.api.Test;

import com.github.bibenga.alns.FakeState;

import static org.junit.jupiter.api.Assertions.*;

class HillClimbingTest {

    @Test
    void testHillClimbing() {
        HillClimbing accept = new HillClimbing();
        FakeState best = new FakeState(2.0);
        FakeState curr = new FakeState(2.1);
        FakeState cand = new FakeState(1.9);

        assertTrue(accept.test(null, best, curr, cand), "expected to be accepted");

        FakeState cand2 = new FakeState(2.9);
        assertFalse(accept.test(null, best, curr, cand2), "expected not to be accepted");
    }
}