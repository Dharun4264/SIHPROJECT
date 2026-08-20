package com.sih.traffic.service.simulation;

import com.sih.traffic.domain.Train;

import java.util.List;

public record TrainTimeline(
        Train train,
        int delayMinutes,
        List<HaltInterval> halts,
        List<TransitInterval> transits
) {}
