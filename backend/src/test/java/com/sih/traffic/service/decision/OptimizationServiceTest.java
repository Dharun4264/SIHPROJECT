package com.sih.traffic.service.decision;

import com.sih.traffic.domain.enums.ActionType;
import com.sih.traffic.domain.enums.ConflictSeverity;
import com.sih.traffic.domain.enums.ConflictType;
import com.sih.traffic.dto.*;
import com.sih.traffic.service.conflict.ConflictDetectionService;
import com.sih.traffic.service.simulation.OccupancyService;
import com.sih.traffic.service.simulation.TrainTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for OptimizationService (Phase 3 Step 4).
 * Uses plain fakes and stubs to stay fully compatible with JDK 25 without ByteBuddy/Mockito limitations.
 */
class OptimizationServiceTest {

    private OptimizationService sut;

    // Stub Services
    private StubOccupancyService occupancyService;
    private StubConflictDetectionService conflictDetectionService;
    private StubConflictContextBuilder conflictContextBuilder;
    private StubCandidateGenerationService candidateGenerationService;
    private StubConstraintValidationService constraintValidationService;
    private StubDecisionScoringService decisionScoringService;

    @BeforeEach
    void setUp() {
        occupancyService = new StubOccupancyService();
        conflictDetectionService = new StubConflictDetectionService();
        conflictContextBuilder = new StubConflictContextBuilder();
        candidateGenerationService = new StubCandidateGenerationService();
        constraintValidationService = new StubConstraintValidationService();
        decisionScoringService = new StubDecisionScoringService();

        sut = new OptimizationService(
                occupancyService,
                conflictDetectionService,
                conflictContextBuilder,
                candidateGenerationService,
                constraintValidationService,
                decisionScoringService
        );
    }

    // =======================================================================
    // Test 1: Highest-scoring feasible candidate is selected
    // =======================================================================
    @Test
    @DisplayName("Highest-scoring feasible candidate is selected as the recommended action")
    void test1_highestScoringFeasibleSelected() {
        ConflictDto conflict = mockConflict(1, ConflictSeverity.HIGH);
        String key = ConflictKey.from(conflict).asString();

        occupancyService.setNextTimelines(List.of());
        conflictDetectionService.setNextConflicts(List.of(conflict));

        CandidateActionDto actionA = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 100L, "T1", null, null,
                "Loop", 1L, null, 10, "Hold T1");
        CandidateActionDto actionB = new CandidateActionDto(
                key, ActionType.DELAY_DEPARTURE, 100L, "T1", null, null,
                "Origin", null, null, 15, "Delay T1");

        candidateGenerationService.putCandidates(key, List.of(actionA, actionB));

        // Both are feasible
        constraintValidationService.putValidationResults(key, List.of(
                new CandidateValidationResultDto(actionA, true, null, null, List.of()),
                new CandidateValidationResultDto(actionB, true, null, null, List.of())
        ));

        // Action A scores 80.0, Action B scores 95.0
        decisionScoringService.putScore(actionA, 80.0);
        decisionScoringService.putScore(actionB, 95.0);

        List<DecisionDto> result = sut.optimize();

