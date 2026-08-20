package com.sih.traffic.service.decision;

import com.sih.traffic.domain.LoopLine;
import com.sih.traffic.domain.Platform;
import com.sih.traffic.domain.Train;
import com.sih.traffic.domain.TrainSchedule;
import com.sih.traffic.domain.enums.ConflictSeverity;
import com.sih.traffic.domain.enums.ConflictType;
import com.sih.traffic.dto.CandidateActionDto;
import com.sih.traffic.dto.CandidateValidationResultDto;
import com.sih.traffic.dto.ConflictDto;
import com.sih.traffic.repository.LoopLineRepository;
import com.sih.traffic.repository.PlatformRepository;
import com.sih.traffic.repository.TrainRepository;
import com.sih.traffic.repository.TrainScheduleRepository;
import com.sih.traffic.service.conflict.ConflictDetectionService;
import com.sih.traffic.service.simulation.HaltInterval;
import com.sih.traffic.service.simulation.TrainTimeline;
import com.sih.traffic.service.simulation.TrainTimelineBuilder;
import com.sih.traffic.service.simulation.TransitInterval;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Phase 3 Step 2 – validates each CandidateActionDto against hard constraints
 * without ever touching the live simulation state or calling
 * TrainDelayService.addDelay(). Hypothetical timelines are built in-memory
 * as copies; the original TrainTimeline objects are never mutated.
 *
 * "Serious" new conflict = severity HIGH or CRITICAL.
 */
@Service
public class ConstraintValidationService {

    /** Severities considered "serious enough" to reject a candidate action. */
    private static final Set<ConflictSeverity> SERIOUS =
            EnumSet.of(ConflictSeverity.HIGH, ConflictSeverity.CRITICAL);

    private final LoopLineRepository loopLineRepository;
    private final PlatformRepository platformRepository;
    private final TrainRepository trainRepository;
    private final TrainScheduleRepository trainScheduleRepository;
    private final TrainTimelineBuilder timelineBuilder;
    private final ConflictDetectionService conflictDetectionService;

