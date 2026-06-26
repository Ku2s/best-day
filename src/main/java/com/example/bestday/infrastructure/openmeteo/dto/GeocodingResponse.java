package com.example.bestday.infrastructure.openmeteo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Maps the JSON returned by the Open-Meteo geocoding endpoint
 * ({@code /v1/search}). Lives in the infrastructure layer: this shape is an external
 * detail and never leaks into the domain.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeocodingResponse(List<Result> results) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(String name, double latitude, double longitude, String country) {
    }
}
