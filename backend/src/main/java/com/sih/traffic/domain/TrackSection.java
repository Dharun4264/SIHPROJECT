package com.sih.traffic.domain;

import com.sih.traffic.domain.enums.SectionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A physical edge of the railway graph between two stations.
 * SINGLE track sections have capacity 1 (mutually exclusive occupancy,
 * both directions). DOUBLE track sections have capacity 1 per direction.
 * Enforced in this phase only via the sectionType flag; occupancy/conflict
 * checks belong to later simulation phases.
 */
@Entity
@Table(name = "track_section")
@Getter
@Setter
@NoArgsConstructor
public class TrackSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "from_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_from_station"))
    private Station fromStation;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "to_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_station"))
    private Station toStation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "section_type", nullable = false, length = 10)
    private SectionType sectionType;

    @NotNull
    @Positive
    @Column(name = "length_km", nullable = false)
    private Double lengthKm;

    @NotNull
    @Positive
    @Column(name = "max_speed_kmph", nullable = false)
    private Integer maxSpeedKmph;
}
