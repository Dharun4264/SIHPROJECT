package com.sih.traffic.dto;

import com.sih.traffic.domain.enums.ActionType;

/**
 * Represents an alternative (or infeasible) action for a conflict,
 * returned inside DecisionDto.
 */
public record AlternativeActionDto(
        ActionType actionType,
        Long targetTrainId,
        String targetTrainNumber,
        String locationLabel,
        int proposedDelayMinutes,
        double score,
        boolean feasible,
        String rejectionReason
) {}
