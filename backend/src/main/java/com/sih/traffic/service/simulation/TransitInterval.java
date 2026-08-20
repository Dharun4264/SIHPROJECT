package com.sih.traffic.service.simulation;

import com.sih.traffic.domain.Station;
import com.sih.traffic.domain.TrackSection;

import java.time.LocalTime;

/**
 * A train's traversal of one TrackSection between two consecutive schedule
 * stops, with effective (delay-adjusted) times. section is null if the
 * network data has no section connecting the two stations (data issue -
 * surfaced rather than silently ignored, see TrainTimelineBuilder).
 */
public record TransitInterval(
        Station fromStation,
        Station toStation,
        TrackSection section,
        LocalTime start,
        LocalTime end
) {
    public boolean isValid() {
        return section != null && start != null && end != null && start.isBefore(end);
    }
}