        assertThat(result).hasSize(1);
        DecisionDto decision = result.get(0);
        assertThat(decision.recommendedAction()).isEqualTo(ActionType.DELAY_DEPARTURE);
        assertThat(decision.score()).isEqualTo(95.0);
        assertThat(decision.reason()).isEqualTo("Delay T1");
        assertThat(decision.estimatedDelayMinutes()).isEqualTo(15);
    }

    // =======================================================================
    // Test 2: Infeasible candidate is never selected even if its score would otherwise be high
    // =======================================================================
    @Test
    @DisplayName("Infeasible candidate is never selected even if its score is highest")
    void test2_infeasibleCandidateNeverSelected() {
        ConflictDto conflict = mockConflict(1, ConflictSeverity.HIGH);
        String key = ConflictKey.from(conflict).asString();

        occupancyService.setNextTimelines(List.of());
        conflictDetectionService.setNextConflicts(List.of(conflict));

        CandidateActionDto actionA = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 100L, "T1", null, null,
                "Loop", 1L, null, 10, "Hold T1");
        CandidateActionDto actionB = new CandidateActionDto(
                key, ActionType.DELAY_DEPARTURE, 100L, "T1", null, null,
                "Origin", null, null, 15, "Delay T1");

        candidateGenerationService.putCandidates(key, List.of(actionA, actionB));

        // Action A is infeasible, Action B is feasible
        constraintValidationService.putValidationResults(key, List.of(
                new CandidateValidationResultDto(actionA, false, "Loop blocked", null, List.of()),
                new CandidateValidationResultDto(actionB, true, null, null, List.of())
        ));

        // Action A has high score, Action B has medium score
        decisionScoringService.putScore(actionA, 100.0);
        decisionScoringService.putScore(actionB, 50.0);

        List<DecisionDto> result = sut.optimize();

        assertThat(result).hasSize(1);
        DecisionDto decision = result.get(0);
        // Should choose B since A is infeasible
        assertThat(decision.recommendedAction()).isEqualTo(ActionType.DELAY_DEPARTURE);
        assertThat(decision.score()).isEqualTo(50.0);
    }

    // =======================================================================
    // Test 3: Candidate with serious new conflicts is not selected over a safe candidate
    // =======================================================================
    @Test
    @DisplayName("Candidate with serious new conflicts (infeasible) is not selected over a safe candidate")
    void test3_seriousNewConflictsNotSelected() {
        ConflictDto conflict = mockConflict(1, ConflictSeverity.HIGH);
        String key = ConflictKey.from(conflict).asString();

        occupancyService.setNextTimelines(List.of());
        conflictDetectionService.setNextConflicts(List.of(conflict));

        CandidateActionDto actionUnsafe = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 100L, "T1", null, null,
                "Loop", 1L, null, 5, "Unsafe hold");
        CandidateActionDto actionSafe = new CandidateActionDto(
                key, ActionType.DELAY_DEPARTURE, 100L, "T1", null, null,
                "Origin", null, null, 15, "Safe delay");

        candidateGenerationService.putCandidates(key, List.of(actionUnsafe, actionSafe));

        // Unsafe has serious new conflicts -> feasible = false, Safe is feasible = true
        constraintValidationService.putValidationResults(key, List.of(
                new CandidateValidationResultDto(actionUnsafe, false, "Creates new HEAD_ON conflict", null, List.of(conflict)),
                new CandidateValidationResultDto(actionSafe, true, null, null, List.of())
        ));

        // Scored values
        decisionScoringService.putScore(actionUnsafe, 0.0);
        decisionScoringService.putScore(actionSafe, 60.0);

        List<DecisionDto> result = sut.optimize();

        assertThat(result).hasSize(1);
        DecisionDto decision = result.get(0);
        assertThat(decision.recommendedAction()).isEqualTo(ActionType.DELAY_DEPARTURE);
        assertThat(decision.score()).isEqualTo(60.0);
    }

    // =======================================================================
    // Test 4: Multiple conflicts produce multiple decisions
    // =======================================================================
    @Test
    @DisplayName("Multiple conflicts produce multiple decisions")
    void test4_multipleConflictsProduceMultipleDecisions() {
        ConflictDto conflict1 = mockConflict(1, ConflictSeverity.HIGH, LocalTime.of(8, 0));
        String key1 = ConflictKey.from(conflict1).asString();

        ConflictDto conflict2 = mockConflict(2, ConflictSeverity.MEDIUM, LocalTime.of(10, 0));
        String key2 = ConflictKey.from(conflict2).asString();

        occupancyService.setNextTimelines(List.of());
        conflictDetectionService.setNextConflicts(List.of(conflict1, conflict2));

        CandidateActionDto action1 = new CandidateActionDto(
                key1, ActionType.HOLD_AT_LOOP, 100L, "T1", null, null,
                "Loop", 1L, null, 10, "Hold T1");
        CandidateActionDto action2 = new CandidateActionDto(
                key2, ActionType.REASSIGN_PLATFORM, 101L, "T2", null, null,
                "Platform 2", null, 2L, 0, "Move T2");

        candidateGenerationService.putCandidates(key1, List.of(action1));
        candidateGenerationService.putCandidates(key2, List.of(action2));

        constraintValidationService.putValidationResults(key1, List.of(
                new CandidateValidationResultDto(action1, true, null, null, List.of())
        ));
        constraintValidationService.putValidationResults(key2, List.of(
                new CandidateValidationResultDto(action2, true, null, null, List.of())
        ));

        decisionScoringService.putScore(action1, 70.0);
        decisionScoringService.putScore(action2, 85.0);

        List<DecisionDto> result = sut.optimize();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).conflict().id()).isEqualTo(1);
        assertThat(result.get(0).recommendedAction()).isEqualTo(ActionType.HOLD_AT_LOOP);

        assertThat(result.get(1).conflict().id()).isEqualTo(2);
        assertThat(result.get(1).recommendedAction()).isEqualTo(ActionType.REASSIGN_PLATFORM);
    }

    // =======================================================================
    // Test 5: No feasible candidate produces a clear 'no feasible resolution' result
    // =======================================================================
    @Test
    @DisplayName("No feasible candidate produces a clear 'no feasible resolution' result")
    void test5_noFeasibleResolutionResult() {
        ConflictDto conflict = mockConflict(1, ConflictSeverity.CRITICAL);
        String key = ConflictKey.from(conflict).asString();

        occupancyService.setNextTimelines(List.of());
        conflictDetectionService.setNextConflicts(List.of(conflict));

        CandidateActionDto action = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 100L, "T1", null, null,
                "Loop", 1L, null, 10, "Hold T1");

        candidateGenerationService.putCandidates(key, List.of(action));

        // Candidate is infeasible
        constraintValidationService.putValidationResults(key, List.of(
                new CandidateValidationResultDto(action, false, "Platform full", null, List.of())
        ));

        decisionScoringService.putScore(action, 0.0);

        List<DecisionDto> result = sut.optimize();

        assertThat(result).hasSize(1);
        DecisionDto decision = result.get(0);
        assertThat(decision.recommendedAction()).isNull();
        assertThat(decision.score()).isEqualTo(0.0);
        assertThat(decision.reason()).isEqualTo("No feasible resolution was found.");
        assertThat(decision.alternatives()).hasSize(1);
        assertThat(decision.alternatives().get(0).feasible()).isFalse();
        assertThat(decision.alternatives().get(0).rejectionReason()).isEqualTo("Platform full");
    }

    // =======================================================================
    // Stub Classes
    // =======================================================================

    private static class StubOccupancyService extends OccupancyService {
        private List<TrainTimeline> nextTimelines = List.of();

        public StubOccupancyService() {
            super(null, null, null, null);
        }

        public void setNextTimelines(List<TrainTimeline> timelines) {
            this.nextTimelines = timelines;
        }

        @Override
        public List<TrainTimeline> buildAllTimelines() {
            return nextTimelines;
        }
    }

    private static class StubConflictDetectionService extends ConflictDetectionService {
        private List<ConflictDto> nextConflicts = List.of();

        public StubConflictDetectionService() {
            super(null);
        }

        public void setNextConflicts(List<ConflictDto> conflicts) {
            this.nextConflicts = conflicts;
        }

        @Override
        public List<ConflictDto> detectConflicts(List<TrainTimeline> timelines) {
            return nextConflicts;
        }
    }

    private static class StubConflictContextBuilder extends ConflictContextBuilder {
        public StubConflictContextBuilder() {
            super(null, null);
        }

        @Override
        public ConflictContext build(ConflictDto conflict, List<TrainTimeline> timelines) {
            return new ConflictContext(conflict, ConflictKey.from(conflict).asString(),
                    null, null, 0, null, null, 0,
                    List.of(), null, List.of());
        }
    }

    private static class StubCandidateGenerationService extends CandidateGenerationService {
        private final Map<String, List<CandidateActionDto>> candidatesMap = new HashMap<>();

        public void putCandidates(String key, List<CandidateActionDto> list) {
            candidatesMap.put(key, list);
        }

        @Override
        public List<CandidateActionDto> generate(ConflictContext ctx) {
            return candidatesMap.getOrDefault(ctx.conflictKey(), List.of());
        }
    }

    private static class StubConstraintValidationService extends ConstraintValidationService {
        private final Map<String, List<CandidateValidationResultDto>> validationMap = new HashMap<>();

        public StubConstraintValidationService() {
            super(null, null, null, null, null, null);
        }

        public void putValidationResults(String key, List<CandidateValidationResultDto> list) {
            validationMap.put(key, list);
        }

        @Override
        public List<CandidateValidationResultDto> validateAll(List<CandidateActionDto> candidates,
                                                              List<ConflictDto> baselineConflicts,
                                                              List<TrainTimeline> allTimelines) {
            List<CandidateValidationResultDto> results = new ArrayList<>();
            for (CandidateActionDto candidate : candidates) {
                List<CandidateValidationResultDto> expectedList = validationMap.get(candidate.conflictKey());
                if (expectedList != null) {
                    for (CandidateValidationResultDto res : expectedList) {
                        if (res.candidate().equals(candidate)) {
                            results.add(res);
                            break;
                        }
                    }
                }
            }
            return results;
        }
    }

    private static class StubDecisionScoringService extends DecisionScoringService {
        private final Map<CandidateActionDto, Double> scoreMap = new HashMap<>();

        public StubDecisionScoringService() {
            super(null);
        }

        public void putScore(CandidateActionDto candidate, double score) {
            scoreMap.put(candidate, score);
        }

        @Override
        public CandidateScoreDto score(CandidateValidationResultDto result, List<ConflictDto> baselineConflicts) {
            double score = scoreMap.getOrDefault(result.candidate(), 0.0);
            return new CandidateScoreDto(
                    result.candidate(),
                    score,
                    Map.of("score", score),
                    result.feasible(),
                    result.rejectionReason()
            );
        }

        @Override
        public List<CandidateScoreDto> scoreAll(List<CandidateValidationResultDto> validationResults,
                                                List<ConflictDto> baselineConflicts) {
            List<CandidateScoreDto> scores = new ArrayList<>();
            for (CandidateValidationResultDto res : validationResults) {
                scores.add(score(res, baselineConflicts));
            }
            return scores;
        }
    }

    private static ConflictDto mockConflict(int id, ConflictSeverity severity) {
        return mockConflict(id, severity, LocalTime.of(8, 0));
    }

    private static ConflictDto mockConflict(int id, ConflictSeverity severity, LocalTime start) {
        return new ConflictDto(
                id,
                ConflictType.HEAD_ON_SINGLE_LINE,
                severity,
                10L,
                "Section Label",
                null,
                null,
                100L,
                "T1",
                101L,
                "T2",
                start,
                start.plusHours(1),
                "Explanation"
        );
    }
}
