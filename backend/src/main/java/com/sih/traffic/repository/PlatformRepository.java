package com.sih.traffic.repository;

import com.sih.traffic.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
    List<Platform> findByStationId(Long stationId);
}
