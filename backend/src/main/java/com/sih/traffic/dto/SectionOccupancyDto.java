package com.sih.traffic.dto;

import com.sih.traffic.domain.enums.SectionType;

import java.time.LocalTime;

public record SectionOccupancyDto(
        Long sectionId,
        String fromStationCode,
        String toStationCode,
        SectionType sectionType,
        Long trainId,
        String trainNumber,
        LocalTime occupiedFrom,
        LocalTime occupiedUntil
) {}
