package com.github.bibenga.alns.accept;

import org.junit.jupiter.api.Test;

import com.github.bibenga.alns.FakeState;

import static org.junit.jupiter.api.Assertions.*;

class HillClimbingTest {

    private final HillClimbing accept = new HillClimbing();
    private final FakeState best = new FakeState(2.0);
    private final FakeState curr = new FakeState(2.1);

    @Test
    void testAccepted() {
        FakeState cand = new FakeState(1.9);
        assertTrue(accept.isAccept(null, best, curr, cand));
    }

    @Test
    void testRejected() {
        FakeState cand = new FakeState(2.9);
        assertFalse(accept.isAccept(null, best, curr, cand));
    }
}