package com.sih.traffic.service.decision;

import com.sih.traffic.domain.Train;
import com.sih.traffic.domain.enums.ConflictSeverity;
import com.sih.traffic.dto.CandidateActionDto;
import com.sih.traffic.dto.CandidateScoreDto;
import com.sih.traffic.dto.CandidateValidationResultDto;
import com.sih.traffic.dto.ConflictDto;
import com.sih.traffic.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase 3 Step 3 – DecisionScoringService.
 * Assigns explainable scores to feasible resolution candidates.
 * Infeasible candidates receive a score of 0.0.
 */
@Service
public class DecisionScoringService {

    private final TrainRepository trainRepository;

    public DecisionScoringService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    /**
     * Scores a single candidate action.
     *
     * @param result           the validation result from Step 2
     * @param baselineConflicts the list of baseline conflicts from conflict detection
     * @return CandidateScoreDto containing the score and explainable breakdown
     */
    public CandidateScoreDto score(CandidateValidationResultDto result, List<ConflictDto> baselineConflicts) {
        CandidateActionDto candidate = result.candidate();

        // 1. Infeasible candidates get 0 score
        if (!result.feasible()) {
            Map<String, Double> breakdown = new LinkedHashMap<>();
            breakdown.put("severityBenefit", 0.0);
            breakdown.put("priorityBenefit", 0.0);
            breakdown.put("throughputBenefit", 0.0);
            breakdown.put("delayPenalty", 0.0);
            breakdown.put("newConflictPenalty", 0.0);

            return new CandidateScoreDto(
                    candidate,
                    0.0,
                    breakdown,
                    false,
                    result.rejectionReason()
            );
        }

        // 2. Compute severity benefit
        double severityBenefit = getSeverityBenefit(candidate.conflictKey(), baselineConflicts);

        // 3. Compute priority benefit
        double priorityBenefit = getPriorityBenefit(candidate.targetTrainId());

        // 4. Compute throughput benefit
        double throughputBenefit = getThroughputBenefit(candidate);

        // 5. Compute delay penalty
        double delayPenalty = Math.max(0.0, candidate.proposedDelayMinutes()) * 2.0;

        // 6. Compute new conflict penalty (for any remaining non-serious new conflicts)
        double newConflictPenalty = 0.0;
        if (result.newlyCreatedConflicts() != null) {
            newConflictPenalty = result.newlyCreatedConflicts().size() * 40.0;
        }

        // 7. Calculate final score (clamped to 0.0 minimum)
        double rawScore = severityBenefit + priorityBenefit + throughputBenefit - delayPenalty - newConflictPenalty;
        double finalScore = Math.max(0.0, rawScore);

        Map<String, Double> breakdown = new LinkedHashMap<>();
        breakdown.put("severityBenefit", severityBenefit);
        breakdown.put("priorityBenefit", priorityBenefit);
        breakdown.put("throughputBenefit", throughputBenefit);
        breakdown.put("delayPenalty", delayPenalty);
        breakdown.put("newConflictPenalty", newConflictPenalty);

        return new CandidateScoreDto(
                candidate,
                finalScore,
                breakdown,
                true,
                null
        );
    }

    /**
     * Scores all candidate validation results.
     */
    public List<CandidateScoreDto> scoreAll(List<CandidateValidationResultDto> validationResults,
                                            List<ConflictDto> baselineConflicts) {
        List<CandidateScoreDto> scores = new ArrayList<>();
        for (CandidateValidationResultDto res : validationResults) {
            scores.add(score(res, baselineConflicts));
        }
        return scores;
    }

    // -----------------------------------------------------------------------
    // Helper Methods
    // -----------------------------------------------------------------------

    private double getSeverityBenefit(String conflictKey, List<ConflictDto> baselineConflicts) {
        if (baselineConflicts == null || conflictKey == null) {
            return 50.0; // default/medium benefit fallback
        }

        ConflictDto matched = baselineConflicts.stream()
                .filter(c -> ConflictKey.from(c).asString().equals(conflictKey))
                .findFirst()
                .orElse(null);

        if (matched == null) {
            return 50.0; // fallback if conflict not found in baseline list
        }

        ConflictSeverity severity = matched.severity();
        if (severity == null) {
            return 50.0;
        }

        return switch (severity) {
            case CRITICAL -> 100.0;
            case HIGH     -> 75.0;
            case MEDIUM   -> 50.0;
            case LOW      -> 25.0;
        };
    }

    private double getPriorityBenefit(Long trainId) {
        if (trainId == null) {
            return 30.0; // default/middle priority benefit fallback
        }

        Train train = trainRepository.findById(trainId).orElse(null);
        if (train == null || train.getPriority() == null) {
            return 30.0;
        }

        // Priority ranges from 1 (highest) to 10 (lowest)
        int priority = train.getPriority();
        int clampedPriority = Math.max(1, Math.min(priority, 10));
        return (11 - clampedPriority) * 5.0;
    }

    private double getThroughputBenefit(CandidateActionDto candidate) {
        if (candidate.actionType() == null) {
            return 0.0;
        }

        return switch (candidate.actionType()) {
            case REASSIGN_PLATFORM  -> 30.0; // Keeps train moving immediately without adding delay
            case HOLD_AT_LOOP       -> 15.0; // Holds train locally on loop line, keeping main track clear
            case DELAY_DEPARTURE    -> 5.0;  // Delays departure at origin
            case DELAY_FOR_PLATFORM -> 5.0;  // Shifts platform halt time
        };
    }
}
