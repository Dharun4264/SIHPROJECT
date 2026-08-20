package com.sih.traffic.controller;

import com.sih.traffic.dto.DecisionDto;
import com.sih.traffic.exception.ResourceNotFoundException;
import com.sih.traffic.service.decision.OptimizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Minimal controller for Phase 3 Step 4.
 * Exposes endpoints to retrieve decision recommendations for active conflicts.
 */
@RestController
@RequestMapping("/api/decisions")
public class DecisionController {

    private final OptimizationService optimizationService;

    public DecisionController(OptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }

    @GetMapping
    public List<DecisionDto> getDecisions() {
        return optimizationService.optimize();
    }

    @GetMapping("/{conflictId}")
    public DecisionDto getDecisionByConflictId(@PathVariable int conflictId) {
        return optimizationService.optimize().stream()
                .filter(d -> d.conflict().id() == conflictId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Decision not found for conflict id=" + conflictId));
    }
}
