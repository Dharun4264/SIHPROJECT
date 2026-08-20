package com.sih.traffic.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A siding at a station that allows a train to be held so another train can
 * cross or overtake on the main line. Only stations with at least one
 * LoopLine can host a crossing/overtake per PROJECT_SPEC.md section 3.4.
 */
@Entity
@Table(name = "loop_line",
        uniqueConstraints = @UniqueConstraint(name = "uk_loop_station_code",
                columnNames = {"station_id", "loop_code"}))
@Getter
@Setter
@NoArgsConstructor
public class LoopLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loopline_station"))
    private Station station;

    @NotBlank
    @Size(max = 10)
    @Column(name = "loop_code", nullable = false, length = 10)
    private String loopCode;

    @NotNull
    @Positive
    @Column(name = "max_length_m", nullable = false)
    private Double maxLengthM;
}
