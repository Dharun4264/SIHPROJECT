package com.sih.traffic.service.decision;

import com.sih.traffic.domain.*;
import com.sih.traffic.domain.enums.*;
import com.sih.traffic.dto.CandidateActionDto;
import com.sih.traffic.dto.CandidateValidationResultDto;
import com.sih.traffic.dto.ConflictDto;
import com.sih.traffic.repository.*;
import com.sih.traffic.service.conflict.ConflictDetectionService;
import com.sih.traffic.service.simulation.HaltInterval;
import com.sih.traffic.service.simulation.TrainTimeline;
import com.sih.traffic.service.simulation.TrainTimelineBuilder;
import com.sih.traffic.service.simulation.TransitInterval;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ConstraintValidationService (Phase 3 Step 2).
 * All tests use plain in-memory objects and fakes/stubs to avoid Mockito's
 * JDK 25 class modification limitations.
 */
class ConstraintValidationServiceTest {

    private ConstraintValidationService sut;

    // Fakes and Stubs
    private LoopLineRepository loopLineRepository;
    private PlatformRepository platformRepository;
    private TrainRepository trainRepository;
    private TrainScheduleRepository trainScheduleRepository;
    private TrackSectionRepository trackSectionRepository;

    private StubConflictDetectionService conflictDetectionService;
    private StubTrainTimelineBuilder timelineBuilder;

    // In-memory data stores for repositories
    private Map<Long, LoopLine> loopLineMap;
    private Map<Long, List<LoopLine>> loopLinesByStationMap;
    private Map<Long, Platform> platformMap;
    private Map<Long, List<Platform>> platformsByStationMap;
    private Map<Long, Train> trainMap;
    private Map<Long, List<TrainSchedule>> trainScheduleMap;

    // Shared domain fixtures
    private Station stationKRR;
    private Station stationED;
    private TrackSection singleSection;   // SINGLE-track KRR -> ED
    private Train trainA;
    private Train trainB;
    private Platform platformP1;
    private Platform platformP2;
    private LoopLine loopLine;

    @BeforeEach
    void setUp() {
        loopLineMap = new HashMap<>();
        loopLinesByStationMap = new HashMap<>();
        platformMap = new HashMap<>();
        platformsByStationMap = new HashMap<>();
        trainMap = new HashMap<>();
        trainScheduleMap = new HashMap<>();

        // Create proxies to avoid JDK 25 Mockito issues
        loopLineRepository = mockRepo(LoopLineRepository.class, (proxy, method, args) -> {
            if (method.getName().equals("findById")) {
                return Optional.ofNullable(loopLineMap.get(args[0]));
            }
            if (method.getName().equals("findByStationId")) {
                return loopLinesByStationMap.getOrDefault(args[0], List.of());
            }
            return null;
        });

        platformRepository = mockRepo(PlatformRepository.class, (proxy, method, args) -> {
            if (method.getName().equals("findById")) {
                return Optional.ofNullable(platformMap.get(args[0]));
            }
            if (method.getName().equals("findByStationId")) {
                return platformsByStationMap.getOrDefault(args[0], List.of());
            }
            return null;
        });

        trainRepository = mockRepo(TrainRepository.class, (proxy, method, args) -> {
            if (method.getName().equals("findById")) {
                return Optional.ofNullable(trainMap.get(args[0]));
            }
            return null;
        });

        trainScheduleRepository = mockRepo(TrainScheduleRepository.class, (proxy, method, args) -> {
            if (method.getName().equals("findByTrainIdOrderBySequenceNoAsc")) {
                return trainScheduleMap.getOrDefault(args[0], List.of());
            }
            return null;
        });

        trackSectionRepository = mockRepo(TrackSectionRepository.class, (proxy, method, args) -> {
            if (method.getName().equals("findByFromStationIdAndToStationId")) {
                return Optional.of(singleSection);
            }
            return null;
        });

        conflictDetectionService = new StubConflictDetectionService(loopLineRepository);
        timelineBuilder = new StubTrainTimelineBuilder(trackSectionRepository);

        sut = new ConstraintValidationService(
                loopLineRepository,
                platformRepository,
                trainRepository,
                trainScheduleRepository,
                timelineBuilder,
                conflictDetectionService
        );

        stationKRR = station(1L, "KRR");
        stationED  = station(2L, "ED");

        singleSection = trackSection(10L, stationKRR, stationED, SectionType.SINGLE);

        trainA = train(100L, "12345", TrainType.EXPRESS, 200.0);
        trainB = train(101L, "67890", TrainType.PASSENGER, 150.0);

        platformP1 = platform(20L, stationED, "1", Set.of(TrainType.EXPRESS, TrainType.PASSENGER));
        platformP2 = platform(21L, stationED, "2", Set.of(TrainType.EXPRESS, TrainType.PASSENGER));

        loopLine = loopLine(30L, stationKRR, "KRR-L1", 300.0);

        // Pre-populate common entities
        loopLineMap.put(30L, loopLine);
        loopLinesByStationMap.put(1L, List.of(loopLine));

        platformMap.put(20L, platformP1);
        platformMap.put(21L, platformP2);
        platformsByStationMap.put(2L, List.of(platformP1, platformP2));

        trainMap.put(100L, trainA);
        trainMap.put(101L, trainB);
    }

