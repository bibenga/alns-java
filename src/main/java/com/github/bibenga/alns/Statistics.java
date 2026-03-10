package com.github.bibenga.alns;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Statistics {

    private final List<Double> objectives = new ArrayList<>();
    private final List<Long> runtimes = new ArrayList<>();
    private final Map<Integer, EnumMap<Outcome, Integer>> destroyOperatorCounts = new LinkedHashMap<>();
    private final Map<Integer, EnumMap<Outcome, Integer>> repairOperatorCounts = new LinkedHashMap<>();

    public int getIterationCount() {
        return objectives.size() - 1;
    }

    public Collection<Double> getObjectives() {
        return objectives;
    }

    public Collection<Long> getRuntimes() {
        return runtimes;
    }

    public Duration getTotalRuntime() {
        return Duration.ofNanos(runtimes.getLast() - runtimes.getFirst());
    }

    public Map<Integer, EnumMap<Outcome, Integer>> getDestroyOperatorCounts() {
        return destroyOperatorCounts;
    }

    public Map<Integer, EnumMap<Outcome, Integer>> getRepairOperatorCounts() {
        return repairOperatorCounts;
    }

    public void collectObjective(double objective) {
        objectives.add(objective);
    }

    public void collectRuntime(long time) {
        runtimes.add(time);
    }

    public void collectDestroyOperator(int operatorId, Outcome outcome) {
        destroyOperatorCounts
                .computeIfAbsent(operatorId, k -> newCounter())
                .merge(outcome, 1, Integer::sum);
    }

    public void collectRepairOperator(int operatorId, Outcome outcome) {
        repairOperatorCounts
                .computeIfAbsent(operatorId, k -> newCounter())
                .merge(outcome, 1, Integer::sum);
    }

    private EnumMap<Outcome, Integer> newCounter() {
        var c = new EnumMap<Outcome, Integer>(Outcome.class);
        for (var o : Outcome.values()) {
            c.put(o, 0);
        }
        return c;
    }
}