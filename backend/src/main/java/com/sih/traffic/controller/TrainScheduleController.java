package com.sih.traffic.controller;

import com.sih.traffic.domain.Platform;
import com.sih.traffic.domain.Station;
import com.sih.traffic.domain.Train;
import com.sih.traffic.domain.TrainSchedule;
import com.sih.traffic.dto.TrainScheduleRequest;
import com.sih.traffic.exception.InvalidRequestException;
import com.sih.traffic.exception.ResourceNotFoundException;
import com.sih.traffic.repository.PlatformRepository;
import com.sih.traffic.repository.StationRepository;
import com.sih.traffic.repository.TrainRepository;
import com.sih.traffic.repository.TrainScheduleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/train-schedules")
public class TrainScheduleController {

    private final TrainScheduleRepository trainScheduleRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final PlatformRepository platformRepository;

    public TrainScheduleController(TrainScheduleRepository trainScheduleRepository,
                                    TrainRepository trainRepository,
                                    StationRepository stationRepository,
                                    PlatformRepository platformRepository) {
        this.trainScheduleRepository = trainScheduleRepository;
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.platformRepository = platformRepository;
    }

    @GetMapping
    public List<TrainSchedule> getAll() {
        return trainScheduleRepository.findAll();
    }

    @GetMapping("/{id}")
    public TrainSchedule getById(@PathVariable Long id) {
        return findOrThrow(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainSchedule create(@Valid @RequestBody TrainScheduleRequest request) {
        TrainSchedule schedule = new TrainSchedule();
        applyRequest(schedule, request);
        return trainScheduleRepository.save(schedule);
    }

    @PutMapping("/{id}")
    public TrainSchedule update(@PathVariable Long id, @Valid @RequestBody TrainScheduleRequest request) {
        TrainSchedule schedule = findOrThrow(id);
        applyRequest(schedule, request);
        return trainScheduleRepository.save(schedule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        findOrThrow(id);
        trainScheduleRepository.deleteById(id);
    }

    private void applyRequest(TrainSchedule schedule, TrainScheduleRequest request) {
        if (request.scheduledArrival() == null && request.scheduledDeparture() == null) {
            throw new InvalidRequestException("At least one of scheduledArrival or scheduledDeparture must be provided");
        }
        Train train = trainRepository.findById(request.trainId())
                .orElseThrow(() -> new ResourceNotFoundException("Train not found: id=" + request.trainId()));
        Station station = stationRepository.findById(request.stationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: id=" + request.stationId()));

        schedule.setTrain(train);
        schedule.setStation(station);
        schedule.setSequenceNo(request.sequenceNo());
        schedule.setScheduledArrival(request.scheduledArrival());
        schedule.setScheduledDeparture(request.scheduledDeparture());

        if (request.plannedPlatformId() != null) {
            Platform platform = platformRepository.findById(request.plannedPlatformId())
                    .orElseThrow(() -> new ResourceNotFoundException("Platform not found: id=" + request.plannedPlatformId()));
            if (!platform.getStation().getId().equals(station.getId())) {
                throw new InvalidRequestException("plannedPlatformId does not belong to stationId=" + station.getId());
            }
            schedule.setPlannedPlatform(platform);
        } else {
            schedule.setPlannedPlatform(null);
        }
    }

    private TrainSchedule findOrThrow(Long id) {
        return trainScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train schedule not found: id=" + id));
    }
}