    // =======================================================================
    // Test 1 – HEAD_ON_SINGLE_LINE: HOLD_AT_LOOP is feasible when the loop
    //           resolves the conflict (hypothetical run is clean)
    // =======================================================================
    @Test
    @DisplayName("HEAD_ON_SINGLE_LINE: HOLD_AT_LOOP accepted when hypothetical run is conflict-free")
    void test1_headOnSingleLine_holdAtLoop_feasible() {
        TransitInterval transitA = transit(stationKRR, stationED, singleSection,
                LocalTime.of(8, 0), LocalTime.of(9, 0));
        TransitInterval transitB = transit(stationED, stationKRR, singleSection,
                LocalTime.of(8, 30), LocalTime.of(9, 30));

        TrainTimeline tlA = timeline(trainA, 0, List.of(), List.of(transitA));
        TrainTimeline tlB = timeline(trainB, 0, List.of(), List.of(transitB));
        List<TrainTimeline> all = List.of(tlA, tlB);

        ConflictDto conflict = sectionConflict(1, ConflictType.HEAD_ON_SINGLE_LINE,
                ConflictSeverity.HIGH, 10L, trainA, trainB,
                LocalTime.of(8, 30), LocalTime.of(9, 0));
        List<ConflictDto> baseline = List.of(conflict);

        CandidateActionDto candidate = new CandidateActionDto(
                ConflictKey.from(conflict).asString(),
                ActionType.HOLD_AT_LOOP,
                100L, "12345", 101L, "67890",
                "KRR loop KRR-L1", 30L, null, 30, "Hold 12345 at KRR-L1");

        TrainTimeline tlADelayed = timeline(trainA, 30, List.of(),
                List.of(transit(stationKRR, stationED, singleSection,
                        LocalTime.of(8, 30), LocalTime.of(9, 30))));
        
        timelineBuilder.addOverride(30, tlADelayed);
        conflictDetectionService.setNextConflicts(List.of());

        CandidateValidationResultDto result = sut.validate(candidate, baseline, all);

        assertThat(result.feasible()).isTrue();
        assertThat(result.rejectionReason()).isNull();
        assertThat(result.newlyCreatedConflicts()).isEmpty();
        assertThat(result.remainingTargetConflict()).isNull();
    }

    // =======================================================================
    // Test 2 – SAME_DIRECTION_OVERLAP: HOLD_AT_LOOP is feasible
    // =======================================================================
    @Test
    @DisplayName("SAME_DIRECTION_OVERLAP: HOLD_AT_LOOP accepted when hypothetical run is conflict-free")
    void test2_sameDirectionOverlap_holdAtLoop_feasible() {
        TransitInterval transitA = transit(stationKRR, stationED, singleSection,
                LocalTime.of(10, 0), LocalTime.of(11, 0));
        TransitInterval transitB = transit(stationKRR, stationED, singleSection,
                LocalTime.of(10, 20), LocalTime.of(11, 20));

        TrainTimeline tlA = timeline(trainA, 0, List.of(), List.of(transitA));
        TrainTimeline tlB = timeline(trainB, 0, List.of(), List.of(transitB));
        List<TrainTimeline> all = List.of(tlA, tlB);

        ConflictDto conflict = sectionConflict(1, ConflictType.SAME_DIRECTION_OVERLAP,
                ConflictSeverity.MEDIUM, 10L, trainA, trainB,
                LocalTime.of(10, 20), LocalTime.of(11, 0));
        List<ConflictDto> baseline = List.of(conflict);

        CandidateActionDto candidate = new CandidateActionDto(
                ConflictKey.from(conflict).asString(),
                ActionType.HOLD_AT_LOOP,
                101L, "67890", 100L, "12345",
                "KRR loop KRR-L1", 30L, null, 20, "Hold 67890 at KRR-L1");

        TrainTimeline tlBDelayed = timeline(trainB, 20, List.of(),
                List.of(transit(stationKRR, stationED, singleSection,
                        LocalTime.of(10, 40), LocalTime.of(11, 40))));
        
        timelineBuilder.addOverride(20, tlBDelayed);
        conflictDetectionService.setNextConflicts(List.of());

        CandidateValidationResultDto result = sut.validate(candidate, baseline, all);

        assertThat(result.feasible()).isTrue();
    }

