package com.sih.traffic.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record TrainScheduleRequest(
        @NotNull Long trainId,
        @NotNull Long stationId,
        @NotNull @Min(1) Integer sequenceNo,
        LocalTime scheduledArrival,
        LocalTime scheduledDeparture,
        Long plannedPlatformId
) {}
