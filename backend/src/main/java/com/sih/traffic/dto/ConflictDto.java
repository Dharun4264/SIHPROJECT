package com.sih.traffic.dto;

import com.sih.traffic.domain.enums.ConflictSeverity;
import com.sih.traffic.domain.enums.ConflictType;

import java.time.LocalTime;

public record ConflictDto(
        int id,
        ConflictType type,
        ConflictSeverity severity,
        Long sectionId,
        String sectionLabel,      // e.g. "KRR -> ED" - null for platform conflicts
        Long platformId,
        String platformLabel,     // e.g. "CBE platform 2" - null for section conflicts
        Long trainAId,
        String trainANumber,
        Long trainBId,            // null for single-train conflicts (e.g. PLATFORM_TYPE_INCOMPATIBLE)
        String trainBNumber,
        LocalTime windowStart,
        LocalTime windowEnd,
        String explanation
) {}
