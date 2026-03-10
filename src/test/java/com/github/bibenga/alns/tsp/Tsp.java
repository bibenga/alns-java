package com.github.bibenga.alns.tsp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import com.github.bibenga.alns.ALNS;
import com.github.bibenga.alns.Operator;
import com.github.bibenga.alns.Outcome;
import com.github.bibenga.alns.State;
import com.github.bibenga.alns.accept.HillClimbing;
import com.github.bibenga.alns.select.RouletteWheel;
import com.github.bibenga.alns.stop.MaxIterations;

public class Tsp {

    public static void main(String[] args) throws Exception {
        double[][] dists = buildDists(COORDS);
        int n = COORDS.length;
        int[] nodes = IntStream.range(0, n).toArray();

        Random rng = new Random(42);

        TspState initSol = new TspState(nodes, new HashMap<>(), dists);
        initSol = (TspState) greedyRepair(initSol, rng);

        System.out.println("optimal solution: 564");
        System.out.printf("initial solution: %.4f%n", initSol.objective());

        String[] destroyNames = { "randomRemoval", "pathRemoval", "worstRemoval" };
        String[] repairNames = { "greedyRepair" };

        Operator[] destroyOperators = { Tsp::randomRemoval, Tsp::pathRemoval, Tsp::worstRemoval };
        Operator[] repairOperators = { Tsp::greedyRepair };

        var scores = new EnumMap<>(Map.of(
                Outcome.BEST, 3.0,
                Outcome.BETTER, 2.0,
                Outcome.ACCEPT, 1.0,
                Outcome.REJECT, 0.5));

        RouletteWheel sel = new RouletteWheel(
                scores,
                0.8,
                destroyOperators.length,
                repairOperators.length,
                null);
        HillClimbing accept = new HillClimbing();
        MaxIterations stop = new MaxIterations(2000);

        ALNS alns = new ALNS(rng);
        for (int i = 0; i < destroyOperators.length; i++) {
            alns.addDestroyOperator(destroyNames[i], destroyOperators[i]);
        }
        for (int i = 0; i < repairOperators.length; i++) {
            alns.addRepairOperator(repairNames[i], repairOperators[i]);
        }
        var result = alns.iterate(initSol, sel, accept, stop);

        var stat = result.statistics();
        TspState best = (TspState) result.bestState();

        System.out.printf("best solution: %.4f%n", best.objective());
        System.out.printf("statistics: IterationCount=%d; TotalRuntime=%s%n",
                stat.getIterationCount(),
                stat.getTotalRuntime());
        System.out.println("  destroy operators");
        for (int i = 0; i < destroyNames.length; i++) {
            System.out.printf("    %d: %14s; %s%n", i, destroyNames[i], stat.getDestroyOperatorCounts().get(i));
        }
        System.out.println("  repair operators");
        for (int i = 0; i < repairNames.length; i++) {
            System.out.printf("    %d: %14s; %s%n", i, repairNames[i], stat.getRepairOperatorCounts().get(i));
        }

        // writeDotFile("tsp.dot", COORDS, best.edges);
    }

    static final double[][] COORDS = {
            { 0, 13 },
            { 0, 26 },
            { 0, 27 },
            { 0, 39 },
            { 2, 0 },
            { 5, 13 },
            { 5, 19 },
            { 5, 25 },
            { 5, 31 },
            { 5, 37 },
            { 5, 43 },
            { 5, 8 },
            { 8, 0 },
            { 9, 10 },
            { 10, 10 },
            { 11, 10 },
            { 12, 10 },
            { 12, 5 },
            { 15, 13 },
            { 15, 19 },
            { 15, 25 },
            { 15, 31 },
            { 15, 37 },
            { 15, 43 },
            { 15, 8 },
            { 18, 11 },
            { 18, 13 },
            { 18, 15 },
            { 18, 17 },
            { 18, 19 },
            { 18, 21 },
            { 18, 23 },
            { 18, 25 },
            { 18, 27 },
            { 18, 29 },
            { 18, 31 },
            { 18, 33 },
            { 18, 35 },
            { 18, 37 },
            { 18, 39 },
            { 18, 41 },
            { 18, 42 },
            { 18, 44 },
            { 18, 45 },
            { 25, 11 },
            { 25, 15 },
            { 25, 22 },
            { 25, 23 },
            { 25, 24 },
            { 25, 26 },
            { 25, 28 },
            { 25, 29 },
            { 25, 9 },
            { 28, 16 },
            { 28, 20 },
            { 28, 28 },
            { 28, 30 },
            { 28, 34 },
            { 28, 40 },
            { 28, 43 },
            { 28, 47 },
            { 32, 26 },
            { 32, 31 },
            { 33, 15 },
            { 33, 26 },
            { 33, 29 },
            { 33, 31 },
            { 34, 15 },
            { 34, 26 },
            { 34, 29 },
            { 34, 31 },
            { 34, 38 },
            { 34, 41 },
            { 34, 5 },
            { 35, 17 },
            { 35, 31 },
            { 38, 16 },
            { 38, 20 },
            { 38, 30 },
            { 38, 34 },
            { 40, 22 },
            { 41, 23 },
            { 41, 32 },
            { 41, 34 },
            { 41, 35 },
            { 41, 36 },
            { 48, 22 },
            { 48, 27 },
            { 48, 6 },
            { 51, 45 },
            { 51, 47 },
            { 56, 25 },
            { 57, 12 },
            { 57, 25 },
            { 57, 44 },
            { 61, 45 },
            { 61, 47 },
            { 63, 6 },
            { 64, 22 },
            { 71, 11 },
            { 71, 13 },
            { 71, 16 },
            { 71, 45 },
            { 71, 47 },
            { 74, 12 },
            { 74, 16 },
            { 74, 20 },
            { 74, 24 },
            { 74, 29 },
            { 74, 35 },
            { 74, 39 },
            { 74, 6 },
            { 77, 21 },
            { 78, 10 },
            { 78, 32 },
            { 78, 35 },
            { 78, 39 },
            { 79, 10 },
            { 79, 33 },
            { 79, 37 },
            { 80, 10 },
            { 80, 41 },
            { 80, 5 },
            { 81, 17 },
            { 84, 20 },
            { 84, 24 },
            { 84, 29 },
            { 84, 34 },
            { 84, 38 },
            { 84, 6 },
            { 107, 27 },
    };

