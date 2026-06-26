package com.example.bestday.domain.model;

import java.util.List;

/**
 * Outcome of a best-day recommendation.
 *
 * @param requestedCity    the city as typed by the caller
 * @param resolvedLocation the location resolved by the geocoder (e.g. "Paris, France")
 * @param activity         the activity the scores were computed for
 * @param bestDay          the highest-scoring day
 * @param ranking          all evaluated days, ordered from best to worst score
 */
public record BestDayResult(String requestedCity,
                            String resolvedLocation,
                            Activity activity,
                            DayScore bestDay,
                            List<DayScore> ranking) {
}
