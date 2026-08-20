package com.sih.traffic.dto;

import java.time.LocalTime;

public record SimulationStatusResponse(
        String clockStatus,
        LocalTime simulationTime,
        double speedMultiplier
) {}
