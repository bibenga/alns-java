package com.github.bibenga.alns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.random.RandomGenerator;

import com.github.bibenga.alns.accept.AcceptanceCriterion;
import com.github.bibenga.alns.select.OperatorSelectionScheme;
import com.github.bibenga.alns.stop.StoppingCriterion;

public class ALNS {
    private Logger log;
    private final RandomGenerator rng;
    private final List<OperatorInfo> dOps = new ArrayList<>();
    private final List<OperatorInfo> rOps = new ArrayList<>();
    private Callback onOutcome;
    private boolean collectObjectives = true;

    public ALNS(RandomGenerator rng) {
        this.rng = rng;
    }

    public ALNS() {
        this(RandomGenerator.getDefault());
    }

    public void setLog(Logger logger) {
        this.log = logger;
    }

    public Map<String, Operator> getDestroyOperators() {
        return makeOperatorMap(dOps);
    }

    public Map<String, Operator> getRepairOperators() {
        return makeOperatorMap(rOps);
    }

    private static Map<String, Operator> makeOperatorMap(List<OperatorInfo> ops) {
        Map<String, Operator> res = new LinkedHashMap<>();
        for (var op : ops) {
            res.put(op.name(), op.operator());
        }
        return Collections.unmodifiableMap(res);
    }

    public void addDestroyOperator(String name, Operator operator) {
        // logger.fine("Adding destroy operator %s.".formatted(name));
        validateOpName(name, dOps);
        dOps.add(new OperatorInfo(name, operator));
    }

    public void addRepairOperator(String name, Operator operator) {
        // logger.fine("Adding repair operator %s.".formatted(name));
        validateOpName(name, rOps);
        rOps.add(new OperatorInfo(name, operator));
    }

    private static void validateOpName(String name, List<OperatorInfo> ops) {
        for (var oi : ops) {
            if (oi.name().equals(name)) {
                throw new IllegalArgumentException("Name %s has already been registered".formatted(name));
            }
        }
    }

    public void setCollectObjectives(boolean collectObjectives) {
        this.collectObjectives = collectObjectives;
    }

    public boolean isCollectObjectives() {
        return collectObjectives;
    }

    public Result iterate(
            State initSol,
            OperatorSelectionScheme select,
            AcceptanceCriterion accept,
            StoppingCriterion stop) {
        if (dOps.isEmpty() || rOps.isEmpty()) {
            throw new IllegalArgumentException("Missing destroy or repair operators");
        }

        State curr = initSol;
        State best = initSol;
        double initObj = initSol.objective();

        if (log != null) {
            log.fine("Initial solution objective: %.2f".formatted(initObj));
        }

        var stats = new Statistics(dOps, rOps);
        if (collectObjectives) {
            stats.collectObjective(0, initObj);
        }

        long started = System.nanoTime();
        while (!stop.isDone(rng, best, curr)) {
            var op = select.select(rng, best, curr);
            var dIdx = op.dIdx();
            var rIdx = op.rIdx();

            var dOp = dOps.get(dIdx);
            var rOp = rOps.get(rIdx);

            if (log != null) {
                log.fine("Selected operators: %s and %s".formatted(dOp.name(), rOp.name()));
            }

            State destroyed = dOp.operator().apply(curr, rng);
            State cand = rOp.operator().apply(destroyed, rng);

            var evalResult = evalCand(accept, best, curr, cand);
            best = evalResult.best();
            curr = evalResult.curr();
            Outcome outcome = evalResult.outcome();

            // // --- inlined evalCand ---
            // Outcome outcome = determineOutcome(accept, best, curr, cand);
            // if (onOutcome != null) {
            //     onOutcome.call(outcome, cand, rng);
            // }
            // switch (outcome) {
            //     case BEST -> {
            //         best = cand;
            //         curr = cand;
            //     }
            //     case REJECT -> {
            //     }
            //     default -> {
            //         curr = cand;
            //     }
            // }
            // // --- end inlined evalCand ---

            select.update(cand, op, outcome);

            stats.collect(op, outcome);
            if (collectObjectives) {
                stats.collectObjective(System.nanoTime() - started, curr.objective());
            }
        }
        var totalRuntime = System.nanoTime() - started;
        stats.setTotalRuntime(totalRuntime);

        if (log != null) {
            log.info("Finished iterating in %.2fs, best objective: %.2f".formatted(
                    totalRuntime / 1_000_000_000.0, best.objective()));
        }

        return new Result(best, stats);
    }

    public void setOnOutcome(Callback cb) {
        onOutcome = cb;
    }

    private record EvalResult(State best, State curr, Outcome outcome) {
    }

    private EvalResult evalCand(AcceptanceCriterion accept, State best, State curr, State cand) {
        Outcome outcome = determineOutcome(accept, best, curr, cand);
        if (onOutcome != null) {
            onOutcome.call(outcome, cand, rng);
        }
        return switch (outcome) {
            case BEST -> new EvalResult(cand, cand, outcome);
            case REJECT -> new EvalResult(best, curr, outcome);
            default -> new EvalResult(best, cand, outcome);
        };
    }

    private Outcome determineOutcome(AcceptanceCriterion accept, State best, State curr, State cand) {
        Outcome outcome = Outcome.REJECT;

        if (accept.isAccept(rng, best, curr, cand)) {
            outcome = Outcome.ACCEPT;
            if (cand.objective() < curr.objective()) {
                outcome = Outcome.BETTER;
            }
        }

        if (cand.objective() < best.objective()) {
            if (log != null) {
                log.info("New best solution: %.2f".formatted(cand.objective()));
            }
            outcome = Outcome.BEST;
        }

        return outcome;
    }
}