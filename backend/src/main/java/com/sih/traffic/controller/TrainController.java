package com.sih.traffic.controller;

import com.sih.traffic.domain.Station;
import com.sih.traffic.domain.Train;
import com.sih.traffic.domain.TrainSchedule;
import com.sih.traffic.dto.TrainRequest;
import com.sih.traffic.exception.InvalidRequestException;
import com.sih.traffic.exception.ResourceNotFoundException;
import com.sih.traffic.repository.StationRepository;
import com.sih.traffic.repository.TrainRepository;
import com.sih.traffic.repository.TrainScheduleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final TrainScheduleRepository trainScheduleRepository;

    public TrainController(TrainRepository trainRepository,
                            StationRepository stationRepository,
                            TrainScheduleRepository trainScheduleRepository) {
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.trainScheduleRepository = trainScheduleRepository;
    }

    @GetMapping
    public List<Train> getAll() {
        return trainRepository.findAll();
    }

    @GetMapping("/{id}")
    public Train getById(@PathVariable Long id) {
        return findOrThrow(id);
    }

    @GetMapping("/{id}/schedule")
    public List<TrainSchedule> getSchedule(@PathVariable Long id) {
        findOrThrow(id);
        return trainScheduleRepository.findByTrainIdOrderBySequenceNoAsc(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Train create(@Valid @RequestBody TrainRequest request) {
        if (trainRepository.existsByTrainNumber(request.trainNumber())) {
            throw new InvalidRequestException("Train number already exists: " + request.trainNumber());
        }
        Train train = new Train();
        applyRequest(train, request);
        return trainRepository.save(train);
    }

    @PutMapping("/{id}")
    public Train update(@PathVariable Long id, @Valid @RequestBody TrainRequest request) {
        Train train = findOrThrow(id);
        applyRequest(train, request);
        return trainRepository.save(train);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        findOrThrow(id);
        trainRepository.deleteById(id);
    }

    private void applyRequest(Train train, TrainRequest request) {
        if (request.originStationId().equals(request.destinationStationId())) {
            throw new InvalidRequestException("originStationId and destinationStationId must be different");
        }
        Station origin = stationRepository.findById(request.originStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: id=" + request.originStationId()));
        Station destination = stationRepository.findById(request.destinationStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: id=" + request.destinationStationId()));
        train.setTrainNumber(request.trainNumber());
        train.setName(request.name());
        train.setType(request.type());
        train.setPriority(request.priority());
        train.setMaxSpeedKmph(request.maxSpeedKmph());
        train.setLengthM(request.lengthM());
        train.setOrigin(origin);
        train.setDestination(destination);
    }

    private Train findOrThrow(Long id) {
        return trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found: id=" + id));
    }
}