    public ConstraintValidationService(LoopLineRepository loopLineRepository,
                                       PlatformRepository platformRepository,
                                       TrainRepository trainRepository,
                                       TrainScheduleRepository trainScheduleRepository,
                                       TrainTimelineBuilder timelineBuilder,
                                       ConflictDetectionService conflictDetectionService) {
        this.loopLineRepository = loopLineRepository;
        this.platformRepository = platformRepository;
        this.trainRepository = trainRepository;
        this.trainScheduleRepository = trainScheduleRepository;
        this.timelineBuilder = timelineBuilder;
        this.conflictDetectionService = conflictDetectionService;
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Validates one candidate action against all hard constraints.
     *
     * @param candidate      the action to validate
     * @param baselineConflicts all conflicts detected from the current (real) timelines
     * @param allTimelines   current (real, unmodified) timelines for all trains
     * @return validation result – never null; feasible==false when any check fails
     */
    public CandidateValidationResultDto validate(CandidateActionDto candidate,
                                                 List<ConflictDto> baselineConflicts,
                                                 List<TrainTimeline> allTimelines) {
        return switch (candidate.actionType()) {
            case HOLD_AT_LOOP       -> validateHoldAtLoop(candidate, baselineConflicts, allTimelines);
            case REASSIGN_PLATFORM  -> validateReassignPlatform(candidate, baselineConflicts, allTimelines);
            case DELAY_DEPARTURE    -> validateDelayDeparture(candidate, baselineConflicts, allTimelines);
            case DELAY_FOR_PLATFORM -> validateDelayForPlatform(candidate, baselineConflicts, allTimelines);
        };
    }

    /**
     * Validates every candidate in the list and returns all results.
     */
    public List<CandidateValidationResultDto> validateAll(List<CandidateActionDto> candidates,
                                                          List<ConflictDto> baselineConflicts,
                                                          List<TrainTimeline> allTimelines) {
        List<CandidateValidationResultDto> results = new ArrayList<>();
        for (CandidateActionDto candidate : candidates) {
            results.add(validate(candidate, baselineConflicts, allTimelines));
        }
        return results;
    }

    // -----------------------------------------------------------------------
    // HOLD_AT_LOOP
    // -----------------------------------------------------------------------

    private CandidateValidationResultDto validateHoldAtLoop(CandidateActionDto candidate,
                                                             List<ConflictDto> baselineConflicts,
                                                             List<TrainTimeline> allTimelines) {
        // 1. Loop line must exist
        if (candidate.loopLineId() == null) {
            return reject(candidate, "No loop line id specified in candidate.");
        }
        LoopLine loop = loopLineRepository.findById(candidate.loopLineId()).orElse(null);
        if (loop == null) {
            return reject(candidate, "Loop line id=" + candidate.loopLineId() + " does not exist.");
        }

        // 2. Loop must be at the correct station for this conflict
        TrainTimeline targetTimeline = findTimeline(allTimelines, candidate.targetTrainId());
        if (targetTimeline == null) {
            return reject(candidate, "Target train id=" + candidate.targetTrainId() + " has no timeline.");
        }
        boolean loopIsAtRelevantStation = targetTimeline.transits().stream()
                .filter(TransitInterval::isValid)
                .anyMatch(t -> t.fromStation().getId().equals(loop.getStation().getId())
                        || t.toStation().getId().equals(loop.getStation().getId()));
        if (!loopIsAtRelevantStation) {
            return reject(candidate, "Loop line " + loop.getLoopCode()
                    + " at station " + loop.getStation().getCode()
                    + " is not on the route of train " + candidate.targetTrainNumber() + ".");
        }

        // 3. Loop must be physically usable (train length <= loop max length)
        Train targetTrain = findTrain(candidate.targetTrainId());
        if (targetTrain != null && targetTrain.getLengthM() > loop.getMaxLengthM()) {
            return reject(candidate, "Train " + candidate.targetTrainNumber()
                    + " (length " + targetTrain.getLengthM() + " m) is too long for loop "
                    + loop.getLoopCode() + " (max " + loop.getMaxLengthM() + " m).");
        }

        // 4. Loop availability: check that no other train's transit uses the same loop's station
        //    during the target train's conflict window. For simplicity we check if another train
        //    occupies the loop station's section simultaneously.
        ConflictDto targetConflict = findTargetConflict(baselineConflicts, candidate.conflictKey());
        if (targetConflict != null) {
            boolean loopOccupied = isLoopStationOccupied(loop, targetConflict, allTimelines,
                    candidate.targetTrainId());
            if (loopOccupied) {
                return reject(candidate, "Loop line " + loop.getLoopCode()
                        + " at station " + loop.getStation().getCode()
                        + " appears to be occupied during the conflict window.");
            }
        }

        // 5. Hypothetical check: apply a hold delay equal to proposedDelayMinutes and re-detect
        return hypotheticalDelayCheck(candidate, baselineConflicts, allTimelines);
    }

    // -----------------------------------------------------------------------
    // REASSIGN_PLATFORM
    // -----------------------------------------------------------------------

    private CandidateValidationResultDto validateReassignPlatform(CandidateActionDto candidate,
                                                                   List<ConflictDto> baselineConflicts,
                                                                   List<TrainTimeline> allTimelines) {
        // 1. Platform must exist
        if (candidate.platformId() == null) {
            return reject(candidate, "No platform id specified in candidate.");
        }
        Platform platform = platformRepository.findById(candidate.platformId()).orElse(null);
        if (platform == null) {
            return reject(candidate, "Platform id=" + candidate.platformId() + " does not exist.");
        }

        // 2. Platform must be at the correct station
        TrainTimeline targetTimeline = findTimeline(allTimelines, candidate.targetTrainId());
        if (targetTimeline == null) {
            return reject(candidate, "Target train id=" + candidate.targetTrainId() + " has no timeline.");
        }
        boolean platformAtCorrectStation = targetTimeline.halts().stream()
                .anyMatch(h -> h.platform() != null
                        && h.station().getId().equals(platform.getStation().getId()));
        // also accept: the train halts at the platform's station even without a pre-assigned platform
        if (!platformAtCorrectStation) {
            platformAtCorrectStation = targetTimeline.halts().stream()
                    .anyMatch(h -> h.station().getId().equals(platform.getStation().getId()));
        }
        if (!platformAtCorrectStation) {
            return reject(candidate, "Platform " + platform.getPlatformNumber()
                    + " is at station " + platform.getStation().getCode()
                    + " but train " + candidate.targetTrainNumber()
                    + " does not stop there.");
        }

        // 3. Type compatibility
        Train targetTrain = findTrain(candidate.targetTrainId());
        if (targetTrain != null) {
            var compatible = platform.getCompatibleTrainTypes();
            if (compatible != null && !compatible.isEmpty() && !compatible.contains(targetTrain.getType())) {
                return reject(candidate, "Platform " + platform.getPlatformNumber()
                        + " does not support train type " + targetTrain.getType()
                        + " (compatible: " + compatible + ").");
            }
        }

        // 4 & 5. Platform availability and no new conflict:
        //   Build hypothetical timelines where the target train uses the new platform,
        //   then re-run conflict detection.
        return hypotheticalPlatformReassignCheck(candidate, platform, baselineConflicts, allTimelines);
    }

    // -----------------------------------------------------------------------
    // DELAY_DEPARTURE
    // -----------------------------------------------------------------------

    private CandidateValidationResultDto validateDelayDeparture(CandidateActionDto candidate,
                                                                 List<ConflictDto> baselineConflicts,
                                                                 List<TrainTimeline> allTimelines) {
        if (candidate.targetTrainId() == null) {
            return reject(candidate, "No target train specified.");
        }
        TrainTimeline targetTimeline = findTimeline(allTimelines, candidate.targetTrainId());
        if (targetTimeline == null) {
            return reject(candidate, "Target train id=" + candidate.targetTrainId() + " has no timeline.");
        }

        // Hypothetical: apply delay to a copy, re-run conflict detection
        return hypotheticalDelayCheck(candidate, baselineConflicts, allTimelines);
    }

    // -----------------------------------------------------------------------
    // DELAY_FOR_PLATFORM
    // -----------------------------------------------------------------------

    private CandidateValidationResultDto validateDelayForPlatform(CandidateActionDto candidate,
                                                                   List<ConflictDto> baselineConflicts,
                                                                   List<TrainTimeline> allTimelines) {
        if (candidate.targetTrainId() == null) {
            return reject(candidate, "No target train specified.");
        }
        TrainTimeline targetTimeline = findTimeline(allTimelines, candidate.targetTrainId());
        if (targetTimeline == null) {
            return reject(candidate, "Target train id=" + candidate.targetTrainId() + " has no timeline.");
        }

        // For DELAY_FOR_PLATFORM the expected outcome is the PLATFORM_OVERLAP (or
        // PLATFORM_TYPE_INCOMPATIBLE) conflict disappears. Reuse the same hypothetical
        // delay approach – after delay the platform halt times shift and the overlap resolves.
        return hypotheticalDelayCheck(candidate, baselineConflicts, allTimelines);
    }

    // -----------------------------------------------------------------------
    // Hypothetical timeline helpers
    // -----------------------------------------------------------------------

    /**
     * Builds a hypothetical set of timelines where the target train has
     * {@code candidate.proposedDelayMinutes()} added to its current delay,
     * runs conflict detection on that set, and evaluates the result.
     * <p>
     * IMPORTANT: never calls TrainDelayService.addDelay(). The delay is
     * applied only to an in-memory copy of the timeline.
     */
    private CandidateValidationResultDto hypotheticalDelayCheck(CandidateActionDto candidate,
                                                                  List<ConflictDto> baselineConflicts,
                                                                  List<TrainTimeline> allTimelines) {
        int extraDelay = Math.max(1, candidate.proposedDelayMinutes());
        List<TrainTimeline> hypothetical = buildHypotheticalTimelinesWithDelay(
                allTimelines, candidate.targetTrainId(), extraDelay);

        return evaluateHypothetical(candidate, baselineConflicts, hypothetical);
    }

    /**
     * Builds hypothetical timelines where the target train is re-routed to a
     * new platform (platform halt uses newPlatform instead of the original one).
     */
    private CandidateValidationResultDto hypotheticalPlatformReassignCheck(CandidateActionDto candidate,
                                                                             Platform newPlatform,
                                                                             List<ConflictDto> baselineConflicts,
                                                                             List<TrainTimeline> allTimelines) {
        List<TrainTimeline> hypothetical = buildHypotheticalTimelinesWithPlatform(
                allTimelines, candidate.targetTrainId(), newPlatform);

        return evaluateHypothetical(candidate, baselineConflicts, hypothetical);
    }

    /**
     * Runs conflict detection on the hypothetical timelines and determines
     * whether the original conflict is resolved and no serious new conflicts appear.
     */
    private CandidateValidationResultDto evaluateHypothetical(CandidateActionDto candidate,
                                                               List<ConflictDto> baselineConflicts,
                                                               List<TrainTimeline> hypothetical) {
        List<ConflictDto> hypotheticalConflicts = conflictDetectionService.detectConflicts(hypothetical);

        // Find the target conflict in the baseline (match by conflictKey structural fields)
        ConflictDto targetConflict = findTargetConflict(baselineConflicts, candidate.conflictKey());

        // Check whether the original conflict still exists in the hypothetical run
        ConflictDto remaining = targetConflict != null
                ? findMatchingConflict(hypotheticalConflicts, targetConflict)
                : null;

        // Find conflicts that are in the hypothetical run but NOT in the baseline,
        // and are serious (HIGH or CRITICAL)
        List<ConflictDto> newSerious = findNewSeriousConflicts(baselineConflicts, hypotheticalConflicts);

        if (remaining != null) {
            return new CandidateValidationResultDto(candidate, false,
                    "The original conflict was not resolved by the hypothetical action.",
                    remaining, newSerious);
        }
        if (!newSerious.isEmpty()) {
            return new CandidateValidationResultDto(candidate, false,
                    "Applying the action creates " + newSerious.size() + " new serious conflict(s).",
                    null, newSerious);
        }

        return new CandidateValidationResultDto(candidate, true, null, null, List.of());
    }

    // -----------------------------------------------------------------------
    // Hypothetical timeline builders (pure in-memory, no DB writes)
    // -----------------------------------------------------------------------

    /**
     * Returns a new list of TrainTimelines where the target train's timeline is
     * replaced by one built with (currentDelay + extraDelayMinutes). All other
     * timelines are returned unchanged (they are records, so sharing is safe).
     */
    private List<TrainTimeline> buildHypotheticalTimelinesWithDelay(List<TrainTimeline> current,
                                                                      Long targetTrainId,
                                                                      int extraDelayMinutes) {
        List<TrainTimeline> result = new ArrayList<>();
        for (TrainTimeline tl : current) {
            if (tl.train().getId().equals(targetTrainId)) {
                int newDelay = tl.delayMinutes() + extraDelayMinutes;
                List<TrainSchedule> stops =
                        trainScheduleRepository.findByTrainIdOrderBySequenceNoAsc(targetTrainId);
                result.add(timelineBuilder.build(tl.train(), stops, newDelay));
            } else {
                result.add(tl);
            }
        }
        return result;
    }

    /**
     * Returns a new list of TrainTimelines where the target train's halt at the
     * new platform's station uses newPlatform instead of whatever was originally
     * planned. Built directly from the existing halt/transit data – no DB writes.
     */
    private List<TrainTimeline> buildHypotheticalTimelinesWithPlatform(List<TrainTimeline> current,
                                                                         Long targetTrainId,
                                                                         Platform newPlatform) {
        List<TrainTimeline> result = new ArrayList<>();
        for (TrainTimeline tl : current) {
            if (tl.train().getId().equals(targetTrainId)) {
                List<HaltInterval> newHalts = new ArrayList<>();
                for (HaltInterval h : tl.halts()) {
                    if (h.station().getId().equals(newPlatform.getStation().getId())) {
                        // Replace only the platform reference; times are unchanged
                        newHalts.add(new HaltInterval(h.station(), h.arrival(), h.departure(),
                                newPlatform, h.isOrigin(), h.isDestination()));
                    } else {
                        newHalts.add(h);
                    }
                }
                result.add(new TrainTimeline(tl.train(), tl.delayMinutes(), newHalts, tl.transits()));
            } else {
                result.add(tl);
            }
        }
        return result;
    }

    // -----------------------------------------------------------------------
    // Conflict matching helpers
    // -----------------------------------------------------------------------

    /**
     * Finds the baseline conflict that corresponds to candidate.conflictKey() by
     * re-computing ConflictKey.from() for every baseline conflict and comparing
     * the string representation.
     */
    private ConflictDto findTargetConflict(List<ConflictDto> baseline, String conflictKey) {
        return baseline.stream()
                .filter(c -> ConflictKey.from(c).asString().equals(conflictKey))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks whether a structurally equivalent conflict still exists in the
     * hypothetical conflict list. Matches on type + sectionId/platformId +
     * both train IDs (order-insensitive for section conflicts).
     */
    private ConflictDto findMatchingConflict(List<ConflictDto> hypothetical, ConflictDto target) {
        return hypothetical.stream()
                .filter(h -> h.type() == target.type()
                        && safeEquals(h.sectionId(), target.sectionId())
                        && safeEquals(h.platformId(), target.platformId())
                        && trainPairMatches(h, target))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns conflicts that appear in hypothetical but NOT in baseline, and whose
     * severity is HIGH or CRITICAL.
     */
    private List<ConflictDto> findNewSeriousConflicts(List<ConflictDto> baseline,
                                                       List<ConflictDto> hypothetical) {
        List<ConflictDto> newSerious = new ArrayList<>();
        for (ConflictDto hc : hypothetical) {
            if (!SERIOUS.contains(hc.severity())) continue;
            boolean existedBefore = baseline.stream()
                    .anyMatch(bc -> bc.type() == hc.type()
                            && safeEquals(bc.sectionId(), hc.sectionId())
                            && safeEquals(bc.platformId(), hc.platformId())
                            && trainPairMatches(bc, hc));
            if (!existedBefore) {
                newSerious.add(hc);
            }
        }
        return newSerious;
    }

    /** True if both conflicts involve the same pair of trains (order-insensitive). */
    private boolean trainPairMatches(ConflictDto a, ConflictDto b) {
        Long aA = a.trainAId(), aB = a.trainBId();
        Long bA = b.trainAId(), bB = b.trainBId();
        if (safeEquals(aA, bA) && safeEquals(aB, bB)) return true;
        // section conflicts: either train can be "A" or "B" depending on list order
        if (safeEquals(aA, bB) && safeEquals(aB, bA)) return true;
        return false;
    }

    // -----------------------------------------------------------------------
    // Loop station occupancy check
    // -----------------------------------------------------------------------

    private boolean isLoopStationOccupied(LoopLine loop, ConflictDto conflict,
                                           List<TrainTimeline> allTimelines, Long targetTrainId) {
        Long loopStationId = loop.getStation().getId();
        LocalTime winStart = conflict.windowStart();
        LocalTime winEnd = conflict.windowEnd();
        if (winStart == null || winEnd == null) return false;

        for (TrainTimeline tl : allTimelines) {
            if (tl.train().getId().equals(targetTrainId)) continue;
            for (HaltInterval h : tl.halts()) {
                if (h.station().getId().equals(loopStationId)) {
                    LocalTime arr = h.arrival() != null ? h.arrival() : LocalTime.MIDNIGHT;
                    LocalTime dep = h.departure() != null ? h.departure() : LocalTime.MAX;
                    if (arr.isBefore(winEnd) && dep.isAfter(winStart)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // Utility
    // -----------------------------------------------------------------------

    private TrainTimeline findTimeline(List<TrainTimeline> timelines, Long trainId) {
        if (trainId == null) return null;
        return timelines.stream()
                .filter(tl -> tl.train().getId().equals(trainId))
                .findFirst()
                .orElse(null);
    }

    private Train findTrain(Long trainId) {
        if (trainId == null) return null;
        return trainRepository.findById(trainId).orElse(null);
    }

    private static boolean safeEquals(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private CandidateValidationResultDto reject(CandidateActionDto candidate, String reason) {
        return new CandidateValidationResultDto(candidate, false, reason, null, List.of());
    }
}
