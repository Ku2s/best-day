package com.example.bestday.web;

import com.example.bestday.application.port.in.RecommendBestDayUseCase;
import com.example.bestday.domain.model.Activity;
import com.example.bestday.domain.model.BestDayResult;
import com.example.bestday.web.dto.BestDayResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST adapter exposing the best-day recommendation.
 *
 * <p>The controller is intentionally thin: it parses the request, delegates the whole computation
 * (including the remote Open-Meteo calls) to the {@link RecommendBestDayUseCase} input port, and
 * maps the domain result to a web DTO. No business logic lives here.
 */
@RestController
@RequestMapping("/api")
public class BestDayController {

    private final RecommendBestDayUseCase recommendBestDayUseCase;

    public BestDayController(RecommendBestDayUseCase recommendBestDayUseCase) {
        this.recommendBestDayUseCase = recommendBestDayUseCase;
    }

    /**
     * {@code GET /api/best-day?city=Paris&activity=running}
     *
     * @param city     required city name
     * @param activity optional activity (defaults to {@code running})
     * @return the best day and the full 7-day ranking
     */
    @GetMapping("/best-day")
    public BestDayResponse bestDay(@RequestParam String city,
                                   @RequestParam(defaultValue = "running") String activity) {
        Activity parsedActivity = Activity.fromString(activity);
        BestDayResult result = recommendBestDayUseCase.recommend(city, parsedActivity);
        return BestDayResponse.from(result);
    }
}
