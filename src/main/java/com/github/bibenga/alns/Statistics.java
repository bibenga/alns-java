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
    private final List<Double> objectives = new ArrayList<>();
    private final List<Long> runtimes = new ArrayList<>();
    private final ArrayList<EnumMap<Outcome, Integer>> dOpsCounts = new ArrayList<>();
    private final ArrayList<EnumMap<Outcome, Integer>> rOpsCounts = new ArrayList<>();

    Statistics(List<OperatorInfo> dOps, List<OperatorInfo> rOps) {
        this.dOps = dOps;
        this.rOps = rOps;
        dOpsCounts.ensureCapacity(dOps.size());
        for (int i = 0; i < dOps.size(); i++) {
            dOpsCounts.add(newCounter());
        }
        rOpsCounts.ensureCapacity(rOps.size());
        for (int i = 0; i < rOps.size(); i++) {
            rOpsCounts.add(newCounter());
        }
    }

    public int getIterationCount() {
        if (objectives == null) {
            return 0;
        }
        return objectives.size() - 1;
    }

    public List<Double> getObjectives() {
        if (objectives == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(objectives);
    }

    public List<Duration> getRuntimes() {
        if (objectives == null) {
            return Collections.emptyList();
        }
        var runtimes = new ArrayList<Duration>(this.runtimes.size());
        for (var d : this.runtimes) {
            runtimes.add(Duration.ofNanos(d));
        }
        return Collections.unmodifiableList(runtimes);
    }

    void setTotalRuntime(long totalRuntime) {
        this.totalRuntime = Duration.ofNanos(totalRuntime);
    }

    public Duration getTotalRuntime() {
        return totalRuntime;
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

    void collectObjective(double objective) {
        objectives.add(objective);
    }

    void collectRuntime(long time) {
        runtimes.add(time);
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