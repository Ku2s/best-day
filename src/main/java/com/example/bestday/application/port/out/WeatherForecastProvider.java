package com.example.bestday.application.port.out;

import com.example.bestday.domain.model.Coordinates;
import com.example.bestday.domain.model.DailyWeather;

import java.util.List;

/**
 * Output port (driven side) of the application.
 *
 * <p>Abstracts the remote weather source so that the use case depends only on this interface,
 * never on a concrete HTTP client. The Open-Meteo adapter in the infrastructure layer implements
 * it (dependency inversion: infrastructure depends on application, not the other way around).
 */
public interface WeatherForecastProvider {

    /**
     * Resolves a free-text city name into geographic coordinates.
     *
     * @param city the city name to geocode
     * @return the coordinates of the best match
     * @throws com.example.bestday.domain.exception.CityNotFoundException if no match is found
     */
    Coordinates geocode(String city);

    /**
     * Returns the daily weather forecast for the next 7 days at the given location.
     *
     * @param coordinates the location to forecast
     * @return one {@link DailyWeather} per day, ordered chronologically
     */
    List<DailyWeather> next7DaysForecast(Coordinates coordinates);
}
