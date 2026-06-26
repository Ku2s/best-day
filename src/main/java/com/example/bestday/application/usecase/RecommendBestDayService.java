package com.example.bestday.application.usecase;

import com.example.bestday.application.port.in.RecommendBestDayUseCase;
import com.example.bestday.application.port.out.WeatherForecastProvider;
import com.example.bestday.domain.exception.ForecastUnavailableException;
import com.example.bestday.domain.model.Activity;
import com.example.bestday.domain.model.BestDayResult;
import com.example.bestday.domain.model.Coordinates;
import com.example.bestday.domain.model.DailyWeather;
import com.example.bestday.domain.model.DayScore;
import com.example.bestday.domain.service.ComfortScoreCalculator;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Application service implementing the {@link RecommendBestDayUseCase} input port.
 *
 * <p>It wires the use case together: it calls the remote weather source through the
 * {@link WeatherForecastProvider} output port (the two Open-Meteo calls happen here, inside the
 * business chain — never in the controller), scores each day with the pure
 * {@link ComfortScoreCalculator}, ranks them and returns the best one.
 */
@Service
public class RecommendBestDayService implements RecommendBestDayUseCase {

    private final WeatherForecastProvider weatherForecastProvider;
    private final ComfortScoreCalculator comfortScoreCalculator;

    public RecommendBestDayService(WeatherForecastProvider weatherForecastProvider) {
        this.weatherForecastProvider = weatherForecastProvider;
        this.comfortScoreCalculator = new ComfortScoreCalculator();
    }

    @Override
    public BestDayResult recommend(String city, Activity activity) {
        // 1. Remote call #1: resolve the city into coordinates.
        Coordinates coordinates = weatherForecastProvider.geocode(city);

        // 2. Remote call #2: fetch the 7-day daily forecast for those coordinates.
        List<DailyWeather> forecast = weatherForecastProvider.next7DaysForecast(coordinates);
        if (forecast == null || forecast.isEmpty()) {
            throw new ForecastUnavailableException("No forecast available for " + city);
        }

        // 3. Business core: score every day, then rank from best to worst.
        List<DayScore> ranking = forecast.stream()
                .map(day -> comfortScoreCalculator.score(day, activity))
                .sorted(Comparator.comparingInt(DayScore::score)
                        .reversed()
                        .thenComparing(DayScore::date))
                .toList();

        String resolvedLocation = coordinates.country() == null || coordinates.country().isBlank()
                ? coordinates.resolvedName()
                : coordinates.resolvedName() + ", " + coordinates.country();

        return new BestDayResult(city, resolvedLocation, activity, ranking.get(0), ranking);
    }
}
