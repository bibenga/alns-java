package com.github.bibenga.alns.select;

public abstract class AvstractOperatorSelectionScheme implements OperatorSelectionScheme {

    private final int numDestroy;
    private final int numRepair;
    private final boolean[][] opCoupling;

    protected AvstractOperatorSelectionScheme(int numDestroy, int numRepair, boolean[][] opCoupling) {
        if (opCoupling == null) {
            opCoupling = allOnes(numDestroy, numRepair);
        }
        validateArguments(numDestroy, numRepair, opCoupling);
        this.numDestroy = numDestroy;
        this.numRepair = numRepair;
        this.opCoupling = opCoupling;
    }

    protected AvstractOperatorSelectionScheme(int numDestroy, int numRepair) {
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
        if (opCoupling.length != numDestroy || opCoupling[0].length != numRepair) {
            throw new IllegalArgumentException(
                    "Coupling matrix of shape [%d][%d], expected [%d][%d].".formatted(
                            opCoupling.length, opCoupling[0].length, numDestroy, numRepair));
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
                        "Destroy op. %d has no coupled repair operators.".formatted(i));
            }
        }
    }

    private static boolean[][] allOnes(int rows, int cols) {
        boolean[][] m = new boolean[rows][cols];
        for (var row : m)
            java.util.Arrays.fill(row, true);
        return m;
    }
}