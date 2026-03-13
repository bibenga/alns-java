package com.github.bibenga.alns;

import java.time.Duration;

/**
 * Represents a measured objective value at a specific point in time.
 *
 * @param time      the timestamp in nanoseconds
 * @param objective the measured objective value at the given time
 */
public record ObjectiveData(long time, double objective) {
    /**
     * Converts the nanosecond timestamp to a {@link Duration}.
     *
     * @return the time as a {@link Duration}
     */
    public Duration timeAsDuration() {
        return Duration.ofNanos(time);
    }
}
