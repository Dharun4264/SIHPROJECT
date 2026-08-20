package com.sih.traffic.service.simulation;

import com.sih.traffic.domain.Train;
import com.sih.traffic.domain.enums.TrainRunStatus;
import com.sih.traffic.dto.TrainStateDto;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/**
 * Stateless: given a TrainTimeline (already delay-adjusted) and a point in
 * simulated time, works out exactly where the train is and what it's doing.
 * No stepping/looping over ticks - each call is a direct lookup.
 */
@Component
public class TrainRuntimeCalculator {

    public TrainStateDto computeState(TrainTimeline timeline, LocalTime now) {
        Train train = timeline.train();
        List<HaltInterval> halts = timeline.halts();
        List<TransitInterval> transits = timeline.transits();
        LocalTime estimatedArrival = halts.get(halts.size() - 1).arrival();

        TrainStateDto.TrainStateDtoBuilder base = TrainStateDto.builder()
                .trainId(train.getId())
                .trainNumber(train.getTrainNumber())
                .name(train.getName())
                .type(train.getType())
                .priority(train.getPriority())
                .delayMinutes(timeline.delayMinutes())
                .estimatedArrival(estimatedArrival);

        HaltInterval firstHalt = halts.get(0);
        HaltInterval lastHalt = halts.get(halts.size() - 1);

        // 1. NOT_STARTED: before the origin's departure
        if (firstHalt.departure() != null && now.isBefore(firstHalt.departure())) {
            return base
                    .status(TrainRunStatus.NOT_STARTED)
                    .currentStationId(firstHalt.station().getId())
                    .currentStationCode(firstHalt.station().getCode())
                    .currentSpeedKmph(0.0)
                    .build();
        }

        // 2. RUNNING: inside a transit window
        for (TransitInterval t : transits) {
            if (t.isValid() && !now.isBefore(t.start()) && now.isBefore(t.end())) {
                double totalSeconds = java.time.Duration.between(t.start(), t.end()).toSeconds();
                double elapsedSeconds = java.time.Duration.between(t.start(), now).toSeconds();
                double progress = totalSeconds <= 0 ? 1.0 : Math.min(1.0, elapsedSeconds / totalSeconds);
                double lengthKm = t.section().getLengthKm();
                double positionKm = progress * lengthKm;
                double speedKmph = Math.min(train.getMaxSpeedKmph(), t.section().getMaxSpeedKmph());

                return base
                        .status(TrainRunStatus.RUNNING)
                        .currentSectionId(t.section().getId())
                        .fromStationCode(t.fromStation().getCode())
                        .toStationCode(t.toStation().getCode())
                        .positionKm(round(positionKm))
                        .sectionLengthKm(lengthKm)
                        .progressPercent(round(progress * 100))
                        .currentSpeedKmph(speedKmph)
                        .build();
            }
        }

        // 3. HALTED: inside an intermediate (or origin/destination edge-case) halt window
        for (HaltInterval h : halts) {
            if (h.arrival() != null && h.departure() != null && !now.isBefore(h.arrival()) && now.isBefore(h.departure())) {
                return base
                        .status(TrainRunStatus.HALTED)
                        .currentStationId(h.station().getId())
                        .currentStationCode(h.station().getCode())
                        .currentPlatformId(h.platform() != null ? h.platform().getId() : null)
                        .currentPlatformNumber(h.platform() != null ? h.platform().getPlatformNumber() : null)
                        .currentSpeedKmph(0.0)
                        .build();
            }
        }

        // 4. COMPLETED: at/after the destination's arrival
        if (lastHalt.arrival() != null && !now.isBefore(lastHalt.arrival())) {
            return base
                    .status(TrainRunStatus.COMPLETED)
                    .currentStationId(lastHalt.station().getId())
                    .currentStationCode(lastHalt.station().getCode())
                    .currentSpeedKmph(0.0)
                    .build();
        }

        // 5. Fallback (e.g. a missing TrackSection meant no transit window matched) -
        // report as halted at the last known station rather than throwing.
        return base
                .status(TrainRunStatus.NOT_STARTED)
                .currentStationId(firstHalt.station().getId())
                .currentStationCode(firstHalt.station().getCode())
                .currentSpeedKmph(0.0)
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
