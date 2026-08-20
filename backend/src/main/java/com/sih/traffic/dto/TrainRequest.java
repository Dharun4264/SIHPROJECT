package com.sih.traffic.dto;

import com.sih.traffic.domain.enums.TrainType;
import jakarta.validation.constraints.*;

public record TrainRequest(
        @NotBlank @Size(max = 15) String trainNumber,
        @NotBlank @Size(max = 100) String name,
        @NotNull TrainType type,
        @NotNull @Min(1) @Max(10) Integer priority,
        @NotNull @Positive Integer maxSpeedKmph,
        @NotNull @Positive Double lengthM,
        @NotNull Long originStationId,
        @NotNull Long destinationStationId
) {}
