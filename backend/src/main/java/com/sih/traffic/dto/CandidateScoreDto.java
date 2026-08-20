package com.sih.traffic.dto;

import java.util.Map;

/**
 * Result DTO containing the score and explainable breakdown for a candidate resolution action.
 * Produced by DecisionScoringService.
 */
public record CandidateScoreDto(
        CandidateActionDto candidate,
        double score,
        Map<String, Double> scoreBreakdown,
        boolean feasible,
        String rejectionReason
) {}