    // =======================================================================
    // Test 3 – PLATFORM_OVERLAP: REASSIGN_PLATFORM resolves conflict
    // =======================================================================
    @Test
    @DisplayName("PLATFORM_OVERLAP: REASSIGN_PLATFORM to free platform is feasible")
    void test3_platformOverlap_reassignPlatform_feasible() {
        HaltInterval haltA = halt(stationED, LocalTime.of(12, 0), LocalTime.of(12, 30), platformP1);
        HaltInterval haltB = halt(stationED, LocalTime.of(12, 15), LocalTime.of(12, 45), platformP1);

        TrainTimeline tlA = timeline(trainA, 0, List.of(haltA), List.of());
        TrainTimeline tlB = timeline(trainB, 0, List.of(haltB), List.of());
        List<TrainTimeline> all = List.of(tlA, tlB);

        ConflictDto conflict = platformConflict(1, ConflictType.PLATFORM_OVERLAP,
                ConflictSeverity.MEDIUM, 20L, trainA, trainB,
                LocalTime.of(12, 15), LocalTime.of(12, 30));
        List<ConflictDto> baseline = List.of(conflict);

        CandidateActionDto candidate = new CandidateActionDto(
                ConflictKey.from(conflict).asString(),
                ActionType.REASSIGN_PLATFORM,
                100L, "12345", 101L, "67890",
                "ED platform 2", null, 21L, 0, "Move 12345 to platform 2");

        conflictDetectionService.setNextConflicts(List.of());

        CandidateValidationResultDto result = sut.validate(candidate, baseline, all);

        assertThat(result.feasible()).isTrue();
        assertThat(result.newlyCreatedConflicts()).isEmpty();
    }

    // =======================================================================
    // Test 4 – PLATFORM_TYPE_INCOMPATIBLE: incompatible platform rejected
    // =======================================================================
    @Test
    @DisplayName("PLATFORM_TYPE_INCOMPATIBLE: REASSIGN_PLATFORM rejected when new platform is incompatible")
    void test4_platformTypeIncompatible_incompatiblePlatform_rejected() {
        Platform freightOnlyPlatform = platform(22L, stationED, "3", Set.of(TrainType.FREIGHT));
        platformMap.put(22L, freightOnlyPlatform);

        HaltInterval haltA = halt(stationED, LocalTime.of(13, 0), LocalTime.of(13, 30), platformP1);
        TrainTimeline tlA = timeline(trainA, 0, List.of(haltA), List.of());
        List<TrainTimeline> all = List.of(tlA);

        ConflictDto conflict = new ConflictDto(1, ConflictType.PLATFORM_TYPE_INCOMPATIBLE,
                ConflictSeverity.HIGH, null, null, 20L, "ED platform 1",
                100L, "12345", null, null,
                LocalTime.of(13, 0), LocalTime.of(13, 30), "Incompatible");
        List<ConflictDto> baseline = List.of(conflict);

        CandidateActionDto candidate = new CandidateActionDto(
                ConflictKey.from(conflict).asString(),
                ActionType.REASSIGN_PLATFORM,
                100L, "12345", null, null,
                "ED platform 3", null, 22L, 0, "Move 12345 to platform 3");

        CandidateValidationResultDto result = sut.validate(candidate, baseline, all);

        assertThat(result.feasible()).isFalse();
        assertThat(result.rejectionReason()).contains("EXPRESS");
    }

