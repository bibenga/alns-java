package com.github.bibenga.alns;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import com.github.bibenga.alns.accept.AcceptanceCriterion;
import com.github.bibenga.alns.select.OperatorSelectionScheme;
import com.github.bibenga.alns.stop.StoppingCriterion;

public class ALNS {
    private final RandomGenerator rng;
    private final List<Operator> dOps = new ArrayList<>();
    private final List<Operator> rOps = new ArrayList<>();
    private Callback onOutcome;
    private boolean collectObjectives;

    public ALNS(RandomGenerator rng) {
        this.rng = rng;
    }

    public ALNS() {
        this(RandomGenerator.getDefault());
    }

    public List<Operator> getDestroyOperators() {
        return dOps;
    }

    public List<Operator> getRepairOperators() {
        return rOps;
    }

    public void addDestroyOperator(String name, Operator operator) {
        // logger.fine("Adding destroy operator %s.".formatted(name));
        dOps.add(operator);
    }

    public void addRepairOperator(String name, Operator operator) {
        // logger.fine("Adding repair operator %s.".formatted(name));
        rOps.add(operator);
    }

    public void setCollectObjectives(boolean collectObjectives) {
        this.collectObjectives = collectObjectives;
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

        // logger.fine("Initial solution has objective %.2f.".formatted(initObj));

        var stats = new Statistics();
        if (collectObjectives) {
            stats.collectObjective(initObj);
            stats.collectRuntime(System.nanoTime());
        }

        while (!stop.isDone(rng, best, curr)) {
            var op = select.select(rng, best, curr);
            var dIdx = op.dIdx();
            var rIdx = op.rIdx();

            var dOp = dOps.get(dIdx);
            var rOp = rOps.get(rIdx);
            // String dName = dEntry.getKey();
            // String rName = rEntry.getKey();

            // logger.fine("Selected operators %s and %s.".formatted(dName, rName));

            State destroyed = dOp.apply(curr, rng);
            State cand = rOp.apply(destroyed, rng);

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

            stats.collectDestroyOperator(dIdx, outcome);
            stats.collectRepairOperator(rIdx, outcome);
            if (collectObjectives) {
                stats.collectObjective(curr.objective());
                stats.collectRuntime(System.nanoTime());
            }
        }

        // logger.info("Finished iterating in %.2fs.".formatted(stats.getTotalRuntime()));

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
            // logger.info("New best with objective %.2f.".formatted(cand.objective()));
            outcome = Outcome.BEST;
        }

        return outcome;
    }
}