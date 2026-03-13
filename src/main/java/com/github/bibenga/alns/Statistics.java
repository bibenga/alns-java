package com.github.bibenga.alns;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Statistics {

    private final List<OperatorInfo> dOps;
    private final List<OperatorInfo> rOps;
    private Duration totalRuntime;
    private int iterationCount;
    private final List<ObjectiveData> objectives = new ArrayList<>();
    private final List<EnumMap<Outcome, Integer>> dOpsCounts;
    private final List<EnumMap<Outcome, Integer>> rOpsCounts;

    Statistics(List<OperatorInfo> dOps, List<OperatorInfo> rOps) {
        this.dOps = dOps;
        this.rOps = rOps;
        dOpsCounts = new ArrayList<>(dOps.size());
        for (int i = 0; i < dOps.size(); i++) {
            dOpsCounts.add(newCounter());
        }
        rOpsCounts = new ArrayList<>(dOps.size());
        for (int i = 0; i < rOps.size(); i++) {
            rOpsCounts.add(newCounter());
        }
    }

    public int getIterationCount() {
        return iterationCount;
    }

    void incIterationCount() {
        iterationCount++;
    }

    void setTotalRuntime(long totalRuntime) {
        this.totalRuntime = Duration.ofNanos(totalRuntime);
    }

    public Duration getTotalRuntime() {
        return totalRuntime;
    }

    public List<ObjectiveData> getObjectives() {
        if (objectives == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(objectives);
    }

    public Map<String, Map<Outcome, Integer>> getDestroyOperatorCounts() {
        return makeCountsMap(dOps, dOpsCounts);
    }

    public Map<String, Map<Outcome, Integer>> getRepairOperatorCounts() {
        return makeCountsMap(rOps, rOpsCounts);
    }

    private static Map<String, Map<Outcome, Integer>> makeCountsMap(List<OperatorInfo> ops,
            List<EnumMap<Outcome, Integer>> opsCounts) {
        Map<String, Map<Outcome, Integer>> res = new LinkedHashMap<>();
        for (int i = 0; i < ops.size(); i++) {
            var name = ops.get(i).name();
            var counts = opsCounts.get(i);
            res.put(name, Collections.unmodifiableMap(counts));
        }
        return Collections.unmodifiableMap(res);
    }

    void collectObjective(long time, double objective) {
        objectives.add(new ObjectiveData(time, objective));
    }

    void collectDestroyOperator(int oIdx, Outcome outcome) {
        dOpsCounts.get(oIdx).merge(outcome, 1, Integer::sum);
    }

    void collectRepairOperator(int oIdx, Outcome outcome) {
        rOpsCounts.get(oIdx).merge(outcome, 1, Integer::sum);
    }

    private static EnumMap<Outcome, Integer> newCounter() {
        var c = new EnumMap<Outcome, Integer>(Outcome.class);
        for (var o : Outcome.values()) {
            c.put(o, 0);
        }
        return c;
    }
}