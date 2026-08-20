package com.sih.traffic.dto;

import com.sih.traffic.domain.enums.StationType;
import jakarta.validation.constraints.*;

public record StationRequest(
        @NotBlank @Size(max = 10) String code,
        @NotBlank @Size(max = 100) String name,
        @NotNull @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0") Double latitude,
        @NotNull @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0") Double longitude,
        @NotNull StationType stationType
) {}
