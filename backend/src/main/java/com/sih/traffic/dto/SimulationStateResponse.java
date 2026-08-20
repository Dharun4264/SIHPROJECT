package com.sih.traffic.dto;

import java.time.LocalTime;
import java.util.List;

public record SimulationStateResponse(
        LocalTime simulationTime,
        String clockStatus,
        double speedMultiplier,
        List<TrainStateDto> trains,
        List<SectionOccupancyDto> occupiedSections,
        List<PlatformOccupancyDto> occupiedPlatforms,
        List<ConflictDto> activeConflicts
) {}
