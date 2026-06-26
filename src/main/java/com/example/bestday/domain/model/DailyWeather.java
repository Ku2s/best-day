package com.example.bestday.domain.model;

import java.time.LocalDate;

/**
 * Daily weather summary for a single day, as needed by the comfort scoring.
 *
 * <p>Pure domain value object: no framework dependency.
 *
 * @param date            the calendar day
 * @param maxTemperatureC maximum temperature of the day, in degrees Celsius
 * @param precipitationMm total precipitation of the day, in millimetres
 * @param maxWindKmh      maximum wind speed of the day, in kilometres per hour
 */
public record DailyWeather(LocalDate date,
                           double maxTemperatureC,
                           double precipitationMm,
                           double maxWindKmh) {
}