    // =======================================================================
    // Test 5 – Hypothetical delay creates a new serious conflict → rejected
    // =======================================================================
    @Test
    @DisplayName("DELAY_DEPARTURE: rejected when hypothetical run creates a new HIGH conflict")
    void test5_delayDeparture_createsNewSeriousConflict_rejected() {
        TransitInterval transitA = transit(stationKRR, stationED, singleSection,
                LocalTime.of(9, 0), LocalTime.of(10, 0));
        TrainTimeline tlA = timeline(trainA, 0, List.of(), List.of(transitA));

        Train trainC = train(102L, "11111", TrainType.PASSENGER, 120.0);
        TransitInterval transitC = transit(stationED, stationKRR, singleSection,
                LocalTime.of(9, 30), LocalTime.of(10, 30));
        TrainTimeline tlC = timeline(trainC, 0, List.of(), List.of(transitC));

        List<TrainTimeline> all = List.of(tlA, tlC);

        ConflictDto originalConflict = sectionConflict(1, ConflictType.HEAD_ON_SINGLE_LINE,
                ConflictSeverity.HIGH, 10L, trainA, trainB,
                LocalTime.of(9, 0), LocalTime.of(9, 30));
        List<ConflictDto> baseline = List.of(originalConflict);

        CandidateActionDto candidate = new CandidateActionDto(
                ConflictKey.from(originalConflict).asString(),
                ActionType.DELAY_DEPARTURE,
                100L, "12345", 101L, "67890",
                "(no loop line - hold at origin)", null, null, 30,
                "Delay 12345 departure by 30 min");

        TrainTimeline tlADelayed = timeline(trainA, 30, List.of(),
                List.of(transit(stationKRR, stationED, singleSection,
                        LocalTime.of(9, 30), LocalTime.of(10, 30))));
        
        timelineBuilder.addOverride(30, tlADelayed);

        ConflictDto newConflict = sectionConflict(2, ConflictType.HEAD_ON_SINGLE_LINE,
                ConflictSeverity.HIGH, 10L, trainA, trainC,
                LocalTime.of(9, 30), LocalTime.of(10, 30));
        conflictDetectionService.setNextConflicts(List.of(newConflict));

        CandidateValidationResultDto result = sut.validate(candidate, baseline, all);

        assertThat(result.feasible()).isFalse();
        assertThat(result.newlyCreatedConflicts()).hasSize(1);
        assertThat(result.newlyCreatedConflicts().get(0).type())
                .isEqualTo(ConflictType.HEAD_ON_SINGLE_LINE);
    }

    // =======================================================================
    // Test 6 – No loop available → DELAY_DEPARTURE fallback is validated
    //          and accepted when it resolves the conflict cleanly
    // =======================================================================
    @Test
    @DisplayName("No loop available: DELAY_DEPARTURE accepted when hypothetical run is conflict-free")
    void test6_noLoopAvailable_delayDeparture_accepted() {
        TransitInterval transitA = transit(stationKRR, stationED, singleSection,
                LocalTime.of(14, 0), LocalTime.of(15, 0));
        TransitInterval transitB = transit(stationED, stationKRR, singleSection,
                LocalTime.of(14, 20), LocalTime.of(15, 20));

        TrainTimeline tlA = timeline(trainA, 0, List.of(), List.of(transitA));
        TrainTimeline tlB = timeline(trainB, 0, List.of(), List.of(transitB));
        List<TrainTimeline> all = List.of(tlA, tlB);

        ConflictDto conflict = sectionConflict(1, ConflictType.HEAD_ON_SINGLE_LINE,
                ConflictSeverity.CRITICAL, 10L, trainA, trainB,
                LocalTime.of(14, 20), LocalTime.of(15, 0));
        List<ConflictDto> baseline = List.of(conflict);

        CandidateActionDto candidate = new CandidateActionDto(
                ConflictKey.from(conflict).asString(),
                ActionType.DELAY_DEPARTURE,
                100L, "12345", 101L, "67890",
                "(no loop line - hold at origin)", null, null, 40,
                "Delay 12345 departure by 40 min (no loop available)");

        TrainTimeline tlADelayed = timeline(trainA, 40, List.of(),
                List.of(transit(stationKRR, stationED, singleSection,
                        LocalTime.of(14, 40), LocalTime.of(15, 40))));
        
        timelineBuilder.addOverride(40, tlADelayed);
        conflictDetectionService.setNextConflicts(List.of());

        CandidateValidationResultDto result = sut.validate(candidate, baseline, all);

        assertThat(result.feasible()).isTrue();
        assertThat(result.rejectionReason()).isNull();
        assertThat(result.newlyCreatedConflicts()).isEmpty();
    }

    // =======================================================================
    // Stub / Fake helper classes
    // =======================================================================

    private static class StubConflictDetectionService extends ConflictDetectionService {
        private List<ConflictDto> nextConflicts = List.of();

