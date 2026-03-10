package com.github.bibenga.alns.select;

public abstract class AbstractOperatorSelectionScheme implements OperatorSelectionScheme {

    protected final int numDestroy;
    protected final int numRepair;
    protected final boolean[][] opCoupling;

    protected AbstractOperatorSelectionScheme(int numDestroy, int numRepair, boolean[][] opCoupling) {
        validateArguments(numDestroy, numRepair, opCoupling);
        this.numDestroy = numDestroy;
        this.numRepair = numRepair;
        this.opCoupling = opCoupling;
    }

    protected AbstractOperatorSelectionScheme(int numDestroy, int numRepair) {
        this(numDestroy, numRepair, null);
    }

    public int getNumDestroy() {
        return numDestroy;
    }

    public int getNumRepair() {
        return numRepair;
    }

    public boolean hasOpCoupling() {
        return opCoupling != null;
    }

    public boolean[][] getOpCoupling() {
        return opCoupling;
    }

    private static void validateArguments(int numDestroy, int numRepair, boolean[][] opCoupling) {
        if (numDestroy <= 0 || numRepair <= 0) {
            throw new IllegalArgumentException("Missing destroy or repair operators.");
        }
        if (opCoupling != null) {
            // if (opCoupling.length != numDestroy || opCoupling[0].length != numRepair) {
            //     throw new IllegalArgumentException(
            //             "Coupling matrix of shape (%d, %d), expected (%d, %d)".formatted(
            //                     opCoupling.length, opCoupling.length > 0 ? opCoupling[0].length : 0,
            //                     numDestroy, numRepair));
            // }

            var rows = opCoupling.length;
            var cols = opCoupling.length > 0 ? opCoupling[0].length : 0;
            for (int i = 0; i < opCoupling.length; i++) {
                if (opCoupling[i].length != cols) {
                    throw new IllegalArgumentException(
                            "The number of columns in a row %d does not match the expected %d".formatted(i, cols));
                }
            }

            if (rows != numDestroy || cols != numRepair) {
                throw new IllegalArgumentException(
                        "Coupling matrix of shape (%d, %d), expected (%d, %d)".formatted(
                                rows, cols, numDestroy, numRepair));
            }

            for (int i = 0; i < numDestroy; i++) {
                boolean hasCoupled = false;
                for (int j = 0; j < numRepair; j++) {
                    if (opCoupling[i][j]) {
                        hasCoupled = true;
                        break;
                    }
                }
                if (!hasCoupled) {
                    throw new IllegalArgumentException(
                            "Destroy operator %d has no coupled repair operators".formatted(i));
                }
            }
        }
    }
}