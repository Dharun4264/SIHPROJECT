package com.sih.traffic.repository;

import com.sih.traffic.domain.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {
    List<TrainSchedule> findByTrainIdOrderBySequenceNoAsc(Long trainId);
    List<TrainSchedule> findByStationIdOrderByScheduledArrivalAsc(Long stationId);
}
