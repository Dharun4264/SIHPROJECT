package com.sih.traffic.controller;

import com.sih.traffic.domain.LoopLine;
import com.sih.traffic.domain.Platform;
import com.sih.traffic.domain.Station;
import com.sih.traffic.dto.StationRequest;
import com.sih.traffic.exception.InvalidRequestException;
import com.sih.traffic.exception.ResourceNotFoundException;
import com.sih.traffic.repository.LoopLineRepository;
import com.sih.traffic.repository.PlatformRepository;
import com.sih.traffic.repository.StationRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationRepository stationRepository;
    private final PlatformRepository platformRepository;
    private final LoopLineRepository loopLineRepository;

    public StationController(StationRepository stationRepository,
                              PlatformRepository platformRepository,
                              LoopLineRepository loopLineRepository) {
        this.stationRepository = stationRepository;
        this.platformRepository = platformRepository;
        this.loopLineRepository = loopLineRepository;
    }

    @GetMapping
    public List<Station> getAll() {
        return stationRepository.findAll();
    }

    @GetMapping("/{id}")
    public Station getById(@PathVariable Long id) {
        return findOrThrow(id);
    }

    @GetMapping("/{id}/platforms")
    public List<Platform> getPlatforms(@PathVariable Long id) {
        findOrThrow(id);
        return platformRepository.findByStationId(id);
    }

    @GetMapping("/{id}/loop-lines")
    public List<LoopLine> getLoopLines(@PathVariable Long id) {
        findOrThrow(id);
        return loopLineRepository.findByStationId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Station create(@Valid @RequestBody StationRequest request) {
        if (stationRepository.existsByCode(request.code())) {
            throw new InvalidRequestException("Station code already exists: " + request.code());
        }
        Station station = new Station();
        applyRequest(station, request);
        return stationRepository.save(station);
    }

    @PutMapping("/{id}")
    public Station update(@PathVariable Long id, @Valid @RequestBody StationRequest request) {
        Station station = findOrThrow(id);
        stationRepository.findByCode(request.code()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new InvalidRequestException("Station code already exists: " + request.code());
            }
        });
        applyRequest(station, request);
        return stationRepository.save(station);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        findOrThrow(id);
        stationRepository.deleteById(id);
    }

    private void applyRequest(Station station, StationRequest request) {
        station.setCode(request.code());
        station.setName(request.name());
        station.setLatitude(request.latitude());
        station.setLongitude(request.longitude());
        station.setStationType(request.stationType());
    }

    private Station findOrThrow(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: id=" + id));
    }
}
