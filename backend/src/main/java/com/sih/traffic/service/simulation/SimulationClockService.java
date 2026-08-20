package com.sih.traffic.service.simulation;

import com.sih.traffic.domain.enums.SimulationClockStatus;
import com.sih.traffic.exception.InvalidRequestException;
import com.sih.traffic.repository.TrainScheduleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

/**
 * Drives the simulated clock. One real-world second advances the simulated
 * clock by `speedMultiplier` simulated seconds (default 60 -> 1 sim-minute
 * per real second), only while RUNNING. All train state is derived
 * on-demand from this clock's currentTime (see OccupancyService /
 * TrainRuntimeCalculator) - nothing here mutates train data directly.
 */
@Service
public class SimulationClockService {

    private static final double MIN_SPEED_MULTIPLIER = 1.0;
    private static final double MAX_SPEED_MULTIPLIER = 3600.0; // 1 sim-hour per real second, upper sanity bound

    private final LocalTime defaultStartTime;
    private final TrainDelayService trainDelayService;

    private volatile SimulationClockStatus status = SimulationClockStatus.STOPPED;
    private volatile LocalTime currentTime;
    private volatile double speedMultiplier = 60.0;

    public SimulationClockService(TrainScheduleRepository trainScheduleRepository, TrainDelayService trainDelayService) {
        this.trainDelayService = trainDelayService;
        this.defaultStartTime = trainScheduleRepository.findAll().stream()
                .filter(s -> s.getSequenceNo() == 1 && s.getScheduledDeparture() != null)
                .map(s -> s.getScheduledDeparture())
                .min(LocalTime::compareTo)
                .orElse(LocalTime.MIDNIGHT);
        this.currentTime = defaultStartTime;
    }

    public synchronized void start() {
        if (status == SimulationClockStatus.STOPPED) {
            currentTime = defaultStartTime;
        }
        status = SimulationClockStatus.RUNNING;
    }

    public synchronized void pause() {
        if (status == SimulationClockStatus.RUNNING) {
            status = SimulationClockStatus.PAUSED;
        }
    }

    /** Stops the clock, rewinds to the network's earliest scheduled departure, and clears all injected delays. */
    public synchronized void reset() {
        status = SimulationClockStatus.STOPPED;
        currentTime = defaultStartTime;
        trainDelayService.clearAll();
    }

    public void setSpeedMultiplier(double multiplier) {
        if (multiplier < MIN_SPEED_MULTIPLIER || multiplier > MAX_SPEED_MULTIPLIER) {
            throw new InvalidRequestException(
                    "speedMultiplier must be between " + MIN_SPEED_MULTIPLIER + " and " + MAX_SPEED_MULTIPLIER);
        }
        this.speedMultiplier = multiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public SimulationClockStatus getStatus() {
        return status;
    }

    public LocalTime getCurrentTime() {
        return currentTime;
    }

    /**
     * Ticks once per real-world second. LocalTime wraps at 24h automatically;
     * for an MVP demo run this is an accepted simplification (see PROJECT_SPEC.md
     * assumptions) rather than modeling a multi-day calendar.
     */
    @Scheduled(fixedRate = 1000)
    public void tick() {
        if (status == SimulationClockStatus.RUNNING) {
            currentTime = currentTime.plusSeconds((long) speedMultiplier);
        }
    }
}
