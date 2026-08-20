package com.sih.traffic.service.decision;

import com.sih.traffic.dto.AlternativeActionDto;
import com.sih.traffic.dto.CandidateActionDto;
import com.sih.traffic.dto.CandidateScoreDto;
import com.sih.traffic.dto.CandidateValidationResultDto;
import com.sih.traffic.dto.ConflictDto;
import com.sih.traffic.dto.DecisionDto;
import com.sih.traffic.service.conflict.ConflictDetectionService;
import com.sih.traffic.service.simulation.OccupancyService;
import com.sih.traffic.service.simulation.TrainTimeline;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Phase 3 Step 4: OptimizationService.
 * Coordinates candidate generation, constraint validation, and decision scoring
 * to recommend the best feasible resolution action for each active conflict.
 * Read-only: never modifies state or database.
 */
@Service
public class OptimizationService {

    private final OccupancyService occupancyService;
    private final ConflictDetectionService conflictDetectionService;
    private final ConflictContextBuilder conflictContextBuilder;
    private final CandidateGenerationService candidateGenerationService;
    private final ConstraintValidationService constraintValidationService;
    private final DecisionScoringService decisionScoringService;

    public OptimizationService(OccupancyService occupancyService,
                               ConflictDetectionService conflictDetectionService,
                               ConflictContextBuilder conflictContextBuilder,
                               CandidateGenerationService candidateGenerationService,
                               ConstraintValidationService constraintValidationService,
                               DecisionScoringService decisionScoringService) {
        this.occupancyService = occupancyService;
        this.conflictDetectionService = conflictDetectionService;
        this.conflictContextBuilder = conflictContextBuilder;
        this.candidateGenerationService = candidateGenerationService;
        this.constraintValidationService = constraintValidationService;
        this.decisionScoringService = decisionScoringService;
    }

    /**
     * Resolves all current active conflicts and returns recommendations.
     */
    public List<DecisionDto> optimize() {
        List<TrainTimeline> timelines = occupancyService.buildAllTimelines();
        List<ConflictDto> conflicts = conflictDetectionService.detectConflicts(timelines);

        List<DecisionDto> decisions = new ArrayList<>();
        for (ConflictDto conflict : conflicts) {
            decisions.add(optimizeConflict(conflict, conflicts, timelines));
        }
        return decisions;
    }

    /**
     * Generates, validates, scores, and recommends actions for a single conflict.
     */
    public DecisionDto optimizeConflict(ConflictDto conflict, List<ConflictDto> baselineConflicts, List<TrainTimeline> timelines) {
        ConflictContext ctx = conflictContextBuilder.build(conflict, timelines);
        List<CandidateActionDto> candidates = candidateGenerationService.generate(ctx);

        List<CandidateValidationResultDto> validationResults =
                constraintValidationService.validateAll(candidates, baselineConflicts, timelines);

        List<CandidateScoreDto> scores =
                decisionScoringService.scoreAll(validationResults, baselineConflicts);

        // Find the highest-scoring feasible candidate
        CandidateScoreDto best = scores.stream()
                .filter(CandidateScoreDto::feasible)
                .max(Comparator.comparingDouble(CandidateScoreDto::score))
                .orElse(null);

        List<AlternativeActionDto> alternatives = new ArrayList<>();
        for (CandidateScoreDto s : scores) {
            if (best != null && s.candidate().equals(best.candidate())) {
                continue; // Skip the chosen recommendation
            }

            alternatives.add(new AlternativeActionDto(
                    s.candidate().actionType(),
                    s.candidate().targetTrainId(),
                    s.candidate().targetTrainNumber(),
                    s.candidate().locationLabel(),
                    s.candidate().proposedDelayMinutes(),
                    s.score(),
                    s.feasible(),
                    s.rejectionReason()
            ));
        }

        if (best != null) {
            int newlyCreatedCount = 0;
            for (CandidateValidationResultDto v : validationResults) {
                if (v.candidate().equals(best.candidate())) {
                    newlyCreatedCount = v.newlyCreatedConflicts() != null ? v.newlyCreatedConflicts().size() : 0;
                    break;
                }
            }

            return new DecisionDto(
                    conflict,
                    best.candidate().actionType(),
                    best.score(),
                    best.candidate().description(),
                    best.candidate().proposedDelayMinutes(),
                    newlyCreatedCount,
                    alternatives
            );
        } else {
            return new DecisionDto(
                    conflict,
                    null,
                    0.0,
                    "No feasible resolution was found.",
                    0,
                    0,
                    alternatives
            );
        }
    }
}