    static double[][] buildDists(double[][] coords) {
        int n = coords.length;
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = euclidean(coords[i][0], coords[i][1], coords[j][0], coords[j][1]);
            }
        }
        return d;
    }

    static double euclidean(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static final double DEGREE_OF_DESTRUCTION = 0.1;

    private static int edgesToRemove(TspState state) {
        int n = (int) (state.edges.size() * DEGREE_OF_DESTRUCTION);
        return n == 0 ? 1 : n;
    }

    public static State randomRemoval(State state, RandomGenerator rng) {
        TspState destroyed = ((TspState) state).clone();
        int toRemove = edgesToRemove(destroyed);
        int removed = 0;
        while (removed < toRemove) {
            int node = destroyed.nodes[rng.nextInt(destroyed.nodes.length)];
            if (destroyed.edges.containsKey(node)) {
                destroyed.edges.remove(node);
                removed++;
            }
        }
        return destroyed;
    }

    public static State pathRemoval(State state, RandomGenerator rng) {
        TspState destroyed = ((TspState) state).clone();
        int node = destroyed.nodes[rng.nextInt(destroyed.nodes.length)];
        int toRemove = edgesToRemove(destroyed);
        for (int i = 0; i < toRemove; i++) {
            int next = destroyed.edges.get(node);
            destroyed.edges.remove(node);
            node = next;
        }
        return destroyed;
    }

    public static State worstRemoval(State state, RandomGenerator rng) {
        var destroyed = ((TspState) state).clone();
        int[] worstEdges = destroyed.nodes.clone();
        // sort ascending by edge distance so we can remove from the end
        Integer[] boxed = Arrays.stream(worstEdges).boxed().toArray(Integer[]::new);
        Arrays.sort(boxed, (a, b) -> Double.compare(
                destroyed.dists[a][destroyed.edges.get(a)],
                destroyed.dists[b][destroyed.edges.get(b)]));
        int toRemove = edgesToRemove(destroyed);
        for (int i = 0; i < toRemove; i++) {
            destroyed.edges.remove(boxed[boxed.length - 1 - i]);
        }
        return destroyed;
    }

    static State greedyRepair(State state, RandomGenerator rng) {
        TspState cur = (TspState) state;

        boolean[] visited = new boolean[cur.nodes.length];
        for (int v : cur.edges.values())
            visited[v] = true;

        int[] idx = IntStream.range(0, cur.nodes.length).toArray();
        shuffleArray(idx, rng);
        int[] shuffled = new int[idx.length];
        for (int i = 0; i < idx.length; i++)
            shuffled[i] = cur.nodes[idx[i]];

        while (cur.edges.size() != cur.nodes.length) {
            int node = -1;
            for (int other : shuffled) {
                if (!cur.edges.containsKey(other)) {
                    node = other;
                    break;
                }
            }
            if (node == -1)
                throw new RuntimeException("node not found");

            final int finalNode = node;
            List<Integer> unvisited = new ArrayList<>();
            for (int other : cur.nodes) {
                if (other != finalNode && !visited[other] && !wouldFormSubcycle(finalNode, other, cur))
                    unvisited.add(other);
            }
            if (unvisited.isEmpty())
                throw new RuntimeException("unvisited is empty");

            int nearest = unvisited.stream()
                    .min(Comparator.comparingDouble(a -> cur.dists[finalNode][a]))
                    .orElseThrow();

            cur.edges.put(node, nearest);
            visited[nearest] = true;
        }
        return cur;
    }

    static void shuffleArray(int[] arr, RandomGenerator rnd) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }

    private static boolean wouldFormSubcycle(int fromNode, int toNode, TspState state) {
        for (int step = 1; step < state.nodes.length; step++) {
            Integer next = state.edges.get(toNode);
            if (next == null)
                return false;
            toNode = next;
            if (fromNode == toNode && step != state.nodes.length - 1)
                return true;
        }
        return false;
    }

    static void writeDotFile(String filename, double[][] coords, Map<Integer, Integer> edges) throws IOException {
        final double k = 3;
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (double[] c : coords) {
            minX = Math.min(minX, c[0] * k);
            maxX = Math.max(maxX, c[0] * k);
            minY = Math.min(minY, c[1] * k);
            maxY = Math.max(maxY, c[1] * k);
        }
        double width = maxX - minX, height = maxY - minY;

        try (PrintWriter w = new PrintWriter(new FileWriter(filename))) {
            w.println("digraph G {");
            w.printf("  graph [size=\"%f,%f!\", dpi=20.0];%n", width, height);
            for (int i = 0; i < coords.length; i++) {
                double x = coords[i][0] * k - minX, y = coords[i][1] * k - minY;
                w.printf(
                        "  %d [label=\"%d\", fontsize=48, pos=\"%f,%f!\", shape=circle, width=2, height=2, fixedsize=true];%n",
                        i, i, x, y);
            }
            for (Map.Entry<Integer, Integer> e : edges.entrySet())
                w.printf("  %d -> %d [arrowsize=4, penwidth=3];%n", e.getKey(), e.getValue());
            w.println("}");
        }
    }
}
