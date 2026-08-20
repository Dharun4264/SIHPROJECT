package com.sih.traffic.service.conflict;

import com.sih.traffic.domain.Station;
import com.sih.traffic.domain.Train;
import com.sih.traffic.domain.enums.ConflictSeverity;
import com.sih.traffic.domain.enums.ConflictType;
import com.sih.traffic.domain.enums.SectionType;
import com.sih.traffic.dto.ConflictDto;
import com.sih.traffic.repository.LoopLineRepository;
import com.sih.traffic.service.simulation.HaltInterval;
import com.sih.traffic.service.simulation.TrainTimeline;
import com.sih.traffic.service.simulation.TransitInterval;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Detects conflicts by comparing every pair of trains' timelines. O(n^2) over
 * legs, which is trivial at this prototype's scale (10 trains / ~40 legs).
 * Detection only - no resolution/optimization (explicitly out of scope for
 * Phase 2).
 */
@Service
public class ConflictDetectionService {

    private final LoopLineRepository loopLineRepository;

    public ConflictDetectionService(LoopLineRepository loopLineRepository) {
        this.loopLineRepository = loopLineRepository;
    }

    public List<ConflictDto> detectConflicts(List<TrainTimeline> timelines) {
        List<ConflictDto> conflicts = new ArrayList<>();
        int idCounter = 1;

        for (int i = 0; i < timelines.size(); i++) {
            for (int j = i + 1; j < timelines.size(); j++) {
                TrainTimeline a = timelines.get(i);
                TrainTimeline b = timelines.get(j);
                idCounter = detectSectionConflicts(a, b, conflicts, idCounter);
                idCounter = detectPlatformConflicts(a, b, conflicts, idCounter);
            }
        }

        for (TrainTimeline tl : timelines) {
            idCounter = detectPlatformTypeIncompatibility(tl, conflicts, idCounter);
        }

        return conflicts;
    }

    // ------------------------------------------------------------------
    // Section conflicts: same section, overlapping time window
    // ------------------------------------------------------------------
    private int detectSectionConflicts(TrainTimeline a, TrainTimeline b, List<ConflictDto> out, int idCounter) {
        for (TransitInterval ta : a.transits()) {
            if (!ta.isValid()) continue;
            for (TransitInterval tb : b.transits()) {
                if (!tb.isValid()) continue;
                if (!ta.section().getId().equals(tb.section().getId())) continue;

                LocalTime[] overlap = overlap(ta.start(), ta.end(), tb.start(), tb.end());
                if (overlap == null) continue;

                boolean sameDirection = ta.fromStation().getId().equals(tb.fromStation().getId())
                        && ta.toStation().getId().equals(tb.toStation().getId());
                boolean oppositeDirection = ta.fromStation().getId().equals(tb.toStation().getId())
                        && ta.toStation().getId().equals(tb.fromStation().getId());
                if (!sameDirection && !oppositeDirection) continue;

                boolean hasLoopEitherEnd = hasLoopLine(ta.fromStation()) || hasLoopLine(ta.toStation());
                String sectionLabel = ta.fromStation().getCode() + " -> " + ta.toStation().getCode();

                if (oppositeDirection && ta.section().getSectionType() == SectionType.SINGLE) {
                    ConflictSeverity severity = hasLoopEitherEnd ? ConflictSeverity.HIGH : ConflictSeverity.CRITICAL;
                    String explanation = "Trains " + a.train().getTrainNumber() + " and " + b.train().getTrainNumber()
                            + " are both scheduled on single-track section " + sectionLabel
                            + " in opposite directions with overlapping times."
                            + (hasLoopEitherEnd
                            ? " Resolvable by holding one train at a loop line at " + ta.fromStation().getCode()
                            + " or " + ta.toStation().getCode() + "."
                            : " No loop line at either endpoint - infeasible without a schedule change.");
                    out.add(sectionConflict(idCounter++, ConflictType.HEAD_ON_SINGLE_LINE, severity, ta.section().getId(),
                            sectionLabel, a, b, overlap, explanation));
                } else if (sameDirection) {
                    ConflictSeverity severity = hasLoopEitherEnd ? ConflictSeverity.MEDIUM : ConflictSeverity.HIGH;
                    String explanation = "Trains " + a.train().getTrainNumber() + " and " + b.train().getTrainNumber()
                            + " both travel " + sectionLabel + " in the same direction with overlapping times"
                            + " - an overtake is implied."
                            + (hasLoopEitherEnd
                            ? " A loop line is available at " + ta.fromStation().getCode() + " or " + ta.toStation().getCode()
                            + " to hold the slower train."
                            : " No loop line at either endpoint to allow an overtake.");
                    out.add(sectionConflict(idCounter++, ConflictType.SAME_DIRECTION_OVERLAP, severity, ta.section().getId(),
                            sectionLabel, a, b, overlap, explanation));
                }
                // opposite direction on a DOUBLE section: not a conflict (separate lines)
            }
        }
        return idCounter;
    }

