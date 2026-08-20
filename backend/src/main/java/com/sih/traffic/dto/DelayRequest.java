package com.sih.traffic.dto;

import jakarta.validation.constraints.NotNull;

/** minutes may be negative to partially recover a previously-added delay (result is clamped at 0). */
public record DelayRequest(
        @NotNull Integer minutes
) {}
