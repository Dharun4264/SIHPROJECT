package com.sih.traffic.service.simulation;

import com.sih.traffic.domain.Train;
import com.sih.traffic.domain.TrainSchedule;
import com.sih.traffic.domain.TrackSection;
import com.sih.traffic.repository.TrackSectionRepository;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a TrainTimeline (ordered halts + transits, with effective
 * delay-shifted times) from a train's static TrainSchedule rows.
 * Pure/stateless aside from the TrackSection lookup.
 */
@Component
public class TrainTimelineBuilder {

    private final TrackSectionRepository trackSectionRepository;

    public TrainTimelineBuilder(TrackSectionRepository trackSectionRepository) {
        this.trackSectionRepository = trackSectionRepository;
    }

    public TrainTimeline build(Train train, List<TrainSchedule> orderedStops, int delayMinutes) {
        List<HaltInterval> halts = new ArrayList<>();
        List<TransitInterval> transits = new ArrayList<>();

        for (int i = 0; i < orderedStops.size(); i++) {
            TrainSchedule stop = orderedStops.get(i);
            boolean isOrigin = (i == 0);
            boolean isDestination = (i == orderedStops.size() - 1);

            LocalTime effArrival = shift(stop.getScheduledArrival(), delayMinutes);
            LocalTime effDeparture = shift(stop.getScheduledDeparture(), delayMinutes);

            halts.add(new HaltInterval(stop.getStation(), effArrival, effDeparture, stop.getPlannedPlatform(), isOrigin, isDestination));

            if (!isDestination) {
                TrainSchedule next = orderedStops.get(i + 1);
                TrackSection section = findConnectingSection(stop.getStation().getId(), next.getStation().getId());
                LocalTime legStart = effDeparture;
                LocalTime legEnd = shift(next.getScheduledArrival(), delayMinutes);
                transits.add(new TransitInterval(stop.getStation(), next.getStation(), section, legStart, legEnd));
            }
        }

        return new TrainTimeline(train, delayMinutes, halts, transits);
    }

    private LocalTime shift(LocalTime time, int minutes) {
        return time == null ? null : time.plusMinutes(minutes);
    }

    private TrackSection findConnectingSection(Long stationAId, Long stationBId) {
        return trackSectionRepository.findByFromStationIdAndToStationId(stationAId, stationBId)
                .or(() -> trackSectionRepository.findByFromStationIdAndToStationId(stationBId, stationAId))
                .orElse(null); // null = no section defined between these consecutive stops (data issue upstream)
    }
}