    // ------------------------------------------------------------------
    // Platform conflicts: same platform, overlapping halt windows
    // ------------------------------------------------------------------
    private int detectPlatformConflicts(TrainTimeline a, TrainTimeline b, List<ConflictDto> out, int idCounter) {
        for (HaltInterval ha : a.halts()) {
            if (ha.platform() == null) continue;
            for (HaltInterval hb : b.halts()) {
                if (hb.platform() == null) continue;
                if (!ha.platform().getId().equals(hb.platform().getId())) continue;

                LocalTime startA = ha.arrival() != null ? ha.arrival() : LocalTime.MIDNIGHT;
                LocalTime endA = ha.departure() != null ? ha.departure() : safeAddMinutes(ha.arrival(), 10);
                LocalTime startB = hb.arrival() != null ? hb.arrival() : LocalTime.MIDNIGHT;
                LocalTime endB = hb.departure() != null ? hb.departure() : safeAddMinutes(hb.arrival(), 10);

                LocalTime[] overlap = overlap(startA, endA, startB, endB);
                if (overlap == null) continue;

                String platformLabel = ha.station().getCode() + " platform " + ha.platform().getPlatformNumber();
                String explanation = "Trains " + a.train().getTrainNumber() + " and " + b.train().getTrainNumber()
                        + " are both assigned to " + platformLabel + " with overlapping halt windows.";

                out.add(new ConflictDto(idCounter++, ConflictType.PLATFORM_OVERLAP, ConflictSeverity.MEDIUM,
                        null, null, ha.platform().getId(), platformLabel,
                        a.train().getId(), a.train().getTrainNumber(),
                        b.train().getId(), b.train().getTrainNumber(),
                        overlap[0], overlap[1], explanation));
            }
        }
        return idCounter;
    }

    // ------------------------------------------------------------------
    // Platform/train-type incompatibility: single-train check (reuses
    // Platform.compatibleTrainTypes, unused until now)
    // ------------------------------------------------------------------
    private int detectPlatformTypeIncompatibility(TrainTimeline tl, List<ConflictDto> out, int idCounter) {
        Train train = tl.train();
        for (HaltInterval h : tl.halts()) {
            if (h.platform() == null) continue;
            var compatible = h.platform().getCompatibleTrainTypes();
            if (compatible != null && !compatible.isEmpty() && !compatible.contains(train.getType())) {
                String platformLabel = h.station().getCode() + " platform " + h.platform().getPlatformNumber();
                String explanation = "Train " + train.getTrainNumber() + " (" + train.getType()
                        + ") is planned onto " + platformLabel + ", which does not support " + train.getType() + ".";
                LocalTime start = h.arrival() != null ? h.arrival() : LocalTime.MIDNIGHT;
                LocalTime end = h.departure() != null ? h.departure() : safeAddMinutes(h.arrival(), 10);
                out.add(new ConflictDto(idCounter++, ConflictType.PLATFORM_TYPE_INCOMPATIBLE, ConflictSeverity.HIGH,
                        null, null, h.platform().getId(), platformLabel,
                        train.getId(), train.getTrainNumber(), null, null,
                        start, end, explanation));
            }
        }
        return idCounter;
    }

    private ConflictDto sectionConflict(int id, ConflictType type, ConflictSeverity severity, Long sectionId,
                                         String sectionLabel, TrainTimeline a, TrainTimeline b,
                                         LocalTime[] overlap, String explanation) {
        return new ConflictDto(id, type, severity, sectionId, sectionLabel, null, null,
                a.train().getId(), a.train().getTrainNumber(),
                b.train().getId(), b.train().getTrainNumber(),
                overlap[0], overlap[1], explanation);
    }

    private boolean hasLoopLine(Station station) {
        return !loopLineRepository.findByStationId(station.getId()).isEmpty();
    }

    private LocalTime safeAddMinutes(LocalTime base, int minutes) {
        return base == null ? null : base.plusMinutes(minutes);
    }

    private LocalTime[] overlap(LocalTime s1, LocalTime e1, LocalTime s2, LocalTime e2) {
        if (s1 == null || e1 == null || s2 == null || e2 == null) return null;
        LocalTime start = s1.isAfter(s2) ? s1 : s2;
        LocalTime end = e1.isBefore(e2) ? e1 : e2;
        return start.isBefore(end) ? new LocalTime[]{start, end} : null;
    }
}
