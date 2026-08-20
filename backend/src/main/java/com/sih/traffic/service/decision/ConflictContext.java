package com.sih.traffic.service.decision;

import com.sih.traffic.domain.LoopLine;
import com.sih.traffic.domain.Platform;
import com.sih.traffic.domain.Train;
import com.sih.traffic.dto.ConflictDto;
import com.sih.traffic.service.simulation.TrainTimeline;

import java.util.List;

/**
 * Everything needed to reason about one conflict: the underlying
 * ConflictDto (from Phase 2's ConflictDetectionService, untouched),
 * both trains' current state, and the relevant available infrastructure.
 * Assembled once per conflict by ConflictContextBuilder.
 *
 * trainB / timelineB are null for single-train conflicts
 * (PLATFORM_TYPE_INCOMPATIBLE) - only trainA is involved there.
 */
public record ConflictContext(
        ConflictDto conflict,
        String conflictKey,
        Train trainA,
        TrainTimeline timelineA,
        int currentDelayA,
        Train trainB,
        TrainTimeline timelineB,
        int currentDelayB,
        List<LoopLine> loopLinesAtEndpoints,   // for section conflicts (HEAD_ON_SINGLE_LINE / SAME_DIRECTION_OVERLAP)
        Platform currentPlatform,              // for platform conflicts
        List<Platform> alternativePlatforms    // platforms at the same station, excluding currentPlatform
) {}
