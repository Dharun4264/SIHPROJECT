package com.sih.traffic.repository;

import com.sih.traffic.domain.LoopLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoopLineRepository extends JpaRepository<LoopLine, Long> {
    List<LoopLine> findByStationId(Long stationId);
}
