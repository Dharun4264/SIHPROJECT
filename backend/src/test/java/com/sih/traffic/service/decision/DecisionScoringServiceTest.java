package com.sih.traffic.service.decision;

import com.sih.traffic.domain.Train;
import com.sih.traffic.domain.enums.ActionType;
import com.sih.traffic.domain.enums.ConflictSeverity;
import com.sih.traffic.domain.enums.ConflictType;
import com.sih.traffic.domain.enums.TrainType;
import com.sih.traffic.dto.CandidateActionDto;
import com.sih.traffic.dto.CandidateScoreDto;
import com.sih.traffic.dto.CandidateValidationResultDto;
import com.sih.traffic.dto.ConflictDto;
import com.sih.traffic.repository.TrainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DecisionScoringService (Phase 3 Step 3).
 * Uses plain in-memory objects and dynamic proxies to bypass JDK 25 Mockito issues.
 */
class DecisionScoringServiceTest {

    private DecisionScoringService sut;
    private TrainRepository trainRepository;
    private Map<Long, Train> trainMap;

    private Train trainHighPriority; // Priority 1
    private Train trainMediumPriority; // Priority 5
    private Train trainLowPriority; // Priority 10

    @BeforeEach
    void setUp() {
        trainMap = new HashMap<>();

        // Create proxy for TrainRepository to avoid JDK 25 Mockito issues
        trainRepository = mockRepo(TrainRepository.class, (proxy, method, args) -> {
            if (method.getName().equals("findById")) {
                return Optional.ofNullable(trainMap.get(args[0]));
            }
            return null;
        });

        sut = new DecisionScoringService(trainRepository);

        trainHighPriority = train(100L, "11111", TrainType.EXPRESS, 1);
        trainMediumPriority = train(101L, "22222", TrainType.EXPRESS, 5);
        trainLowPriority = train(102L, "33333", TrainType.EXPRESS, 10);

        trainMap.put(100L, trainHighPriority);
        trainMap.put(101L, trainMediumPriority);
        trainMap.put(102L, trainLowPriority);
    }

    // =======================================================================
    // Test 1: Feasible low-delay candidate scores higher than high-delay candidate
    // =======================================================================
    @Test
    @DisplayName("Feasible low-delay candidate scores higher than an otherwise similar high-delay candidate")
    void test1_lowDelayScoresHigherThanHighDelay() {
        ConflictDto conflict = mockConflict(ConflictSeverity.MEDIUM, 1);
        String key = ConflictKey.from(conflict).asString();
        List<ConflictDto> baseline = List.of(conflict);

        // Low delay candidate: 5 minutes delay
        CandidateActionDto actionLow = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 101L, "22222", null, null,
                "KRR-L1", 1L, null, 5, "Hold 5 mins");
        CandidateValidationResultDto resultLow = new CandidateValidationResultDto(
                actionLow, true, null, null, List.of());

