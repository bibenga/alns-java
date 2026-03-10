package com.github.bibenga.alns.select;

import java.util.EnumMap;
import java.util.Map;

import com.github.bibenga.alns.Outcome;

public class Scores {
    public static Map<Outcome, Double> of(double best, double better, double accept, double reject) {
        var m = new EnumMap<Outcome, Double>(Outcome.class);
        m.put(Outcome.BEST, best);
        m.put(Outcome.BETTER, better);
        m.put(Outcome.ACCEPT, accept);
        m.put(Outcome.REJECT, reject);
        return m;
    }
}
