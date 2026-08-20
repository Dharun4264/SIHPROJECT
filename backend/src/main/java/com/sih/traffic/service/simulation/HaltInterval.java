package com.sih.traffic.service.simulation;

import com.sih.traffic.domain.Platform;
import com.sih.traffic.domain.Station;

import java.time.LocalTime;

/**
 * A train's halt at one station, with effective (delay-adjusted) times.
 * arrival is null for the origin stop; departure is null for the destination stop.
 */
public record HaltInterval(
        Station station,
        LocalTime arrival,
        LocalTime departure,
        Platform platform,
        boolean isOrigin,
        boolean isDestination
) {}
