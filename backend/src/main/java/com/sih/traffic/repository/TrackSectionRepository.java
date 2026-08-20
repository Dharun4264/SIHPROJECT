package com.sih.traffic.repository;

import com.sih.traffic.domain.TrackSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackSectionRepository extends JpaRepository<TrackSection, Long> {
    List<TrackSection> findByFromStationIdOrToStationId(Long fromStationId, Long toStationId);

    // Added for Phase 2 simulation: resolves the section a train travels on
    // between two consecutive schedule stops. Direction-aware; callers try
    // both orderings since a train may traverse a section in either direction.
    Optional<TrackSection> findByFromStationIdAndToStationId(Long fromStationId, Long toStationId);
}
