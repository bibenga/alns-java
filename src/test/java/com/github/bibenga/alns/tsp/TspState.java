package com.github.bibenga.alns.tsp;

import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;

import com.github.bibenga.alns.State;

class TspState implements State {
    final int[] nodes;
    final MutableIntIntMap edges;
    final double[][] dists;
    private double objective = Double.NaN;

    TspState(int[] nodes, MutableIntIntMap edges, double[][] dists) {
        this.nodes = nodes;
        this.edges = edges;
        this.dists = dists;
    }

    @Override
    public TspState clone() {
        return new TspState(nodes, new IntIntHashMap(edges), dists);
    }

    @Override
    public double objective() {
        if (Double.isNaN(objective)) {
            double v = 0.0;
            int from = 0;
            while (true) {
                Integer to = edges.get(from);
                if (to == null || to == 0)
                    break;
                v += dists[from][to];
                from = to;
            }
            objective = v;
        }
        return objective;
    }
}