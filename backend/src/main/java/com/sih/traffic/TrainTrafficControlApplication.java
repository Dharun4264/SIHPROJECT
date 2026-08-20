package com.sih.traffic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Phase 1: static railway network foundation.
 * Phase 2: simulation clock, train movement derivation, occupancy and
 * conflict detection - all computed from the static timetable plus an
 * in-memory delay offset (no ML/OR-Tools/WebSocket/auth in this phase).
 *
 * Explicitly OUT of scope for this phase: AI/ML, OR-Tools optimization,
 * WebSocket, authentication.
 */
@SpringBootApplication
@EnableScheduling
public class TrainTrafficControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainTrafficControlApplication.class, args);
    }
}
