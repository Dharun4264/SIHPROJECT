
package com.sih.traffic.controller;

import com.sih.traffic.domain.LoopLine;
import com.sih.traffic.domain.Station;
import com.sih.traffic.dto.LoopLineRequest;
import com.sih.traffic.exception.ResourceNotFoundException;
import com.sih.traffic.repository.LoopLineRepository;
import com.sih.traffic.repository.StationRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loop-lines")
public class LoopLineController {

    private final LoopLineRepository loopLineRepository;
    private final StationRepository stationRepository;

    public LoopLineController(LoopLineRepository loopLineRepository, StationRepository stationRepository) {
        this.loopLineRepository = loopLineRepository;
        this.stationRepository = stationRepository;
    }

    @GetMapping
    public List<LoopLine> getAll() {
        return loopLineRepository.findAll();
    }

    @GetMapping("/{id}")
    public LoopLine getById(@PathVariable Long id) {
        return findOrThrow(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoopLine create(@Valid @RequestBody LoopLineRequest request) {
        LoopLine loopLine = new LoopLine();
        applyRequest(loopLine, request);
        return loopLineRepository.save(loopLine);
    }

    @PutMapping("/{id}")
    public LoopLine update(@PathVariable Long id, @Valid @RequestBody LoopLineRequest request) {
        LoopLine loopLine = findOrThrow(id);
        applyRequest(loopLine, request);
        return loopLineRepository.save(loopLine);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        findOrThrow(id);
        loopLineRepository.deleteById(id);
    }

    private void applyRequest(LoopLine loopLine, LoopLineRequest request) {
        Station station = stationRepository.findById(request.stationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found: id=" + request.stationId()));
        loopLine.setStation(station);
        loopLine.setLoopCode(request.loopCode());
        loopLine.setMaxLengthM(request.maxLengthM());
    }

    private LoopLine findOrThrow(Long id) {
        return loopLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loop line not found: id=" + id));
    }
}
