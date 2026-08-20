package com.sih.traffic.service.simulation;

import com.sih.traffic.domain.Train;
import com.sih.traffic.domain.TrainSchedule;
import com.sih.traffic.dto.PlatformOccupancyDto;
import com.sih.traffic.dto.SectionOccupancyDto;
import com.sih.traffic.repository.TrainRepository;
import com.sih.traffic.repository.TrainScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class OccupancyService {

    private final TrainRepository trainRepository;
    private final TrainScheduleRepository trainScheduleRepository;
    private final TrainTimelineBuilder timelineBuilder;
    private final TrainDelayService trainDelayService;

    public OccupancyService(TrainRepository trainRepository,
                             TrainScheduleRepository trainScheduleRepository,
                             TrainTimelineBuilder timelineBuilder,
                             TrainDelayService trainDelayService) {
        this.trainRepository = trainRepository;
        this.trainScheduleRepository = trainScheduleRepository;
        this.timelineBuilder = timelineBuilder;
        this.trainDelayService = trainDelayService;
    }

    /** Builds every train's effective (delay-adjusted) timeline for the whole day. */
    public List<TrainTimeline> buildAllTimelines() {
        List<Train> trains = trainRepository.findAll();
        return trains.stream()
                .map(train -> {
                    List<TrainSchedule> stops = trainScheduleRepository.findByTrainIdOrderBySequenceNoAsc(train.getId());
                    int delay = trainDelayService.getDelayMinutes(train.getId());
                    return timelineBuilder.build(train, stops, delay);
                })
                .filter(tl -> !tl.halts().isEmpty())
                .toList();
    }

    public List<SectionOccupancyDto> currentSectionOccupancy(List<TrainTimeline> timelines, LocalTime now) {
        return timelines.stream()
                .flatMap(tl -> tl.transits().stream()
                        .filter(t -> t.isValid() && !now.isBefore(t.start()) && now.isBefore(t.end()))
                        .map(t -> new SectionOccupancyDto(
                                t.section().getId(),
                                t.fromStation().getCode(),
                                t.toStation().getCode(),
                                t.section().getSectionType(),
                                tl.train().getId(),
                                tl.train().getTrainNumber(),
                                t.start(),
                                t.end())))
                .toList();
    }

    public List<PlatformOccupancyDto> currentPlatformOccupancy(List<TrainTimeline> timelines, LocalTime now) {
        return timelines.stream()
                .flatMap(tl -> tl.halts().stream()
                        .filter(h -> h.platform() != null && h.arrival() != null && h.departure() != null
                                && !now.isBefore(h.arrival()) && now.isBefore(h.departure()))
                        .map(h -> new PlatformOccupancyDto(
                                h.platform().getId(),
                                h.station().getCode(),
                                h.platform().getPlatformNumber(),
                                tl.train().getId(),
                                tl.train().getTrainNumber(),
                                h.arrival(),
                                h.departure())))
                .toList();
    }
}
