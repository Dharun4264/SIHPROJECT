package com.sih.traffic.service.decision;

import com.sih.traffic.domain.LoopLine;
import com.sih.traffic.domain.Platform;
import com.sih.traffic.domain.Train;
import com.sih.traffic.dto.ConflictDto;
import com.sih.traffic.repository.LoopLineRepository;
import com.sih.traffic.repository.PlatformRepository;
import com.sih.traffic.service.simulation.TrainTimeline;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Assembles a ConflictContext from a ConflictDto plus the full set of
 * TrainTimelines for the current snapshot (see
 * OccupancyService.buildAllTimelines() - reused, not duplicated).
 * Pure read/lookup: no mutation, no interaction with
 * TrainDelayService.addDelay(). Real simulation state is never touched
 * here, which is also what makes this safe to reuse later for what-if
 * scenarios (a hypothetical timeline list can be passed in just the same).
 */
@Component
public class ConflictContextBuilder {

    private final LoopLineRepository loopLineRepository;
    private final PlatformRepository platformRepository;

    public ConflictContextBuilder(LoopLineRepository loopLineRepository, PlatformRepository platformRepository) {
        this.loopLineRepository = loopLineRepository;
        this.platformRepository = platformRepository;
    }

    public ConflictContext build(ConflictDto conflict, List<TrainTimeline> timelines) {
        Map<Long, TrainTimeline> byTrainId = timelines.stream()
                .collect(Collectors.toMap(tl -> tl.train().getId(), tl -> tl));

        TrainTimeline timelineA = byTrainId.get(conflict.trainAId());
        TrainTimeline timelineB = conflict.trainBId() == null ? null : byTrainId.get(conflict.trainBId());

        Train trainA = timelineA != null ? timelineA.train() : null;
        Train trainB = timelineB != null ? timelineB.train() : null;

        int delayA = timelineA != null ? timelineA.delayMinutes() : 0;
        int delayB = timelineB != null ? timelineB.delayMinutes() : 0;

        List<LoopLine> loopLines = new ArrayList<>();
        Platform currentPlatform = null;
        List<Platform> alternatives = new ArrayList<>();

        if (conflict.sectionId() != null) {
            loopLines.addAll(loopLinesForSection(timelineA, conflict.sectionId()));
            if (loopLines.isEmpty() && timelineB != null) {
                loopLines.addAll(loopLinesForSection(timelineB, conflict.sectionId()));
            }
        }

                if (conflict.platformId() != null) {
            Platform foundPlatform = platformRepository.findById(conflict.platformId()).orElse(null);
            currentPlatform = foundPlatform;
            if (foundPlatform != null) {
                alternatives = platformRepository.findByStationId(foundPlatform.getStation().getId()).stream()
                        .filter(p -> !p.getId().equals(foundPlatform.getId()))
                        .toList();
            }
        }
        

        return new ConflictContext(conflict, ConflictKey.from(conflict).asString(),
                trainA, timelineA, delayA, trainB, timelineB, delayB,
                loopLines, currentPlatform, alternatives);
    }

    /** Finds the TrackSection's endpoint stations via the train's own transit leg, then looks up loop lines there. */
    private List<LoopLine> loopLinesForSection(TrainTimeline timeline, Long sectionId) {
        if (timeline == null) return List.of();
        return timeline.transits().stream()
                .filter(t -> t.isValid() && t.section().getId().equals(sectionId))
                .findFirst()
                .map(t -> {
                    List<LoopLine> result = new ArrayList<>();
                    result.addAll(loopLineRepository.findByStationId(t.fromStation().getId()));
                    result.addAll(loopLineRepository.findByStationId(t.toStation().getId()));
                    return result;
                })
                .orElse(List.of());
    }
}
