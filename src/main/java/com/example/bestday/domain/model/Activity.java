package com.example.bestday.domain.model;

import java.util.Locale;

/**
 * Outdoor activity the recommendation is computed for.
 *
 * <p>Each activity carries its own comfort profile: an ideal temperature and the relative
 * importance (weights) of the three sub-scores — temperature, precipitation and wind. The three
 * weights always sum to 1.0 so the weighted average stays bounded to 0-100.
 *
 * <p>Example: cycling weights wind heavily, beach weights temperature heavily.
 */
public enum Activity {

    RUNNING(12.0, 0.40, 0.40, 0.20),
    CYCLING(16.0, 0.30, 0.30, 0.40),
    PICNIC(23.0, 0.40, 0.45, 0.15),
    BEACH(27.0, 0.50, 0.35, 0.15);

    private final double idealTemperatureC;
    private final double temperatureWeight;
    private final double precipitationWeight;
    private final double windWeight;

    Activity(double idealTemperatureC,
             double temperatureWeight,
             double precipitationWeight,
             double windWeight) {
        this.idealTemperatureC = idealTemperatureC;
        this.temperatureWeight = temperatureWeight;
        this.precipitationWeight = precipitationWeight;
        this.windWeight = windWeight;
    }

    /**
     * Parses a case-insensitive activity name, falling back to {@link #RUNNING} when blank.
     *
     * @param raw the raw activity parameter (may be {@code null} or blank)
     * @return the matching activity
     * @throws IllegalArgumentException if the value is non-blank but unknown
     */
    public static Activity fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            return RUNNING;
        }
        try {
            return Activity.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown activity: " + raw
                    + " (expected one of: running, cycling, picnic, beach)");
        }
    }

    public double idealTemperatureC() {
        return idealTemperatureC;
    }

    public double temperatureWeight() {
        return temperatureWeight;
    }

    public double precipitationWeight() {
        return precipitationWeight;
    }

    public double windWeight() {
        return windWeight;
    }
}