        // High delay candidate: 25 minutes delay
        CandidateActionDto actionHigh = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 101L, "22222", null, null,
                "KRR-L1", 1L, null, 25, "Hold 25 mins");
        CandidateValidationResultDto resultHigh = new CandidateValidationResultDto(
                actionHigh, true, null, null, List.of());

        CandidateScoreDto scoreLow = sut.score(resultLow, baseline);
        CandidateScoreDto scoreHigh = sut.score(resultHigh, baseline);

        assertThat(scoreLow.feasible()).isTrue();
        assertThat(scoreHigh.feasible()).isTrue();
        assertThat(scoreLow.score()).isGreaterThan(scoreHigh.score());

        // Low delay penalty: 5 * 2 = 10
        // High delay penalty: 25 * 2 = 50
        assertThat(scoreLow.scoreBreakdown().get("delayPenalty")).isEqualTo(10.0);
        assertThat(scoreHigh.scoreBreakdown().get("delayPenalty")).isEqualTo(50.0);
    }

    // =======================================================================
    // Test 2: Candidate resolving HIGH severity scores appropriately
    // =======================================================================
    @Test
    @DisplayName("Candidate resolving HIGH severity scores appropriately higher than MEDIUM severity")
    void test2_highSeverityResolvingScoresAppropriately() {
        ConflictDto highConflict = mockConflict(ConflictSeverity.HIGH, 1, LocalTime.of(8, 0));
        String highKey = ConflictKey.from(highConflict).asString();

        ConflictDto medConflict = mockConflict(ConflictSeverity.MEDIUM, 2, LocalTime.of(10, 0));
        String medKey = ConflictKey.from(medConflict).asString();

        List<ConflictDto> baseline = List.of(highConflict, medConflict);

        CandidateActionDto actionHigh = new CandidateActionDto(
                highKey, ActionType.REASSIGN_PLATFORM, 101L, "22222", null, null,
                "Platform 1", null, 1L, 0, "Reassign platform");
        CandidateValidationResultDto resultHigh = new CandidateValidationResultDto(
                actionHigh, true, null, null, List.of());

        CandidateActionDto actionMed = new CandidateActionDto(
                medKey, ActionType.REASSIGN_PLATFORM, 101L, "22222", null, null,
                "Platform 1", null, 1L, 0, "Reassign platform");
        CandidateValidationResultDto resultMed = new CandidateValidationResultDto(
                actionMed, true, null, null, List.of());

        CandidateScoreDto scoreHigh = sut.score(resultHigh, baseline);
        CandidateScoreDto scoreMed = sut.score(resultMed, baseline);

        assertThat(scoreHigh.scoreBreakdown().get("severityBenefit")).isEqualTo(75.0);
        assertThat(scoreMed.scoreBreakdown().get("severityBenefit")).isEqualTo(50.0);
        assertThat(scoreHigh.score()).isGreaterThan(scoreMed.score());
    }

    // =======================================================================
    // Test 3: Higher-priority train/action receives appropriate priority benefit
    // =======================================================================
    @Test
    @DisplayName("Higher-priority train/action receives appropriate priority benefit")
    void test3_highPriorityTrainReceivesBenefit() {
        ConflictDto conflict = mockConflict(ConflictSeverity.MEDIUM, 1);
        String key = ConflictKey.from(conflict).asString();
        List<ConflictDto> baseline = List.of(conflict);

        // Target: trainHighPriority (Priority 1)
        CandidateActionDto actionHigh = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 100L, "11111", null, null,
                "KRR-L1", 1L, null, 10, "Hold");
        CandidateValidationResultDto resultHigh = new CandidateValidationResultDto(
                actionHigh, true, null, null, List.of());

        // Target: trainLowPriority (Priority 10)
        CandidateActionDto actionLow = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 102L, "33333", null, null,
                "KRR-L1", 1L, null, 10, "Hold");
        CandidateValidationResultDto resultLow = new CandidateValidationResultDto(
                actionLow, true, null, null, List.of());

        CandidateScoreDto scoreHigh = sut.score(resultHigh, baseline);
        CandidateScoreDto scoreLow = sut.score(resultLow, baseline);

        // Priority 1 benefit: (11 - 1) * 5 = 50.0
        // Priority 10 benefit: (11 - 10) * 5 = 5.0
        assertThat(scoreHigh.scoreBreakdown().get("priorityBenefit")).isEqualTo(50.0);
        assertThat(scoreLow.scoreBreakdown().get("priorityBenefit")).isEqualTo(5.0);
        assertThat(scoreHigh.score()).isGreaterThan(scoreLow.score());
    }

    // =======================================================================
    // Test 4: Candidate creating serious new conflicts cannot beat a safe candidate
    // =======================================================================
    @Test
    @DisplayName("Candidate creating serious new conflicts receives a very large penalty or cannot beat a safe candidate")
    void test4_newConflictsPenaltyExclusion() {
        ConflictDto conflict = mockConflict(ConflictSeverity.MEDIUM, 1);
        String key = ConflictKey.from(conflict).asString();
        List<ConflictDto> baseline = List.of(conflict);

        // Safe candidate: feasible, no new conflicts
        CandidateActionDto actionSafe = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 101L, "22222", null, null,
                "KRR-L1", 1L, null, 5, "Hold");
        CandidateValidationResultDto resultSafe = new CandidateValidationResultDto(
                actionSafe, true, null, null, List.of());

        // Unsafe candidate: has serious new conflicts -> marked infeasible by Step 2 validation
        CandidateActionDto actionUnsafe = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 101L, "22222", null, null,
                "KRR-L1", 1L, null, 5, "Hold");
        CandidateValidationResultDto resultUnsafe = new CandidateValidationResultDto(
                actionUnsafe, false, "Creates serious head-on conflicts", null, List.of(conflict));

        CandidateScoreDto scoreSafe = sut.score(resultSafe, baseline);
        CandidateScoreDto scoreUnsafe = sut.score(resultUnsafe, baseline);

        // Safe gets a positive score
        assertThat(scoreSafe.feasible()).isTrue();
        assertThat(scoreSafe.score()).isGreaterThan(0.0);

        // Unsafe gets 0.0 score and is marked infeasible
        assertThat(scoreUnsafe.feasible()).isFalse();
        assertThat(scoreUnsafe.score()).isEqualTo(0.0);
        assertThat(scoreUnsafe.rejectionReason()).isEqualTo("Creates serious head-on conflicts");
    }

    // =======================================================================
    // Test 5: Infeasible candidate is not treated as a recommended candidate
    // =======================================================================
    @Test
    @DisplayName("Infeasible candidate is scored as 0 and preserves rejection reason")
    void test5_infeasibleCandidateScoresZero() {
        ConflictDto conflict = mockConflict(ConflictSeverity.MEDIUM, 1);
        String key = ConflictKey.from(conflict).asString();
        List<ConflictDto> baseline = List.of(conflict);

        CandidateActionDto action = new CandidateActionDto(
                key, ActionType.HOLD_AT_LOOP, 101L, "22222", null, null,
                "KRR-L1", 1L, null, 10, "Hold");
        CandidateValidationResultDto result = new CandidateValidationResultDto(
                action, false, "Platform incompatible", null, List.of());

        CandidateScoreDto scoreDto = sut.score(result, baseline);

        assertThat(scoreDto.feasible()).isFalse();
        assertThat(scoreDto.score()).isEqualTo(0.0);
        assertThat(scoreDto.rejectionReason()).isEqualTo("Platform incompatible");
    }

    // =======================================================================
    // Test 6: Score breakdown is generated correctly
    // =======================================================================
    @Test
    @DisplayName("Score breakdown is generated correctly with all components")
    void test6_scoreBreakdownCorrectlyGenerated() {
        ConflictDto conflict = mockConflict(ConflictSeverity.CRITICAL, 1);
        String key = ConflictKey.from(conflict).asString();
        List<ConflictDto> baseline = List.of(conflict);

        // Critical conflict (100) + Priority 5 (30) + Reassign platform (30) - 10 min delay (20)
        CandidateActionDto action = new CandidateActionDto(
                key, ActionType.REASSIGN_PLATFORM, 101L, "22222", null, null,
                "Platform 1", null, 1L, 10, "Reassign");
        CandidateValidationResultDto result = new CandidateValidationResultDto(
                action, true, null, null, List.of());

        CandidateScoreDto scoreDto = sut.score(result, baseline);

        assertThat(scoreDto.feasible()).isTrue();
        // 100 (severity) + 30 (priority) + 30 (throughput) - 20 (delay) = 140
        assertThat(scoreDto.score()).isEqualTo(140.0);

        Map<String, Double> breakdown = scoreDto.scoreBreakdown();
        assertThat(breakdown).containsOnlyKeys(
                "severityBenefit", "priorityBenefit", "throughputBenefit", "delayPenalty", "newConflictPenalty"
        );
        assertThat(breakdown.get("severityBenefit")).isEqualTo(100.0);
        assertThat(breakdown.get("priorityBenefit")).isEqualTo(30.0);
        assertThat(breakdown.get("throughputBenefit")).isEqualTo(30.0);
        assertThat(breakdown.get("delayPenalty")).isEqualTo(20.0);
        assertThat(breakdown.get("newConflictPenalty")).isEqualTo(0.0);
    }

    // =======================================================================
    // Helpers
    // =======================================================================

    private static Train train(Long id, String number, TrainType type, int priority) {
        Train t = new Train();
        t.setId(id);
        t.setTrainNumber(number);
        t.setName("Train " + number);
        t.setType(type);
        t.setPriority(priority);
        t.setMaxSpeedKmph(120);
        t.setLengthM(250.0);
        return t;
    }

    private static ConflictDto mockConflict(ConflictSeverity severity, int id) {
        return mockConflict(severity, id, LocalTime.of(8, 0));
    }

    private static ConflictDto mockConflict(ConflictSeverity severity, int id, LocalTime start) {
        return new ConflictDto(
                id,
                ConflictType.HEAD_ON_SINGLE_LINE,
                severity,
                10L,
                "Section Label",
                null,
                null,
                100L,
                "11111",
                101L,
                "22222",
                start,
                start.plusHours(1),
                "Explanation"
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> T mockRepo(Class<T> repoInterface, java.lang.reflect.InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class<?>[]{repoInterface},
                handler
        );
    }
}
