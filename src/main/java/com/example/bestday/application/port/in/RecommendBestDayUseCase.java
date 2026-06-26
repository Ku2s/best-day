package com.example.bestday.application.port.in;

import com.example.bestday.domain.model.Activity;
import com.example.bestday.domain.model.BestDayResult;

/**
 * Input port (driving side): the single business operation exposed by the application.
 *
 * <p>The web layer depends on this interface, not on the concrete service implementation.
 */
public interface RecommendBestDayUseCase {

    /**
     * Recommends the best day among the next 7 for the given activity in the given city.
     *
     * @param city     the city to evaluate
     * @param activity the activity to optimise comfort for
     * @return the best day plus the full ranking of the 7 days
     */
    BestDayResult recommend(String city, Activity activity);
}
