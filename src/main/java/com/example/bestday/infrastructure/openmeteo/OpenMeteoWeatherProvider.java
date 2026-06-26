package com.example.bestday.infrastructure.openmeteo;

import com.example.bestday.application.port.out.WeatherForecastProvider;
import com.example.bestday.domain.exception.CityNotFoundException;
import com.example.bestday.domain.exception.ForecastUnavailableException;
import com.example.bestday.domain.model.Coordinates;
import com.example.bestday.domain.model.DailyWeather;
import com.example.bestday.infrastructure.openmeteo.dto.ForecastResponse;
import com.example.bestday.infrastructure.openmeteo.dto.GeocodingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Open-Meteo adapter: concrete implementation of the {@link WeatherForecastProvider}
 * output port using the public Open-Meteo REST API (no API key required).
 *
 * <p>Two remote calls are exposed: geocoding (city name -> coordinates) and the
 * 7-day daily forecast. The use case orchestrates them; this class only knows how to talk HTTP
 * and how to translate external JSON DTOs into pure domain objects.
 */
@Component
public class OpenMeteoWeatherProvider implements WeatherForecastProvider {

    private static final int FORECAST_DAYS = 7;

    private final RestClient restClient;
    private final String geocodingUrl;
    private final String forecastUrl;

    public OpenMeteoWeatherProvider(RestClient.Builder restClientBuilder,
                                    @Value("${openmeteo.geocoding-url}") String geocodingUrl,
                                    @Value("${openmeteo.forecast-url}") String forecastUrl) {
        this.restClient = restClientBuilder.build();
        this.geocodingUrl = geocodingUrl;
        this.forecastUrl = forecastUrl;
    }

    @Override
    public Coordinates geocode(String city) {
        GeocodingResponse response;
        try {
            response = restClient.get()
                    .uri(geocodingUrl, uri -> uri
                            .queryParam("name", city)
                            .queryParam("count", 1)
                            .queryParam("language", "fr")
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(GeocodingResponse.class);
        } catch (RestClientException e) {
            throw new ForecastUnavailableException("Geocoding request to Open-Meteo failed", e);
        }

        if (response == null || response.results() == null || response.results().isEmpty()) {
            throw new CityNotFoundException(city);
        }

        GeocodingResponse.Result match = response.results().get(0);
        return new Coordinates(match.latitude(), match.longitude(), match.name(), match.country());
    }

    @Override
    public List<DailyWeather> next7DaysForecast(Coordinates coordinates) {
        ForecastResponse response;
        try {
            response = restClient.get()
                    .uri(forecastUrl, uri -> uri
                            .queryParam("latitude", coordinates.latitude())
                            .queryParam("longitude", coordinates.longitude())
                            .queryParam("daily", "temperature_2m_max,precipitation_sum,wind_speed_10m_max")
                            .queryParam("timezone", "auto")
                            .queryParam("forecast_days", FORECAST_DAYS)
                            .build())
                    .retrieve()
                    .body(ForecastResponse.class);
        } catch (RestClientException e) {
            throw new ForecastUnavailableException("Forecast request to Open-Meteo failed", e);
        }

        return toDailyWeather(response);
    }

    private List<DailyWeather> toDailyWeather(ForecastResponse response) {
        if (response == null || response.daily() == null || response.daily().time() == null) {
            throw new ForecastUnavailableException("Open-Meteo returned an empty forecast");
        }

        ForecastResponse.Daily daily = response.daily();
        List<String> dates = daily.time();
        List<Double> temps = daily.temperatureMax();
        List<Double> precip = daily.precipitationSum();
        List<Double> wind = daily.windSpeedMax();

        if (temps == null || precip == null || wind == null
                || temps.size() != dates.size()
                || precip.size() != dates.size()
                || wind.size() != dates.size()) {
            throw new ForecastUnavailableException("Open-Meteo returned inconsistent forecast arrays");
        }

        List<DailyWeather> result = new ArrayList<>(dates.size());
        for (int i = 0; i < dates.size(); i++) {
            result.add(new DailyWeather(
                    LocalDate.parse(dates.get(i)),
                    temps.get(i),
                    precip.get(i),
                    wind.get(i)));
        }
        return result;
    }
}
