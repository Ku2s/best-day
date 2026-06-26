package com.example.bestday.domain.model;

import java.time.LocalDate;

/**
 * Comfort score computed for a single day and a given activity.
 *
 * @param date               the scored day
 * @param score              overall comfort score, 0 (worst) to 100 (best)
 * @param reason             human-readable explanation of the score
 * @param temperatureScore   temperature sub-score, 0-100
 * @param precipitationScore precipitation sub-score, 0-100
 * @param windScore          wind sub-score, 0-100
 */
public record DayScore(LocalDate date,
                       int score,
                       String reason,
                       int temperatureScore,
                       int precipitationScore,
                       int windScore) {
}
