package com.github.bibenga.alns.tsp;

import java.util.HashMap;
import java.util.Map;

import com.github.bibenga.alns.State;

class TspState implements State {

    final int[] nodes;
    final Map<Integer, Integer> edges;
    final double[][] dists;
    private double objective = Double.NaN;

    TspState(int[] nodes, Map<Integer, Integer> edges, double[][] dists) {
        this.nodes = nodes;
        this.edges = edges;
        this.dists = dists;
    }

    public TspState cloneState() {
        return new TspState(nodes, new HashMap<>(edges), dists);
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