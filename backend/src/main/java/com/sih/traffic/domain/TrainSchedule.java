package com.sih.traffic.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/**
 * One ordered stop of a Train's static timetable. sequenceNo defines stop
 * order along the route (1 = origin). scheduledArrival is null for the
 * origin stop; scheduledDeparture is null for the destination stop.
 * plannedPlatform is the originally-timetabled platform and may later be
 * overridden by a (Phase 4+) recommendation - not implemented in this phase.
 */
@Entity
@Table(name = "train_schedule",
        uniqueConstraints = @UniqueConstraint(name = "uk_schedule_train_sequence",
                columnNames = {"train_id", "sequence_no"}))
@Getter
@Setter
@NoArgsConstructor
public class TrainSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "train_id", nullable = false, foreignKey = @ForeignKey(name = "fk_schedule_train"))
    private Train train;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_schedule_station"))
    private Station station;

    @NotNull
    @Min(1)
    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    @Column(name = "scheduled_arrival")
    private LocalTime scheduledArrival;

    @Column(name = "scheduled_departure")
    private LocalTime scheduledDeparture;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "planned_platform_id", foreignKey = @ForeignKey(name = "fk_schedule_platform"))
    private Platform plannedPlatform;
}
