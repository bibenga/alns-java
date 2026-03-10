package com.github.bibenga.alns;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Statistics {

    private final List<Double> objectives = new ArrayList<>();
    private final List<Double> runtimes = new ArrayList<>();
    private final Map<Integer, int[]> destroyOperatorCounts = new LinkedHashMap<>();
    private final Map<Integer, int[]> repairOperatorCounts = new LinkedHashMap<>();

    public double[] getObjectives() {
        return objectives.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public double getStartTime() {
        return runtimes.getFirst();
    }

    public double getTotalRuntime() {
        return runtimes.getLast() - runtimes.getFirst();
    }

    public double[] getRuntimes() {
        double[] diffs = new double[runtimes.size() - 1];
        for (int i = 0; i < diffs.length; i++) {
            diffs[i] = runtimes.get(i + 1) - runtimes.get(i);
        }
        return diffs;
    }

    public Map<Integer, int[]> getDestroyOperatorCounts() {
        return destroyOperatorCounts;
    }

    public Map<Integer, int[]> getRepairOperatorCounts() {
        return repairOperatorCounts;
    }

    public void collectObjective(double objective) {
        objectives.add(objective);
    }

    public void collectRuntime(double time) {
        runtimes.add(time);
    }

    public void collectDestroyOperator(Integer operatorName, Outcome outcome) {
        destroyOperatorCounts.computeIfAbsent(operatorName, k -> new int[4])[outcome.getValue()]++;
    }

    public void collectRepairOperator(Integer operatorName, Outcome outcome) {
        repairOperatorCounts.computeIfAbsent(operatorName, k -> new int[4])[outcome.getValue()]++;
    }
}