        public StubConflictDetectionService(LoopLineRepository loopLineRepository) {
            super(loopLineRepository);
        }

        public void setNextConflicts(List<ConflictDto> conflicts) {
            this.nextConflicts = conflicts;
        }

        @Override
        public List<ConflictDto> detectConflicts(List<TrainTimeline> timelines) {
            return nextConflicts;
        }
    }

    private static class StubTrainTimelineBuilder extends TrainTimelineBuilder {
        private final Map<Integer, TrainTimeline> overrides = new HashMap<>();

        public StubTrainTimelineBuilder(TrackSectionRepository trackSectionRepository) {
            super(trackSectionRepository);
        }

        public void addOverride(int delayMinutes, TrainTimeline timeline) {
            overrides.put(delayMinutes, timeline);
        }

        @Override
        public TrainTimeline build(Train train, List<TrainSchedule> orderedStops, int delayMinutes) {
            if (overrides.containsKey(delayMinutes)) {
                return overrides.get(delayMinutes);
            }
            return super.build(train, orderedStops, delayMinutes);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T mockRepo(Class<T> repoInterface, java.lang.reflect.InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class<?>[]{repoInterface},
                handler
        );
    }

    // =======================================================================
    // Domain fixture builders
    // =======================================================================

    private static Station station(Long id, String code) {
        Station s = new Station();
        s.setId(id);
        s.setCode(code);
        s.setName(code + " Station");
        s.setLatitude(10.0);
        s.setLongitude(77.0);
        s.setStationType(StationType.JUNCTION);
        return s;
    }

    private static TrackSection trackSection(Long id, Station from, Station to, SectionType type) {
        TrackSection ts = new TrackSection();
        ts.setId(id);
        ts.setFromStation(from);
        ts.setToStation(to);
        ts.setSectionType(type);
        ts.setLengthKm(50.0);
        ts.setMaxSpeedKmph(120);
        return ts;
    }

    private static Train train(Long id, String number, TrainType type, double lengthM) {
        Train t = new Train();
        t.setId(id);
        t.setTrainNumber(number);
        t.setName("Train " + number);
        t.setType(type);
        t.setPriority(5);
        t.setMaxSpeedKmph(120);
        t.setLengthM(lengthM);
        return t;
    }

    private static Platform platform(Long id, Station station, String number, Set<TrainType> compatible) {
        Platform p = new Platform();
        p.setId(id);
        p.setStation(station);
        p.setPlatformNumber(number);
        p.setLengthM(300.0);
        p.setCompatibleTrainTypes(new HashSet<>(compatible));
        return p;
    }

    private static LoopLine loopLine(Long id, Station station, String code, double maxLengthM) {
        LoopLine l = new LoopLine();
        l.setId(id);
        l.setStation(station);
        l.setLoopCode(code);
        l.setMaxLengthM(maxLengthM);
        return l;
    }

    private static TrainTimeline timeline(Train train, int delay,
                                          List<HaltInterval> halts,
                                          List<TransitInterval> transits) {
        return new TrainTimeline(train, delay, halts, transits);
    }

    private static TransitInterval transit(Station from, Station to, TrackSection section,
                                           LocalTime start, LocalTime end) {
        return new TransitInterval(from, to, section, start, end);
    }

    private static HaltInterval halt(Station station, LocalTime arrival, LocalTime departure,
                                     Platform platform) {
        return new HaltInterval(station, arrival, departure, platform, false, false);
    }

    private static ConflictDto sectionConflict(int id, ConflictType type, ConflictSeverity severity,
                                                Long sectionId, Train a, Train b,
                                                LocalTime start, LocalTime end) {
        String label = (a != null ? a.getTrainNumber() : "?") + " vs " + (b != null ? b.getTrainNumber() : "?");
        return new ConflictDto(id, type, severity, sectionId, label, null, null,
                a != null ? a.getId() : null, a != null ? a.getTrainNumber() : null,
                b != null ? b.getId() : null, b != null ? b.getTrainNumber() : null,
                start, end, "Test conflict");
    }

    private static ConflictDto platformConflict(int id, ConflictType type, ConflictSeverity severity,
                                                 Long platformId, Train a, Train b,
                                                 LocalTime start, LocalTime end) {
        return new ConflictDto(id, type, severity, null, null, platformId, "Platform " + platformId,
                a != null ? a.getId() : null, a != null ? a.getTrainNumber() : null,
                b != null ? b.getId() : null, b != null ? b.getTrainNumber() : null,
                start, end, "Platform test conflict");
    }
}
