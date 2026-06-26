package com.example.bestday.domain.service;

import com.example.bestday.domain.model.Activity;
import com.example.bestday.domain.model.DailyWeather;
import com.example.bestday.domain.model.DayScore;

/**
 * Core business logic: turns a day's raw weather into a 0-100 comfort score for a given activity.
 *
 * <p>The overall score is a weighted average of three sub-scores, each bounded to 0-100:
 * <ul>
 *   <li><b>temperature</b> — penalises the absolute gap to the activity's ideal temperature;</li>
 *   <li><b>precipitation</b> — penalises rain linearly up to a saturation threshold;</li>
 *   <li><b>wind</b> — penalises wind linearly up to a saturation threshold.</li>
 * </ul>
 * Weights come from the {@link Activity} profile and always sum to 1, so the result stays in 0-100.
 *
 * <p>This class is pure (no I/O, no framework): it is trivially unit-testable and deterministic.
 */
public class ComfortScoreCalculator {

    /** Temperature gap (°C) at which the temperature sub-score reaches 0. */
    private static final double MAX_TEMPERATURE_GAP_C = 15.0;

    /** Daily precipitation (mm) at which the precipitation sub-score reaches 0. */
    private static final double MAX_PRECIPITATION_MM = 20.0;

    /** Max wind speed (km/h) at which the wind sub-score reaches 0. */
    private static final double MAX_WIND_KMH = 40.0;

    /**
     * Computes the comfort score of a single day for the given activity.
     *
     * @param day      the day's weather
     * @param activity the activity profile (ideal temperature + weights)
     * @return the {@link DayScore} including the overall score, sub-scores and a readable reason
     */
    public DayScore score(DailyWeather day, Activity activity) {
        int temperatureScore = temperatureSubScore(day.maxTemperatureC(), activity.idealTemperatureC());
        int precipitationScore = precipitationSubScore(day.precipitationMm());
        int windScore = windSubScore(day.maxWindKmh());

        double weighted = activity.temperatureWeight() * temperatureScore
                + activity.precipitationWeight() * precipitationScore
                + activity.windWeight() * windScore;
        int overall = (int) Math.round(clamp(weighted));

        String reason = buildReason(day, temperatureScore, precipitationScore, windScore);
        return new DayScore(day.date(), overall, reason, temperatureScore, precipitationScore, windScore);
    }

    private int temperatureSubScore(double temperatureC, double idealTemperatureC) {
        double gap = Math.abs(temperatureC - idealTemperatureC);
        double score = 100.0 * (1.0 - gap / MAX_TEMPERATURE_GAP_C);
        return (int) Math.round(clamp(score));
    }

    private int precipitationSubScore(double precipitationMm) {
        double score = 100.0 * (1.0 - precipitationMm / MAX_PRECIPITATION_MM);
        return (int) Math.round(clamp(score));
    }

    private int windSubScore(double windKmh) {
        double score = 100.0 * (1.0 - windKmh / MAX_WIND_KMH);
        return (int) Math.round(clamp(score));
    }

    private String buildReason(DailyWeather day, int temperatureScore, int precipitationScore, int windScore) {
        String temperaturePart = "%.0f°C (%s)".formatted(day.maxTemperatureC(), qualify(temperatureScore));
        String precipitationPart = day.precipitationMm() <= 0.1
                ? "pas de pluie"
                : "%.1f mm de pluie (%s)".formatted(day.precipitationMm(), qualify(precipitationScore));
        String windPart = "vent %.0f km/h (%s)".formatted(day.maxWindKmh(), qualify(windScore));
        return "%s, %s, %s".formatted(temperaturePart, precipitationPart, windPart);
    }

    private String qualify(int subScore) {
        if (subScore >= 80) {
            return "idéal";
        } else if (subScore >= 60) {
            return "bon";
        } else if (subScore >= 40) {
            return "moyen";
        } else if (subScore >= 20) {
            return "médiocre";
        }
        return "mauvais";
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(100.0, value));
    }
}
