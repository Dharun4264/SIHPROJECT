package com.sih.traffic.domain.enums;

/**
 * The kinds of resolution actions CandidateGenerationService can propose
 * for a conflict. Detection-only concerns (Phase 2) are unaffected - this
 * is purely Phase 3 vocabulary.
 */
public enum ActionType {
    /** Hold a train at a loop line so the other train can clear a shared section first. */
    HOLD_AT_LOOP,
    /** Delay a train's departure (used when no loop line is available to hold at). */
    DELAY_DEPARTURE,
    /** Move a train to a different, structurally compatible platform at the same station. */
    REASSIGN_PLATFORM,
    /** Shift a train's platform halt time so it no longer overlaps another train's use of the same platform. */
    DELAY_FOR_PLATFORM
}
