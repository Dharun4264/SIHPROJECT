package com.sih.traffic.dto;

import com.sih.traffic.domain.enums.TrainType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PlatformRequest(
        @NotNull Long stationId,
        @NotBlank @Size(max = 10) String platformNumber,
        @NotNull @Positive Double lengthM,
        Set<TrainType> compatibleTrainTypes
) {}
