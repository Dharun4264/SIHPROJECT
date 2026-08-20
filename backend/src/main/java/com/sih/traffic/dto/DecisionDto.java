package com.sih.traffic.dto;

import com.sih.traffic.domain.enums.ActionType;
import java.util.List;

/**
 * Main result DTO for Phase 3 Step 4: OptimizationService.
 * Contains the conflict, the selected recommended action (if feasible),
 * and the alternative actions.
 */
public record DecisionDto(
        ConflictDto conflict,
        ActionType recommendedAction,
        Double score,
        String reason,
        Integer estimatedDelayMinutes,
        Integer newConflicts,
        List<AlternativeActionDto> alternatives
) {}
