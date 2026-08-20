package com.sih.traffic.controller;

import com.sih.traffic.dto.*;
import com.sih.traffic.service.conflict.ConflictDetectionService;
import com.sih.traffic.service.simulation.OccupancyService;
import com.sih.traffic.service.simulation.SimulationClockService;
import com.sih.traffic.service.simulation.TrainDelayService;
import com.sih.traffic.service.simulation.TrainRuntimeCalculator;
import com.sih.traffic.service.simulation.TrainTimeline;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationClockService clockService;
    private final TrainDelayService delayService;
    private final OccupancyService occupancyService;
    private final ConflictDetectionService conflictDetectionService;
    private final TrainRuntimeCalculator runtimeCalculator;

    public SimulationController(SimulationClockService clockService,
                                 TrainDelayService delayService,
                                 OccupancyService occupancyService,
                                 ConflictDetectionService conflictDetectionService,
                                 TrainRuntimeCalculator runtimeCalculator) {
        this.clockService = clockService;
        this.delayService = delayService;
        this.occupancyService = occupancyService;
        this.conflictDetectionService = conflictDetectionService;
        this.runtimeCalculator = runtimeCalculator;
    }

    // ------------------------------------------------------------------
    // 1. Simulation clock
    // ------------------------------------------------------------------

    @PostMapping("/start")
    public SimulationStatusResponse start() {
        clockService.start();
        return status();
    }

    @PostMapping("/pause")
    public SimulationStatusResponse pause() {
        clockService.pause();
        return status();
    }

    @PostMapping("/reset")
    public SimulationStatusResponse reset() {
        clockService.reset();
        return status();
    }

    @PostMapping("/speed")
    public SimulationStatusResponse setSpeed(@Valid @RequestBody SpeedRequest request) {
        clockService.setSpeedMultiplier(request.speedMultiplier());
        return status();
    }

    // ------------------------------------------------------------------
    // 6. Manual disruption
    // ------------------------------------------------------------------

    @PostMapping("/delay/{trainId}")
    public Map<String, Object> addDelay(@PathVariable Long trainId, @Valid @RequestBody DelayRequest request) {
        int totalDelay = delayService.addDelay(trainId, request.minutes());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("trainId", trainId);
        response.put("appliedMinutes", request.minutes());
        response.put("totalDelayMinutes", totalDelay);
        return response;
    }

    // ------------------------------------------------------------------
    // 7. Simulation state API
    // ------------------------------------------------------------------

    @GetMapping("/state")
    public SimulationStateResponse getState() {
        LocalTime now = clockService.getCurrentTime();
        List<TrainTimeline> timelines = occupancyService.buildAllTimelines();

        List<TrainStateDto> trainStates = timelines.stream()
                .map(tl -> runtimeCalculator.computeState(tl, now))
                .toList();

        List<SectionOccupancyDto> sections = occupancyService.currentSectionOccupancy(timelines, now);
        List<PlatformOccupancyDto> platforms = occupancyService.currentPlatformOccupancy(timelines, now);

        List<ConflictDto> activeConflicts = conflictDetectionService.detectConflicts(timelines).stream()
                .filter(c -> !now.isBefore(c.windowStart()) && now.isBefore(c.windowEnd()))
                .toList();

        return new SimulationStateResponse(
                now,
                clockService.getStatus().name(),
                clockService.getSpeedMultiplier(),
                trainStates,
                sections,
                platforms,
                activeConflicts);
    }

    // ------------------------------------------------------------------
    // 8. Conflict API - all conflicts across the full schedule horizon,
    // not just ones active right now (useful for advance warning).
    // ------------------------------------------------------------------

    @GetMapping("/conflicts")
    public List<ConflictDto> getConflicts() {
        List<TrainTimeline> timelines = occupancyService.buildAllTimelines();
        return conflictDetectionService.detectConflicts(timelines);
    }

    private SimulationStatusResponse status() {
        return new SimulationStatusResponse(
                clockService.getStatus().name(),
                clockService.getCurrentTime(),
                clockService.getSpeedMultiplier());
    }
}
