package com.sih.traffic.service.simulation;

import com.sih.traffic.exception.InvalidRequestException;
import com.sih.traffic.exception.ResourceNotFoundException;
import com.sih.traffic.repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds each train's cumulative delay (minutes) in memory for this
 * simulation run. Deliberately NOT persisted to the database - this is
 * Phase 2 MVP runtime state, reset via SimulationClockService.reset().
 * A delayed train has every one of its remaining scheduled times shifted
 * uniformly by this amount (see TrainTimelineBuilder).
 */
@Service
public class TrainDelayService {

    private final Map<Long, Integer> delayMinutesByTrainId = new ConcurrentHashMap<>();
    private final TrainRepository trainRepository;

    public TrainDelayService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    public int getDelayMinutes(Long trainId) {
        return delayMinutesByTrainId.getOrDefault(trainId, 0);
    }

    /**
     * Adds (or subtracts, if negative) minutes to a train's cumulative delay.
     * Result is clamped at 0 - a train cannot run "negative delay" in this
     * simple model. Returns the new total.
     */
    public int addDelay(Long trainId, int minutesToAdd) {
        if (!trainRepository.existsById(trainId)) {
            throw new ResourceNotFoundException("Train not found: id=" + trainId);
        }
        if (minutesToAdd == 0) {
            throw new InvalidRequestException("minutes must be a non-zero value");
        }
        return delayMinutesByTrainId.compute(trainId,
                (id, current) -> Math.max(0, (current == null ? 0 : current) + minutesToAdd));
    }

    public void clearAll() {
        delayMinutesByTrainId.clear();
    }
}
