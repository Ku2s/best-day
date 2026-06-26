package com.example.bestday.web.dto;

import com.example.bestday.domain.model.BestDayResult;

import java.util.List;
import java.util.Locale;

/**
 * REST representation of a best-day recommendation. Web-layer DTO built from the domain
 * {@link BestDayResult}.
 */
public record BestDayResponse(String requestedCity,
                              String resolvedLocation,
                              String activity,
                              DayScoreResponse bestDay,
                              List<DayScoreResponse> ranking) {

    public static BestDayResponse from(BestDayResult result) {
        List<DayScoreResponse> ranking = result.ranking().stream()
                .map(DayScoreResponse::from)
                .toList();
        return new BestDayResponse(
                result.requestedCity(),
                result.resolvedLocation(),
                result.activity().name().toLowerCase(Locale.ROOT),
                DayScoreResponse.from(result.bestDay()),
                ranking);
    }
}
