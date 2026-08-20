package com.sih.traffic.dto;

import com.sih.traffic.domain.enums.TrainRunStatus;
import com.sih.traffic.domain.enums.TrainType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class TrainStateDto {
    private Long trainId;
    private String trainNumber;
    private String name;
    private TrainType type;
    private Integer priority;
    private TrainRunStatus status;
    private Integer delayMinutes;
    private LocalTime estimatedArrival; // effective (delay-adjusted) arrival at final destination

    // populated when NOT_STARTED / HALTED / COMPLETED
    private Long currentStationId;
    private String currentStationCode;
    private Long currentPlatformId;
    private String currentPlatformNumber;

    // populated when RUNNING (in transit between two stations)
    private Long currentSectionId;
    private String fromStationCode;
    private String toStationCode;
    private Double positionKm;
    private Double sectionLengthKm;
    private Double progressPercent;
    private Double currentSpeedKmph;
}
