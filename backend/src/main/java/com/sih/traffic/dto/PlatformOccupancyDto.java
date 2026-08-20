package com.sih.traffic.dto;

import java.time.LocalTime;

public record PlatformOccupancyDto(
        Long platformId,
        String stationCode,
        String platformNumber,
        Long trainId,
        String trainNumber,
        LocalTime occupiedFrom,
        LocalTime occupiedUntil
) {}
