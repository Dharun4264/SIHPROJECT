package com.sih.traffic.dto;

import com.sih.traffic.domain.enums.SectionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TrackSectionRequest(
        @NotNull Long fromStationId,
        @NotNull Long toStationId,
        @NotNull SectionType sectionType,
        @NotNull @Positive Double lengthKm,
        @NotNull @Positive Integer maxSpeedKmph
) {}
