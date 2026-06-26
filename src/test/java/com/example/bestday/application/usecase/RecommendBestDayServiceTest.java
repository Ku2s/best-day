package com.example.bestday.application.usecase;

import com.example.bestday.application.port.out.WeatherForecastProvider;
import com.example.bestday.domain.exception.ForecastUnavailableException;
import com.example.bestday.domain.model.Activity;
import com.example.bestday.domain.model.BestDayResult;
import com.example.bestday.domain.model.Coordinates;
import com.example.bestday.domain.model.DailyWeather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecommendBestDayServiceTest {

    private WeatherForecastProvider provider;
    private RecommendBestDayService service;

    private static final Coordinates PARIS =
            new Coordinates(48.85, 2.35, "Paris", "France");

    @BeforeEach
    void setUp() {
        provider = mock(WeatherForecastProvider.class);
        service = new RecommendBestDayService(provider);
    }

    @Test
    @DisplayName("Picks the highest-scoring day and ranks all days from best to worst")
    void picksBestDayAndRanksDescending() {
        LocalDate badDay = LocalDate.of(2026, 6, 26);
        LocalDate perfectDay = LocalDate.of(2026, 6, 27);
        LocalDate averageDay = LocalDate.of(2026, 6, 28);

        when(provider.geocode("Paris")).thenReturn(PARIS);
        when(provider.next7DaysForecast(PARIS)).thenReturn(List.of(
                new DailyWeather(badDay, 30.0, 15.0, 35.0),     // hot, rainy, windy
                new DailyWeather(perfectDay, 12.0, 0.0, 0.0),   // ideal for running
                new DailyWeather(averageDay, 20.0, 5.0, 10.0)   // so-so
        ));

        BestDayResult result = service.recommend("Paris", Activity.RUNNING);

        // The remote calls happen inside the business chain, in order.
        verify(provider).geocode("Paris");
        verify(provider).next7DaysForecast(PARIS);

        assertThat(result.bestDay().date()).isEqualTo(perfectDay);
        assertThat(result.bestDay().score()).isEqualTo(100);
        assertThat(result.resolvedLocation()).isEqualTo("Paris, France");
        assertThat(result.ranking()).hasSize(3);
        // ranking sorted by descending score
        assertThat(result.ranking())
                .extracting(d -> d.date())
                .containsExactly(perfectDay, averageDay, badDay);
        assertThat(result.ranking())
                .isSortedAccordingTo((a, b) -> Integer.compare(b.score(), a.score()));
    }

    @Test
    @DisplayName("Defaults the activity to running when omitted upstream is handled, here uses cycling")
    void usesProvidedActivity() {
        when(provider.geocode(any())).thenReturn(PARIS);
        when(provider.next7DaysForecast(any())).thenReturn(List.of(
                new DailyWeather(LocalDate.of(2026, 6, 26), 16.0, 0.0, 0.0)
        ));

        BestDayResult result = service.recommend("Paris", Activity.CYCLING);

        assertThat(result.activity()).isEqualTo(Activity.CYCLING);
        assertThat(result.bestDay().score()).isEqualTo(100); // 16°C is cycling's ideal
    }

    @Test
    @DisplayName("Throws when the forecast is empty")
    void throwsOnEmptyForecast() {
        when(provider.geocode(eq("Nowhere"))).thenReturn(
                new Coordinates(0.0, 0.0, "Nowhere", ""));
        when(provider.next7DaysForecast(any())).thenReturn(List.of());

        assertThatThrownBy(() -> service.recommend("Nowhere", Activity.RUNNING))
                .isInstanceOf(ForecastUnavailableException.class);
    }
}
