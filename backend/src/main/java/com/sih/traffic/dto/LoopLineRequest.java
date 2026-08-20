package com.sih.traffic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record LoopLineRequest(
        @NotNull Long stationId,
        @NotBlank @Size(max = 10) String loopCode,
        @NotNull @Positive Double maxLengthM
) {}
