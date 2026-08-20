package com.sih.traffic.service.decision;

import com.sih.traffic.domain.LoopLine;
import com.sih.traffic.domain.Platform;
import com.sih.traffic.domain.Train;
import com.sih.traffic.domain.enums.ActionType;
import com.sih.traffic.domain.enums.TrainType;
import com.sih.traffic.dto.CandidateActionDto;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates plausible candidate resolution actions for a conflict, based
 * on ConflictType (per the four types ConflictDetectionService already
 * produces - HEAD_ON_SINGLE_LINE, SAME_DIRECTION_OVERLAP, PLATFORM_OVERLAP,
 * PLATFORM_TYPE_INCOMPATIBLE). This step does STRUCTURAL/STATIC filtering
 * only (does a loop line exist here? is this platform type-compatible?).
 * Dynamic/cross-train feasibility (is it free at this time? would this
 * create a NEW conflict?) is ConstraintValidationService's job - a later
 * Phase 3 step, not implemented yet.
 */
@Service
public class CandidateGenerationService {

    public List<CandidateActionDto> generate(ConflictContext ctx) {
        return switch (ctx.conflict().type()) {
            case HEAD_ON_SINGLE_LINE -> generateHoldCandidates(ctx, "single-track section");
            case SAME_DIRECTION_OVERLAP -> generateHoldCandidates(ctx, "shared-direction section");
            case PLATFORM_OVERLAP -> generatePlatformOverlapCandidates(ctx);
            case PLATFORM_TYPE_INCOMPATIBLE -> generatePlatformIncompatibleCandidates(ctx);
        };
    }

    // ------------------------------------------------------------------
    // HEAD_ON_SINGLE_LINE / SAME_DIRECTION_OVERLAP: hold one of the two
    // trains (at a loop line if one exists at either endpoint station,
    // else fall back to a plain departure delay) so the other can clear
    // the section first. Generated symmetrically for both trains -
    // "allow the other to proceed first" is the natural complement of
    // whichever candidate ends up chosen, captured in the description text
    // rather than as a separate action type.
    // ------------------------------------------------------------------
    private List<CandidateActionDto> generateHoldCandidates(ConflictContext ctx, String sectionKind) {
        List<CandidateActionDto> candidates = new ArrayList<>();
        int overlapMinutes = overlapMinutes(ctx);

        if (ctx.trainA() != null) {
            candidates.addAll(holdOrDelayCandidates(ctx, ctx.trainA(), ctx.trainB(), overlapMinutes, sectionKind));
        }
        if (ctx.trainB() != null) {
            candidates.addAll(holdOrDelayCandidates(ctx, ctx.trainB(), ctx.trainA(), overlapMinutes, sectionKind));
        }
        return candidates;
    }

    private List<CandidateActionDto> holdOrDelayCandidates(ConflictContext ctx, Train target, Train counterpart,
                                                             int overlapMinutes, String sectionKind) {
        List<CandidateActionDto> candidates = new ArrayList<>();

        if (!ctx.loopLinesAtEndpoints().isEmpty()) {
            for (LoopLine loop : ctx.loopLinesAtEndpoints()) {
                String location = loop.getStation().getCode() + " loop " + loop.getLoopCode();
                String description = "Hold " + target.getTrainNumber() + " at " + location
                        + " to let " + safeNumber(counterpart) + " clear the " + sectionKind + " first.";
                candidates.add(new CandidateActionDto(
                        ctx.conflictKey(), ActionType.HOLD_AT_LOOP,
                        target.getId(), target.getTrainNumber(),
                        counterpart != null ? counterpart.getId() : null,
                        counterpart != null ? counterpart.getTrainNumber() : null,
                        location, loop.getId(), null,
                        overlapMinutes, description));
            }
        } else {
            String description = "Delay " + target.getTrainNumber() + "'s departure so it enters the "
                    + sectionKind + " after " + safeNumber(counterpart) + " has cleared it "
                    + "(no loop line available at either endpoint).";
            candidates.add(new CandidateActionDto(
                    ctx.conflictKey(), ActionType.DELAY_DEPARTURE,
                    target.getId(), target.getTrainNumber(),
                    counterpart != null ? counterpart.getId() : null,
                    counterpart != null ? counterpart.getTrainNumber() : null,
                    "(no loop line - hold at origin)", null, null,
                    overlapMinutes, description));
        }
        return candidates;
    }

