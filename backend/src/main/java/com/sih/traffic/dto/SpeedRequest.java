package com.sih.traffic.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** speedMultiplier = simulated seconds elapsed per real-world second (e.g. 60 = 1 sim-minute/real-second). */
public record SpeedRequest(
        @NotNull @Positive Double speedMultiplier
) {}
