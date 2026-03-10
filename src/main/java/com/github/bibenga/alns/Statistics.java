package com.github.bibenga.alns;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
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

    public List<Double> getObjectives() {
        return Collections.unmodifiableList(objectives);
    }

    public List<Long> getRuntimes() {
        return Collections.unmodifiableList(runtimes);
    }

    public Duration getTotalRuntime() {
        return Duration.ofNanos(runtimes.getLast() - runtimes.getFirst());
    }

    public Map<Integer, EnumMap<Outcome, Integer>> getDestroyOperatorCounts() {
        // TODO: value is still modifible
        return Collections.unmodifiableMap(destroyOperatorCounts);
    }

    public Map<Integer, EnumMap<Outcome, Integer>> getRepairOperatorCounts() {
        // TODO: value is still modifible
        return Collections.unmodifiableMap(repairOperatorCounts);
    }

    void collectObjective(double objective) {
        objectives.add(objective);
    }

    void collectRuntime(long time) {
        runtimes.add(time);
    }

    void collectDestroyOperator(int oIdx, Outcome outcome) {
        destroyOperatorCounts
                .computeIfAbsent(oIdx, k -> newCounter())
                .merge(outcome, 1, Integer::sum);
    }

    void collectRepairOperator(int oIdx, Outcome outcome) {
        repairOperatorCounts
                .computeIfAbsent(oIdx, k -> newCounter())
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