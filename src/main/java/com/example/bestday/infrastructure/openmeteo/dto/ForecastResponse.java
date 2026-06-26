package com.example.bestday.infrastructure.openmeteo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Maps the JSON returned by the Open-Meteo forecast endpoint
 * ({@code /v1/forecast}) for the requested daily variables. The daily fields are
 * parallel arrays: index {@code i} of each list refers to {@code time.get(i)}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ForecastResponse(Daily daily) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Daily(
            List<String> time,
            @JsonProperty("temperature_2m_max") List<Double> temperatureMax,
            @JsonProperty("precipitation_sum") List<Double> precipitationSum,
            @JsonProperty("wind_speed_10m_max") List<Double> windSpeedMax) {
    }
}
