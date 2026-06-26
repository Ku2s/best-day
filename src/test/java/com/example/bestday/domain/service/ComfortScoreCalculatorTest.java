package com.example.bestday.domain.service;

import com.example.bestday.domain.model.Activity;
import com.example.bestday.domain.model.DailyWeather;
import com.example.bestday.domain.model.DayScore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ComfortScoreCalculatorTest {

    private final ComfortScoreCalculator calculator = new ComfortScoreCalculator();

    private static DailyWeather day(double temp, double precip, double wind) {
        return new DailyWeather(LocalDate.of(2026, 6, 26), temp, precip, wind);
    }

    @Test
    @DisplayName("A day matching the ideal profile scores 100")
    void idealDayScoresHundred() {
        // running ideal temperature is 12°C, no rain, no wind
        DayScore score = calculator.score(day(12.0, 0.0, 0.0), Activity.RUNNING);

        assertThat(score.score()).isEqualTo(100);
        assertThat(score.temperatureScore()).isEqualTo(100);
        assertThat(score.precipitationScore()).isEqualTo(100);
        assertThat(score.windScore()).isEqualTo(100);
        assertThat(score.reason()).contains("pas de pluie");
    }

    @Test
    @DisplayName("Sub-scores combine using the activity weights (running 0.4/0.4/0.2)")
    void weightedAverageUsesActivityWeights() {
        // temp ideal (100), 10mm rain -> 50, 20km/h wind -> 50
        // running: 0.4*100 + 0.4*50 + 0.2*50 = 70
        DayScore score = calculator.score(day(12.0, 10.0, 20.0), Activity.RUNNING);

        assertThat(score.temperatureScore()).isEqualTo(100);
        assertThat(score.precipitationScore()).isEqualTo(50);
        assertThat(score.windScore()).isEqualTo(50);
        assertThat(score.score()).isEqualTo(70);
    }

    @Test
    @DisplayName("Scores are clamped to the 0-100 range for extreme weather")
    void scoresAreClamped() {
        // way past every saturation threshold -> everything floors at 0
        DayScore worst = calculator.score(day(60.0, 100.0, 200.0), Activity.RUNNING);
        assertThat(worst.score()).isEqualTo(0);
        assertThat(worst.temperatureScore()).isEqualTo(0);
        assertThat(worst.precipitationScore()).isEqualTo(0);
        assertThat(worst.windScore()).isEqualTo(0);
    }

    @Test
    @DisplayName("The same weather is scored differently depending on the activity profile")
    void activityProfileChangesScore() {
        DailyWeather warmDay = day(27.0, 0.0, 0.0); // beach ideal temperature

        DayScore beach = calculator.score(warmDay, Activity.BEACH);
        DayScore running = calculator.score(warmDay, Activity.RUNNING);

        // 27°C is perfect for the beach but 15°C above running's ideal -> running temp sub-score is lower
        assertThat(beach.temperatureScore()).isEqualTo(100);
        assertThat(running.temperatureScore()).isLessThan(beach.temperatureScore());
        assertThat(beach.score()).isGreaterThan(running.score());
    }
}
