package com.example.bestday.domain.exception;

/**
 * Raised when the geocoding step returns no match for the requested city.
 */
public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(String city) {
        super("No location found for city: " + city);
    }
}
