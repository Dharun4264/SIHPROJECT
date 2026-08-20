package com.sih.traffic.dto;

import java.util.List;

/**
 * Result of validating one CandidateActionDto against all hard constraints.
 * Produced by ConstraintValidationService. Never modifies real simulation state.
 *
 * Fields:
 * - candidate            : the action that was validated (unchanged from input)
 * - feasible             : true only when ALL hard constraints pass and the hypothetical
 *                          timeline run shows the original conflict resolved with no new
 *                          serious conflict introduced
 * - rejectionReason      : human-readable reason when feasible == false; null when feasible
 * - remainingTargetConflict : if the original conflict is still present in the hypothetical
 *                             run (action did not resolve it) this is set; null otherwise
 * - newlyCreatedConflicts   : conflicts with severity >= HIGH that appear in the hypothetical
 *                             run but were absent from the baseline; empty list when none
 */
public record CandidateValidationResultDto(
        CandidateActionDto candidate,
        boolean feasible,
        String rejectionReason,
        ConflictDto remainingTargetConflict,
        List<ConflictDto> newlyCreatedConflicts
) {}
