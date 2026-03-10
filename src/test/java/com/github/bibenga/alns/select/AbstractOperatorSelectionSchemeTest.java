package com.github.bibenga.alns.select;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import com.github.bibenga.alns.Outcome;
import com.github.bibenga.alns.State;

public class AbstractOperatorSelectionSchemeTest {

    @Test
    void testValid() {
        assertDoesNotThrow(() -> new DummyOperatorSelection(2, 3, null));
    }

    @Test
    void testNegativeDestroyCount() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new DummyOperatorSelection(-2, 3, null));
        assertEquals("Missing destroy or repair operators.", ex.getMessage());
    }

    @Test
    void testValicCoupling() {
        new DummyOperatorSelection(2, 3,
                new boolean[][] { { true, true, true }, { true, true, true } });
    }

    @Test
    void testEmptyCoupling() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new DummyOperatorSelection(2, 3, new boolean[][] {}));
        assertEquals("Coupling matrix of shape (0, 0), expected (2, 3)", ex.getMessage());
    }

    @Test
    void testDifferentSizeCoupling() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new DummyOperatorSelection(2, 3,
                        new boolean[][] { { true, true }, { true, true }, { true, true } }));
        assertEquals("Coupling matrix of shape (3, 2), expected (2, 3)", ex.getMessage());
    }

    @Test
    void testMissedCoupling() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new DummyOperatorSelection(2, 3,
                        new boolean[][] { { true, true }, { true } }));
        assertEquals("The number of columns in a row 1 does not match the expected 2",
                ex.getMessage());
    }

    @Test
    void testDestroyWithoutRepairCoupling() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new DummyOperatorSelection(2, 3,
                        new boolean[][] { { true, false, false }, { false, false, false } }));
        assertEquals("Destroy operator 1 has no coupled repair operators", ex.getMessage());
    }

    static class DummyOperatorSelection extends AbstractOperatorSelectionScheme {
        protected DummyOperatorSelection(int numDestroy, int numRepair) {
            super(numDestroy, numRepair);
        }

        protected DummyOperatorSelection(int numDestroy, int numRepair, boolean[][] opCoupling) {
            super(numDestroy, numRepair, opCoupling);
        }

        @Override
        public SelectedOperator select(RandomGenerator rng, State best, State curr) {
            throw new UnsupportedOperationException("Unimplemented method 'select'");
        }

        @Override
        public void update(State candidate, SelectedOperator op, Outcome outcome) {
            throw new UnsupportedOperationException("Unimplemented method 'update'");
        }
    }
}
