package com.sih.traffic.controller;

import com.sih.traffic.domain.Platform;
import com.sih.traffic.domain.Station;
import com.sih.traffic.dto.PlatformRequest;
import com.sih.traffic.exception.ResourceNotFoundException;
import com.sih.traffic.repository.PlatformRepository;
import com.sih.traffic.repository.StationRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/platforms")
public class PlatformController {

    private final PlatformRepository platformRepository;
    private final StationRepository stationRepository;

    public PlatformController(PlatformRepository platformRepository, StationRepository stationRepository) {
        this.platformRepository = platformRepository;
        this.stationRepository = stationRepository;
    }

    @GetMapping
    public List<Platform> getAll() {
        return platformRepository.findAll();
    }

    @GetMapping("/{id}")
    public Platform getById(@PathVariable Long id) {
        return findOrThrow(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Platform create(@Valid @RequestBody PlatformRequest request) {
        Platform platform = new Platform();
        applyRequest(platform, request);
        return platformRepository.save(platform);
    }

    @PutMapping("/{id}")
    public Platform update(@PathVariable Long id, @Valid @RequestBody PlatformRequest request) {
        Platform platform = findOrThrow(id);
        applyRequest(platform, request);
        return platformRepository.save(platform);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        findOrThrow(id);
        platformRepository.deleteById(id);
    }

    private void applyRequest(Platform platform, PlatformRequest request) {
        Station station = stationRepository.findById(request.stationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: id=" + request.stationId()));
        platform.setStation(station);
        platform.setPlatformNumber(request.platformNumber());
        platform.setLengthM(request.lengthM());
        platform.setCompatibleTrainTypes(request.compatibleTrainTypes() == null
                ? new HashSet<>()
                : new HashSet<>(request.compatibleTrainTypes()));
    }

    private Platform findOrThrow(Long id) {
        return platformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform not found: id=" + id));
    }
}
