package com.example.bestday.domain.model;

/**
 * Geographic coordinates of a resolved city, as returned by the geocoding step.
 *
 * <p>Pure domain value object: no framework dependency.
 *
 * @param latitude     latitude in decimal degrees
 * @param longitude    longitude in decimal degrees
 * @param resolvedName name of the city as resolved by the geocoder (may differ from the input)
 * @param country      country of the resolved city
 */
public record Coordinates(double latitude, double longitude, String resolvedName, String country) {
}
