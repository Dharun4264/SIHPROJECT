package com.sih.traffic.service.decision;

import com.sih.traffic.domain.enums.ConflictType;
import com.sih.traffic.dto.ConflictDto;

import java.time.LocalTime;

/**
 * Internal, deterministic identity for a conflict, derived purely from
 * ConflictDto's existing fields (type + sectionId/platformId + trainAId
 * + trainBId + windowStart + windowEnd). This does NOT replace or modify
 * ConflictDto.id - that remains Phase 2's sequential, per-call counter,
 * completely untouched.
 *
 * Why this exists: ConflictDto.id is regenerated fresh (starting at 1)
 * every time ConflictDetectionService.detectConflicts() runs, so it is
 * not stable across two separate API calls. Phase 3 needs a stable
 * identifier so /api/decisions/{conflictId}-style lookups keep working
 * even if a delay was injected (and the conflict list reshuffled) between
 * calls. Two ConflictDto instances describing the same underlying
 * conflict always produce the same ConflictKey, regardless of list order.
 */
public record ConflictKey(
        ConflictType type,
        Long sectionId,
        Long platformId,
        Long trainAId,
        Long trainBId,
        LocalTime windowStart,
        LocalTime windowEnd
) {
    public static ConflictKey from(ConflictDto dto) {
        return new ConflictKey(dto.type(), dto.sectionId(), dto.platformId(),
                dto.trainAId(), dto.trainBId(), dto.windowStart(), dto.windowEnd());
    }

    /** Stable string form, safe to use as a map key or an API path segment. */
    public String asString() {
        return type + ":" + nz(sectionId) + ":" + nz(platformId) + ":" + nz(trainAId) + ":" + nz(trainBId)
                + ":" + windowStart + ":" + windowEnd;
    }

    private static String nz(Long value) {
        return value == null ? "-" : String.valueOf(value);
    }
}
