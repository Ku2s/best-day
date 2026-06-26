package com.example.bestday.web.dto;

import com.example.bestday.domain.model.DayScore;

/**
 * REST representation of a single day's score. Web-layer DTO: decouples the JSON contract
 * from the domain model.
 */
public record DayScoreResponse(String date,
                               int score,
                               String reason,
                               int temperatureScore,
                               int precipitationScore,
                               int windScore) {

    public static DayScoreResponse from(DayScore dayScore) {
        return new DayScoreResponse(
                dayScore.date().toString(),
                dayScore.score(),
                dayScore.reason(),
                dayScore.temperatureScore(),
                dayScore.precipitationScore(),
                dayScore.windScore());
    }
}
