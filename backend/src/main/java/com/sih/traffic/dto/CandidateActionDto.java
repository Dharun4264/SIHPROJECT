package com.sih.traffic.dto;

import com.sih.traffic.domain.enums.ActionType;

/**
 * One candidate resolution action for a conflict, as produced by
 * CandidateGenerationService. Not yet checked against constraints and not
 * yet scored - see ConstraintValidationService / DecisionScoringService
 * (later Phase 3 steps) for that.
 */
public record CandidateActionDto(
        String conflictKey,           // internal deterministic key (see ConflictKey), NOT ConflictDto.id
        ActionType actionType,
        Long targetTrainId,           // the train this action applies to
        String targetTrainNumber,
        Long counterpartTrainId,      // the other train in the conflict, null if not applicable
        String counterpartTrainNumber,
        String locationLabel,         // human-readable, e.g. "KRR loop KRR-L1" or "CBE platform 2"
        Long loopLineId,              // set when actionType == HOLD_AT_LOOP
        Long platformId,              // set when actionType involves a platform
        int proposedDelayMinutes,     // raw estimate; refined at scoring stage
        String description            // short one-line description (full "reason" text is a later step)
) {}
