package com.sih.traffic.controller;

import com.sih.traffic.domain.Station;
import com.sih.traffic.domain.TrackSection;
import com.sih.traffic.dto.TrackSectionRequest;
import com.sih.traffic.exception.InvalidRequestException;
import com.sih.traffic.exception.ResourceNotFoundException;
import com.sih.traffic.repository.StationRepository;
import com.sih.traffic.repository.TrackSectionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/track-sections")
public class TrackSectionController {

    private final TrackSectionRepository trackSectionRepository;
    private final StationRepository stationRepository;

    public TrackSectionController(TrackSectionRepository trackSectionRepository, StationRepository stationRepository) {
        this.trackSectionRepository = trackSectionRepository;
        this.stationRepository = stationRepository;
    }

    @GetMapping
    public List<TrackSection> getAll() {
        return trackSectionRepository.findAll();
    }

    @GetMapping("/{id}")
    public TrackSection getById(@PathVariable Long id) {
        return findOrThrow(id);
    }

    @GetMapping("/by-station/{stationId}")
    public List<TrackSection> getByStation(@PathVariable Long stationId) {
        return trackSectionRepository.findByFromStationIdOrToStationId(stationId, stationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrackSection create(@Valid @RequestBody TrackSectionRequest request) {
        TrackSection section = new TrackSection();
        applyRequest(section, request);
        return trackSectionRepository.save(section);
    }

    @PutMapping("/{id}")
    public TrackSection update(@PathVariable Long id, @Valid @RequestBody TrackSectionRequest request) {
        TrackSection section = findOrThrow(id);
        applyRequest(section, request);
        return trackSectionRepository.save(section);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        findOrThrow(id);
        trackSectionRepository.deleteById(id);
    }

    private void applyRequest(TrackSection section, TrackSectionRequest request) {
        if (request.fromStationId().equals(request.toStationId())) {
            throw new InvalidRequestException("fromStationId and toStationId must be different (no self-loop section)");
        }
        Station from = stationRepository.findById(request.fromStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: id=" + request.fromStationId()));
        Station to = stationRepository.findById(request.toStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: id=" + request.toStationId()));
        section.setFromStation(from);
        section.setToStation(to);
        section.setSectionType(request.sectionType());
        section.setLengthKm(request.lengthKm());
        section.setMaxSpeedKmph(request.maxSpeedKmph());
    }

    private TrackSection findOrThrow(Long id) {
        return trackSectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Track section not found: id=" + id));
    }
}