    // ------------------------------------------------------------------
    // PLATFORM_OVERLAP: reassign one train to a type-compatible alternative
    // platform at the same station, or delay one train's halt.
    // ------------------------------------------------------------------
    private List<CandidateActionDto> generatePlatformOverlapCandidates(ConflictContext ctx) {
        List<CandidateActionDto> candidates = new ArrayList<>();
        int overlapMinutes = overlapMinutes(ctx);
        String stationCode = ctx.currentPlatform() != null ? ctx.currentPlatform().getStation().getCode() : "";

        candidates.addAll(platformCandidatesForTarget(ctx, ctx.trainA(), ctx.trainB(), stationCode, overlapMinutes));
        candidates.addAll(platformCandidatesForTarget(ctx, ctx.trainB(), ctx.trainA(), stationCode, overlapMinutes));
        return candidates;
    }

    private List<CandidateActionDto> platformCandidatesForTarget(ConflictContext ctx, Train target, Train counterpart,
                                                                   String stationCode, int overlapMinutes) {
        List<CandidateActionDto> candidates = new ArrayList<>();
        if (target == null) return candidates;

        for (Platform alt : ctx.alternativePlatforms()) {
            if (!isTypeCompatible(alt, target.getType())) continue;
            String location = stationCode + " platform " + alt.getPlatformNumber();
            String description = "Move " + target.getTrainNumber() + " to " + location
                    + " instead, freeing the original platform for " + safeNumber(counterpart) + ".";
            candidates.add(new CandidateActionDto(
                    ctx.conflictKey(), ActionType.REASSIGN_PLATFORM,
                    target.getId(), target.getTrainNumber(),
                    counterpart != null ? counterpart.getId() : null,
                    counterpart != null ? counterpart.getTrainNumber() : null,
                    location, null, alt.getId(),
                    0, description));
        }

        String delayDescription = "Delay " + target.getTrainNumber() + "'s platform halt so it no longer overlaps "
                + "with " + safeNumber(counterpart) + " on the same platform.";
        candidates.add(new CandidateActionDto(
                ctx.conflictKey(), ActionType.DELAY_FOR_PLATFORM,
                target.getId(), target.getTrainNumber(),
                counterpart != null ? counterpart.getId() : null,
                counterpart != null ? counterpart.getTrainNumber() : null,
                stationCode + " (same platform, shifted time)", null,
                ctx.currentPlatform() != null ? ctx.currentPlatform().getId() : null,
                overlapMinutes, delayDescription));

        return candidates;
    }

    // ------------------------------------------------------------------
    // PLATFORM_TYPE_INCOMPATIBLE: single-train conflict (trainA only).
    // Only ever proposes platforms whose compatibleTrainTypes actually
    // includes the train's type - never recommends an incompatible one.
    // Falls back to a delay only if genuinely no compatible platform exists.
    // ------------------------------------------------------------------
    private List<CandidateActionDto> generatePlatformIncompatibleCandidates(ConflictContext ctx) {
        List<CandidateActionDto> candidates = new ArrayList<>();
        Train target = ctx.trainA();
        if (target == null) return candidates;
        String stationCode = ctx.currentPlatform() != null ? ctx.currentPlatform().getStation().getCode() : "";

        boolean foundCompatible = false;
        for (Platform alt : ctx.alternativePlatforms()) {
            if (!isTypeCompatible(alt, target.getType())) continue;
            foundCompatible = true;
            String location = stationCode + " platform " + alt.getPlatformNumber();
            String description = "Reassign " + target.getTrainNumber() + " to " + location
                    + ", which supports " + target.getType() + ".";
            candidates.add(new CandidateActionDto(
                    ctx.conflictKey(), ActionType.REASSIGN_PLATFORM,
                    target.getId(), target.getTrainNumber(), null, null,
                    location, null, alt.getId(), 0, description));
        }

        if (!foundCompatible) {
            String description = "No compatible platform currently modeled at " + stationCode + " for "
                    + target.getType() + " - delaying " + target.getTrainNumber()
                    + " is the only fallback until a compatible platform is available.";
            candidates.add(new CandidateActionDto(
                    ctx.conflictKey(), ActionType.DELAY_DEPARTURE,
                    target.getId(), target.getTrainNumber(), null, null,
                    stationCode + " (no compatible platform)", null, null,
                    overlapMinutes(ctx), description));
        }
        return candidates;
    }

    // ------------------------------------------------------------------
    private int overlapMinutes(ConflictContext ctx) {
        long minutes = Duration.between(ctx.conflict().windowStart(), ctx.conflict().windowEnd()).toMinutes();
        return (int) Math.max(1, minutes);
    }

    private boolean isTypeCompatible(Platform platform, TrainType type) {
        var compatible = platform.getCompatibleTrainTypes();
        return compatible == null || compatible.isEmpty() || compatible.contains(type);
    }

    private String safeNumber(Train train) {
        return train != null ? train.getTrainNumber() : "the other train";
    }
}
