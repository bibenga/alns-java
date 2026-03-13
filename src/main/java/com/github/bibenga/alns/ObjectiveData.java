package com.github.bibenga.alns;

import java.time.Duration;

public record ObjectiveData(long time, double objective) {
    public Duration timeAsDuration() {
        return Duration.ofNanos(time);
    }
}
